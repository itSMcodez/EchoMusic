package com.itsmcodez.echomusic.models;

import com.itsmcodez.echomusic.markups.Model;

public class NowPlayingQueueItemsModel implements Model {
    private String title;

    public NowPlayingQueueItemsModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
