package edu.wpi.TeamE.views.serviceRequestObjects;

public class ExternalPatientObj extends ServiceRequestObjs {

    private String severity;

    private String requestType;

    private String patientID;

    private String eta;

    private String details;

    public ExternalPatientObj(int userID, String nodeID, int assigneeID, String severity, String requestType, String patientID, String eta, String details) {

        super.assigneeID = assigneeID;
        super.userID = userID;
        super.nodeID = nodeID;
        this.severity = severity;
        this.requestType = requestType;
        this.patientID = patientID;
        this.eta = eta;
        this.details = details;
        super.status = "In Progress";

    }



}
