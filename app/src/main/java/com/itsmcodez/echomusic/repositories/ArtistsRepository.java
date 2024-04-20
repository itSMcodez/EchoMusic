package com.itsmcodez.echomusic.repositories;
import android.app.Application;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.models.ArtistsModel;
import com.itsmcodez.echomusic.models.SongsModel;
import java.util.ArrayList;

public class ArtistsRepository {
    private static Application application;
    private static ArtistsRepository instance;
    private ArrayList<ArtistsModel> artists;
    private MutableLiveData<ArrayList<ArtistsModel>> allArtists;
    private SongsRepository songsRepository;
    
    private ArtistsRepository(){
        artists = new ArrayList<>();
        allArtists = new MutableLiveData<>();
        songsRepository = SongsRepository.getInstance(application);
        
        for(SongsModel song : songsRepository.getSongs()) {
        	artists.add(new ArtistsModel(song.getArtist(), song.getAlbumId(), song.getAlbumArtwork()));
        }
        allArtists.setValue(removeDuplicates(artists));
    }
    
    public static synchronized ArtistsRepository getInstance(Application application) {
    	ArtistsRepository.application = application;
        if(instance == null) {
        	instance = new ArtistsRepository();
        }
        return ArtistsRepository.instance;
    }
    
    public ArrayList<ArtistsModel> getArtists() {
    	return this.artists;
    }
    
    public MutableLiveData<ArrayList<ArtistsModel>> getAllArtists() {
    	return this.allArtists;
    }
    
    private ArrayList<ArtistsModel> removeDuplicates(ArrayList<ArtistsModel> artistsList) {
        ArrayList<ArtistsModel> filteredList = new ArrayList<>();
        ArrayList<String> duplicates = new ArrayList<>();
        String title;
        for(ArtistsModel artist : artistsList) {
            title = artist.getArtist();
            if(duplicates.size() == 0 && filteredList.size() == 0) {
            	duplicates.add(title);
                filteredList.add(artist);
            }
            if(duplicates.contains(title)) {
            	duplicates.add(title);
            }
            else{
                filteredList.add(artist);
                duplicates.add(title);
            }
        }
        return filteredList;
    }
}
