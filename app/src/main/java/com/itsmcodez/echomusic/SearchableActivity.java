package com.itsmcodez.echomusic;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.itsmcodez.echomusic.adapters.SongsAdapter;
import com.itsmcodez.echomusic.databinding.ActivitySearchableBinding;
import com.itsmcodez.echomusic.models.SongsModel;
import com.itsmcodez.echomusic.services.MusicService;
import com.itsmcodez.echomusic.utils.MusicUtils;
import com.itsmcodez.echomusic.viewmodels.SongsViewModel;
import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ActivitySearchableBinding binding;
    private SongsAdapter songsAdapter;
    private SongsViewModel songsViewModel;
    private MediaController mediaController;
    private ListenableFuture<MediaController> controllerFuture;
    
    @Override
    protected void onStart() {
        super.onStart();
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, MusicService.class));
        controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
                
                if(controllerFuture.isDone()) {
                    try {
                        mediaController = controllerFuture.get();
                    } catch(Exception err) {
                        err.printStackTrace();
                        mediaController = null;
                    }
                }
        }, MoreExecutors.directExecutor());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind to views
        binding = ActivitySearchableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        songsViewModel = new ViewModelProvider(this).get(SongsViewModel.class);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.searchView.setOnQueryTextListener(this);
        binding.searchView.setFocusable(true);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        
        if(!newText.trim().isEmpty()) {
        	songsViewModel.getAllSongs().observe(this, new Observer<ArrayList<SongsModel>>(){
                    @Override
                    public void onChanged(ArrayList<SongsModel> allSongs) {
                        ArrayList<SongsModel> filteredList = new ArrayList<>();
                        
                        if(binding.searchFilters.getCheckedChipId() == binding.filterAll.getId()) {
                        	for(SongsModel song : allSongs) {
                                if(song.getTitle().toLowerCase().contains(newText.toLowerCase()) || 
                                    song.getArtist().toLowerCase().contains(newText.toLowerCase())) {
                                    filteredList.add(song);
                                }
                            }
                        }
                        
                        if(binding.searchFilters.getCheckedChipId() == binding.filterSongs.getId()) {
                        	for(SongsModel song : allSongs) {
                                if(song.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                                    filteredList.add(song);
                                }
                            }
                        }
                        
                        if(binding.searchFilters.getCheckedChipId() == binding.filterArtists.getId()) {
                        	for(SongsModel song : allSongs) {
                                if(song.getArtist().toLowerCase().contains(newText.toLowerCase())) {
                                    filteredList.add(song);
                                }
                            }
                        }
                        
                    	songsAdapter = new SongsAdapter(SearchableActivity.this, getLayoutInflater(), filteredList);
                        binding.recyclerView.setAdapter(songsAdapter);
                        
                        songsAdapter.setOnItemClickListener((view, _song, position) -> {
                                mediaController.setMediaItems(MusicUtils.makeMediaItems(filteredList), position, 0);
                                if(!mediaController.isPlaying()) {
                                    mediaController.prepare();
                                    mediaController.play();
                                }
                                startActivity(new Intent(SearchableActivity.this, PlayerActivity.class));
                        });
                    }
            });
        } else binding.recyclerView.setAdapter(null);
        
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
    
    @Override
    public void onStop() {
        super.onStop();
        MediaController.releaseFuture(controllerFuture);
    }
}
