package com.itsmcodez.echomusic.fragments;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public abstract class PreferencesFragment extends PreferenceFragmentCompat {
    private int PREF_SCREEN;
    private Context PREF_CONTEXT;
    
    public PreferencesFragment(final Context PREF_CONTEXT, final int PREF_SCREEN){
        this.PREF_SCREEN = PREF_SCREEN;
        this.PREF_CONTEXT = PREF_CONTEXT;
    }
    
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootkey) {
        setPreferencesFromResource(PREF_SCREEN, rootkey);
        onPrefScreenAttached(PreferenceManager.getDefaultSharedPreferences(PREF_CONTEXT));
    }
    
    protected abstract void onPrefScreenAttached(final SharedPreferences KEY);
}
