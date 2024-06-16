package com.itsmcodez.echomusic.models;

import com.itsmcodez.echomusic.markups.Model;

public class ArtistsModel implements Model {
    private String artist;

    public ArtistsModel(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
