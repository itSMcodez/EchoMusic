package com.itsmcodez.echomusic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.itsmcodez.echomusic.databinding.ActivityPlayerBinding;
import com.itsmcodez.echomusic.fragments.NPMD3Fragment;

public class PlayerActivity extends AppCompatActivity {
    private ActivityPlayerBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind to views
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize Now Playing Fragments
        NPMD3Fragment NPMD3Fragment = new NPMD3Fragment();
        
        // Show Now playing screen fragment
        replaceFragment(NPMD3Fragment);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
    
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.now_playing_fragment, fragment);
        fragmentTransaction.commit();
    }
}
