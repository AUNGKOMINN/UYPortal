package uycs.uyportal.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Dell on 10/28/2015.
 */
@ParseClassName("posts")
public class posts  extends ParseObject{
    public posts() {
        super();
    }

  public void setStatus(String status){
      put("status", status);
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
    public void setToPlace(String toPlace){
        put("to",toPlace);
    }
    public String getToplace(){
        return getString("to");
    }
    public void setFromUserid(ParseUser userid){
        put("from",userid.getObjectId());
    }
    public String getUserid(){
      return getString("from");
    }
    public String getPostid(){
        return getObjectId();
    }
}
