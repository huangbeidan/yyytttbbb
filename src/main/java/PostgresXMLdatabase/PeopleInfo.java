package PostgresXMLdatabase;

import java.util.List;

public class PeopleInfo {

    List<String> peopleSpeakerArray;
    List<String> WitnessesArray;
    String Doc_title;
    String HearingID;
    String info;
    String committeeName;
    String subcommitteeName;

    public PeopleInfo(List<String> peopleSpeakerArray, List<String> witnessesArray, String doc_title, String hearingID, String info, String committeeName, String subcommitteeName) {
        this.peopleSpeakerArray = peopleSpeakerArray;
        WitnessesArray = witnessesArray;
        Doc_title = doc_title;
        HearingID = hearingID;
        this.info = info;
        this.committeeName = committeeName;
        this.subcommitteeName = subcommitteeName;
    }

    public List<String> getPeopleSpeakerArray() {
        return peopleSpeakerArray;
    }

    public List<String> getWitnessesArray() {
        return WitnessesArray;
    }

    public String getDoc_title() {
        return Doc_title;
    }

    public String getHearingID() {
        return HearingID;
    }

    public String getInfo() {
        return info;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    public String getSubcommitteeName() {
        return subcommitteeName;
    }
}
