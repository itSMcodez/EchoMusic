package com.itsmcodez.echomusic.preferences;
import androidx.appcompat.app.AppCompatDelegate;
import com.itsmcodez.echomusic.preferences.utils.PreferenceUtils;

public class Settings {
    public static final String UI_MODE_SWITCH = "mode_dark";
    
    private Settings(){
        // required empty constructor to avoid 
        // inheritance and instantiation
    }
    
    public static void applyUIModeSettings() {
    	boolean isDarkModeEnabled = PreferenceUtils.getKey().getBoolean(UI_MODE_SWITCH, false);
        if (isDarkModeEnabled) {
            if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
            	AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        } else {
            if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
            	AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
}
