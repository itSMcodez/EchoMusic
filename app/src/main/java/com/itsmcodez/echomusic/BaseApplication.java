package com.itsmcodez.echomusic;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import com.itsmcodez.echomusic.preferences.utils.PreferenceUtils;

public class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        this.registerActivityLifecycleCallbacks(this);
        boolean isDarkModeEnabled = PreferenceUtils.getKey().getBoolean("mode_dark", false);
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public static Application getApplication() {
        return BaseApplication.application;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}
}
