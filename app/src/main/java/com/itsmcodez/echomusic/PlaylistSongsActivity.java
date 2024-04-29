package com.itsmcodez.echomusic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.itsmcodez.echomusic.adapters.PlaylistSongsAdapter;
import com.itsmcodez.echomusic.databinding.ActivityPlaylistSongsBinding;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.viewmodels.PlaylistSongsViewModel;
import java.util.ArrayList;
import java.util.Collections;

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
                    
                    // Swap song item position logic
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                        new ItemTouchHelper.Callback(){
                            
                            @Override
                            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
                            }
                            
                            @Override
                            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder targetViewHolder) {
                                // Swap song position
                                int draggedItemIndex = viewHolder.getAdapterPosition();
                                int targetIndex = targetViewHolder.getAdapterPosition();
                                playlistSongsViewModel.swapPlaylistSongPosAt(playlistPosition, draggedItemIndex, targetIndex);
                                // Update adapter's contents after swapping 
                                Collections.swap(allSongs, draggedItemIndex, targetIndex);
                                playlistSongsAdapter.notifyItemMoved(draggedItemIndex, targetIndex);
                                return true;
                            }
                            
                            @Override
                            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                                // TODO: implement this method
                            }
                            
                        }
                    );
                    itemTouchHelper.attachToRecyclerView(binding.recyclerView);
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
