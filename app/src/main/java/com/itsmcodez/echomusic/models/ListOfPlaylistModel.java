package com.itsmcodez.echomusic.models;
import com.itsmcodez.echomusic.markups.Model;

public class ListOfPlaylistModel implements Model {
    private String title;

    public ListOfPlaylistModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
