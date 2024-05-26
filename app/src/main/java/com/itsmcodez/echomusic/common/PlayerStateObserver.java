package com.itsmcodez.echomusic.common;
import com.itsmcodez.echomusic.callbacks.OnPlayerStateChange;
import java.util.ArrayList;

public class PlayerStateObserver {
    private static ArrayList<OnPlayerStateChange> playerStateCallbacks = new ArrayList<>();
    
    private PlayerStateObserver(){}
    
    public static void registerCallback(OnPlayerStateChange playerStateCallback) {
    	playerStateCallbacks.add(playerStateCallback);
    }
    
    public static void unregisterCallback(OnPlayerStateChange playerStateCallback) {
    	playerStateCallbacks.remove(playerStateCallback);
    }
    
    public static void unregisterAllCallbacks() {
    	playerStateCallbacks.clear();
    }
    
    public static void update(PlayerState playerState, PlayerStateInfo playerStateInfo) {
        if(playerStateCallbacks.size() != 0) {
            
            if(playerState == PlayerState.ON_MEDIA_ITEM_TRANSITION) {
                for(OnPlayerStateChange playerStateCallback : playerStateCallbacks) {
                    playerStateCallback.onMediaItemTransition(playerStateInfo.getMediaItem(), playerStateInfo.getReason());
                }
            }
            
            if(playerState == PlayerState.ON_PLAYBACK_STATE_CHANGED) {
                for(OnPlayerStateChange playerStateCallback : playerStateCallbacks) {
                    playerStateCallback.onPlaybackStateChanged(playerStateInfo.getPlaybackState());
                }
            }
            
            if(playerState == PlayerState.ON_IS_PLAYING_CHANGED) {
                for(OnPlayerStateChange playerStateCallback : playerStateCallbacks) {
                    playerStateCallback.onIsPlayingChanged(playerStateInfo.getIsPlaying());
                }
            }
        }
    }
}
