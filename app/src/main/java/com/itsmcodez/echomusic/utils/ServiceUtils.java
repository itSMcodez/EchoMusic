package com.itsmcodez.echomusic.utils;
import android.content.Context;
import android.content.Intent;
import com.itsmcodez.echomusic.services.MusicService;

public final class ServiceUtils {
    
    public static void startMusicService(Context context) {
    	context.startService(new Intent(context, MusicService.class));
    }
}
