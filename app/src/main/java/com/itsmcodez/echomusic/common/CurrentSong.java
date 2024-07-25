package com.itsmcodez.echomusic.common;

import androidx.media3.common.MediaItem;

public class CurrentSong {
    private static CurrentSong instance;
    private MediaItem currentSong;

    private CurrentSong() {}

    public static synchronized CurrentSong getInstance() {
        if (instance == null) {
            instance = new CurrentSong();
        }
        return instance;
    }

    public MediaItem getCurrentSong() {
        return this.currentSong;
    }

    public void setCurrentSong(MediaItem currentSong) {
        this.currentSong = currentSong;
    }
}
