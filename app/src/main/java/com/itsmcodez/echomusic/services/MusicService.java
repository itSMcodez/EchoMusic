package com.itsmcodez.echomusic.services;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import com.itsmcodez.echomusic.PlayerActivity;
import com.itsmcodez.echomusic.preferences.PlaybackSettings;

public class MusicService extends MediaSessionService {
    private MediaSession mediaSession;
    private ExoPlayer exoPlayer;
    private static boolean isRunning;
    
    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = false;
        
        // Initialize exoplayer and mediasession
        exoPlayer = new ExoPlayer.Builder(this)
        .setHandleAudioBecomingNoisy(true)
        .setDeviceVolumeControlEnabled(true)
        .setWakeMode(PlaybackSettings.WAKELOCK)
        .setAudioAttributes(PlaybackSettings.AUDIO_ATTRIBUTES, true)
        .setPauseAtEndOfMediaItems(false)
        .build();
        mediaSession = new MediaSession.Builder(this, exoPlayer)
        .setSessionActivity(PendingIntent.getActivity(this ,0 , new Intent(this, PlayerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
        .setCallback(PlaybackSettings.MEDIA_SESSION_CALLBACK)
        .build();
        
    }
    
    @Override
    public void onDestroy() {
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
        isRunning = false;
        super.onDestroy();
    }
    
    @Override
    public void onTaskRemoved(Intent intent) {
        if(!mediaSession.getPlayer().getPlayWhenReady() || mediaSession.getPlayer().getMediaItemCount() == 0) {
        	stopSelf();
        }
        super.onTaskRemoved(intent);
    }
    
    
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        isRunning = true;
        return this.mediaSession;
    }
    
    public static boolean isRunning() {
    	return MusicService.isRunning();
    }
}
