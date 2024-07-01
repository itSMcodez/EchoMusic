package com.itsmcodez.echomusic.common;
import androidx.media3.common.MediaItem;
import com.itsmcodez.echomusic.markups.Model;

public interface OnUpdateCurrentSong {
    public void onUpdate(Model song, int position);
    public void updateCurrentSong(MediaItem song);
    
}
