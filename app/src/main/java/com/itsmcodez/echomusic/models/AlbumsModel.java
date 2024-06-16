package com.itsmcodez.echomusic.models;

import android.net.Uri;
import com.itsmcodez.echomusic.markups.Model;

public class AlbumsModel implements Model {
    private String album, albumArtist, albumId;
    private Uri albumArtwork;

    public AlbumsModel(
            String album,
            String albumArtist,
            String albumId,
            Uri albumArtwork) {
        this.album = album;
        this.albumId = albumId;
        this.albumArtwork = albumArtwork;
        this.albumArtist = albumArtist;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
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

    public String getAlbumArtist() {
        return this.albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }
}
