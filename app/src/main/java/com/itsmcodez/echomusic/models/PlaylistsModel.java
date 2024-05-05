package com.itsmcodez.echomusic.models;

import com.itsmcodez.echomusic.markups.Model;
import java.util.ArrayList;

public class PlaylistsModel implements Model {
    private String title;
    private ArrayList<PlaylistSongsModel> songs;
    private int songCount;
    private long totalDuration;

    public PlaylistsModel(
            String title,
            ArrayList<PlaylistSongsModel> songs,
            int songCount,
            long totalDuration) {
        this.title = title;
        this.songs = songs;
        this.songCount = songCount;
        this.totalDuration = totalDuration;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSongCount() {
        return this.songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public long getTotalDuration() {
        return this.totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public ArrayList<PlaylistSongsModel> getSongs() {
        return this.songs;
    }

    public void setSongs(ArrayList<PlaylistSongsModel> songs) {
        this.songs = songs;
    }
}
