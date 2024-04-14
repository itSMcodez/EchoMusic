package com.itsmcodez.echomusic.utils;
import android.app.Application;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import java.util.ArrayList;

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
    
    public static void deletePlaylistAt(Application application, int position) {
    	allPlaylists = getPlaylistsFromDB(application);
        allPlaylists.remove(position);
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
