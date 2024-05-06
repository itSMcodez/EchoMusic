package com.itsmcodez.echomusic.adapters;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsmcodez.echomusic.BaseApplication;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.callbacks.OnClickEvents;
import com.itsmcodez.echomusic.databinding.LayoutRecyclerviewBinding;
import com.itsmcodez.echomusic.databinding.LayoutSongItemBinding;
import com.itsmcodez.echomusic.markups.Adapter;
import com.itsmcodez.echomusic.models.ListOfPlaylistModel;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import com.itsmcodez.echomusic.models.SongsModel;
import com.itsmcodez.echomusic.repositories.PlaylistsRepository;
import com.itsmcodez.echomusic.utils.MusicUtils;
import java.util.ArrayList;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> implements Adapter {
    private LayoutSongItemBinding binding;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<SongsModel> songs;
    private OnClickEvents.OnItemClickListener onItemClickListener;
    private OnClickEvents.OnItemLongClickListener onItemLongClickListener;

    public SongsAdapter(Context context, LayoutInflater inflater, ArrayList<SongsModel> songs) {
        this.context = context;
        this.inflater = inflater;
        this.songs = songs;
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

    @Override
    public SongsAdapter.SongsViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        // Bind to views
        binding = LayoutSongItemBinding.inflate(inflater, parent, false);
        return new SongsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(SongsAdapter.SongsViewHolder viewHolder, int position) {
        SongsModel song = songs.get(position);
        
        viewHolder.title.setText(song.getTitle());
        viewHolder.artist.setText(song.getArtist());
        Glide.with(context).load(song.getAlbumArtwork()).diskCacheStrategy(DiskCacheStrategy.ALL)
        .error(R.drawable.ic_music_note_outline)
        .into(viewHolder.albumArtwork);
        
        viewHolder.itemView.setOnClickListener(view -> {
                if(onItemClickListener != null) {
                	onItemClickListener.onItemClick(view, song, position);
                }
        });
        
        viewHolder.itemView.setOnLongClickListener(view -> {
                if(onItemLongClickListener != null) {
                	onItemLongClickListener.onItemLongClick(view, song, position);
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
                            	
                                return true;
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
}
