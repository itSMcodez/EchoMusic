package com.itsmcodez.echomusic.preferences.utils;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.itsmcodez.echomusic.BaseApplication;

public class PreferenceUtils {
    private static SharedPreferences key;
    
    static{
        key = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getApplication());
    }
    
    public static SharedPreferences getKey() {
    	return PreferenceUtils.key;
    }
    
}
