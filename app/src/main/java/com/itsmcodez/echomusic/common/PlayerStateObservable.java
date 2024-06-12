package com.itsmcodez.echomusic.common;
import java.util.ArrayList;

public class PlayerStateObservable {
    
    private PlayerStateObservable(){}
    
    public static void notifyPlayerStateObserver(PlayerState playerState, PlayerStateInfo playerStateInfo) {
    	PlayerStateObserver.update(playerState, playerStateInfo);
    }
}
