package com.itsmcodez.echomusic.utils;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import com.itsmcodez.echomusic.markups.Model;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.SongsModel;
import java.util.ArrayList;
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
            .setCompilation(song.getSongId())
            .build();
            
        	MediaItem mediaItem = new MediaItem.Builder()
            .setMediaMetadata(metadata)
            .setUri(songUri)
            .build();
            mediaItems.add(mediaItem);
        }
        
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
            .setCompilation(song.getSongId())
            .build();
            
        	MediaItem mediaItem = new MediaItem.Builder()
            .setMediaMetadata(metadata)
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
        .setCompilation(song.getSongId())
        .build();
        
        return new MediaItem.Builder()
            .setMediaMetadata(metadata)
            .setUri(songUri)
            .build();
    }
}
