package com.itsmcodez.echomusic.fragments;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.itsmcodez.echomusic.AlbumArtistSongsActivity;
import com.itsmcodez.echomusic.adapters.AlbumsAdapter;
import com.itsmcodez.echomusic.databinding.FragmentAlbumsBinding;
import com.itsmcodez.echomusic.models.AlbumsModel;
import com.itsmcodez.echomusic.viewmodels.AlbumsViewModel;
import java.util.ArrayList;

public class AlbumsFragment extends Fragment {
    private FragmentAlbumsBinding binding;
    private AlbumsAdapter albumsAdapter;
    private AlbumsViewModel albumsViewModel;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumsViewModel = new ViewModelProvider(this).get(AlbumsViewModel.class);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Bind to views
        binding = FragmentAlbumsBinding.inflate(inflater, container, false);
        
        binding.recyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), 2));
        
        albumsViewModel.getAllAlbums().observe(getViewLifecycleOwner(), new Observer<ArrayList<AlbumsModel>>(){
                @Override
                public void onChanged(ArrayList<AlbumsModel> allAlbums) {
                	albumsAdapter = new AlbumsAdapter(container.getContext(), getLayoutInflater(), allAlbums);
                    binding.recyclerView.setAdapter(albumsAdapter);
                    
                    albumsAdapter.setOnItemClickListener((view, _album, position) -> {
                            AlbumsModel album = (AlbumsModel) _album;
                            startActivity(new Intent(container.getContext(), AlbumArtistSongsActivity.class)
                                .putExtra("title", album.getAlbum())
                                .putExtra("album_id", album.getAlbumId())
                                .putExtra("from", "album"));
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
