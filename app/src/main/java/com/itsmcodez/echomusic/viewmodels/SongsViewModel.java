package com.itsmcodez.echomusic.viewmodels;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import com.itsmcodez.echomusic.repositories.SongsRepository;
import java.util.ArrayList;
import com.itsmcodez.echomusic.models.SongsModel;
import androidx.lifecycle.MutableLiveData;

public class SongsViewModel extends AndroidViewModel {
    private Application application;
    private SongsRepository songsRepository;
    private ArrayList<SongsModel> songs;
    private MutableLiveData<ArrayList<SongsModel>> allSongs;
    
    public SongsViewModel(Application application){
        super(application);
        this.application = application;
        songsRepository = SongsRepository.getInstance(application);
        songs = songsRepository.getSongs();
        allSongs = songsRepository.getAllSongs();
    }
    
    public ArrayList<SongsModel> getSongs() {
    	return this.songs;
    }
    
    public MutableLiveData<ArrayList<SongsModel>> getAllSongs() {
    	return this.allSongs;
    }
}
