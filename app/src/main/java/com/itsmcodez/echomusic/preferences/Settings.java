package com.itsmcodez.echomusic.preferences;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import com.itsmcodez.echomusic.common.SortOrder;
import com.itsmcodez.echomusic.preferences.utils.PreferenceUtils;

public final class Settings {
    public static final String UI_MODE_SWITCH = "mode_dark";
    public static final String SONGS_SORT_ORDER = "songs_sorting";
    
    private Settings(){
        // required private empty constructor
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
    
    public static void setSongsSortOrder(SortOrder sortOrder) {
    	final String ORDER = sortOrder == SortOrder.ASCENDING ? "SORT_ASC" : 
        sortOrder == SortOrder.DESCENDING ? "SORT_DESC" : sortOrder == SortOrder.DURATION ? "SORT_DURATION" : 
        sortOrder == SortOrder.SIZE ? "SORT_SIZE" : "SORT_DEFAULT";
        
        SharedPreferences.Editor editor = PreferenceUtils.getKey().edit();
        editor.putString(SONGS_SORT_ORDER, ORDER);
        editor.commit();
    }
    
    public static String getSongsSortOrder() {
    	final String ORDER = PreferenceUtils.getKey().getString(SONGS_SORT_ORDER, "SORT_DEFAULT");
        return ORDER;
    }
}
