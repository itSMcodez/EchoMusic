package com.itsmcodez.echomusic.common;
import android.net.Uri;
import android.provider.MediaStore;

public final class MediaProperties {
    
    // MediaStore URIs
    public static final Uri MEDIA_STORE_EXTERNAL_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public static final Uri MEDIA_STORE_INTERNAL_URI = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
    
    // Sort songs
    public static final String SORT_TITLE_DESC = MediaStore.Audio.Media.TITLE + " DESC";
    public static final String SORT_TITLE_ASC = MediaStore.Audio.Media.TITLE + " ASC";
    public static final String SORT_SIZE_ASC = MediaStore.Audio.Media.SIZE + " ASC";
    public static final String SORT_SIZE_DESC = MediaStore.Audio.Media.SIZE + " DESC";
    public static final String SORT_DURATION_ASC = MediaStore.Audio.Media.DURATION + " ASC";
    public static final String SORT_DURATION_DESC = MediaStore.Audio.Media.DURATION + " DESC";
    public static final String SORT_DATE_ADDED_ASC = MediaStore.Audio.Media.DATE_ADDED + " ASC";
    public static final String SORT_DATE_ADDED_DESC = MediaStore.Audio.Media.DATE_ADDED + " DESC";
    public static final String SORT_ALBUM_TITLE_ASC = MediaStore.Audio.AlbumColumns.ALBUM + " ASC";
    public static final String SORT_ALBUM_TITLE_DESC = MediaStore.Audio.AlbumColumns.ALBUM + " DESC";
    public static final String SORT_ARTIST_TITLE_ASC = MediaStore.Audio.ArtistColumns.ARTIST + " ASC";
    public static final String SORT_ARTIST_TITLE_DESC = MediaStore.Audio.ArtistColumns.ARTIST + " DESC";
    
    // AudioColumns
    public static final String DATA_PATH = MediaStore.Audio.AudioColumns.DATA;
    public static final String TITLE = MediaStore.Audio.AudioColumns.TITLE;
    public static final String ARTIST = MediaStore.Audio.AudioColumns.ARTIST;
    public static final String DURATION = MediaStore.Audio.AudioColumns.DURATION;
    public static final String ALBUM = MediaStore.Audio.AudioColumns.ALBUM;
    public static final String ALBUM_ID = MediaStore.Audio.AudioColumns.ALBUM_ID;
    public static final String SONG_ID = MediaStore.Audio.AudioColumns._ID;
    public static final String ALBUM_ARTIST = MediaStore.Audio.AudioColumns.ALBUM_ARTIST;
    public static final String SIZE = MediaStore.Audio.AudioColumns.SIZE;
    public static final String GENRE = MediaStore.Audio.AudioColumns.GENRE;
    public static final String MIME_TYPE = MediaStore.Audio.AudioColumns.MIME_TYPE;
    public static final String DATE_ADDED = MediaStore.Audio.AudioColumns.DATE_ADDED;
    public static final String IS_MUSIC = MediaStore.Audio.AudioColumns.IS_MUSIC + " != 0";
    
    // AlbumArt URI
    public static final String ALBUM_ART_URI = "content://media/external/audio/albumart";
    
    // AlbumColumns
    public static final String COLUMN_ALBUM = MediaStore.Audio.AlbumColumns.ALBUM;
    public static final String COLUMN_ALBUM_ID = MediaStore.Audio.AlbumColumns.ALBUM_ID;
    public static final String COLUMN_ALBUM_ARTIST = MediaStore.Audio.AlbumColumns.ARTIST;
    
    // ArtistColumns
    public static final String COLUMN_ARTIST = MediaStore.Audio.ArtistColumns.ARTIST;
}
