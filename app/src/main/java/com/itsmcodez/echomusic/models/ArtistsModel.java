package com.itsmcodez.echomusic.models;

import android.net.Uri;
import com.itsmcodez.echomusic.markups.Model;

public class ArtistsModel implements Model {
    private String artist, albumId;
    private Uri albumArtwork;

    public ArtistsModel(String artist, String albumId, Uri albumArtwork) {
        this.artist = artist;
        this.albumId = albumId;
        this.albumArtwork = albumArtwork;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public Uri getAlbumArtwork() {
        return this.albumArtwork;
    }

    public void setAlbumArtwork(Uri albumArtwork) {
        this.albumArtwork = albumArtwork;
    }
}
