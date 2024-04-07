package com.itsmcodez.echomusic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.itsmcodez.echomusic.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind to views
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // ActionBar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(R.string.activity_settings_action_bar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
