package com.baurine.instamaterial;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;

public class InstaMaterialApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AVOSCloud.initialize(this,
                "6xfzwyvk2cq9903d81tj9k40bf6gkwwvz71gjwc13h7arvbb",
                "nxm2dtmwnuqboi3aln26hmvif3xmlu4vbzghi938v896n3oj");

        testLeanCloud();
    }

    private void testLeanCloud() {
        AVObject testObject = new AVObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }

}
