package com.baurine.instamaterial.data;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSnsProfile {

    public String mName;
    public String mAvatarUrlLarge;
    public String mAbout;

    public static UserSnsProfile createFromJsonObj(JSONObject jsonObject) {
        UserSnsProfile userSnsProfile = new UserSnsProfile();

        try {
            JSONObject userInfo = jsonObject.getJSONObject("user_info");
            userSnsProfile.mName = userInfo.getString("name");
            userSnsProfile.mAvatarUrlLarge = userInfo.getString("avatar_large");
            userSnsProfile.mAbout = userInfo.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
            userSnsProfile = null;
        }

        return userSnsProfile;
    }

}
