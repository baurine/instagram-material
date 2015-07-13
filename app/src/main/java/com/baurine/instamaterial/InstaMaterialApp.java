package com.baurine.instamaterial;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;

import org.afinal.simplecache.ACache;

public class InstaMaterialApp extends Application {

    private static InstaMaterialApp mInstance;

    private ACache mACache;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        mACache = ACache.get(this);

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

    public static InstaMaterialApp getInstance() {
        return mInstance;
    }

    public static ACache getACache() {
        return getInstance().mACache;
    }
}
