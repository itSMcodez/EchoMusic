package com.itsmcodez.echomusic.preferences.utils;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import com.itsmcodez.echomusic.SettingsActivity;
import com.itsmcodez.echomusic.databinding.LayoutPreferenceItemBinding;
import com.itsmcodez.echomusic.fragments.PreferencesFragment;

public class PreferenceItem {
    private LayoutPreferenceItemBinding binding;
    private SettingsActivity settingsActivity;
    private PreferencesFragment fragment;
    
    private PreferenceItem(Builder builder){
        binding = LayoutPreferenceItemBinding.inflate(builder.inflater);
        setItemIcon(builder.icon);
        setItemTitle(builder.title);
        setItemSummary(builder.summary);
        setItemFragment(builder.fragment);
        setSettingsActivity(builder.settingsActivity);
        
        this.settingsActivity = builder.settingsActivity;
        this.fragment = builder.fragment;
        
        binding.item.setOnClickListener(view -> {
                settingsActivity.replaceFragment(fragment);
                settingsActivity.getSupportActionBar().setTitle(builder.title);
                settingsActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        });
        
        binding.icon.setOnClickListener(view -> {
                settingsActivity.replaceFragment(fragment);
                settingsActivity.getSupportActionBar().setTitle(builder.title);
                settingsActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        });
    }
    
    public View getRoot(){
        return binding.getRoot();
    }
    
    public void setItemIcon(Drawable icon) {
    	if(icon != null) {
    		binding.icon.setIcon(icon);
    	}
    }
    
    public void setItemTitle(String title) {
    	if(title != null) {
    		binding.title.setText(title);
    	}
    }
    
    public void setItemSummary(String summary) {
    	if(summary != null) {
            binding.summary.setText(summary);
        }
    }
    
    public void setItemFragment(PreferencesFragment fragment) {
    	if(fragment != null) {
    		this.fragment = fragment;
    	}
    }
    
    public void setSettingsActivity(SettingsActivity settingsActivity) {
    	this.settingsActivity = settingsActivity;
    }
    
    
    public static class Builder {
        private LayoutInflater inflater;
        private Drawable icon;
        private String title, summary;
        private PreferencesFragment fragment;
        private SettingsActivity settingsActivity;
        
        public Builder(LayoutInflater inflater){
            this.inflater = inflater;
        }
        
        public Builder setItemIcon(Drawable icon) {
            this.icon = icon;
            return this;
        }
        
        public Builder setItemTitle(String title) {
            this.title = title;
            return this;
        }
        
        public Builder setItemSummary(String summary) {
            this.summary = summary;
            return this;
        }
        
        public Builder setItemFragment(PreferencesFragment fragment) {
            this.fragment = fragment;
            return this;
        }
        
        public Builder setSettingsActivity(SettingsActivity settingsActivity) {
            this.settingsActivity = settingsActivity;
        	return this;
        }
        
        public PreferenceItem build(){
            return new PreferenceItem(this);
        }
    }
}
