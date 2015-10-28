package uycs.uyportal.ui;

import android.graphics.drawable.Drawable;

/**
 * Created by AungKo on 10/26/2015.
 */
public class User {
    private String mName,mEmail,mPwd,mMajor,mUsername;
    private Drawable mNameImg;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public Drawable getmNameImg() {
        return mNameImg;
    }

    public void setmNameImg(Drawable mNameImg) {
        this.mNameImg = mNameImg;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPwd() {
        return mPwd;
    }

    public void setmPwd(String mPwd) {
        this.mPwd = mPwd;
    }

    public String getmMajor() {
        return mMajor;
    }

    public void setmMajor(String mMajor) {
        this.mMajor = mMajor;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }
}