package com.itsmcodez.echomusic.utils;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import com.itsmcodez.echomusic.common.MediaItemsQueue;
import com.itsmcodez.echomusic.markups.Model;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.SongsModel;
import com.itsmcodez.echomusic.viewmodels.PlaylistSongsViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MusicUtils {
    
    public static String getReadableDuration(long duration) {
    	var minutes = duration / 1000 / 60;
        var seconds = duration / 1000 % 60;
        if(minutes < 60) {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        } else {
            var hours = minutes / 60;
        	minutes %= 60;
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        }
    }
    
    public static ArrayList<MediaItem> makeMediaItems(ArrayList<SongsModel> songs) {
    	ArrayList<MediaItem> mediaItems = new ArrayList<>();
        MediaItemsQueue.setNowPlayingQueue(songs);
        for(SongsModel song : songs) {
            Uri path = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Uri songUri = ContentUris.withAppendedId(path, Long.parseLong(song.getSongId()));
            
            MediaMetadata metadata = new MediaMetadata.Builder()
            .setArtworkUri(song.getAlbumArtwork())
            .setTitle(song.getTitle())
            .setAlbumTitle(song.getAlbum())
            .setArtist(song.getArtist())
            .setDisplayTitle(song.getPath())
            .setDescription(song.getAlbumId())
            .build();
            
        	MediaItem mediaItem = new MediaItem.Builder()
            .setMediaMetadata(metadata)
            .setMediaId(song.getSongId())
            .setUri(songUri)
            .build();
            mediaItems.add(mediaItem);
        }
        
        return mediaItems;
    }
    
    public static ArrayList<MediaItem> makeMediaItems(ArrayList<PlaylistSongsModel> songs, String tag) {
    	ArrayList<MediaItem> mediaItems = new ArrayList<>();
        MediaItemsQueue.setNowPlayingQueue(songs, tag);
        for(PlaylistSongsModel song : songs) {
            Uri path = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Uri songUri = ContentUris.withAppendedId(path, Long.parseLong(song.getSongId()));
            
            MediaMetadata metadata = new MediaMetadata.Builder()
            .setArtworkUri(ArtworkUtils.getArtworkFrom(Long.parseLong(song.getAlbumId())))
            .setTitle(song.getTitle())
            .setAlbumTitle(song.getAlbum())
            .setArtist(song.getArtist())
            .setDisplayTitle(song.getPath())
            .setDescription(song.getAlbumId())
            .build();
            
        	MediaItem mediaItem = new MediaItem.Builder()
            .setMediaMetadata(metadata)
            .setMediaId(song.getSongId())
            .setUri(songUri)
            .build();
            mediaItems.add(mediaItem);
        }
        
        return mediaItems;
    }
    
    public static MediaItem makeMediaItem(SongsModel song) {
        Uri path = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri songUri = ContentUris.withAppendedId(path, Long.parseLong(song.getSongId()));
        
        MediaMetadata metadata = new MediaMetadata.Builder()
        .setArtworkUri(ArtworkUtils.getArtworkFrom(Long.parseLong(song.getAlbumId())))
        .setTitle(song.getTitle())
        .setAlbumTitle(song.getAlbum())
        .setArtist(song.getArtist())
        .setDisplayTitle(song.getPath())
        .setDescription(song.getAlbumId())
        .build();
        
        return new MediaItem.Builder()
            .setMediaMetadata(metadata)
            .setMediaId(song.getSongId())
            .setUri(songUri)
            .build();
    }
    
    public static boolean isFavouriteSong(LifecycleOwner lifecycleOwner, long songId) {
    	PlaylistSongsViewModel playlistSongsViewModel = new ViewModelProvider((ViewModelStoreOwner)lifecycleOwner).get(PlaylistSongsViewModel.class);
        ArrayList<Long> songIds = new ArrayList<>();
        // Query favourite songs
        ArrayList<PlaylistSongsModel> FavouriteSongs = playlistSongsViewModel.getSongs(PlaylistUtils.FAVOURITES);
        for(PlaylistSongsModel song : FavouriteSongs) {
            songIds.add(Long.parseLong(song.getSongId()));
        }
        
        if(songIds.contains(songId)) {
        	return true;
        }
        return false;
    }
    
    public static int indexOfSongById(long songId, List<PlaylistSongsModel> songs) {
    	ArrayList<Long> songIds = new ArrayList<>();
        var indexOfSong = 0;
        for(PlaylistSongsModel song : songs) {
        	songIds.add(Long.parseLong(song.getSongId()));
        }
        if(songIds.contains(songId)) {
        	indexOfSong = songIds.indexOf(songId);
            return indexOfSong;
        }
        return -1;
    }
    
    public static int indexOfSongById(long songId, List<SongsModel> songs, String tag) {
    	ArrayList<Long> songIds = new ArrayList<>();
        var indexOfSong = 0;
        for(SongsModel song : songs) {
        	songIds.add(Long.parseLong(song.getSongId()));
        }
        if(songIds.contains(songId)) {
        	indexOfSong = songIds.indexOf(songId);
            return indexOfSong;
        }
        return -1;
    }
    
    public static void deleteSong() {
    	
    }
}
