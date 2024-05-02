package com.itsmcodez.echomusic.utils;
import android.app.Application;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import java.util.ArrayList;
import java.util.Collections;

public final class PlaylistUtils {
    private static ArrayList<PlaylistsModel> allPlaylists;
    
    public static ArrayList<PlaylistsModel> getAllPlaylists(Application application) {
    	allPlaylists = getPlaylistsFromDB(application);
        return PlaylistUtils.allPlaylists;
    }
    
    public static void addNewPlaylist(Application application, PlaylistsModel playlist) {
    	allPlaylists = getAllPlaylists(application);
        allPlaylists.add(playlist);
        savePlaylistsToDB(application, allPlaylists);
    }
    
    public static void addNewPlaylistAt(Application application, PlaylistsModel playlist, int position) {
    	allPlaylists = getAllPlaylists(application);
        allPlaylists.add(position, playlist);
        savePlaylistsToDB(application, allPlaylists);
    }
    
    public static void deletePlaylistAt(Application application, int position) {
    	allPlaylists = getPlaylistsFromDB(application);
        allPlaylists.remove(position);
        savePlaylistsToDB(application, allPlaylists);
    }
    
    public static void renamePlaylistAt(Application application, String name, int position) {
    	allPlaylists = getPlaylistsFromDB(application);
        allPlaylists.get(position).setTitle(name);
        savePlaylistsToDB(application, allPlaylists);
    }
    
    public static void addSongToPlaylistAt(Application application, PlaylistSongsModel song, int position) {
    	allPlaylists = getPlaylistsFromDB(application);
        ArrayList<PlaylistSongsModel> songs = allPlaylists.get(position).getSongs() != null ? allPlaylists.get(position).getSongs() : new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        for(PlaylistSongsModel _song : songs) {
        	titles.add(_song.getTitle());
        }
        if(titles.contains(song.getTitle())) {
        	Toast.makeText(application, R.string.msg_add_to_playlist_exists, Toast.LENGTH_LONG).show();
        } else {
            songs.add(song);
            Toast.makeText(application, application.getString(R.string.msg_add_to_playlist_success, song.getTitle(), allPlaylists.get(position).getTitle()), Toast.LENGTH_LONG).show();
        }
        allPlaylists.get(position).setSongs(songs);
        savePlaylistsToDB(application, allPlaylists);
    }
    
    public static void removeSongFromPlaylistAt(Application application, int songPosition, int position) {
    	allPlaylists = getPlaylistsFromDB(application);
        ArrayList<PlaylistSongsModel> songs = allPlaylists.get(position).getSongs();
        Toast.makeText(application, application.getString(R.string.msg_remove_song_from_playlist_success, songs.get(songPosition).getTitle(), allPlaylists.get(position).getTitle()), Toast.LENGTH_LONG).show();
        songs.remove(songPosition);
        allPlaylists.get(position).setSongs(songs);
        savePlaylistsToDB(application, allPlaylists);
    }
    
    public static void swapPlaylistSongPosAt(Application application, int position, int songCurrentIndex, int songTargetIndex) {
    	allPlaylists = getPlaylistsFromDB(application);
        ArrayList<PlaylistSongsModel> songs = allPlaylists.get(position).getSongs();
        Collections.swap(songs, songCurrentIndex, songTargetIndex);
        allPlaylists.get(position).setSongs(songs);
        savePlaylistsToDB(application, allPlaylists);
    }
    
    public static void clearSongsFromPlaylistAt(Application application, int position) {
    	allPlaylists = getPlaylistsFromDB(application);
        ArrayList<PlaylistSongsModel> songs = allPlaylists.get(position).getSongs() != null ? allPlaylists.get(position).getSongs() : new ArrayList<>();
        Toast.makeText(application, application.getResources().getQuantityString(R.plurals.msg_clear_playlist_success, songs.size(), songs.size(), allPlaylists.get(position).getTitle()), Toast.LENGTH_LONG).show();
        songs.clear();
        allPlaylists.get(position).setSongs(songs);
        savePlaylistsToDB(application, allPlaylists);
    }
    
    private static void savePlaylistsToDB(Application application, ArrayList<PlaylistsModel> playlists) {
    	Gson gson = new Gson();
        String playlistsData = gson.toJson(playlists);
        SharedPreferences sharedPref = application.getSharedPreferences("USER_PLAYLISTS", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(sharedPref.getString("USER_PLAYLISTS", "") != "") {
            editor.remove("USER_PLAYLISTS");
            editor.putString("USER_PLAYLISTS", playlistsData);
            editor.commit();
        } else {
            editor.putString("USER_PLAYLISTS", playlistsData);
            editor.commit();
        }
    }
    
    private static ArrayList<PlaylistsModel> getPlaylistsFromDB(Application application) {
        ArrayList<PlaylistsModel> playlists = new ArrayList<>();
        Gson gson = new Gson();
        String playlistsData;
        SharedPreferences sharedPref = application.getSharedPreferences("USER_PLAYLISTS", 0);
        if(sharedPref.getString("USER_PLAYLISTS", "") != "") {
        	playlistsData = sharedPref.getString("USER_PLAYLISTS", "");
            playlists = gson.fromJson(playlistsData, new TypeToken<ArrayList<PlaylistsModel>>(){}.getType());
        }
        
    	return playlists;
    }
    
}
