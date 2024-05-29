
package com.itsmcodez.echomusic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import android.content.ComponentName;
import com.bumptech.glide.Glide;
import com.itsmcodez.echomusic.callbacks.OnPlayerStateChange;
import com.itsmcodez.echomusic.common.PlayerStateObserver;
import com.itsmcodez.echomusic.services.MusicService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ListenableFuture;
import com.itsmcodez.echomusic.databinding.ActivityMainBinding;
import com.itsmcodez.echomusic.fragments.AlbumsFragment;
import com.itsmcodez.echomusic.fragments.ArtistsFragment;
import com.itsmcodez.echomusic.fragments.PlaylistsFragment;
import com.itsmcodez.echomusic.fragments.SongsFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MediaController mediaController;
    private ListenableFuture<MediaController> controllerFuture;
    private OnPlayerStateChange playerStateCallback;
    
    @Override
    public void onStart() {
        super.onStart();
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, MusicService.class));
        controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
                
                if(controllerFuture.isDone()) {
                    try {
                        mediaController = controllerFuture.get();
                        // update UI if music is being played
                        if(mediaController != null && mediaController.getCurrentMediaItem() != null) {
                        	updateUI(mediaController.getCurrentMediaItem());
                            binding.playPauseBt.setImageDrawable(mediaController.isPlaying() ? getDrawable(R.drawable.ic_pause_outline) : getDrawable(R.drawable.ic_play_outline));
                            updateProgress();
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
                if(playbackState == ExoPlayer.STATE_READY) {
                    binding.playPauseBt.setImageDrawable(getDrawable(R.drawable.ic_pause_outline));
                    updateUI(mediaController.getCurrentMediaItem());
                    updateProgress();
                } else binding.playPauseBt.setImageDrawable(getDrawable(R.drawable.ic_play_outline));
            }
            
            @Override
            public void onMediaItemTransition(MediaItem mediaItem, int reason) {
                updateUI(mediaItem);
                updateProgress();
            }
            
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if(isPlaying) {
                	binding.playPauseBt.setImageDrawable(getDrawable(R.drawable.ic_pause_outline));
                } else binding.playPauseBt.setImageDrawable(getDrawable(R.drawable.ic_play_outline));
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // set content view to binding's root
        setContentView(binding.getRoot());
        
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        	ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        
        // ActionBar
        setSupportActionBar(binding.toolbar);
        
        // Mini controller
        binding.miniController.setOnClickListener(view -> {
                startActivity(new Intent(MainActivity.this, PlayerActivity.class));
        });
        
        // skipNextBt
        binding.skipNextBt.setOnClickListener(view -> {
                if(mediaController.getPlayWhenReady() || mediaController.getMediaItemCount() != 0) {
                	if(mediaController.hasNextMediaItem()) {
                		mediaController.seekToNextMediaItem();
                	}
                }
        });
        
        // playPauseBt
        binding.playPauseBt.setOnClickListener(view -> {
                if(mediaController.getPlayWhenReady() || mediaController.getMediaItemCount() != 0) {
                	if(mediaController.isPlaying()) {
                		mediaController.pause();
                	} else {
                        mediaController.play();
                    }
                }
        });
        
        // Fragments
        SongsFragment songsFragment = new SongsFragment();
        AlbumsFragment albumsFragment = new AlbumsFragment();
        ArtistsFragment artistsFragment = new ArtistsFragment();
        PlaylistsFragment playlistsFragment = new PlaylistsFragment();
        replaceFragment(songsFragment);
        
        // BottomNavigation
        binding.bottomNav.setOnItemSelectedListener( item -> {
                
                if(item.getItemId() == R.id.songs_menu_item) {
                    replaceFragment(songsFragment);
                    return true;
                }
                
                if(item.getItemId() == R.id.albums_menu_item) {
                    replaceFragment(albumsFragment);
                    return true;
                }
                
                if(item.getItemId() == R.id.artists_menu_item) {
                    replaceFragment(artistsFragment);
                    return true;
                }
                
                if(item.getItemId() == R.id.playlists_menu_item) {
                    replaceFragment(playlistsFragment);
                    return true;
                }
                
                return false;
        });
    }
    
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, fragment);
        fragmentTransaction.commit();
    }

    private void updateProgress() {
        if(mediaController != null) {
            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(binding != null && mediaController.getCurrentMediaItem() != null) {
                            // Player progress
                            binding.progress.setMax((int)mediaController.getDuration());
                            if(mediaController.getPlayWhenReady() || mediaController.isPlaying()) {
                                binding.progress.setProgress((int)mediaController.getCurrentPosition());
                            }
                        }
                        new Handler().postDelayed(this, 1000);
                    }
            });
        }
    }
    
    private void updateUI(MediaItem mediaItem) {
        if(!binding.title.isSelected()) {
        	binding.title.setSelected(true);
        }
        if(!binding.artist.isSelected()) {
        	binding.artist.setSelected(true);
        }
        binding.title.setText(mediaItem.mediaMetadata.title);
        binding.artist.setText(mediaItem.mediaMetadata.artist);
        Glide.with(MainActivity.this).load(mediaItem.mediaMetadata.artworkUri)
            .error(R.drawable.ic_music_note_outline)
            .into(binding.albumArtwork);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main_action_bar, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if(item.getItemId() == R.id.settings_menu_item) {
        	startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        
        if(item.getItemId() == R.id.search_menu_item) {
        	startActivity(new Intent(MainActivity.this, SearchableActivity.class));
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        	recreate();
        }
    }
    
    
}
