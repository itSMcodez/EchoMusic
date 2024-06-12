package com.itsmcodez.echomusic.viewmodels;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import com.itsmcodez.echomusic.models.NowPlayingQueueItemsModel;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.repositories.NowPlayingQueueItemsRepository;
import java.util.ArrayList;

public class NowPlayingQueueItemsViewModel extends AndroidViewModel {
    private Application application;
    private NowPlayingQueueItemsRepository nowPlayingQueueItemsRepository;
    private ArrayList<NowPlayingQueueItemsModel> songs;
    private MutableLiveData<ArrayList<NowPlayingQueueItemsModel>> allSongs;
    
    public NowPlayingQueueItemsViewModel(Application application) {
    	super(application);
        this.application = application;
        nowPlayingQueueItemsRepository = NowPlayingQueueItemsRepository.getInstance(application);
        this.songs = nowPlayingQueueItemsRepository.getSongs();
        this.allSongs = nowPlayingQueueItemsRepository.getAllSongs();
    }
    
    public ArrayList<NowPlayingQueueItemsModel> getSongs() {
        return this.songs;
    }

    public MutableLiveData<ArrayList<NowPlayingQueueItemsModel>> getAllSongs() {
        return this.allSongs;
    }

    public void setSongs(ArrayList<NowPlayingQueueItemsModel> songs) {
        nowPlayingQueueItemsRepository.setSongs(songs);
        this.songs = nowPlayingQueueItemsRepository.getSongs();
        allSongs.setValue(songs);
    }
}
