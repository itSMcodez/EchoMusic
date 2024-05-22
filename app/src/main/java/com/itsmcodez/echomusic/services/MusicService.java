package com.itsmcodez.echomusic.services;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import com.itsmcodez.echomusic.PlayerActivity;
import com.itsmcodez.echomusic.common.PlayerStateObservable;
import com.itsmcodez.echomusic.preferences.PlaybackSettings;

public class MusicService extends MediaSessionService {
    private static MediaSession mediaSession;
    private ExoPlayer exoPlayer;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize exoplayer and mediasession
        exoPlayer = new ExoPlayer.Builder(this)
        .setHandleAudioBecomingNoisy(true)
        .setDeviceVolumeControlEnabled(true)
        .setWakeMode(PlaybackSettings.WAKELOCK)
        .setAudioAttributes(PlaybackSettings.AUDIO_ATTRIBUTES, true)
        .setPauseAtEndOfMediaItems(false)
        .build();
        exoPlayer.addListener(new Player.Listener(){
                
                @Override
                public void onMediaItemTransition(MediaItem mediaItem, int reason) {
                    if(!exoPlayer.isPlaying()) {
                        exoPlayer.prepare();
                    	exoPlayer.play();
                    }
                    PlayerStateObservable.notifyPlayerStateObserver();
                }
                
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if(playbackState == ExoPlayer.STATE_READY) {
                        if(!exoPlayer.isPlaying()) {
                            exoPlayer.prepare();
                            exoPlayer.play();
                        }
                    	PlayerStateObservable.notifyPlayerStateObserver();
                    } else {
                    	PlayerStateObservable.notifyPlayerStateObserver();
                    }
                }
        });
        
        mediaSession = new MediaSession.Builder(this, exoPlayer)
        .setSessionActivity(PendingIntent.getActivity(this ,0 , new Intent(this, PlayerActivity.class), 0))
        .setCallback(PlaybackSettings.MEDIA_SESSION_CALLBACK)
        .build();
        
    }
    
    @Override
    public void onDestroy() {
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
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
        return mediaSession;
    }
    
    public static MediaSession getMediaSession() {
    	return MusicService.mediaSession;
    }
}
