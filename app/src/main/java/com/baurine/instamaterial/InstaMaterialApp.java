package com.baurine.instamaterial;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;

public class InstaMaterialApp extends Application {

    private static InstaMaterialApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        AVOSCloud.initialize(this,
                "6xfzwyvk2cq9903d81tj9k40bf6gkwwvz71gjwc13h7arvbb",
                "nxm2dtmwnuqboi3aln26hmvif3xmlu4vbzghi938v896n3oj");
        // testLeanCloud();
    }

    @SuppressWarnings("unused")
    private void testLeanCloud() {
        AVObject testObject = new AVObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }

    public static Context getContext() {
        return mInstance;
    }
}
