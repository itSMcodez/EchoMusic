package com.itsmcodez.echomusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.databinding.LayoutSongItemBinding;
import com.itsmcodez.echomusic.models.SongsModel;
import java.util.ArrayList;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {
    private LayoutSongItemBinding binding;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<SongsModel> songs;

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
        viewHolder.albumArtwork.setImageURI(song.getAlbumArtwork());
        if(viewHolder.albumArtwork.getDrawable() == null) {
        	viewHolder.albumArtwork.setImageDrawable(context.getDrawable(R.drawable.ic_music_note_outline));
        }
        
        viewHolder.itemView.setOnClickListener(view -> {
                
        });
        
        viewHolder.itemMenu.setOnClickListener(view -> {
                
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
}