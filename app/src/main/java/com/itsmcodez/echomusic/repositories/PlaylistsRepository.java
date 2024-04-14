package com.itsmcodez.echomusic.repositories;
import android.app.Application;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import com.itsmcodez.echomusic.utils.PlaylistUtils;
import java.util.ArrayList;

public final class PlaylistsRepository {
    private static Application application;
    private static PlaylistsRepository instance;
    private ArrayList<PlaylistsModel> playlists;
    private MutableLiveData<ArrayList<PlaylistsModel>> allPlaylists;
    
    private PlaylistsRepository(){
        playlists = new ArrayList<>(PlaylistUtils.getAllPlaylists(application));
        allPlaylists = new MutableLiveData<>(playlists);
    }
    
    public static synchronized PlaylistsRepository getInstance(Application application) {
    	PlaylistsRepository.application = application;
        if(instance == null) {
        	instance = new PlaylistsRepository();
        }
        return PlaylistsRepository.instance;
    }
    
    public ArrayList<PlaylistsModel> getPlaylists() {
    	return this.playlists;
    }
    
    public MutableLiveData<ArrayList<PlaylistsModel>> getAllPlaylists() {
    	return this.allPlaylists;
    }
    
    public void addNewPlaylist(PlaylistsModel playlist) {
    	PlaylistUtils.addNewPlaylist(application, playlist);
        playlists = PlaylistUtils.getAllPlaylists(application);
        allPlaylists.setValue(playlists);
    }
}
