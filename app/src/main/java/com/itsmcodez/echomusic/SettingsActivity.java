package com.itsmcodez.echomusic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.itsmcodez.echomusic.databinding.ActivitySettingsBinding;
import com.itsmcodez.echomusic.fragments.PrefItemsFragment;
import com.itsmcodez.echomusic.preferences.fragments.GeneralPreferenceFragment;
import com.itsmcodez.echomusic.preferences.utils.PreferenceItem;
import java.util.ArrayList;

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
        
        // Preference items
        PreferenceItem generalPrefItem = new PreferenceItem.Builder(getLayoutInflater())
        .setItemTitle("General Preferences")
        .setItemSummary("Settings for app")
        .setItemIcon(getDrawable(R.drawable.ic_settings))
        .setItemFragment(new GeneralPreferenceFragment(this))
        .setSettingsActivity(this)
        .build();
        
        ArrayList<PreferenceItem> prefItems = new ArrayList<>();
        prefItems.add(generalPrefItem);
        
        // Fragments 
        PrefItemsFragment prefItemsFragment = new PrefItemsFragment(prefItems);
        replaceFragment(prefItemsFragment);
    }
    
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, fragment);
        fragmentTransaction.commit();
    }
}
