package edu.wpi.cs3733.D21.teamE.views.serviceRequestObjects;

public class ReligiousRequestObj extends ServiceRequestObjs {

    private String religion;
    private String description;


    public ReligiousRequestObj(int requestID, int userID, String nodeID, int assigneeID, String religion, String description) {

        super.requestID = requestID;
        super.nodeID = nodeID;
        super.userID = userID;
        super.assigneeID = assigneeID;
        this.religion = religion;
        this.description = description;
        super.status = "In Progress";

    }

    public String getReligion() {
        return this.religion;
    }

    public String getDescription() {
        return this.description;
    }

}
