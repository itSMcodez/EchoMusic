package com.itsmcodez.echomusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.databinding.LayoutAlbumItemBinding;
import com.itsmcodez.echomusic.models.AlbumsModel;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder> {
    private LayoutAlbumItemBinding binding;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<AlbumsModel> albums;

    public AlbumsAdapter(Context context, LayoutInflater inflater, ArrayList<AlbumsModel> albums) {
        this.context = context;
        this.inflater = inflater;
        this.albums = albums;
    }

    public static class AlbumsViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout itemView;
        public CircleImageView albumArtwork;
        public TextView title;

        public AlbumsViewHolder(LayoutAlbumItemBinding binding) {
            super(binding.getRoot());
            this.itemView = binding.itemView;
            this.albumArtwork = binding.albumArtwork;
            this.title = binding.title;
        }
    }

    @Override
    public AlbumsAdapter.AlbumsViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        // Bind to views
        binding = LayoutAlbumItemBinding.inflate(inflater, parent, false);
        return new AlbumsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(AlbumsAdapter.AlbumsViewHolder viewHolder, int position) {
        AlbumsModel album = albums.get(position);
        
        viewHolder.title.setText(album.getAlbum());
        viewHolder.albumArtwork.setImageURI(album.getAlbumArtwork());
        if(viewHolder.albumArtwork.getDrawable() == null) {
        	viewHolder.albumArtwork.setImageDrawable(context.getDrawable(R.drawable.ic_album_artwork));
        }
        
        viewHolder.itemView.setOnClickListener(view -> {
                
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }
}
