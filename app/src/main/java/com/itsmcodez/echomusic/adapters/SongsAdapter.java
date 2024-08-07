package com.itsmcodez.echomusic.adapters;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsmcodez.echomusic.BaseApplication;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.callbacks.OnClickEvents;
import com.itsmcodez.echomusic.common.CurrentSong;
import com.itsmcodez.echomusic.common.OnUpdateCurrentSong;
import com.itsmcodez.echomusic.databinding.LayoutRecyclerviewBinding;
import com.itsmcodez.echomusic.databinding.LayoutSongItemBinding;
import com.itsmcodez.echomusic.markups.Adapter;
import com.itsmcodez.echomusic.markups.Model;
import com.itsmcodez.echomusic.models.ListOfPlaylistModel;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import com.itsmcodez.echomusic.models.SongsModel;
import com.itsmcodez.echomusic.repositories.PlaylistsRepository;
import com.itsmcodez.echomusic.utils.MusicUtils;
import com.itsmcodez.echomusic.utils.PlaylistUtils;
import java.util.ArrayList;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> implements Adapter, 
OnClickEvents.OnMultiSelectListener, OnClickEvents.OnMultiSelectListener.OnSelectChangedListener {
    
    private LayoutSongItemBinding binding;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<SongsModel> songs;
    private ArrayList<String> songTitles;
    private OnClickEvents.OnItemClickListener onItemClickListener;
    private OnClickEvents.OnItemLongClickListener onItemLongClickListener;
    private SparseBooleanArray selectionMap = new SparseBooleanArray();
    private ArrayList<Integer> selectedIndices = new ArrayList<>();
    private boolean isSelectModeOn = false;
    private int selectedIndex = -1;
    private boolean isCurrentSongIndicatorOn = false;
    private String currentlyPlayedSongTitle = "";
    private String recentlyPlayedSongTitle = "";
    private int currentlyPlayedSongPos = -1;
    private int recentlyPlayedSongPos = -1;
    private ColorStateList textColors;
    private CurrentSong currentSong;

    public SongsAdapter(Context context, LayoutInflater inflater, ArrayList<SongsModel> songs) {
        this.context = context;
        this.inflater = inflater;
        this.songs = songs;
        currentSong = CurrentSong.getInstance();
        songTitles = new ArrayList<>();
        for(SongsModel song : songs) {
        	songTitles.add(song.getTitle());
        }
    }

    public static class SongsViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout itemView;
        public ImageView albumArtwork, itemMenu;
        public TextView title, artist;

        public SongsViewHolder(LayoutSongItemBinding binding) {
            super(binding.getRoot());
            this.itemView = binding.itemView;
            this.albumArtwork = binding.albumArtwork;
            this.itemMenu = binding.itemMenu;
            this.title = binding.title;
            this.artist = binding.artist;
        }
    }
    
    public interface OnPlayNextClickListener {
        public void onPlayNextClick(SongsModel song);
    }
    private OnPlayNextClickListener onPlayNextClickListener;

    public void setOnPlayNextClickListener(OnPlayNextClickListener onPlayNextClickListener) {
    	this.onPlayNextClickListener = onPlayNextClickListener;
    }
    
    @Override
    public SongsAdapter.SongsViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        // Bind to views
        binding = LayoutSongItemBinding.inflate(inflater, parent, false);
        return new SongsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(SongsAdapter.SongsViewHolder viewHolder, int position) {
        SongsModel song = songs.get(position);
        
        // Set itemView active state for selection -> change background to grey if selected or otherwise
        viewHolder.itemView.setActivated(selectionMap.get(position, false));
        viewHolder.itemMenu.setVisibility(selectionMap.get(position, false) ? View.GONE : View.VISIBLE);
        
        // Set active state for current song
        if(textColors == null) {
        	textColors = viewHolder.title.getTextColors();
        }
        if(currentSong.getCurrentSong() != null) {
        	if(currentSong.getCurrentSong().mediaMetadata.title.toString().equals(song.getTitle())) {
                onUpdateCurrentSong.onUpdate(song, position);
                viewHolder.title.setTextColor(context.getColor(R.color.colorAccent));
                viewHolder.artist.setTextColor(context.getColor(R.color.colorAccent));
            } else {
                if(textColors != null) {
                    viewHolder.title.setTextColor(textColors);
                    viewHolder.artist.setTextColor(textColors);
                }
            }
        }
        
        viewHolder.title.setText(song.getTitle());
        viewHolder.artist.setText(song.getArtist());
        Glide.with(context).load(song.getAlbumArtwork()).diskCacheStrategy(DiskCacheStrategy.ALL)
        .error(R.drawable.ic_music_note_outline)
        .into(viewHolder.albumArtwork);
        
        viewHolder.itemView.setOnClickListener(view -> {
                if(onItemClickListener != null) {
                	onItemClickListener.onItemClick(view, song, position);
                    if(isSelectModeOn) {
                    	viewHolder.itemMenu.setVisibility(selectionMap.get(position, false) ? View.GONE : View.VISIBLE);
                    }
                }
        });
        
        viewHolder.itemView.setOnLongClickListener(view -> {
                if(onItemLongClickListener != null) {
                	onItemLongClickListener.onItemLongClick(view, song, position);
                    viewHolder.itemMenu.setVisibility(selectionMap.get(position, false) ? View.GONE : View.VISIBLE);
                }
                return true;
        });
        
        viewHolder.itemMenu.setOnClickListener(view -> {
                PopupMenu menu = new PopupMenu(context, view);
                menu.inflate(R.menu.menu_song_item);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            
                            if(item.getItemId() == R.id.play_next_menu_item) {
                            	if(onPlayNextClickListener != null) {
                            		onPlayNextClickListener.onPlayNextClick(song);
                            	}
                                return true;
                            }
                            
                            if(item.getItemId() == R.id.add_to_fav_menu_item) {
                                PlaylistSongsModel playlistSong = new PlaylistSongsModel(song.getPath(), song.getTitle(),
                                     song.getArtist(), song.getDuration(), song.getAlbum(), song.getAlbumId(), song.getSongId());
                                MusicUtils.addSongToFavourites(playlistSong);
                            }
                            
                            if(item.getItemId() == R.id.add_to_playlist_menu_item) {
                                // List of playlists logic
                                ArrayList<ListOfPlaylistModel> listOfPlaylists = new ArrayList<>();
                                PlaylistsRepository playlistRepo = PlaylistsRepository.getInstance(BaseApplication.getApplication());
                                LayoutRecyclerviewBinding recyclerViewBinding = LayoutRecyclerviewBinding.inflate(inflater);
                                for(PlaylistsModel playlist : playlistRepo.getPlaylists()) {
                                	listOfPlaylists.add(new ListOfPlaylistModel(playlist.getTitle()));
                                }
                                
                                // ListOfPlaylistAdapter and dialog logic
                            	ListOfPlaylistAdapter adapter = new ListOfPlaylistAdapter(context, inflater, listOfPlaylists);
                                recyclerViewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
                                recyclerViewBinding.recyclerView.setAdapter(adapter);
                                AlertDialog dialog = new MaterialAlertDialogBuilder(context)
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
                                        PlaylistSongsModel playlistSong = new PlaylistSongsModel(song.getPath(), song.getTitle(),
                                            song.getArtist(), song.getDuration(), song.getAlbum(), song.getAlbumId(), song.getSongId());
                                        playlistRepo.addSongToPlaylistAt(playlistSong, position);
                                        dialog.dismiss();
                                });
                                
                                return true;
                            }
                            
                            if(item.getItemId() == R.id.edit_metadata_menu_item) {
                            	
                                return true;
                            }
                            
                            if(item.getItemId() == R.id.set_ringtone_menu_item) {
                            	
                                return true;
                            }
                            
                            if(item.getItemId() == R.id.share_menu_item) {
                            	
                                return true;
                            }
                            
                            if(item.getItemId() == R.id.delete_song_menu_item) {
                            	
                                return true;
                            }
                            
                            if(item.getItemId() == R.id.song_details_menu_item) {
                            	
                                AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                                .setTitle(R.string.details)
                                .setMessage(context.getString(R.string.song_details, song.getTitle(), song.getPath(), song.getArtist(), song.getAlbum(), MusicUtils.getReadableDuration(Long.parseLong(song.getDuration()))))
                                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                })
                                .create();
                                dialog.show();
                                
                                return true;
                            }
                            
                            return false;
                        }
                });
                menu.show();
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
    
    public void setOnItemClickListener(OnClickEvents.OnItemClickListener onItemClickListener) {
    	this.onItemClickListener = onItemClickListener;
    }
    
    public void setOnItemLongClickListener(OnClickEvents.OnItemLongClickListener onItemLongClickListener) {
    	this.onItemLongClickListener = onItemLongClickListener;
    }
    
    public boolean isSelectModeOn() {
    	return this.isSelectModeOn;
    }
    
    public boolean isCurrentSongIndicatorOn() {
    	return this.isCurrentSongIndicatorOn;
    }
    
    /* Now Playing Logic */
    private void showCurrentSongIndicator(SongsModel song, int position) {
    	if(!isCurrentSongIndicatorOn) {
            isCurrentSongIndicatorOn = true;
        }
        if(currentlyPlayedSongPos == -1) {
            currentlyPlayedSongPos = position;
        } else if(currentlyPlayedSongPos != -1 && currentlyPlayedSongPos != position) {
            recentlyPlayedSongPos = currentlyPlayedSongPos;
            currentlyPlayedSongPos = position;
        }
        if(currentlyPlayedSongTitle.isEmpty()) {
            currentlyPlayedSongTitle = song.getTitle();
        } else if(!currentlyPlayedSongTitle.isEmpty() && !currentlyPlayedSongTitle.equals(song.getTitle())) {
            recentlyPlayedSongTitle = currentlyPlayedSongTitle;
            currentlyPlayedSongTitle = song.getTitle();
        }
    }
    
    public int getCurrentlyPlayedSongPos() {
    	return this.currentlyPlayedSongPos;
    }
    
    public OnUpdateCurrentSong onUpdateCurrentSong = new OnUpdateCurrentSong() {
        
        @Override
        public void onUpdate(Model _song, int position) {
            SongsModel song = (SongsModel) _song;
            if(currentSong.getCurrentSong() != null) {
                if(!isCurrentSongIndicatorOn) {
                    isCurrentSongIndicatorOn = true;
                }
                showCurrentSongIndicator(song, position);
            }
        }
        
        @Override
        public void updateCurrentSong(MediaItem song) {
            isCurrentSongIndicatorOn = true;
            currentSong.setCurrentSong(song);
            String songTitle = currentSong.getCurrentSong().mediaMetadata.title.toString();
            if(songTitles.contains(songTitle)) {
                int songPos = songTitles.indexOf(songTitle);
                // make sure current song position is updated
                currentlyPlayedSongPos = songPos;
                notifyItemChanged(songPos);
                notifyDataSetChanged();
            }
        }
        
    };
    
    /* Multiple selection logic */
    @Override
    public ArrayList<Integer> getSelectedIndices() {
        return selectedIndices;
    }
    
    @Override
    public SparseBooleanArray getSelectionMap() {
        return selectionMap;
    }
    
    @Override
    public void addIndexToSelection(int index, View view) {
        // Check if index already exists or not
        if(selectionMap.get(index, false)) {
            // if index exists then remove it
            selectedIndex = selectionMap.indexOfKey(index);
            selectionMap.delete(index);
            onSelectChanged(index, false, view);
        } else {
            // if index doesn't exist then add it
            selectionMap.put(index, true);
            selectedIndex = selectionMap.indexOfKey(index);
            onSelectChanged(index, true, view);
        }
    }
    
    
    @Override
    public void toggleSelectionMode(int index, boolean isSelectModeOn, View view) {
        this.isSelectModeOn = isSelectModeOn;
        if(isSelectModeOn) {
        	addIndexToSelection(index, view);
        }
    }
    
    
    @Override
    public void onSelectChanged(int index, boolean isActive, View view) {
        
        if(isActive) {
        	selectedIndices.add(selectedIndex, index);
            // Set itemView active state for selection -> change background to grey if selected or otherwise
            view.setActivated(isActive);
        } else {
            if(selectedIndices.contains(index)) {
                selectedIndices.remove(selectedIndex);
            }
            // Set itemView active state for selection -> change background to grey if selected or otherwise
            view.setActivated(false);
        }
        isSelectModeOn = selectedIndices.size() != 0 ? true : false;
    }
    
    @Override
    public void clearSelection() {
    	isSelectModeOn = false;
        selectedIndex = -1;
        selectedIndices.clear();
        selectionMap.clear();
        notifyDataSetChanged();
    }
}
