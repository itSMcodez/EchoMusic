package com.itsmcodez.echomusic.repositories;
import android.app.Application;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import java.util.ArrayList;

public class PlaylistSongsRepository {
    private static Application application;
    private static PlaylistSongsRepository instance;
    private ArrayList<PlaylistSongsModel> songs;
    private MutableLiveData<ArrayList<PlaylistSongsModel>> allSongs;
    private PlaylistsRepository playlistsRepository;
    
    private PlaylistSongsRepository() {
        playlistsRepository = PlaylistsRepository.getInstance(application);
    }
    
    public static PlaylistSongsRepository getInstance(Application application) {
    	PlaylistSongsRepository.application = application;
        if(instance == null) {
        	instance = new PlaylistSongsRepository();
        }
        return instance;
    }
    
    public ArrayList<PlaylistSongsModel> getSongs(int position) {
        ArrayList<PlaylistsModel> playlists = playlistsRepository.getPlaylists();
        PlaylistsModel playlist = playlists.get(position);
        songs = playlist.getSongs() != null ? playlist.getSongs() : new ArrayList<>();
        return this.songs;
    }
    
    public MutableLiveData<ArrayList<PlaylistSongsModel>> getAllSongs(int position) {
        allSongs = new MutableLiveData<>(getSongs(position));
    	return this.allSongs;
    }
    
    public void removeSongFromPlaylistAt(int songPosition, int position) {
    	playlistsRepository.removeSongFromPlaylistAt(songPosition, position);
        ArrayList<PlaylistsModel> playlists = playlistsRepository.getPlaylists();
        PlaylistsModel playlist = playlists.get(position);
        songs = playlist.getSongs();
        allSongs.setValue(songs);
    }
    
    public void swapPlaylistSongPosAt(int position, int songCurrentIndex, int songTargetIndex) {
        playlistsRepository.swapPlaylistSongPosAt(position, songCurrentIndex, songTargetIndex);
    }
}
