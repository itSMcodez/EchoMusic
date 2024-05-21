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
    
    public static void update() {
        if(playerStateCallbacks.size() != 0) {
            for(OnPlayerStateChange playerStateCallback : playerStateCallbacks) {
                playerStateCallback.onStateChanged();
            }
        }
    }
}
