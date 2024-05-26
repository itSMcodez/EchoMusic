package com.itsmcodez.echomusic.callbacks;
import androidx.media3.common.MediaItem;

public interface OnPlayerStateChange {
    public void onPlaybackStateChanged(int playbackState);
    public void onMediaItemTransition(MediaItem mediaItem, int reason);
    public void onIsPlayingChanged(boolean isPlaying);
}
