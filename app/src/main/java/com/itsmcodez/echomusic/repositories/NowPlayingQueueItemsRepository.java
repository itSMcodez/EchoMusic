package com.itsmcodez.echomusic.repositories;

import android.app.Application;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.models.NowPlayingQueueItemsModel;
import java.util.ArrayList;
import java.util.Collections;

public class NowPlayingQueueItemsRepository {
    private static Application application;
    private static NowPlayingQueueItemsRepository instance;
    private ArrayList<NowPlayingQueueItemsModel> songs;
    private MutableLiveData<ArrayList<NowPlayingQueueItemsModel>> allSongs;

    private NowPlayingQueueItemsRepository() {
        songs = new ArrayList<>();
        allSongs = new MutableLiveData<>();
    }

    public static synchronized NowPlayingQueueItemsRepository getInstance(Application application) {
        NowPlayingQueueItemsRepository.application = application;
        if (instance == null) {
            instance = new NowPlayingQueueItemsRepository();
        }
        return NowPlayingQueueItemsRepository.instance;
    }

    public ArrayList<NowPlayingQueueItemsModel> getSongs() {
        return this.songs;
    }

    public MutableLiveData<ArrayList<NowPlayingQueueItemsModel>> getAllSongs() {
        return this.allSongs;
    }

    public void setSongs(ArrayList<NowPlayingQueueItemsModel> songs) {
        this.songs = songs;
        allSongs.setValue(songs);
    }
}
