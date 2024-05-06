package com.itsmcodez.echomusic.fragments;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.itsmcodez.echomusic.adapters.SongsAdapter;
import com.itsmcodez.echomusic.databinding.FragmentSongsBinding;
import com.itsmcodez.echomusic.models.SongsModel;
import com.itsmcodez.echomusic.viewmodels.SongsViewModel;
import java.util.ArrayList;

public class SongsFragment extends Fragment {
    private FragmentSongsBinding binding;
    private SongsAdapter songsAdapter;
    private SongsViewModel songsViewModel;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songsViewModel = new ViewModelProvider(this).get(SongsViewModel.class);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Bind to views
        binding = FragmentSongsBinding.inflate(inflater, container, false);
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), RecyclerView.VERTICAL, false));
        
        songsViewModel.getAllSongs().observe(getViewLifecycleOwner(), new Observer<ArrayList<SongsModel>>() {
                @Override
                public void onChanged(ArrayList<SongsModel> allSongs) {
                	songsAdapter = new SongsAdapter(container.getContext(), getLayoutInflater(), allSongs);
                    binding.recyclerView.setAdapter(songsAdapter);
                    
                    songsAdapter.setOnItemClickListener((view, _song, position) -> {
                            
                    });
                    
                    songsAdapter.setOnItemLongClickListener((view, _song, position) -> {
                            
                            return true;
                    });
                }
        });
        
        return binding.getRoot();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.binding = null;
    }
}
