package com.itsmcodez.echomusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.callbacks.OnClickEvents;
import com.itsmcodez.echomusic.databinding.LayoutNowPlayingQueueItemBinding;
import com.itsmcodez.echomusic.markups.Adapter;
import com.itsmcodez.echomusic.models.NowPlayingQueueItemsModel;
import java.util.ArrayList;

public class NowPlayingQueueItemsAdapter extends RecyclerView.Adapter<NowPlayingQueueItemsAdapter.NowPlayingQueueItemsViewHolder> implements Adapter {
    private LayoutNowPlayingQueueItemBinding binding;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<NowPlayingQueueItemsModel> songs;
    private OnClickEvents.OnItemClickListener onItemClickListener;

    public NowPlayingQueueItemsAdapter(Context context, LayoutInflater inflater, ArrayList<NowPlayingQueueItemsModel> songs) {
        this.context = context;
        this.inflater = inflater;
        this.songs = songs;
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
}
