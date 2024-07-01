package com.itsmcodez.echomusic.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.callbacks.OnClickEvents;
import com.itsmcodez.echomusic.common.CurrentSong;
import com.itsmcodez.echomusic.common.OnUpdateCurrentSong;
import com.itsmcodez.echomusic.databinding.LayoutAlbumItemBinding;
import com.itsmcodez.echomusic.markups.Adapter;
import com.itsmcodez.echomusic.markups.Model;
import com.itsmcodez.echomusic.models.AlbumsModel;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder> implements Adapter {
    private LayoutAlbumItemBinding binding;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<AlbumsModel> albums;
    private ArrayList<String> albumTitles;
    private OnClickEvents.OnItemClickListener onItemClickListener;
    private boolean isCurrentSongIndicatorOn = false;
    private String currentlyPlayedSongAlbum = "";
    private String recentlyPlayedSongAlbum = "";
    private int currentlyPlayedSongAlbumPos = -1;
    private int recentlyPlayedSongAlbumPos = -1;
    private ColorStateList textColors;
    private CurrentSong currentSong;
    
    public AlbumsAdapter(Context context, LayoutInflater inflater, ArrayList<AlbumsModel> albums) {
        this.context = context;
        this.inflater = inflater;
        this.albums = albums;
        currentSong = CurrentSong.getInstance();
        albumTitles = new ArrayList<>();
        for(AlbumsModel album : albums) {
        	albumTitles.add(album.getAlbum());
        }
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
        
        // Set active state for current song
        if(textColors == null) {
        	textColors = viewHolder.title.getTextColors();
        }
        if(currentSong.getCurrentSong() != null) {
        	if(currentSong.getCurrentSong().mediaMetadata.albumTitle.toString().equals(album.getAlbum())) {
                onUpdateCurrentSong.onUpdate(album, position);
                viewHolder.title.setTextColor(context.getColor(R.color.colorAccent));
            } else {
                if(textColors != null) {
                    viewHolder.title.setTextColor(textColors);
                }
            }
        }
        
        viewHolder.title.setText(album.getAlbum());
        Glide.with(context).load(album.getAlbumArtwork()).diskCacheStrategy(DiskCacheStrategy.ALL)
        .error(R.drawable.ic_album_artwork)
        .into(viewHolder.albumArtwork);
        
        viewHolder.itemView.setOnClickListener(view -> {
                if(onItemClickListener != null) {
                	onItemClickListener.onItemClick(view, album, position);
                }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }
    
    public void setOnItemClickListener(OnClickEvents.OnItemClickListener onItemClickListener) {
    	this.onItemClickListener = onItemClickListener;
    }
    
    /* Now Playing Logic */
    private void showCurrentSongIndicator(AlbumsModel album, int position) {
    	if(!isCurrentSongIndicatorOn) {
            isCurrentSongIndicatorOn = true;
        }
        if(currentlyPlayedSongAlbumPos == -1) {
            currentlyPlayedSongAlbumPos = position;
        } else if(currentlyPlayedSongAlbumPos != -1 && currentlyPlayedSongAlbumPos != position) {
            recentlyPlayedSongAlbumPos = currentlyPlayedSongAlbumPos;
            currentlyPlayedSongAlbumPos = position;
        }
        if(currentlyPlayedSongAlbum.isEmpty()) {
            currentlyPlayedSongAlbum = album.getAlbum();
        } else if(!currentlyPlayedSongAlbum.isEmpty() && !currentlyPlayedSongAlbum.equals(album.getAlbum())) {
            recentlyPlayedSongAlbum = currentlyPlayedSongAlbum;
            currentlyPlayedSongAlbum = album.getAlbum();
        }
    }
    
    public OnUpdateCurrentSong onUpdateCurrentSong = new OnUpdateCurrentSong() {
        
        @Override
        public void onUpdate(Model _album, int position) {
            AlbumsModel album = (AlbumsModel) _album;
            if(currentSong.getCurrentSong() != null) {
                if(!isCurrentSongIndicatorOn) {
                    isCurrentSongIndicatorOn = true;
                }
                showCurrentSongIndicator(album, position);
            }
        }
        
        @Override
        public void updateCurrentSong(MediaItem song) {
            isCurrentSongIndicatorOn = true;
            currentSong.setCurrentSong(song);
            String albumTitle = currentSong.getCurrentSong().mediaMetadata.albumTitle.toString();
            if(albumTitles.contains(albumTitle)) {
                int albumPos = albumTitles.indexOf(albumTitle);
                notifyItemChanged(albumPos);
                notifyDataSetChanged();
            }
        }
        
    };
}
