
package com.itsmcodez.echomusic;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.itsmcodez.echomusic.databinding.ActivityMainBinding;
import com.itsmcodez.echomusic.fragments.AlbumsFragment;
import com.itsmcodez.echomusic.fragments.ArtistsFragment;
import com.itsmcodez.echomusic.fragments.PlaylistsFragment;
import com.itsmcodez.echomusic.fragments.SongsFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // set content view to binding's root
        setContentView(binding.getRoot());
        
        // ActionBar
        setSupportActionBar(binding.toolbar);
        
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_action_bar_menu, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if(item.getItemId() == R.id.settings_menu_item) {
        	startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
}
