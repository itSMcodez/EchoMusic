package com.itsmcodez.echomusic;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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
import com.itsmcodez.echomusic.databinding.ActivityAlbumArtistSongsBinding;
import com.itsmcodez.echomusic.databinding.LayoutRecyclerviewBinding;
import com.itsmcodez.echomusic.models.ListOfPlaylistModel;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import com.itsmcodez.echomusic.models.SongsModel;
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
                    binding.recyclerView.setAdapter(songsAdapter);
                    
                    songsAdapter.setOnItemClickListener((view, _song, position) -> {
                            mediaController.setMediaItems(MusicUtils.makeMediaItems(songs), position, 0);
                            if(!mediaController.isPlaying()) {
                                mediaController.prepare();
                                mediaController.play();
                            }
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
        if(!mediaController.isPlaying()) {
        	mediaController.release();
            stopService(new Intent(this, MusicService.class));
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_album_artist_songs, menu);
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
        
        return super.onOptionsItemSelected(item);
    }
    
}
