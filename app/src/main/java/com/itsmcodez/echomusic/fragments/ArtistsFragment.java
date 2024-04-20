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
import com.itsmcodez.echomusic.adapters.ArtistsAdapter;
import com.itsmcodez.echomusic.databinding.FragmentArtistsBinding;
import com.itsmcodez.echomusic.models.ArtistsModel;
import com.itsmcodez.echomusic.viewmodels.ArtistsViewModel;
import java.util.ArrayList;

public class ArtistsFragment extends Fragment {
    private FragmentArtistsBinding binding;
    private ArtistsAdapter artistsAdapter;
    private ArtistsViewModel artistsViewModel;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistsViewModel = new ViewModelProvider(this).get(ArtistsViewModel.class);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Bind to views
        binding = FragmentArtistsBinding.inflate(inflater, container, false);
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), RecyclerView.VERTICAL, false));
        
        artistsViewModel.getAllArtists().observe(getViewLifecycleOwner(), new Observer<ArrayList<ArtistsModel>>(){
                @Override
                public void onChanged(ArrayList<ArtistsModel> allArtists) {
                	artistsAdapter = new ArtistsAdapter(container.getContext(), getLayoutInflater(), allArtists);
                    binding.recyclerView.setAdapter(artistsAdapter);
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
