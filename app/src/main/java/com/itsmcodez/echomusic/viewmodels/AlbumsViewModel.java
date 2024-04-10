package com.itsmcodez.echomusic.viewmodels;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.models.AlbumsModel;
import com.itsmcodez.echomusic.repositories.AlbumsRepository;
import java.util.ArrayList;

public class AlbumsViewModel extends AndroidViewModel {
    private Application application;
    private AlbumsRepository albumsRepository;
    private ArrayList<AlbumsModel> albums;
    private MutableLiveData<ArrayList<AlbumsModel>> allAlbums;
    
    public AlbumsViewModel(Application application){
        super(application);
        this.application = application;
        albumsRepository = AlbumsRepository.getInstance(application);
        albums = albumsRepository.getAlbums();
        allAlbums = albumsRepository.getAllAlbums();
    }
    
    public ArrayList<AlbumsModel> getAlbums() {
    	return this.albums;
    }
    
    public MutableLiveData<ArrayList<AlbumsModel>> getAllAlbums() {
    	return this.allAlbums;
    }
}
