package edu.wpi.cs3733.D21.teamE.scheduler;

import edu.wpi.cs3733.D21.teamE.Date;
import edu.wpi.cs3733.D21.teamE.Time;
import edu.wpi.cs3733.D21.teamE.map.Node;

public class ToDo implements Comparable<ToDo>{
    private final int todoID;
    private final String title;
    private final int userID;
    private final int status;
    private final int priority;

    //possibly null
    private Node location;
    private Date scheduledDate;
    private Time startTime;
    private Time endTime;
    private String detail;
    private Date notificationDate;
    private Time notificationTime;

    public ToDo(int _todoID, String _title, int _userID, int _status, int _priority){
        todoID = _todoID;
        title = _title;
	    userID = _userID;
	    status = _status;
        priority = _priority;
    }

    public ToDo(int _todoID, String _title, int _userID, int _status, int _priority, Node _location, Date _scheduleDate, Time _startTime, Time _endTime, String _detail, Date _notificationDate, Time _notificationTime){
        this(_todoID, _title, _userID, _status, _priority);
	    location = _location;
	    scheduledDate = _scheduleDate;
        startTime = _startTime;
	    endTime = _endTime;
	    detail = _detail;
        notificationDate = _notificationDate;
        notificationTime = _notificationTime;
    }

    public ToDo(int _toDoID, String _title, int _userID, int _status, int _priority, Node _location, String _scheduledDate, String _startTime, String _endTime, String _detail, String _notificationDate, String _notificationTime) {
        this(_toDoID, _title, _userID, _status, _priority, _location, Date.parseString(_scheduledDate), Time.parseString(_startTime), Time.parseString(_endTime), _detail, Date.parseString(_notificationDate), Time.parseString(_notificationTime));
    }

    public int getTodoID() {
        return todoID;
    }

    public String getTitle() {
        return title;
    }

    public int getUserID() {
        return userID;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusString() {
        if ((Integer) status != null) {
            if (status == 1) {
                return "Ongoing";
            } else if (status == 10) {
                return "Completed";
            } else {
                return "Deleted";
            }
        } else {
            return "";
        }
    }

    public int getPriority() {
        return priority;
    }

    public String getPriorityString() {
        switch (priority) {
            case 1:
                return "Low";
            case 2:
                return "Medium";
            case 3:
                return "High";
            default:
                return "";
        }
    }

    public Time getNotificationTime() {
        return notificationTime;
    }

    public Time getStartTime() {
        return startTime;
    }

    public String getStartTimeString() {
        if(startTime != null) {
            return startTime.hourMinString();
        } else {
            return "";
        }
    }

    public Time getEndTime() {
        return endTime;
    }

    public Node getLocation() {
        return location;
    }

    public String getLocationString() {
        if (location != null) {
            return location.get("longName");
        } else {
            return "";
        }
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public String getDetail() {
        return detail;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    @Override
    public int compareTo(ToDo t) {
        return startTime.compareTo(t.startTime);
    }

    public boolean isConflict(ToDo t) {
        return isConflict(t.startTime, t.endTime);
    }

    public boolean isConflict(Time startTime, Time endTime){
        if(endTime.isEmpty() && this.endTime.isEmpty()){
            return startTime.compareTo(this.startTime) == 0;
        } else if(endTime.isEmpty()){
            return startTime.compareTo(this.startTime) > 0 && startTime.compareTo(this.endTime) < 0;
        } else if(this.endTime.isEmpty()){
            return this.startTime.compareTo(startTime) > 0 && this.startTime.compareTo(endTime) < 0;
        } else {
            return (startTime.compareTo(this.startTime) >= 0 && startTime.compareTo(this.endTime) <= 0) ||
                    (this.startTime.compareTo(startTime) >= 0 && this.startTime.compareTo(endTime) <= 0);
        }
    }

    public boolean equals(ToDo t){
        return todoID == t.todoID;
    }
}
