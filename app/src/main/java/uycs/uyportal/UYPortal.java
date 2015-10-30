package uycs.uyportal;

import android.app.Application;
import android.content.Intent;

import com.parse.Parse;
import com.parse.ParseObject;

import uycs.uyportal.model.comments;
import uycs.uyportal.model.posts;

/**
 * Created by AungKo on 10/14/2015.
 */
public class UYPortal extends Application {

    public static final String APP_ID = "I6b99wcqRWRvMidSF0qm6fBDfqxiq9dK8gR85Arg";
    public static final String CLIENT_ID = "C9pDP6hgMUsmMca1DLodci6aXtxvwo3u8eZL0JTR";


    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(posts.class);
        ParseObject.registerSubclass(comments.class);
        Parse.initialize(this, APP_ID, CLIENT_ID);
    }
}
