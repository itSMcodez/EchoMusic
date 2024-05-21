package com.itsmcodez.echomusic.common;
import java.util.ArrayList;

public class PlayerStateObservable {
    
    private PlayerStateObservable(){}
    
    public static void notifyPlayerStateObserver() {
    	PlayerStateObserver.update();
    }
}
