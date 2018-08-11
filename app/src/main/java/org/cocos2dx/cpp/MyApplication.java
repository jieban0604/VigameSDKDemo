package org.cocos2dx.cpp;



import android.app.Application;
import android.content.Context;

//import com.libExtention.BuglyExt;
import com.libVigame.VigameLoader;

public class MyApplication extends Application {
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        VigameLoader.applicationAttachBaseContext(this, base);
    }

    public void onCreate() {
        super.onCreate();
        VigameLoader.applicationOnCreate(this);
    }

}
