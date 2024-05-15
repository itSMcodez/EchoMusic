package com.itsmcodez.echomusic.preferences;
import android.os.PowerManager;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.session.MediaSession;

public final class PlaybackSettings {
    
    public static final AudioAttributes AUDIO_ATTRIBUTES = new AudioAttributes.Builder()
    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
    .setUsage(C.USAGE_MEDIA)
    .build();
    
    public static final int WAKELOCK = PowerManager.PARTIAL_WAKE_LOCK;
    
    public static final MediaSession.Callback MEDIA_SESSION_CALLBACK = new MediaSession.Callback(){
        
        @Override
        public MediaSession.ConnectionResult onConnect(MediaSession session, MediaSession.ControllerInfo controllerInfo) {
            return new MediaSession.ConnectionResult.AcceptedResultBuilder(session).build();
        }
        
    };
}
