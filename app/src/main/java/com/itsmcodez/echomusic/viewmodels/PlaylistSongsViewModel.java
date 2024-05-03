package com.itsmcodez.echomusic.viewmodels;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.repositories.PlaylistSongsRepository;
import java.util.ArrayList;

public class PlaylistSongsViewModel extends AndroidViewModel {
    private Application application;
    private PlaylistSongsRepository playlistSongsRepo;
    private ArrayList<PlaylistSongsModel> songs;
    private MutableLiveData<ArrayList<PlaylistSongsModel>> allSongs;
    
    public PlaylistSongsViewModel(Application application) {
        super(application);
        this.application = application;
        playlistSongsRepo = PlaylistSongsRepository.getInstance(application);
    }
    
    public ArrayList<PlaylistSongsModel> getSongs(int position) {
        songs = playlistSongsRepo.getSongs(position);
    	return this.songs;
    }
    
    public MutableLiveData<ArrayList<PlaylistSongsModel>> getAllSongs(int position) {
    	allSongs = playlistSongsRepo.getAllSongs(position);
        return this.allSongs;
    }
    
    public void removeSongFromPlaylistAt(int songPosition, int position) {
        playlistSongsRepo.removeSongFromPlaylistAt(songPosition, position);
        songs = playlistSongsRepo.getSongs(position);
        allSongs.setValue(songs);
    }
    
    public void swapPlaylistSongPosAt(int position, int songCurrentIndex, int songTargetIndex) {
        playlistSongsRepo.swapPlaylistSongPosAt(position, songCurrentIndex, songTargetIndex);
    }
    
    public void clearSongsFromPlaylistAt(int position) {
        playlistSongsRepo.clearSongsFromPlaylistAt(position);
        songs = playlistSongsRepo.getSongs(position);
        allSongs.setValue(songs);
    }
}
