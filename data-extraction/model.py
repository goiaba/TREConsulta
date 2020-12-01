from peewee import *

db = SqliteDatabase("treconsulta.db")


class BaseModel(Model):
    class Meta:
        database = db


class ElectoralZone(BaseModel):
    code = IntegerField(null=False)


class County(BaseModel):
    electoral_zone_id = ForeignKeyField(ElectoralZone, backref="counties")
    code = IntegerField(null=False)
    name = CharField()

    class Meta:
        indexes = ((("electoral_zone_id", "code"), True),)


class VotingPlace(BaseModel):
    county_id = ForeignKeyField(County, backref="places")
    name = CharField()

    class Meta:
        indexes = ((("county_id", "name"), True),)


class VotingSection(BaseModel):
    voting_place_id = ForeignKeyField(VotingPlace, backref="sections")
    code = CharField()

    class Meta:
        indexes = ((("voting_place_id", "code"), True),)


class Voter(BaseModel):
    name = CharField()
    birthdate = CharField()
    voter_id = CharField(unique=True)
    voting_section_id = ForeignKeyField(VotingSection, backref="voters")


def create_tables(dtb=None):
    tables_names = [ElectoralZone, County, VotingPlace, VotingSection, Voter]
    if dtb:
        dtb.create_tables(tables_names)
    else:
        db.create_tables(tables_names)


db.connect()
create_tables()
