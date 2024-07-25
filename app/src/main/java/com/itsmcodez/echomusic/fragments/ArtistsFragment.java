package com.itsmcodez.echomusic.fragments;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import android.content.ComponentName;
import com.itsmcodez.echomusic.services.MusicService;
import com.google.common.util.concurrent.MoreExecutors;
import com.itsmcodez.echomusic.common.PlayerStateObserver;
import com.google.common.util.concurrent.ListenableFuture;
import com.itsmcodez.echomusic.callbacks.OnPlayerStateChange;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.itsmcodez.echomusic.AlbumArtistSongsActivity;
import com.itsmcodez.echomusic.adapters.ArtistsAdapter;
import com.itsmcodez.echomusic.databinding.FragmentArtistsBinding;
import com.itsmcodez.echomusic.models.ArtistsModel;
import com.itsmcodez.echomusic.viewmodels.ArtistsViewModel;
import java.util.ArrayList;

public class ArtistsFragment extends Fragment {
    private FragmentArtistsBinding binding;
    private ArtistsAdapter artistsAdapter;
    private ArtistsViewModel artistsViewModel;
    private MediaController mediaController;
    private ListenableFuture<MediaController> controllerFuture;
    private OnPlayerStateChange playerStateCallback;
    
    @Override
    public void onStart() {
        super.onStart();
        SessionToken sessionToken = new SessionToken(getContext(), new ComponentName(getContext(), MusicService.class));
        controllerFuture = new MediaController.Builder(getContext(), sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
                if(controllerFuture.isDone()) {
                    try {
                        mediaController = controllerFuture.get();
                        if(mediaController != null && mediaController.getCurrentMediaItem() != null) {
                            if(artistsAdapter != null) {
                            	artistsAdapter.onUpdateCurrentSong.updateCurrentSong(mediaController.getCurrentMediaItem());
                            }
                        }
                    } catch(Exception err) {
                        err.printStackTrace();
                        mediaController = null;
                    }
                }
        }, MoreExecutors.directExecutor());
        
        // Player state callback
        playerStateCallback = new OnPlayerStateChange() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if(artistsAdapter != null) {
                    artistsAdapter.onUpdateCurrentSong.updateCurrentSong(mediaController.getCurrentMediaItem());
                }
            }
            
            @Override
            public void onMediaItemTransition(MediaItem mediaItem, int reason) {
                if(artistsAdapter != null) {
                    artistsAdapter.onUpdateCurrentSong.updateCurrentSong(mediaItem);
                }
            }
            
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                // TODO: Implement this method
            }
        };
        PlayerStateObserver.registerCallback(playerStateCallback);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        MediaController.releaseFuture(controllerFuture);
        PlayerStateObserver.unregisterCallback(playerStateCallback);
        playerStateCallback = null;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistsViewModel = new ViewModelProvider(this).get(ArtistsViewModel.class);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Bind to views
        binding = FragmentArtistsBinding.inflate(inflater, container, false);
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), RecyclerView.VERTICAL, false));
        
        artistsViewModel.getAllArtists().observe(getViewLifecycleOwner(), new Observer<ArrayList<ArtistsModel>>(){
                @Override
                public void onChanged(ArrayList<ArtistsModel> allArtists) {
                	artistsAdapter = new ArtistsAdapter(container.getContext(), getLayoutInflater(), allArtists);
                    binding.recyclerView.setAdapter(artistsAdapter);
                    
                    artistsAdapter.setOnItemClickListener((view, _artist, position) -> {
                            ArtistsModel artist = (ArtistsModel) _artist;
                            startActivity(new Intent(container.getContext(), AlbumArtistSongsActivity.class)
                                .putExtra("title", artist.getArtist())
                                .putExtra("from", "artist"));
                    });
                }
        });
        
        return binding.getRoot();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.binding = null;
    }
    
}
