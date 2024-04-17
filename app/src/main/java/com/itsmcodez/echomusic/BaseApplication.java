package com.itsmcodez.echomusic;
import android.app.Application;

public class BaseApplication extends Application {
    private static Application application;
    
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
    
    public static Application getApplication() {
    	return BaseApplication.application;
    }
}
