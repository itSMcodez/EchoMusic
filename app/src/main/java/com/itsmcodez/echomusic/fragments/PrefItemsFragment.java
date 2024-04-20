package com.itsmcodez.echomusic.fragments;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.itsmcodez.echomusic.databinding.FragmentPrefItemsBinding;
import com.itsmcodez.echomusic.preferences.utils.PreferenceItem;
import java.util.ArrayList;

public class PrefItemsFragment extends Fragment {
    private FragmentPrefItemsBinding binding;
    private ArrayList<PreferenceItem> prefItems;
    
    public PrefItemsFragment(ArrayList<PreferenceItem> prefItems){
        this.prefItems = prefItems;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Bind to views
        binding = FragmentPrefItemsBinding.inflate(inflater, container, false);
        
        for(PreferenceItem prefItem : prefItems) {
        	addPreferenceItem(prefItem);
        }
        
        return binding.getRoot();
    }
    
    private void addPreferenceItem(PreferenceItem item) {
    	binding.getRoot().addView(item.getRoot());
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.binding = null;
    }
}
