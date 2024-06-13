package com.itsmcodez.echomusic.fragments;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import android.view.ActionMode;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.itsmcodez.echomusic.BaseApplication;
import com.itsmcodez.echomusic.PlayerActivity;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.adapters.ListOfPlaylistAdapter;
import com.itsmcodez.echomusic.adapters.SongsAdapter;
import com.itsmcodez.echomusic.common.MediaItemsQueue;
import com.itsmcodez.echomusic.common.SortOrder;
import com.itsmcodez.echomusic.databinding.FragmentSongsBinding;
import com.itsmcodez.echomusic.databinding.LayoutRecyclerviewBinding;
import com.itsmcodez.echomusic.models.ListOfPlaylistModel;
import com.itsmcodez.echomusic.models.NowPlayingQueueItemsModel;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import com.itsmcodez.echomusic.models.SongsModel;
import com.itsmcodez.echomusic.preferences.Settings;
import com.itsmcodez.echomusic.repositories.PlaylistsRepository;
import com.itsmcodez.echomusic.services.MusicService;
import com.itsmcodez.echomusic.utils.MusicUtils;
import com.itsmcodez.echomusic.utils.ServiceUtils;
import com.itsmcodez.echomusic.viewmodels.SongsViewModel;
import java.util.ArrayList;

public class SongsFragment extends Fragment {
    private FragmentSongsBinding binding;
    private SongsAdapter songsAdapter;
    private SongsViewModel songsViewModel;
    private ActionMode actionMode;
    private ArrayList<SongsModel> songs = new ArrayList<>();
    private MediaController mediaController;
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
        songsViewModel = new ViewModelProvider(this).get(SongsViewModel.class);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Bind to views
        binding = FragmentSongsBinding.inflate(inflater, container, false);
        
        binding.toolbar.addMenuProvider(new MenuProvider(){
                @Override
                public boolean onMenuItemSelected(MenuItem item) {
                    
                    if(item.getItemId() == R.id.sort_desc) {
                        songsViewModel.sortSongs(SortOrder.DESCENDING);
                        Settings.setSongsSortOrder(SortOrder.DESCENDING);
                        item.setChecked(true);
                        return true;
                    }
                    
                    if(item.getItemId() == R.id.sort_asc) {
                        songsViewModel.sortSongs(SortOrder.ASCENDING);
                        Settings.setSongsSortOrder(SortOrder.ASCENDING);
                        item.setChecked(true);
                        return true;
                    }
                    
                    if(item.getItemId() == R.id.sort_size) {
                        songsViewModel.sortSongs(SortOrder.SIZE);
                        Settings.setSongsSortOrder(SortOrder.SIZE);
                        item.setChecked(true);
                        return true;
                    }
                    
                    if(item.getItemId() == R.id.sort_duration) {
                        songsViewModel.sortSongs(SortOrder.DURATION);
                        Settings.setSongsSortOrder(SortOrder.DURATION);
                        item.setChecked(true);
                        return true;
                    }
                    
                    if(item.getItemId() == R.id.sort_default) {
                        songsViewModel.sortSongs(SortOrder.DEFAULT);
                        Settings.setSongsSortOrder(SortOrder.DEFAULT);
                        item.setChecked(true);
                        return true;
                    }
                    
                    return false;
                }
                
                @Override
                public void onCreateMenu(Menu menu, MenuInflater inflater) {
                    inflater.inflate(R.menu.menu_sort_songs, menu);
                    
                    final String SORT_ORDER = Settings.getSongsSortOrder();
                    switch(SORT_ORDER){
                        
                        case "SORT_ASC" : {
                            menu.findItem(R.id.sort_asc).setChecked(true);
                            songsViewModel.sortSongs(SortOrder.ASCENDING);
                        }
                        break;
                        
                        case "SORT_DESC" : {
                            menu.findItem(R.id.sort_desc).setChecked(true);
                            songsViewModel.sortSongs(SortOrder.DESCENDING);
                        }
                        break;
                        
                        case "SORT_SIZE" : {
                            menu.findItem(R.id.sort_size).setChecked(true);
                            songsViewModel.sortSongs(SortOrder.SIZE);
                        }
                        break;
                        
                        case "SORT_DURATION" : {
                            menu.findItem(R.id.sort_duration).setChecked(true);
                            songsViewModel.sortSongs(SortOrder.DURATION);
                        }
                        break;
                        
                        default: {
                            menu.findItem(R.id.sort_default).setChecked(true);
                            songsViewModel.sortSongs(SortOrder.DEFAULT);
                        }
                    }
                }
                
        });
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), RecyclerView.VERTICAL, false));
        
        // Observe LiveData
        songsViewModel.getAllSongs().observe(getViewLifecycleOwner(), new Observer<ArrayList<SongsModel>>() {
                @Override
                public void onChanged(ArrayList<SongsModel> allSongs) {
                    songs = allSongs;
                	songsAdapter = new SongsAdapter(container.getContext(), getLayoutInflater(), allSongs);
                    songsAdapter.setOnPlayNextClickListener((song) -> {
                            mediaController.addMediaItem(mediaController.getCurrentMediaItem() != null ? mediaController.getCurrentMediaItemIndex()+1 : 0, MusicUtils.makeMediaItem(song));
                            if(mediaController.getMediaItemCount() != 0 && mediaController.getCurrentMediaItem() != null) {
                                if(MediaItemsQueue.getNowPlayingQueue().size() == 0) {
                                	MediaItemsQueue.getNowPlayingQueue().add(new NowPlayingQueueItemsModel(song.getTitle()));
                                } else MediaItemsQueue.getNowPlayingQueue().add(mediaController.getCurrentMediaItemIndex()+1, new NowPlayingQueueItemsModel(song.getTitle()));
                            } else MediaItemsQueue.getNowPlayingQueue().add(new NowPlayingQueueItemsModel(song.getTitle()));
                            Toast.makeText(getContext(), getString(R.string.msg_add_song_to_playing_queue_success, song.getTitle()), Toast.LENGTH_SHORT).show();
                    });
                    binding.recyclerView.setAdapter(songsAdapter);
                    
                    songsAdapter.setOnItemClickListener((view, _song, position) -> {
                            SongsModel song = (SongsModel) _song;
                            
                            // If multiple selection is toggled
                            if(songsAdapter.isSelectModeOn()) {
                                songsAdapter.addIndexToSelection(position, view);
                                actionMode.setTitle(String.valueOf(songsAdapter.getSelectedIndices().size() + " selected"));
                                if(songsAdapter.getSelectedIndices().size() == 0) {
                                	actionMode.finish();
                                }
                            	return;
                            }
                            // Update MediaItems
                            mediaController.setMediaItems(MusicUtils.makeMediaItems(allSongs), position, 0);
                            MediaItemsQueue.setNowPlayingQueue(allSongs);
                            startActivity(new Intent(getContext(), PlayerActivity.class));
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
                
                // get selected items (songs)
                ArrayList<SongsModel> selectedSongs = new ArrayList<>();
                var index = 0;
                for(SongsModel song : songs) {
                	if(songsAdapter.getSelectionMap().get(index, false)) {
                		selectedSongs.add(song);
                	}
                    index++;
                }
                
                AlertDialog dialog = new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.add_to_queue)
                .setMessage(getString(R.string.msg_add_songs_to_playing_queue, selectedSongs.size()))
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mode.finish();
                        }
                })
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mediaController.addMediaItems(MusicUtils.makeMediaItems(selectedSongs));
                            for(SongsModel song : selectedSongs) {
                                MediaItemsQueue.getNowPlayingQueue().add(new NowPlayingQueueItemsModel(song.getTitle()));
                            }
                            Toast.makeText(getContext(), getString(R.string.msg_add_songs_to_queue_success, selectedSongs.size()), Toast.LENGTH_SHORT).show();
                            mode.finish();
                        }
                })
                .create();
                dialog.show();
                
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
