package com.itsmcodez.echomusic.utils;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import com.itsmcodez.echomusic.BaseApplication;
import com.itsmcodez.echomusic.common.MediaItemsQueue;
import com.itsmcodez.echomusic.markups.Model;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.SongsModel;
import com.itsmcodez.echomusic.repositories.PlaylistsRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MusicUtils {
    private static ArrayList<PlaylistSongsModel> favouriteSongs;
    private static ArrayList<Long> favSongIds = new ArrayList<>();
    private static PlaylistsRepository playlistRepo = PlaylistsRepository.getInstance(BaseApplication.getApplication());
    
    static {
        // Query favourite songs
        if(playlistRepo.getPlaylists().size() != 0) {
            favouriteSongs = playlistRepo.getPlaylists().get(PlaylistUtils.FAVOURITES).getTitle().equals("Favourites") ? playlistRepo.getPlaylists().get(PlaylistUtils.FAVOURITES).getSongs() : new ArrayList<>();
            if(favouriteSongs.size() != 0) {
                for(PlaylistSongsModel song : favouriteSongs) {
                    favSongIds.add(Long.parseLong(song.getSongId()));
                }
            }
        }
    }
    
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
        MediaItemsQueue.setNowPlayingQueue(songs);
        return mediaItems;
    }
    
    public static ArrayList<MediaItem> makeMediaItems(ArrayList<PlaylistSongsModel> songs, String tag) {
    	ArrayList<MediaItem> mediaItems = new ArrayList<>();
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
        MediaItemsQueue.setNowPlayingQueue(songs, tag);
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
    
    public static void removeSongFromFavourites(long songId) {
    	if(favSongIds.contains(songId)) {
    		playlistRepo.removeSongFromPlaylistAt(favSongIds.indexOf(songId), PlaylistUtils.FAVOURITES);
            favSongIds.remove(songId);
    	}
    }
    
    public static void addSongToFavourites(PlaylistSongsModel song) {
    	playlistRepo.addSongToPlaylistAt(song, PlaylistUtils.FAVOURITES);
        favSongIds.add(Long.parseLong(song.getSongId()));
    }
    
    public static boolean isFavouriteSong(long songId) {
        if(favSongIds.contains(songId)) {
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
