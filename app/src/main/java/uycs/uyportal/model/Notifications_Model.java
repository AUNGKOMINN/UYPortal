package uycs.uyportal.model;

/**
 * Created by All User on 10/29/2015.
 */
public class Notifications_Model {
    String notification_Type;
    String from_User;

    public String getNotification_Type() {
        return notification_Type;
    }

    public String getFrom_User() {
        return from_User;
    }



    public Notifications_Model(String notification_type, String from_user) {
        this.notification_Type = notification_type;
        this.from_User = from_user;
    }
}
