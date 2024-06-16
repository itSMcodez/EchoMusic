package com.itsmcodez.echomusic.utils;
import android.content.ContentUris;
import android.media.MediaMetadataEditor;
import android.net.Uri;
import com.itsmcodez.echomusic.common.MediaProperties;

public final class ArtworkUtils {
    
    public static Uri getArtworkFrom(long albumId) {
        Uri artworkPath = Uri.parse(MediaProperties.ALBUM_ART_URI);
        Uri albumArtwork = ContentUris.withAppendedId(artworkPath, albumId);
        return albumArtwork;
    }
}
