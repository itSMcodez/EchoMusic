package com.itsmcodez.echomusic.utils;
import android.content.ContentUris;
import android.media.MediaMetadataEditor;
import android.net.Uri;

public final class ArtworkUtils {
    
    public static Uri getArtworkFrom(long albumId) {
        Uri artworkPath = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtwork = ContentUris.withAppendedId(artworkPath, albumId);
        return albumArtwork;
    }
}
