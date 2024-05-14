package com.itsmcodez.echomusic.fragments;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import android.view.ActionMode;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsmcodez.echomusic.BaseApplication;
import com.itsmcodez.echomusic.PlayerActivity;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.adapters.ListOfPlaylistAdapter;
import com.itsmcodez.echomusic.adapters.SongsAdapter;
import com.itsmcodez.echomusic.databinding.FragmentSongsBinding;
import com.itsmcodez.echomusic.databinding.LayoutRecyclerviewBinding;
import com.itsmcodez.echomusic.models.ListOfPlaylistModel;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import com.itsmcodez.echomusic.models.SongsModel;
import com.itsmcodez.echomusic.repositories.PlaylistsRepository;
import com.itsmcodez.echomusic.viewmodels.SongsViewModel;
import java.util.ArrayList;

public class SongsFragment extends Fragment {
    private FragmentSongsBinding binding;
    private SongsAdapter songsAdapter;
    private SongsViewModel songsViewModel;
    private ActionMode actionMode;
    private ArrayList<SongsModel> songs = new ArrayList<>();
    
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
                    songs = allSongs;
                	songsAdapter = new SongsAdapter(container.getContext(), getLayoutInflater(), allSongs);
                    binding.recyclerView.setAdapter(songsAdapter);
                    
                    songsAdapter.setOnItemClickListener((view, _song, position) -> {
                            // If multiple selection is toggled
                            if(songsAdapter.isSelectModeOn()) {
                                songsAdapter.addIndexToSelection(position, view);
                                actionMode.setTitle(String.valueOf(songsAdapter.getSelectedIndices().size() + " selected"));
                                if(songsAdapter.getSelectedIndices().size() == 0) {
                                	actionMode.finish();
                                }
                            	return;
                            }
                            
                            startActivity(new Intent(container.getContext(), PlayerActivity.class));
                    });
                    
                    songsAdapter.setOnItemLongClickListener((view, _song, position) -> {
                            // toggle multiple selection
                            if(!songsAdapter.isSelectModeOn()) {
                            	songsAdapter.toggleSelectionMode(position, true, view);
                            	startActionMode();
                            } else {
                            	songsAdapter.addIndexToSelection(position, view);
                            }
                            actionMode.setTitle(String.valueOf(songsAdapter.getSelectedIndices().size() + " selected"));
                            if(songsAdapter.getSelectedIndices().size() == 0) {
                            	actionMode.finish();
                            }
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
    
    @Override
    public void onDetach() {
        if(actionMode != null) {
        	actionMode.finish();
        }
        super.onDetach();
    }
    
    /* Multiple selection ActionMode logic */
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback(){
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_fragment_songs_multiselect, menu);
            return true;
        }
    
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            
            if(item.getItemId() == R.id.add_songs_to_playlist_menu_item) {
                
                // get selected items (songs)
                ArrayList<SongsModel> selectedSongs = new ArrayList<>();
                var index = 0;
                for(SongsModel song : songs) {
                	if(songsAdapter.getSelectionMap().get(index, false)) {
                		selectedSongs.add(song);
                	}
                    index++;
                }
                
                // List of playlists logic
                ArrayList<ListOfPlaylistModel> listOfPlaylists = new ArrayList<>();
                PlaylistsRepository playlistRepo = PlaylistsRepository.getInstance(BaseApplication.getApplication());
                LayoutRecyclerviewBinding recyclerViewBinding = LayoutRecyclerviewBinding.inflate(getLayoutInflater());
                for(PlaylistsModel playlist : playlistRepo.getPlaylists()) {
                    listOfPlaylists.add(new ListOfPlaylistModel(playlist.getTitle()));
                }
            
                // ListOfPlaylistAdapter and dialog logic
                ListOfPlaylistAdapter adapter = new ListOfPlaylistAdapter(getContext(), getLayoutInflater(), listOfPlaylists);
                recyclerViewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                recyclerViewBinding.recyclerView.setAdapter(adapter);
                AlertDialog dialog = new MaterialAlertDialogBuilder(getContext())
                .setView(recyclerViewBinding.getRoot())
                .setTitle(R.string.choose_playlist_dialog_title)
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                })
                .create();
                dialog.show();
                
                adapter.setOnItemClickListener((view, _playlist, position) -> {
                        ListOfPlaylistModel playlist = (ListOfPlaylistModel) _playlist;
                        ArrayList<PlaylistSongsModel> songs = new ArrayList<>();
                        for(SongsModel song : selectedSongs) {
                            songs.add(new PlaylistSongsModel(song.getPath(), song.getTitle(), 
                                    song.getArtist(), song.getDuration(), 
                                    song.getAlbum(), song.getAlbumId(), 
                                    song.getSongId()));
                        }
                        playlistRepo.addSongsToPlaylistAt(songs, position);
                        mode.finish();
                        dialog.dismiss();
                });
                
            	return true;
            }
            
            if(item.getItemId() == R.id.add_songs_to_queue_menu_item) {
                
            	return true;
            }
            
            return false;
        }
    
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            songsAdapter.clearSelection();
            mode = null;
        }
    };
    
    public void startActionMode() {
    	actionMode = getActivity().startActionMode(actionModeCallback);
    }
}
