package com.itsmcodez.echomusic.repositories;
import android.app.Application;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.common.MediaProperties;
import com.itsmcodez.echomusic.common.SortOrder;
import com.itsmcodez.echomusic.models.SongsModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SongsRepository {
    private static Application application;
    private static SongsRepository instance;
    private ArrayList<SongsModel> songs;
    private MutableLiveData<ArrayList<SongsModel>> allSongs;
    private ExecutorService executor = Executors.newFixedThreadPool(4);
    
    private SongsRepository(){
        songs = new ArrayList<>();
        allSongs = new MutableLiveData<>();
        Future<ArrayList<SongsModel>> songsFuture = executor.submit(querySongs(SortOrder.DEFAULT));
        try {
            songs = songsFuture.get();
    		allSongs.setValue(songs);
    	} catch(Exception err) {
    		err.printStackTrace();
    	}
    }
    
    private Callable<ArrayList<SongsModel>> querySongs(SortOrder sortOrder) {
        
        if(songs.size() > 0) {
        	songs.clear();
        }
        
        ArrayList<SongsModel> songs = new ArrayList<>();
        Callable<ArrayList<SongsModel>> songsCallable = () -> {
        
            Uri songsUri = MediaProperties.MEDIA_STORE_EXTERNAL_URI;
            String[] projection = {MediaProperties.DATA_PATH, MediaProperties.TITLE, MediaProperties.ARTIST,
                MediaProperties.DURATION, MediaProperties.ALBUM, MediaProperties.ALBUM_ID, MediaProperties.SONG_ID};
            String selection = MediaProperties.IS_MUSIC;
            
            String songsSortOrder = sortOrder == SortOrder.DESCENDING ? MediaProperties.SORT_TITLE_DESC : 
            sortOrder == SortOrder.ASCENDING ? MediaProperties.SORT_TITLE_ASC : sortOrder == SortOrder.SIZE ? MediaProperties.SORT_SIZE_ASC : 
            sortOrder == SortOrder.DURATION ? MediaProperties.SORT_DURATION_ASC : MediaProperties.SORT_DATE_ADDED_ASC;
            
            Cursor cursor = application.getContentResolver().query(songsUri, projection, selection, null, songsSortOrder);
            if(cursor.moveToFirst()) {
                while(cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    String title = cursor.getString(1);
                    String artist = cursor.getString(2);
                    String duration = cursor.getString(3);
                    String album = cursor.getString(4);
                    String albumId = cursor.getString(5);
                    String songId = cursor.getString(6);
                
                    long _albumId = Long.parseLong(albumId);
                    Uri artworkPath = Uri.parse(MediaProperties.ALBUM_ART_URI);
                    Uri albumArtwork = ContentUris.withAppendedId(artworkPath, _albumId);
                
                    songs.add(new SongsModel(path, title, artist, duration, album, albumId, songId, albumArtwork));
                }
                cursor.close();
            }
            
            return songs;
        };
        
        return songsCallable;
    }
    
    public static synchronized SongsRepository getInstance(Application application) {
    	SongsRepository.application = application;
        
        if(instance == null) {
        	instance = new SongsRepository();
        }
        return SongsRepository.instance;
    }
    
    public ArrayList<SongsModel> getSongs() {
    	return this.songs;
    }
    
    public MutableLiveData<ArrayList<SongsModel>> getAllSongs() {
    	return this.allSongs;
    }
    
    public void sortSongs(SortOrder sortOrder) {
    	
        if(sortOrder == SortOrder.DEFAULT) {
            Future<ArrayList<SongsModel>> songsFuture = executor.submit(querySongs(SortOrder.DEFAULT));
            try {
                songs = songsFuture.get();
                allSongs.setValue(songs);
            } catch(Exception err) {
                err.printStackTrace();
            }
            return;
        }
        
        if(sortOrder == SortOrder.ASCENDING) {
        	Future<ArrayList<SongsModel>> songsFuture = executor.submit(querySongs(SortOrder.ASCENDING));
            try {
                songs = songsFuture.get();
                allSongs.setValue(songs);
            } catch(Exception err) {
                err.printStackTrace();
            }
            return;
        }
        
        if(sortOrder == SortOrder.DESCENDING) {
        	Future<ArrayList<SongsModel>> songsFuture = executor.submit(querySongs(SortOrder.DESCENDING));
            try {
                songs = songsFuture.get();
                allSongs.setValue(songs);
            } catch(Exception err) {
                err.printStackTrace();
            }
            return;
        }
        
        if(sortOrder == SortOrder.DATE_ADDED) {
        	Future<ArrayList<SongsModel>> songsFuture = executor.submit(querySongs(SortOrder.DATE_ADDED));
            try {
                songs = songsFuture.get();
                allSongs.setValue(songs);
            } catch(Exception err) {
                err.printStackTrace();
            }
            return;
        }
        
        if(sortOrder == SortOrder.DURATION) {
        	Future<ArrayList<SongsModel>> songsFuture = executor.submit(querySongs(SortOrder.DURATION));
            try {
                songs = songsFuture.get();
                allSongs.setValue(songs);
            } catch(Exception err) {
                err.printStackTrace();
            }
            return;
        }
        
    }
}
