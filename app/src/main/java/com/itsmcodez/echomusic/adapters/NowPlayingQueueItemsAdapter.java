package com.itsmcodez.echomusic.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.callbacks.OnClickEvents;
import com.itsmcodez.echomusic.common.CurrentSong;
import com.itsmcodez.echomusic.common.OnUpdateCurrentSong;
import com.itsmcodez.echomusic.databinding.LayoutNowPlayingQueueItemBinding;
import com.itsmcodez.echomusic.markups.Adapter;
import com.itsmcodez.echomusic.markups.Model;
import com.itsmcodez.echomusic.models.NowPlayingQueueItemsModel;
import java.util.ArrayList;

public class NowPlayingQueueItemsAdapter extends RecyclerView.Adapter<NowPlayingQueueItemsAdapter.NowPlayingQueueItemsViewHolder> implements Adapter {
    private LayoutNowPlayingQueueItemBinding binding;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<NowPlayingQueueItemsModel> songs;
    private ArrayList<String> songTitles;
    private OnClickEvents.OnItemClickListener onItemClickListener;
    private boolean isCurrentSongIndicatorOn = false;
    private String currentlyPlayedSongTitle = "";
    private String recentlyPlayedSongTitle = "";
    private int currentlyPlayedSongPos = -1;
    private int recentlyPlayedSongPos = -1;
    private ColorStateList textColors;
    private CurrentSong currentSong;
    
    public NowPlayingQueueItemsAdapter(Context context, LayoutInflater inflater, ArrayList<NowPlayingQueueItemsModel> songs) {
        this.context = context;
        this.inflater = inflater;
        this.songs = songs;
        currentSong = CurrentSong.getInstance();
        songTitles = new ArrayList<>();
        for(NowPlayingQueueItemsModel song : songs) {
        	songTitles.add(song.getTitle());
        }
    }

    public static class NowPlayingQueueItemsViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout itemView;
        public TextView title;
        public ImageView swapItemPosition;
        public ImageView removeItem;

        public NowPlayingQueueItemsViewHolder(LayoutNowPlayingQueueItemBinding binding) {
            super(binding.getRoot());
            this.itemView = binding.itemView;
            this.title = binding.title;
            this.swapItemPosition = binding.swapItemPosition;
            this.removeItem = binding.removeItem;
        }
    }
    
    public interface OnRemoveItemClickListener {
        public void onRemoveItemClick(int position);
    }
    private OnRemoveItemClickListener onRemoveItemClickListener;
    
    public void setOnRemoveItemClickListener(OnRemoveItemClickListener onRemoveItemClickListener) {
    	this.onRemoveItemClickListener = onRemoveItemClickListener;
    }

    @Override
    public NowPlayingQueueItemsAdapter.NowPlayingQueueItemsViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        // Bind to views
        binding = LayoutNowPlayingQueueItemBinding.inflate(inflater, parent, false);
        return new NowPlayingQueueItemsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(NowPlayingQueueItemsAdapter.NowPlayingQueueItemsViewHolder viewHolder, int position) {
        NowPlayingQueueItemsModel song = songs.get(position);
        
        // Set active state for current song
        if(textColors == null) {
        	textColors = viewHolder.title.getTextColors();
        }
        if(currentSong.getCurrentSong() != null) {
        	if(currentSong.getCurrentSong().mediaMetadata.title.toString().equals(song.getTitle())) {
                onUpdateCurrentSong.onUpdate(song, position);
                viewHolder.title.setTextColor(context.getColor(R.color.colorAccent));
            } else {
                if(textColors != null) {
                    viewHolder.title.setTextColor(textColors);
                }
            }
        }
        
        viewHolder.title.setText(song.getTitle());
        
        viewHolder.itemView.setOnClickListener(view -> {
                if(onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, song, position);
                }
        });
        
        viewHolder.itemView.setOnLongClickListener(view -> {
                Toast.makeText(context, R.string.msg_swap_song_position, Toast.LENGTH_LONG).show();
                return true;
        });
        
        viewHolder.removeItem.setOnClickListener(view -> {
                if(onRemoveItemClickListener != null) {
                	onRemoveItemClickListener.onRemoveItemClick(position);
                }
        });
        
        viewHolder.swapItemPosition.setOnClickListener(view -> {
                Toast.makeText(context, R.string.msg_swap_item_position_help, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
    
    public void setOnItemClickListener(OnClickEvents.OnItemClickListener onItemClickListener) {
    	this.onItemClickListener = onItemClickListener;
    }
    
    /* Now Playing Logic */
    private void showCurrentSongIndicator(NowPlayingQueueItemsModel song, int position) {
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
    
    public OnUpdateCurrentSong onUpdateCurrentSong = new OnUpdateCurrentSong() {
        
        @Override
        public void onUpdate(Model _song, int position) {
            NowPlayingQueueItemsModel song = (NowPlayingQueueItemsModel) _song;
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
                notifyItemChanged(songPos);
                notifyDataSetChanged();
            }
        }
        
    };
}
