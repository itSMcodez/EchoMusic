package com.itsmcodez.echomusic.fragments;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.recyclerview.widget.GridLayoutManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.itsmcodez.echomusic.PlaylistSongsActivity;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.adapters.PlaylistsAdapter;
import com.itsmcodez.echomusic.databinding.FragmentPlaylistsBinding;
import com.itsmcodez.echomusic.databinding.LayoutMaterialTextinputBinding;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import com.itsmcodez.echomusic.services.MusicService;
import com.itsmcodez.echomusic.utils.MusicUtils;
import com.itsmcodez.echomusic.utils.PlaylistUtils;
import com.itsmcodez.echomusic.viewmodels.PlaylistsViewModel;
import java.util.ArrayList;

public class PlaylistsFragment extends Fragment {
    private FragmentPlaylistsBinding binding;
    private PlaylistsAdapter playlistsAdapter;
    private static PlaylistsViewModel playlistsViewModel;
    private static MediaController mediaController;
    private ListenableFuture<MediaController> controllerFuture;
    
    @Override
    public void onStart() {
        super.onStart();
        SessionToken sessionToken = new SessionToken(getContext(), new ComponentName(getContext(), MusicService.class));
        controllerFuture = new MediaController.Builder(getContext(), sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
                
                if(controllerFuture.isDone()) {
                    try {
                        mediaController = controllerFuture.get();
                    } catch(Exception err) {
                        err.printStackTrace();
                        mediaController = null;
                    }
                }
        }, MoreExecutors.directExecutor());
    }
    
    @Override
    public void onStop() {
        super.onStop();
        MediaController.releaseFuture(controllerFuture);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playlistsViewModel = new ViewModelProvider(this).get(PlaylistsViewModel.class);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Bind to views
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false);
        
        binding.recyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), 2));
        
        // Add playlist function
        binding.addPlaylistFab.setOnClickListener(view -> {
                showAddPlaylistDialog(container.getContext());
        });
        
        // Favourite songs function
        binding.favouritesFab.setOnClickListener(view -> {
                startActivity(new Intent(container.getContext(), PlaylistSongsActivity.class).putExtra("title", "Favourites").putExtra("position", PlaylistUtils.FAVOURITES));
        });
        // Create Favourites playlist if it doesn't exist
        if(playlistsViewModel.getPlaylists().size() == 0 || !playlistsViewModel.getPlaylists().get(PlaylistUtils.FAVOURITES).getTitle().equals("Favourites")) {
            PlaylistsModel favourites = new PlaylistsModel("Favourites", new ArrayList<PlaylistSongsModel>(), 0, 0);
            playlistsViewModel.addNewPlaylistAt(favourites, PlaylistUtils.FAVOURITES);
        }
        
        // Observe LiveData
        playlistsViewModel.getAllPlaylists().observe(getViewLifecycleOwner(), new Observer<ArrayList<PlaylistsModel>>(){
                @Override
                public void onChanged(ArrayList<PlaylistsModel> allPlaylists) {
                	playlistsAdapter = new PlaylistsAdapter(container.getContext(), getLayoutInflater(), allPlaylists);
                    binding.recyclerView.setAdapter(playlistsAdapter);
                    
                    // On item click
                    playlistsAdapter.setOnItemClickListener((view, _playlist, position) -> {
                            PlaylistsModel playlist = (PlaylistsModel) _playlist;
                            startActivity(new Intent(container.getContext(), PlaylistSongsActivity.class).putExtra("title", playlist.getTitle()).putExtra("position", position));
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

    private void showAddPlaylistDialog(Context context) {
        
        LayoutMaterialTextinputBinding textInputBinding = LayoutMaterialTextinputBinding.inflate(getLayoutInflater());
        textInputBinding.textInputLayout.setHint(R.string.add_playlist_textinput_hint);
        MaterialAlertDialogBuilder addPlaylistDialog = new MaterialAlertDialogBuilder(context);
        addPlaylistDialog.setTitle(R.string.add_playlist_dialog_title);
        addPlaylistDialog.setView(textInputBinding.getRoot());
        addPlaylistDialog.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
        });
        addPlaylistDialog.setPositiveButton(R.string.create, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    
                    // check for null input
                    if(textInputBinding.textInput.getText().toString().trim().isEmpty()) {
                    	Toast.makeText(context, R.string.add_playlist_dialog_empty_input_rationale, Toast.LENGTH_LONG).show();
                    } else {
                        String input = textInputBinding.textInput.getText().toString();
                        PlaylistsModel newPlaylist = new PlaylistsModel(input, new ArrayList<PlaylistSongsModel>(), 0, 0);
                        playlistsViewModel.addNewPlaylist(newPlaylist);
                        binding.recyclerView.scrollToPosition(binding.recyclerView.getAdapter().getItemCount()-1); // Scroll to the newly added playlist item
                    }
                }
        });
        addPlaylistDialog.show();
    }
    
    public static void deletePlaylistAt(int position) {
    	playlistsViewModel.deletePlaylistAt(position);
    }
    
    public static void renamePlaylistAt(String playlistName, int position) {
    	playlistsViewModel.renamePlaylistAt(playlistName, position);
    }
    
    public static void clearSongsFromPlaylistAt(int position) {
    	playlistsViewModel.clearSongsFromPlaylistAt(position);
    }
    
    public static void addPlaylistSongsToPlayingQueue(ArrayList<PlaylistSongsModel> songs) {
        mediaController.addMediaItems(MusicUtils.makeMediaItems(songs, "Playlist songs"));
    }
}
