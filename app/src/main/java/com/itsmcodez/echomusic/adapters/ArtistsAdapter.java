package com.itsmcodez.echomusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.itsmcodez.echomusic.callbacks.OnClickEvents;
import com.itsmcodez.echomusic.databinding.LayoutArtistItemBinding;
import com.itsmcodez.echomusic.markups.Adapter;
import com.itsmcodez.echomusic.models.ArtistsModel;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistsViewHolder> implements Adapter {
    private LayoutArtistItemBinding binding;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ArtistsModel> artists;
    private OnClickEvents.OnItemClickListener onItemClickListener;

    public ArtistsAdapter(Context context, LayoutInflater inflater, ArrayList<ArtistsModel> artists) {
        this.context = context;
        this.inflater = inflater;
        this.artists = artists;
    }

    public static class ArtistsViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout itemView;
        public CircleImageView albumArtwork;
        public TextView title;

        public ArtistsViewHolder(LayoutArtistItemBinding binding) {
            super(binding.getRoot());
            this.itemView = binding.itemView;
            this.albumArtwork = binding.albumArtwork;
            this.title = binding.title;
        }
    }

    @Override
    public ArtistsAdapter.ArtistsViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        // Bind to views
        binding = LayoutArtistItemBinding.inflate(inflater, parent, false);
        return new ArtistsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ArtistsAdapter.ArtistsViewHolder viewHolder, int position) {
        ArtistsModel artist = artists.get(position);
        
        viewHolder.title.setText(artist.getArtist());
        
        viewHolder.itemView.setOnClickListener(view -> {
                if(onItemClickListener != null) {
                	onItemClickListener.onItemClick(view, artist, position);
                }
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }
    
    public void setOnItemClickListener(OnClickEvents.OnItemClickListener onItemClickListener) {
    	this.onItemClickListener = onItemClickListener;
    }
}
