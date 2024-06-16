package com.itsmcodez.echomusic.repositories;

import android.app.Application;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.common.MediaProperties;
import com.itsmcodez.echomusic.common.SortOrder;
import com.itsmcodez.echomusic.models.AlbumsModel;
import com.itsmcodez.echomusic.models.SongsModel;
import java.util.ArrayList;

public class AlbumsRepository {
    private static Application application;
    private static AlbumsRepository instance;
    private ArrayList<AlbumsModel> albums;
    private MutableLiveData<ArrayList<AlbumsModel>> allAlbums;
    
    private AlbumsRepository(){
        albums = new ArrayList<>();
        allAlbums = new MutableLiveData<>();
        queryAlbums(albums, SortOrder.ASCENDING);
    }
    
    private void queryAlbums(ArrayList<AlbumsModel> albums, SortOrder sortOrder) {
    	
        if(albums.size() > 0) {
        	albums.clear();
        }
        
        Uri albumsUri = MediaProperties.MEDIA_STORE_EXTERNAL_URI;
        String[] projection = { MediaProperties.COLUMN_ALBUM, MediaProperties.COLUMN_ALBUM_ARTIST, MediaProperties.COLUMN_ALBUM_ID};
        String selection = MediaProperties.IS_MUSIC;
        
        String songsSortOrder = sortOrder == SortOrder.ASCENDING ? MediaProperties.SORT_ALBUM_TITLE_ASC : MediaProperties.SORT_ALBUM_TITLE_DESC;
        
        Cursor cursor = application.getContentResolver().query(albumsUri, projection, selection, null, songsSortOrder);
        if(cursor.moveToFirst()) {
        	while(cursor.moveToNext()) {
                String album = cursor.getString(0);
                String albumArtist = cursor.getString(1);
                String albumId = cursor.getString(2);
            
                long _albumId = Long.parseLong(albumId);
                Uri artworkPath = Uri.parse(MediaProperties.ALBUM_ART_URI);
                Uri albumArtwork = ContentUris.withAppendedId(artworkPath, _albumId);
            
                albums.add(new AlbumsModel(album, albumArtist, albumId, albumArtwork));
            }
            cursor.close();
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
