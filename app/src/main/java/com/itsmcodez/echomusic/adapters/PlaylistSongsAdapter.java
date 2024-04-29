package com.itsmcodez.echomusic.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.itsmcodez.echomusic.PlaylistSongsActivity;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.databinding.LayoutPlaylistSongItemBinding;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import java.util.ArrayList;

public class PlaylistSongsAdapter extends RecyclerView.Adapter<PlaylistSongsAdapter.PlaylistSongsViewHolder> {
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
