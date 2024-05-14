package com.itsmcodez.echomusic.services;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

public class MusicService extends MediaSessionService {
    private MediaSession mediaSession;
    private ExoPlayer exoPlayer;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize exoplayer and mediasession
        exoPlayer = new ExoPlayer.Builder(this).build();
        mediaSession = new MediaSession.Builder(this, exoPlayer).build();
    }
    
    @Override
    public void onDestroy() {
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
        super.onDestroy();
    }
    
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return this.mediaSession;
    }
}
