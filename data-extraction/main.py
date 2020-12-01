import fitz
import itertools
import json
import logging
import re
from model import db, ElectoralZone, County, VotingPlace, VotingSection, Voter


logging.basicConfig(level=logging.DEBUG)


def transform(filename, handle_voting_place, handle_voter):
    header_regex = "ELEIÇÕES (20[0-9]{2})\nELEITORADO APTO POR ZONA ELEITORAL E MUNICÍPIO\nZona.: ([0-9]+)\nMunicípi" \
                   "o: (.*?)\n\(\*\) Eleitores com Nome iniciado pela letra ([A-Z])\nNome do Eleitor\n.*\nData-Nasc" \
                   "\.\n.*\nInscrição\nSeção\n.*\nLocal de Votação\n.*\n"
    footer_regex = "\nTRE-SP/STI/CID/ScBD\nPágina ([0-9]+)\n"
    with fitz.Document(filename) as doc:
        for page in range(doc.pageCount):
            content = doc.loadPage(page).getText("text")
            if header_matches := re.match(header_regex, content):
                page_header = header_matches.groups()
                electoral_zone_code = int(page_header[1])
                county_code, county_name = _split_fields(page_header[2], "-", 2, slice(2))
                county_code = int(county_code)
            if footer_matches := re.search(footer_regex, content):
                page_footer = footer_matches.groups()

            # remove header from content
            content = re.sub(header_regex, '', content)
            # remove footer from content
            content = re.sub(footer_regex, '', content)
            # each line represents a single voter
            lines = content.split("\n")

            # Expected line fields: "name", "birthdate+voterId", "votingSection", "votingPlaceId+votingPlace"
            num_of_expected_fields = 4
            if not len(lines) % num_of_expected_fields == 0:
                raise ValueError("Wrong number of lines. Please check PDF format before trying again.")
            record_tuples = _grouper(lines, num_of_expected_fields)

            for record in record_tuples:
                voter_name = record[0]
                birthdate, voter_id = _split_fields(record[1], " ", 2, slice(2))
                voting_section = record[2]
                place_code, place_name = _split_fields(record[3], "->", 2, slice(2))
                place_code = int(place_code)

                handle_voting_place(electoral_zone_code, county_code, county_name, place_code, place_name)
                handle_voter(voter_name, birthdate, voter_id, voting_section, place_code, county_code,
                             electoral_zone_code)
    logging.info("PDF file transformation finished")


def pdf_to_json(filename, json_filename):
    def handle_voting_place(electoral_zone, county_id, county, voting_place_id, voting_place):
        if not records["electoralZone"].get(electoral_zone, {}):
            records["electoralZone"][electoral_zone] = {"county": {}}

        counties = records["electoralZone"][electoral_zone]["county"]
        if not counties.get(county_id, {}):
            counties[county_id] = {"name": county, "votingPlace": {}}

        voting_places = counties[county_id]["votingPlace"]
        if not voting_places.get(voting_place_id, {}):
            voting_places[voting_place_id] = {"name": voting_place, "votingSection": {}}

    def handle_voter(name, birthdate, voter_id, voting_section, voting_place_id, county_id, electoral_zone):
        voting_places = records["electoralZone"][electoral_zone]["county"][county_id]["votingPlace"]
        voting_sections = voting_places[voting_place_id]["votingSection"]
        if not voting_sections.get(voting_section, {}):
            voting_sections[voting_section] = {"voter": []}
        voting_sections[voting_section]["voter"].append({
            "name": name.upper(),
            "birthdate": birthdate,
            "voterId": voter_id
        })

    records = {"electoralZone": {}}
    transform(filename, handle_voting_place, handle_voter)
    with open(json_filename, "w") as fh:
        json.dump(records, fh, indent=4, sort_keys=True, ensure_ascii=False)


def pdf_to_db(filename):
    def handle_voting_place(electoral_zone_code, county_code, county_name, place_code, place_name):
        place_db_id = place_cache.get(f"{electoral_zone_code}#{county_code}#{place_code}")
        if not place_db_id:
            county_db_id = county_cache.get(f"{electoral_zone_code}#{county_code}")
            if not county_db_id:
                electoral_zone_db_id = electoral_zone_cache.get(electoral_zone_code)
                if not electoral_zone_db_id:
                    electoral_zone = ElectoralZone.create(code=electoral_zone_code)
                    electoral_zone_cache.update({electoral_zone_code: electoral_zone.id})
                    electoral_zone_db_id = electoral_zone.id
                county = County.create(electoral_zone_id=electoral_zone_db_id, code=county_code, name=county_name)
                county_cache.update({f"{electoral_zone_code}#{county_code}": county.id})
                county_db_id = county.id
            place = VotingPlace.create(county_id=county_db_id, name=place_name)
            place_cache.update({f"{electoral_zone_code}#{county_code}#{place_code}": place.id})

    def handle_voter(name, birthdate, voter_id, section_code, place_code, county_code, electoral_zone_code):
        section_db_id = section_cache.get(f"{electoral_zone_code}#{county_code}#{place_code}#{section_code}")
        if not section_db_id:
            place_db_id = place_cache.get(f"{electoral_zone_code}#{county_code}#{place_code}")
            section = VotingSection.create(voting_place_id=place_db_id, code=section_code)
            section_cache.update({f"{electoral_zone_code}#{county_code}#{place_code}#{section_code}": section.id})
            section_db_id = section.id
        voters.append({
            "voting_section_id": section_db_id,
            "name": name.upper(),
            "birthdate": birthdate,
            "voter_id": voter_id
        })

    electoral_zone_cache = {}
    county_cache = {}
    place_cache = {}
    section_cache = {}
    voters = []
    transform(filename, handle_voting_place, handle_voter)
    db_batch_size = 100
    with db.atomic():
        for idx in range(0, len(voters), db_batch_size):
            Voter.insert_many(voters[idx:idx + db_batch_size]).execute()


def _split_fields(string, sep, expected_count_fields, slices):
    split = string.split(sep)
    if len(split) != expected_count_fields:
        raise ValueError(f"Error parsing {string}")
    return split[slices]


def _grouper(iterable, n):
    args = [iter(iterable)] * n
    return itertools.zip_longest(*args, fillvalue=None)


pdf_to_json("input.pdf", "output.json")
pdf_to_db("input.pdf")
