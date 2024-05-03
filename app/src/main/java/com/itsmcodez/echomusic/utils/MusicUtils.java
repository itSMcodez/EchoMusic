package com.itsmcodez.echomusic.utils;
import java.util.Locale;

public final class MusicUtils {
    
    public static String getReadableDuration(long duration) {
    	var minutes = duration / 1000 / 60;
        var seconds = duration / 1000 % 60;
        if(minutes < 60) {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        } else {
            var hours = minutes / 60;
        	minutes %= 60;
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        }
    }
}
