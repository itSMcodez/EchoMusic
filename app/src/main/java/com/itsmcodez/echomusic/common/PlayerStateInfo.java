package com.itsmcodez.echomusic.common;

import androidx.media3.common.MediaItem;

public class PlayerStateInfo {
    private int playbackState;
    private MediaItem mediaItem;
    private int reason;
    private boolean isPlaying;

    public int getPlaybackState() {
        return this.playbackState;
    }

    public void setPlaybackState(int playbackState) {
        this.playbackState = playbackState;
    }

    public MediaItem getMediaItem() {
        return this.mediaItem;
    }

    public void setMediaItem(MediaItem mediaItem) {
        this.mediaItem = mediaItem;
    }

    public int getReason() {
        return this.reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public boolean getIsPlaying() {
        return this.isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
}
