package com.itsmcodez.echomusic.viewmodels;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.models.ArtistsModel;
import com.itsmcodez.echomusic.repositories.ArtistsRepository;
import java.util.ArrayList;

public class ArtistsViewModel extends AndroidViewModel {
    private Application application;
    private ArtistsRepository artistsRepository;
    private ArrayList<ArtistsModel> artists;
    private MutableLiveData<ArrayList<ArtistsModel>> allArtists;
    
    public ArtistsViewModel(Application application){
        super(application);
        this.application = application;
        artistsRepository = ArtistsRepository.getInstance(application);
        artists = artistsRepository.getArtists();
        allArtists = artistsRepository.getAllArtists();
    }
    
    public ArrayList<ArtistsModel> getArtists() {
    	return this.artists;
    }
    
    public MutableLiveData<ArrayList<ArtistsModel>> getAllArtists() {
    	return this.allArtists;
    }
}
