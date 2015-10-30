package uycs.uyportal.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Dell on 10/28/2015.
 */
@ParseClassName("comments")
public class comments extends ParseObject {

    public comments() {
        super();
    }
    public void setcomment(String comment){
        put("status", comment);
    }
    public String getStatus(){
        return getString("status");
    }
    public void setUsername(String username){
        put("username",username);
    }
    public String getUsername(){
        return getString("username");
    }
    public void setToPostid(String toPost){
        put("to", toPost);
    }
    public String getTopost(){
        return getString("to");
    }
    public void setFromUserid(ParseUser userid){
        put("from",userid.getObjectId());
    }
    public String getUserid(){
        return getString("from");
    }

}
