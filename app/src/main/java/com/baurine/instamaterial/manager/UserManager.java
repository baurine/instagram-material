package com.baurine.instamaterial.manager;

import com.avos.avoscloud.AVUser;
import com.baurine.instamaterial.data.UserSnsProfile;

import org.json.JSONObject;

public class UserManager {

    private UserSnsProfile mUserSnsProfile;
    private AVUser mAVUser;

    private static UserManager mInstance;

    public static UserManager getInstance() {
        if (mInstance == null) {
            mInstance = new UserManager();
        }
        return mInstance;
    }

    private UserManager() {
    }

    public void setUserSnsProfile(JSONObject jsonObject) {
        mUserSnsProfile = UserSnsProfile.createFromJsonObj(jsonObject);
    }

    public void setAVUser(AVUser avUser) {
        mAVUser = avUser;
    }

}
