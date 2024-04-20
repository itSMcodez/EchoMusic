package com.itsmcodez.echomusic.repositories;
import android.app.Application;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.models.SongsModel;
import java.util.ArrayList;

public class SongsRepository {
    private static Application application;
    private static SongsRepository instance;
    private ArrayList<SongsModel> songs;
    private MutableLiveData<ArrayList<SongsModel>> allSongs;
    
    private SongsRepository(){
        songs = new ArrayList<>();
        allSongs = new MutableLiveData<>();
        Uri songsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media._ID};
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED;
        
        Cursor cursor = application.getContentResolver().query(songsUri, projection, selection, null, sortOrder);
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
                Uri artworkPath = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtwork = ContentUris.withAppendedId(artworkPath, _albumId);
            
                songs.add(new SongsModel(path, title, artist, duration, album, albumId, songId, albumArtwork));
            }
            cursor.close();
            allSongs.setValue(songs);
        }
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
}
