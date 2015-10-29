package uycs.uyportal.model;

/**
 * Created by All User on 10/24/2015.
 */
public class Events_Model {
    String objectId;
    String eventName;
    String eventDate;
    String eventLocation;
    String importance;
    int importance_Int;

    public Events_Model(String objectId, String eventName, String eventDate, String eventLocation, String importance, int importance_Int) {
        this.objectId = objectId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.importance = importance;
        this.importance_Int = importance_Int;

    }

    public String getImportance() {
        return importance;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public int getImportance_Int() {
        return importance_Int;
    }



}
