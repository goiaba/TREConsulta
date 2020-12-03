package br.com.goiaba.treconsulta.model;

public class Voter {
    private String name;
    private String birthdate;
    private String voterId;
    private String votingSection;
    private String votingPlace;
    private String county;
    private String electoralZone;

    public Voter() {}

    public Voter(String name, String birthdate, String voterId, String votingSection,
                 String votingPlace, String county, String electoralZone) {
        this.name = name;
        this.birthdate = birthdate;
        this.voterId = voterId;
        this.votingSection = votingSection;
        this.votingPlace = votingPlace;
        this.county = county;
        this.electoralZone = electoralZone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public String getVotingPlace() {
        return votingPlace;
    }

    public void setVotingPlace(String votingPlace) {
        this.votingPlace = votingPlace;
    }

    public String getVotingSection() {
        return votingSection;
    }

    public void setVotingSection(String votingSection) {
        this.votingSection = votingSection;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getElectoralZone() {
        return electoralZone;
    }

    public void setElectoralZone(String electoralZone) {
        this.electoralZone = electoralZone;
    }
}
