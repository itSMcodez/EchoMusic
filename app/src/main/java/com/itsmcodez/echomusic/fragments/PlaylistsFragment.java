package com.itsmcodez.echomusic.fragments;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.adapters.PlaylistsAdapter;
import com.itsmcodez.echomusic.databinding.FragmentPlaylistsBinding;
import com.itsmcodez.echomusic.databinding.LayoutMaterialTextinputBinding;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import com.itsmcodez.echomusic.viewmodels.PlaylistsViewModel;
import java.util.ArrayList;

public class PlaylistsFragment extends Fragment {
    private FragmentPlaylistsBinding binding;
    private PlaylistsAdapter playlistsAdapter;
    private static PlaylistsViewModel playlistsViewModel;
    
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
        binding.addPlaylistBt.setOnClickListener(view -> {
                showAddPlaylistDialog(container.getContext());
        });
        
        playlistsViewModel.getAllPlaylists().observe(getViewLifecycleOwner(), new Observer<ArrayList<PlaylistsModel>>(){
                @Override
                public void onChanged(ArrayList<PlaylistsModel> allPlaylists) {
                	playlistsAdapter = new PlaylistsAdapter(container.getContext(), getLayoutInflater(), allPlaylists);
                    binding.recyclerView.setAdapter(playlistsAdapter);
                }
        });
        
        return binding.getRoot();
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
                        PlaylistsModel newPlaylist = new PlaylistsModel(input, null, 0, 0);
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
}
