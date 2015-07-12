package com.baurine.instamaterial.manager;

import com.avos.avoscloud.AVUser;
import com.baurine.instamaterial.InstaMaterialApp;
import com.baurine.instamaterial.data.UserSnsProfile;

import org.afinal.simplecache.ACache;
import org.json.JSONObject;

public class UserManager {

    private static final String WEIBO_PROFILE_KEY = "weibo_profile";

    private JSONObject mSnsJsonObj;
    private UserSnsProfile mUserSnsProfile;
    private AVUser mAVUser;

    private ACache mACache;

    private static UserManager mInstance;

    public static UserManager getInstance() {
        if (mInstance == null) {
            mInstance = new UserManager();
        }
        return mInstance;
    }

    private UserManager() {
        mACache = ACache.get(InstaMaterialApp.getContext());
    }

    public void saveUserSnsProfile(JSONObject jsonObject) {
        mSnsJsonObj = jsonObject;
        if (jsonObject != null) {
            mACache.put(WEIBO_PROFILE_KEY, jsonObject);
        }
    }

    public void saveAVUser(AVUser avUser) {
        mAVUser = avUser;

        if (mSnsJsonObj == null) {
            mSnsJsonObj = mACache.getAsJSONObject(WEIBO_PROFILE_KEY);
        }
        if (mSnsJsonObj != null) {
            mUserSnsProfile = UserSnsProfile.createFromJsonObj(mSnsJsonObj);
        }
    }

    public UserSnsProfile getUserSnsProfile() {
        return mUserSnsProfile;
    }

}
