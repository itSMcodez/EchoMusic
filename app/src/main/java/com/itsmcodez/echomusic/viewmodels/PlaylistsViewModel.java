package com.itsmcodez.echomusic.viewmodels;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import com.itsmcodez.echomusic.repositories.PlaylistsRepository;
import com.itsmcodez.echomusic.utils.PlaylistUtils;
import java.util.ArrayList;

public class PlaylistsViewModel extends AndroidViewModel {
    private Application application;
    private PlaylistsRepository playlistsRepository;
    private ArrayList<PlaylistsModel> playlists;
    private MutableLiveData<ArrayList<PlaylistsModel>> allPlaylists;
    
    public PlaylistsViewModel(Application application) {
        super(application);
        this.application = application;
        playlistsRepository = PlaylistsRepository.getInstance(application);
        playlists = playlistsRepository.getPlaylists();
        allPlaylists = playlistsRepository.getAllPlaylists();
    }
    
    public ArrayList<PlaylistsModel> getPlaylists() {
    	return this.playlists;
    }
    
    public MutableLiveData<ArrayList<PlaylistsModel>> getAllPlaylists() {
    	return this.allPlaylists;
    }
    
    public void addNewPlaylist(PlaylistsModel playlist) {
    	playlistsRepository.addNewPlaylist(playlist);
        playlists = PlaylistUtils.getAllPlaylists(application);
        allPlaylists.setValue(playlists);
    }
    
    public void addNewPlaylistAt(PlaylistsModel playlist, int position) {
    	playlistsRepository.addNewPlaylistAt(playlist, position);
        playlists = PlaylistUtils.getAllPlaylists(application);
        allPlaylists.setValue(playlists);
    }
    
    public void deletePlaylistAt(int position) {
    	playlistsRepository.deletePlaylistAt(position);
        playlists = PlaylistUtils.getAllPlaylists(application);
        allPlaylists.setValue(playlists);
    }
    
    public void renamePlaylistAt(String name, int position) {
    	playlistsRepository.renamePlaylistAt(name, position);
        playlists = PlaylistUtils.getAllPlaylists(application);
        allPlaylists.setValue(playlists);
    }
    
    public void addSongToPlaylistAt(PlaylistSongsModel song, int position) {
    	playlistsRepository.addSongToPlaylistAt(song, position);
        playlists = PlaylistUtils.getAllPlaylists(application);
        allPlaylists.setValue(playlists);
    }
    
    public void addSongsToPlaylistAt(ArrayList<PlaylistSongsModel> songsList, int position) {
    	playlistsRepository.addSongsToPlaylistAt(songsList, position);
        playlists = PlaylistUtils.getAllPlaylists(application);
        allPlaylists.setValue(playlists);
    }
    
    public void clearSongsFromPlaylistAt(int position) {
        playlistsRepository.clearSongsFromPlaylistAt(position);
        playlists = PlaylistUtils.getAllPlaylists(application);
        allPlaylists.setValue(playlists);
    }
}
