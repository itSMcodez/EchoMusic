package com.itsmcodez.echomusic.repositories;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.common.MediaProperties;
import com.itsmcodez.echomusic.common.SortOrder;
import com.itsmcodez.echomusic.models.ArtistsModel;
import com.itsmcodez.echomusic.models.SongsModel;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ArtistsRepository {
    private static Application application;
    private static ArtistsRepository instance;
    private ArrayList<ArtistsModel> artists;
    private MutableLiveData<ArrayList<ArtistsModel>> allArtists;
    private ExecutorService executor = Executors.newFixedThreadPool(4);
    
    private ArtistsRepository(){
        artists = new ArrayList<>();
        allArtists = new MutableLiveData<>();
        Future<ArrayList<ArtistsModel>> artistsFuture = executor.submit(queryArtists(SortOrder.ASCENDING));
        try {
            artists = artistsFuture.get();
    		allArtists.setValue(removeDuplicates(artists));
    	} catch(Exception err) {
    		err.printStackTrace();
    	}
    }
    
    private Callable<ArrayList<ArtistsModel>> queryArtists(SortOrder sortOrder) {
    	
        if(artists.size() > 0) {
        	artists.clear();
        }
        
        ArrayList<ArtistsModel> artists = new ArrayList<>();
        Callable<ArrayList<ArtistsModel>> artistsCallable = () -> {
        
            Uri artistsUri = MediaProperties.MEDIA_STORE_EXTERNAL_URI;
            String[] projection = { MediaProperties.COLUMN_ARTIST};
            String selection = MediaProperties.IS_MUSIC;
            
            String songsSortOrder = sortOrder == SortOrder.ASCENDING ? MediaProperties.SORT_ARTIST_TITLE_ASC : MediaProperties.SORT_ARTIST_TITLE_DESC;
            
            Cursor cursor = application.getContentResolver().query(artistsUri, projection, selection, null, songsSortOrder);
            if(cursor.moveToFirst()) {
                while(cursor.moveToNext()) {
                    String artist = cursor.getString(0);
                    artists.add(new ArtistsModel(artist));
                }
                cursor.close();
            }
            
            return artists;
        };
            
        return artistsCallable;
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
