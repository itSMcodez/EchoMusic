package com.itsmcodez.echomusic;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.itsmcodez.echomusic.adapters.ListOfPlaylistAdapter;
import com.itsmcodez.echomusic.adapters.SongsAdapter;
import com.itsmcodez.echomusic.common.SortOrder;
import com.itsmcodez.echomusic.databinding.ActivityAlbumArtistSongsBinding;
import com.itsmcodez.echomusic.databinding.LayoutRecyclerviewBinding;
import com.itsmcodez.echomusic.models.ListOfPlaylistModel;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import com.itsmcodez.echomusic.models.SongsModel;
import com.itsmcodez.echomusic.preferences.Settings;
import com.itsmcodez.echomusic.repositories.PlaylistsRepository;
import com.itsmcodez.echomusic.services.MusicService;
import com.itsmcodez.echomusic.utils.ArtworkUtils;
import com.itsmcodez.echomusic.utils.MusicUtils;
import com.itsmcodez.echomusic.viewmodels.SongsViewModel;
import java.util.ArrayList;

public class AlbumArtistSongsActivity extends AppCompatActivity {
    private ActivityAlbumArtistSongsBinding binding;
    private SongsAdapter songsAdapter;
    private SongsViewModel songsViewModel;
    private ArrayList<SongsModel> songs = new ArrayList<>();
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
        binding = ActivityAlbumArtistSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Intent key
        Intent intent = getIntent();
        
        // ActionBar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(intent.getStringExtra("title"));
        
        // shuffleAllBt logic
        binding.shuffleAllBt.setOnClickListener(view -> {
                if(!mediaController.getShuffleModeEnabled()) {
                	mediaController.setShuffleModeEnabled(true);
                    Toast.makeText(this, getString(R.string.msg_shuffle_on), Toast.LENGTH_SHORT).show();
                }
                mediaController.setMediaItems(MusicUtils.makeMediaItems(songs));
        });
        
        // playAllBt logic
        binding.playAllBt.setOnClickListener(view -> {
                if(mediaController.getShuffleModeEnabled()) {
                	mediaController.setShuffleModeEnabled(false);
                    Toast.makeText(this, getString(R.string.msg_shuffle_off), Toast.LENGTH_SHORT).show();
                }
                mediaController.setMediaItems(MusicUtils.makeMediaItems(songs));
        });
        
        // RecyclerView and ViewModel
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        songsViewModel = new ViewModelProvider(this).get(SongsViewModel.class);
        
        // Observe LiveData
        songsViewModel.getAllSongs().observe(this, new Observer<ArrayList<SongsModel>>(){
                @Override
                public void onChanged(ArrayList<SongsModel> allSongs) {
                    // filter songs to match album/artist name
                	ArrayList<SongsModel> filteredList = new ArrayList<>();
                    if(intent.getStringExtra("from").equals("album")) {
                    	for(SongsModel song : allSongs) {
                            if(song.getAlbum().equals(intent.getStringExtra("title"))) {
                                filteredList.add(song);
                            }
                        }
                    } else if(intent.getStringExtra("from").equals("artist")) {
                    	for(SongsModel song : allSongs) {
                            if(song.getArtist().equals(intent.getStringExtra("title"))) {
                                filteredList.add(song);
                            }
                        }
                    }
                    
                    // Get filtered songs
                    songs = filteredList;
                    
                    // songs list size and duration
                    var songsDuration = 0l;
                    var sum = 0l;
                    for(SongsModel song : songs) {
                    	songsDuration = sum + Long.parseLong(song.getDuration());
                        sum += Long.parseLong(song.getDuration());
                    }
                    binding.info.setText(getResources().getQuantityString(R.plurals.playlist_songs_count, songs.size(), songs.size()) + " " + MusicUtils.getReadableDuration(songsDuration));
                    
                    songsAdapter = new SongsAdapter(AlbumArtistSongsActivity.this, getLayoutInflater(), songs);
                    songsAdapter.setOnPlayNextClickListener((song) -> {
                            mediaController.addMediaItem(mediaController.getCurrentMediaItem() != null ? mediaController.getCurrentMediaItemIndex()+1 : 0, MusicUtils.makeMediaItem(song));
                            Toast.makeText(AlbumArtistSongsActivity.this, getString(R.string.msg_add_song_to_playing_queue_success, song.getTitle()), Toast.LENGTH_SHORT).show();
                    });
                    binding.recyclerView.setAdapter(songsAdapter);
                    
                    songsAdapter.setOnItemClickListener((view, _song, position) -> {
                            // Update MediaItems
                            mediaController.setMediaItems(MusicUtils.makeMediaItems(songs), position, 0);
                            
                            startActivity(new Intent(AlbumArtistSongsActivity.this, PlayerActivity.class));
                    });
                    
                    // Cover Art
                    binding.albumArtwork.setImageURI(ArtworkUtils.getArtworkFrom(Long.parseLong(intent.getStringExtra("album_id"))));
                    if(binding.albumArtwork.getDrawable() == null) {
                    	binding.albumArtwork.setImageDrawable(getDrawable(R.drawable.ic_library_music_outline));
                    }
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
        getMenuInflater().inflate(R.menu.menu_activity_album_artist_songs, menu);
        
        final String SORT_ORDER = Settings.getSongsSortOrder();
        switch(SORT_ORDER){
            
            case "SORT_ASC" : {
                menu.findItem(R.id.sort_asc).setChecked(true);
                songsViewModel.sortSongs(SortOrder.ASCENDING);
            }
            break;
            
            case "SORT_DESC" : {
                menu.findItem(R.id.sort_desc).setChecked(true);
                songsViewModel.sortSongs(SortOrder.DESCENDING);
            }
            break;
            
            case "SORT_SIZE" : {
                menu.findItem(R.id.sort_size).setChecked(true);
                songsViewModel.sortSongs(SortOrder.SIZE);
            }
            break;
            
            case "SORT_DURATION" : {
                menu.findItem(R.id.sort_duration).setChecked(true);
                songsViewModel.sortSongs(SortOrder.DURATION);
            }
            break;
            
            default: {
                menu.findItem(R.id.sort_default).setChecked(true);
                songsViewModel.sortSongs(SortOrder.DEFAULT);
            }
        }
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if(item.getItemId() == R.id.add_songs_to_playlist_menu_item) {
            
            // List of playlists logic
            ArrayList<ListOfPlaylistModel> listOfPlaylists = new ArrayList<>();
            PlaylistsRepository playlistRepo = PlaylistsRepository.getInstance(BaseApplication.getApplication());
            LayoutRecyclerviewBinding recyclerViewBinding = LayoutRecyclerviewBinding.inflate(getLayoutInflater());
            for(PlaylistsModel playlist : playlistRepo.getPlaylists()) {
                listOfPlaylists.add(new ListOfPlaylistModel(playlist.getTitle()));
            }
            
            // ListOfPlaylistAdapter and dialog logic
            ListOfPlaylistAdapter adapter = new ListOfPlaylistAdapter(AlbumArtistSongsActivity.this, getLayoutInflater(), listOfPlaylists);
            recyclerViewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(AlbumArtistSongsActivity.this, RecyclerView.VERTICAL, false));
            recyclerViewBinding.recyclerView.setAdapter(adapter);
            AlertDialog dialog = new MaterialAlertDialogBuilder(AlbumArtistSongsActivity.this)
            .setView(recyclerViewBinding.getRoot())
            .setTitle(R.string.choose_playlist_dialog_title)
            .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
            })
            .create();
            dialog.show();
            
            adapter.setOnItemClickListener((view, _playlist, position) -> {
                    ListOfPlaylistModel playlist = (ListOfPlaylistModel) _playlist;
                    ArrayList<PlaylistSongsModel> songs = new ArrayList<>();
                    for(SongsModel song : this.songs) {
                    	songs.add(new PlaylistSongsModel(song.getPath(), song.getTitle(), 
                                song.getArtist(), song.getDuration(), 
                                song.getAlbum(), song.getAlbumId(), 
                                song.getSongId()));
                    }
                    playlistRepo.addSongsToPlaylistAt(songs, position);
                    dialog.dismiss();
            });
            
        	return true;
        }
        
        if(item.getItemId() == R.id.add_songs_to_queue_menu_item) {
        	AlertDialog dialog = new MaterialAlertDialogBuilder(AlbumArtistSongsActivity.this)
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
                            mediaController.addMediaItems(MusicUtils.makeMediaItems(songs));
                            Toast.makeText(AlbumArtistSongsActivity.this, getString(R.string.msg_add_songs_to_queue_success, songs.size()), Toast.LENGTH_SHORT).show();
                        }
                })
                .create();
                dialog.show();
            
            return true;
        }
        
        if(item.getItemId() == R.id.sort_desc) {
        	songsViewModel.sortSongs(SortOrder.DESCENDING);
            Settings.setSongsSortOrder(SortOrder.DESCENDING);
            item.setChecked(true);
            return true;
        }
        
        if(item.getItemId() == R.id.sort_asc) {
        	songsViewModel.sortSongs(SortOrder.ASCENDING);
            Settings.setSongsSortOrder(SortOrder.ASCENDING);
            item.setChecked(true);
            return true;
        }
        
        if(item.getItemId() == R.id.sort_size) {
        	songsViewModel.sortSongs(SortOrder.SIZE);
            Settings.setSongsSortOrder(SortOrder.SIZE);
            item.setChecked(true);
            return true;
        }
        
        if(item.getItemId() == R.id.sort_duration) {
        	songsViewModel.sortSongs(SortOrder.DURATION);
            Settings.setSongsSortOrder(SortOrder.DURATION);
            item.setChecked(true);
            return true;
        }
        
        if(item.getItemId() == R.id.sort_default) {
        	songsViewModel.sortSongs(SortOrder.DEFAULT);
            Settings.setSongsSortOrder(SortOrder.DEFAULT);
            item.setChecked(true);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
}
