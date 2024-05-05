package com.itsmcodez.echomusic.adapters;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsmcodez.echomusic.BaseApplication;
import com.itsmcodez.echomusic.PlaylistSongsActivity;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.databinding.LayoutPlaylistSongItemBinding;
import com.itsmcodez.echomusic.databinding.LayoutRecyclerviewBinding;
import com.itsmcodez.echomusic.markups.Adapter;
import com.itsmcodez.echomusic.models.ListOfPlaylistModel;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import com.itsmcodez.echomusic.repositories.PlaylistsRepository;
import java.util.ArrayList;

public class PlaylistSongsAdapter extends RecyclerView.Adapter<PlaylistSongsAdapter.PlaylistSongsViewHolder> implements Adapter {
    private LayoutPlaylistSongItemBinding binding;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<PlaylistSongsModel> songs;
    private int playlistPosition;

    public PlaylistSongsAdapter(Context context, LayoutInflater inflater, int playlistPosition, ArrayList<PlaylistSongsModel> songs) {
        this.context = context;
        this.inflater = inflater;
        this.songs = songs;
        this.playlistPosition = playlistPosition;
    }

    public static class PlaylistSongsViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout itemView;
        public TextView title;
        public ImageView itemMenu, swapItemPosition;

        public PlaylistSongsViewHolder(LayoutPlaylistSongItemBinding binding) {
            super(binding.getRoot());
            this.itemView = binding.itemView;
            this.itemMenu = binding.itemMenu;
            this.title = binding.title;
            this.swapItemPosition = binding.swapItemPosition;
        }
    }

    @Override
    public PlaylistSongsAdapter.PlaylistSongsViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        // Bind to views
        binding = LayoutPlaylistSongItemBinding.inflate(inflater, parent, false);
        return new PlaylistSongsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(PlaylistSongsViewHolder viewHolder, final int position) {
        PlaylistSongsModel song = songs.get(position);
        
        viewHolder.title.setText(" - " + song.getTitle());
        
        viewHolder.itemView.setOnClickListener(view -> {
                
        });
        
        viewHolder.itemView.setOnLongClickListener(view -> {
                Toast.makeText(context, R.string.msg_swap_song_position, Toast.LENGTH_LONG).show();
                return true;
        });
        
        viewHolder.swapItemPosition.setOnClickListener(view -> {
                Toast.makeText(context, R.string.msg_swap_item_position_help, Toast.LENGTH_LONG).show();
        });
        
        viewHolder.swapItemPosition.setOnLongClickListener(view -> {
                Toast.makeText(context, R.string.msg_swap_song_position, Toast.LENGTH_LONG).show();
                return true;
        });
        
        viewHolder.itemMenu.setOnClickListener(view -> {
                PopupMenu menu = new PopupMenu(context, view);
                menu.inflate(R.menu.menu_playlist_song_item);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            
                            if(item.getItemId() == R.id.remove_song_menu_item) {
                            	Log.d("RemoveSong:", "Removed " + song.getTitle());
                                // Remove song at position (songPosition) from playlist at position (playlistPosition)
                                int songPosition = position;
                                PlaylistSongsActivity.removeSongFromPlaylistAt(songPosition, playlistPosition);
                                // Update adapter's contents after song item removal
                                songs.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                                
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
}
