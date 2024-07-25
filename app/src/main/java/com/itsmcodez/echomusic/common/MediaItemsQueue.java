package com.itsmcodez.echomusic.common;
import com.itsmcodez.echomusic.models.NowPlayingQueueItemsModel;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.SongsModel;
import java.util.ArrayList;

public class MediaItemsQueue {
    private static ArrayList<NowPlayingQueueItemsModel> nowPlayingQueue = new ArrayList<>();
    
    public static ArrayList<NowPlayingQueueItemsModel> getNowPlayingQueue() {
    	return nowPlayingQueue;
    }
    
    public static void setNowPlayingQueue(ArrayList<SongsModel> songs) {
        ArrayList<NowPlayingQueueItemsModel> nowPlayingQueue = new ArrayList<>();
        for(SongsModel song : songs) {
        	nowPlayingQueue.add(new NowPlayingQueueItemsModel(song.getTitle()));
        }
    	MediaItemsQueue.nowPlayingQueue = nowPlayingQueue;
    }
    
    public static void setNowPlayingQueue(ArrayList<PlaylistSongsModel> songs, String tag) {
        ArrayList<NowPlayingQueueItemsModel> nowPlayingQueue = new ArrayList<>();
        for(PlaylistSongsModel song : songs) {
        	nowPlayingQueue.add(new NowPlayingQueueItemsModel(song.getTitle()));
        }
    	MediaItemsQueue.nowPlayingQueue = nowPlayingQueue;
    }
}
