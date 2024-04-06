
package com.itsmcodez.echomusic;

import android.view.Menu;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.itsmcodez.echomusic.databinding.ActivityMainBinding;

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
        
        // BottomNavigation
        binding.bottomNav.setOnItemSelectedListener( item -> {
                
                if(item.getItemId() == R.id.songs_menu_item) {
                    
                    return true;
                }
                
                if(item.getItemId() == R.id.albums_menu_item) {
                    
                    return true;
                }
                
                if(item.getItemId() == R.id.artists_menu_item) {
                    
                    return true;
                }
                
                if(item.getItemId() == R.id.playlists_menu_item) {
                    
                    return true;
                }
                
                return false;
        });
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
    
}
