package com.itsmcodez.echomusic.repositories;

import android.app.Application;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.models.AlbumsModel;
import com.itsmcodez.echomusic.models.SongsModel;
import java.util.ArrayList;

public class AlbumsRepository {
    private static Application application;
    private static AlbumsRepository instance;
    private ArrayList<AlbumsModel> albums;
    private MutableLiveData<ArrayList<AlbumsModel>> allAlbums;
    private SongsRepository songsRepository;
    
    private AlbumsRepository(){
        albums = new ArrayList<>();
        allAlbums = new MutableLiveData<>();
        songsRepository = SongsRepository.getInstance(application);
        
        for(SongsModel song : songsRepository.getSongs()) {
        	albums.add(new AlbumsModel(song.getAlbum(), song.getAlbumId(), song.getAlbumArtwork()));
        }
        allAlbums.setValue(removeDuplicates(albums));
    }
    
    public static synchronized AlbumsRepository getInstance(Application application) {
        AlbumsRepository.application = application;
        if(instance == null) {
        	instance = new AlbumsRepository();
        }
        return AlbumsRepository.instance;
    }
    
    public ArrayList<AlbumsModel> getAlbums() {
    	return this.albums;
    }
    
    public MutableLiveData<ArrayList<AlbumsModel>> getAllAlbums() {
    	return this.allAlbums;
    }
    
    private ArrayList<AlbumsModel> removeDuplicates(ArrayList<AlbumsModel> albumsList) {
        ArrayList<AlbumsModel> filteredList = new ArrayList<>();
        ArrayList<String> duplicates = new ArrayList<>();
        String title;
        for(AlbumsModel album : albumsList) {
            title = album.getAlbum();
            if(duplicates.size() == 0 && filteredList.size() == 0) {
            	duplicates.add(title);
                filteredList.add(album);
            }
            if(duplicates.contains(title)) {
            	duplicates.add(title);
            }
            else{
                filteredList.add(album);
                duplicates.add(title);
            }
        }
        return filteredList;
    }
}
