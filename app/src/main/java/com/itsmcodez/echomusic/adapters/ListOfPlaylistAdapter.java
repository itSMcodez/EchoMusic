package com.itsmcodez.echomusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.itsmcodez.echomusic.databinding.LayoutListOfPlaylistBinding;
import com.itsmcodez.echomusic.models.ListOfPlaylistModel;
import java.util.ArrayList;

public class ListOfPlaylistAdapter extends RecyclerView.Adapter<ListOfPlaylistAdapter.ListOfPlaylistViewHolder> {
    private LayoutListOfPlaylistBinding binding;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ListOfPlaylistModel> playlists;
    private OnItemClickListener onItemClickListener;

    public ListOfPlaylistAdapter(Context context, LayoutInflater inflater, ArrayList<ListOfPlaylistModel> playlists) {
        this.context = context;
        this.inflater = inflater;
        this.playlists = playlists;
    }

    public static class ListOfPlaylistViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public LinearLayout itemView;

        public ListOfPlaylistViewHolder(LayoutListOfPlaylistBinding binding) {
            super(binding.getRoot());
            this.title = binding.title;
            this.itemView = binding.itemView;
        }
    }

    @Override
    public ListOfPlaylistAdapter.ListOfPlaylistViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        // Bind to views
        binding = LayoutListOfPlaylistBinding.inflate(inflater, parent, false);
        return new ListOfPlaylistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ListOfPlaylistViewHolder viewHolder, int position) {
        ListOfPlaylistModel playlist = playlists.get(position);
        viewHolder.title.setText(playlist.getTitle());
        
        viewHolder.itemView.setOnClickListener(view -> {
                if(onItemClickListener != null) {
                	onItemClickListener.onItemClick(view, playlist, position);
                }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }
    
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    	this.onItemClickListener = onItemClickListener;
    }
    
    @FunctionalInterface
    public interface OnItemClickListener {
        public void onItemClick(View view, ListOfPlaylistModel playlist, int position);
    }
}
