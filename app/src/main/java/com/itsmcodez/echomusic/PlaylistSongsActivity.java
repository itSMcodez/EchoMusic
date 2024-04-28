package com.itsmcodez.echomusic;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.itsmcodez.echomusic.adapters.PlaylistSongsAdapter;
import com.itsmcodez.echomusic.databinding.ActivityPlaylistSongsBinding;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.viewmodels.PlaylistSongsViewModel;
import java.util.ArrayList;

public class PlaylistSongsActivity extends AppCompatActivity {
    private ActivityPlaylistSongsBinding binding;
    private PlaylistSongsAdapter playlistSongsAdapter;
    private static PlaylistSongsViewModel playlistSongsViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind to views
        binding = ActivityPlaylistSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // ActionBar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Intent keys
        Intent intent = getIntent();
        binding.title.setText(intent.getStringExtra("title"));
        
        
        // initialize viewmodel and recyclerview
        playlistSongsViewModel = new ViewModelProvider(this).get(PlaylistSongsViewModel.class);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        
        // Observe LiveData
        var playlistPosition = intent.getIntExtra("position", -1);
        playlistSongsViewModel.getAllSongs(playlistPosition).observe(this, new Observer<ArrayList<PlaylistSongsModel>>(){
                @Override
                public void onChanged(ArrayList<PlaylistSongsModel> allSongs) {
                	playlistSongsAdapter = new PlaylistSongsAdapter(PlaylistSongsActivity.this, getLayoutInflater(), playlistPosition, allSongs);
                    binding.recyclerView.setAdapter(playlistSongsAdapter);
                }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
    
    public static void removeSongFromPlaylistAt(int songPosition, int position) {
        playlistSongsViewModel.removeSongFromPlaylistAt(songPosition, position);
    }
}
