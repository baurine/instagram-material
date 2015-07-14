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

    // 注意，静态变量中不要维持对 context 的引用
    // 所引用的成员在不用时也要尽早释放
    private static UserManager mInstance;

    public static UserManager getInstance() {
        if (mInstance == null) {
            mInstance = new UserManager();
        }
        return mInstance;
    }

    private UserManager() {
        mACache = InstaMaterialApp.getACache();
    }

    public void resetUserInfo() {
        mSnsJsonObj = null;
        mUserSnsProfile = null;
        mAVUser = null;
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
            // 释放引用
            mSnsJsonObj = null;
        }
    }

    public UserSnsProfile getUserSnsProfile() {
        return mUserSnsProfile;
    }
}
