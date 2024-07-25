package com.itsmcodez.echomusic;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import androidx.appcompat.app.AppCompatDelegate;
import com.itsmcodez.echomusic.preferences.Settings;
import com.itsmcodez.echomusic.preferences.utils.PreferenceUtils;
import com.itsmcodez.echomusic.utils.ServiceUtils;

public class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static Application application;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        this.registerActivityLifecycleCallbacks(this);
        
        // start MusicService
        ServiceUtils.startMusicService(this);
        
        Settings.applyUIModeSettings();
        
        // CrashHandler logic
        this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				Intent intent = new Intent(getApplicationContext(), CrashHandlerActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.putExtra("error", getStackTrace(ex));
				PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 11111, intent, PendingIntent.FLAG_ONE_SHOT);
				AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
				am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, pendingIntent);
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(2);
				uncaughtExceptionHandler.uncaughtException(thread, ex);
			}
		});
    }

    public static Application getApplication() {
        return BaseApplication.application;
    }
    
    private String getStackTrace(Throwable th){
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		Throwable cause = th;
		while(cause != null){
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		final String stacktraceAsString = result.toString();
		printWriter.close();
		return stacktraceAsString;
	}

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Settings.applyUIModeSettings();
    }

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {
        Settings.applyUIModeSettings();
    }

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
        
    }
}
