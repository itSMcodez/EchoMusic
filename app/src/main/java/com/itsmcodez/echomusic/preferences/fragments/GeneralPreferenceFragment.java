package com.itsmcodez.echomusic.preferences.fragments;
import android.content.Context;
import android.content.SharedPreferences;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.fragments.PreferencesFragment;

public class GeneralPreferenceFragment extends PreferencesFragment {
    private Context context;
    
    public GeneralPreferenceFragment(Context context){
        super(context, R.xml.preference_general);
        this.context = context;
    }
    
    @Override
    protected void onPrefScreenAttached(final SharedPreferences KEY) {
        
    }
    
}
