package com.itsmcodez.echomusic;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.itsmcodez.echomusic.adapters.PlaylistSongsAdapter;
import com.itsmcodez.echomusic.databinding.ActivityPlaylistSongsBinding;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.services.MusicService;
import com.itsmcodez.echomusic.utils.MusicUtils;
import com.itsmcodez.echomusic.utils.PlaylistUtils;
import com.itsmcodez.echomusic.viewmodels.PlaylistSongsViewModel;
import java.util.ArrayList;
import java.util.Collections;

public class PlaylistSongsActivity extends AppCompatActivity {
    private ActivityPlaylistSongsBinding binding;
    private PlaylistSongsAdapter playlistSongsAdapter;
    private ArrayList<PlaylistSongsModel> songs = new ArrayList<>();
    private static PlaylistSongsViewModel playlistSongsViewModel;
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
        binding = ActivityPlaylistSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // ActionBar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // shuffleAllBt logic
        binding.shuffleAllBt.setOnClickListener(view -> {
                if(!mediaController.getShuffleModeEnabled()) {
                	mediaController.setShuffleModeEnabled(true);
                    Toast.makeText(this, getString(R.string.msg_shuffle_on), Toast.LENGTH_SHORT).show();
                }
                mediaController.setMediaItems(MusicUtils.makeMediaItems(songs, "Playlist songs"));
        });
        
        // playAllBt logic
        binding.playAllBt.setOnClickListener(view -> {
                if(mediaController.getShuffleModeEnabled()) {
                	mediaController.setShuffleModeEnabled(false);
                    Toast.makeText(this, getString(R.string.msg_shuffle_off), Toast.LENGTH_SHORT).show();
                }
                mediaController.setMediaItems(MusicUtils.makeMediaItems(songs, "Playlist songs"));
        });
        
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
                    // get playlist songs
                    songs = allSongs;
                	playlistSongsAdapter = new PlaylistSongsAdapter(PlaylistSongsActivity.this, getLayoutInflater(), playlistPosition, allSongs);
                    binding.recyclerView.setAdapter(playlistSongsAdapter);
                    
                    playlistSongsAdapter.setOnItemClickListener((view, _song, position) -> {
                            // Update MediaItems
                            mediaController.setMediaItems(MusicUtils.makeMediaItems(allSongs, "Playlist Songs"), position, 0);
                            startActivity(new Intent(PlaylistSongsActivity.this, PlayerActivity.class));
                    });
                    
                    // Cover Art
                    binding.coverArt.setImageDrawable(playlistPosition == 0 ? getDrawable(R.drawable.ic_heart) : getDrawable(R.drawable.ic_library_music_outline));
                    
                    // Songs list and duration
                    var songsDuration = 0l;
                    var sum = 0l;
                    for(PlaylistSongsModel song : allSongs) {
                    	songsDuration = sum + Long.parseLong(song.getDuration());
                        sum += Long.parseLong(song.getDuration());
                    }
                    binding.info.setText(getResources().getQuantityString(R.plurals.playlist_songs_count, allSongs.size(), allSongs.size()) + " " + MusicUtils.getReadableDuration(songsDuration));
                    
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
    
    @Override
    public void onStop() {
        super.onStop();
        MediaController.releaseFuture(controllerFuture);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_playlist_songs, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if(item.getItemId() == R.id.add_pl_songs_to_queue_menu_item) {
            AlertDialog dialog = new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.add_to_queue)
            .setMessage(getString(R.string.msg_add_songs_to_playing_queue, songs.size()))
            .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
                @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
            })
            .setPositiveButton(R.string.add, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaController.addMediaItems(MusicUtils.makeMediaItems(songs, "Playlist songs"));
                        Toast.makeText(PlaylistSongsActivity.this, getString(R.string.msg_add_songs_to_queue_success, songs.size()), Toast.LENGTH_SHORT).show();
                    }
            })
            .create();
            dialog.show();
            
            return true;
        }
        
        if(item.getItemId() == R.id.sort_custom) {
        	
            return true;
        }
        
        if(item.getItemId() == R.id.sort_asc) {
        	
            return true;
        }
        
        if(item.getItemId() == R.id.sort_desc) {
        	
            return true;
        }
        
        if(item.getItemId() == R.id.sort_size) {
        	
            return true;
        }
        
        if(item.getItemId() == R.id.sort_duration) {
        	
            return true;
        }
        
        if(item.getItemId() == R.id.clear_pl_songs_menu_item) {
            // get playlist position
            var playlistPosition = getIntent().getIntExtra("position", -1);
            
            AlertDialog dialog = new MaterialAlertDialogBuilder(PlaylistSongsActivity.this)
            .setTitle(R.string.clear_playlist)
            .setMessage(getString(R.string.msg_clear_playlist_rationale, PlaylistUtils.getAllPlaylists(getApplication()).get(playlistPosition).getTitle()))
            .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
            })
            .setPositiveButton(R.string.clear, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playlistSongsViewModel.clearSongsFromPlaylistAt(playlistPosition);
                    }
            })
            .create();
            dialog.show();
            
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    public static void removeSongFromPlaylistAt(int songPosition, int position) {
        playlistSongsViewModel.removeSongFromPlaylistAt(songPosition, position);
    }
}
