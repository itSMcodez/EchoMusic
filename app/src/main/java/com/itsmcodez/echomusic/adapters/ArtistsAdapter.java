package com.itsmcodez.echomusic.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.callbacks.OnClickEvents;
import com.itsmcodez.echomusic.common.CurrentSong;
import com.itsmcodez.echomusic.databinding.LayoutArtistItemBinding;
import com.itsmcodez.echomusic.markups.Adapter;
import com.itsmcodez.echomusic.markups.Model;
import com.itsmcodez.echomusic.models.AlbumsModel;
import com.itsmcodez.echomusic.common.OnUpdateCurrentSong;
import com.itsmcodez.echomusic.models.ArtistsModel;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistsViewHolder> implements Adapter {
    private LayoutArtistItemBinding binding;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ArtistsModel> artists;
    private ArrayList<String> artistTitles;
    private OnClickEvents.OnItemClickListener onItemClickListener;
    private boolean isCurrentSongIndicatorOn = false;
    private String currentlyPlayedSongArtist = "";
    private String recentlyPlayedSongArtist = "";
    private int currentlyPlayedSongArtistPos = -1;
    private int recentlyPlayedSongArtistPos = -1;
    private ColorStateList textColors;
    private CurrentSong currentSong;

    public ArtistsAdapter(Context context, LayoutInflater inflater, ArrayList<ArtistsModel> artists) {
        this.context = context;
        this.inflater = inflater;
        this.artists = artists;
        currentSong = CurrentSong.getInstance();
        artistTitles = new ArrayList<>();
        for(ArtistsModel artist : artists) {
        	artistTitles.add(artist.getArtist());
        }
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
        
        // Set active state for current song
        if(textColors == null) {
        	textColors = viewHolder.title.getTextColors();
        }
        if(currentSong.getCurrentSong() != null) {
        	if(currentSong.getCurrentSong().mediaMetadata.artist.toString().equals(artist.getArtist())) {
                onUpdateCurrentSong.onUpdate(artist, position);
                viewHolder.title.setTextColor(context.getColor(R.color.colorAccent));
            } else {
                if(textColors != null) {
                    viewHolder.title.setTextColor(textColors);
                }
            }
        }
        
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
    
    /* Now Playing Logic */
    private void showCurrentSongIndicator(ArtistsModel artist, int position) {
    	if(!isCurrentSongIndicatorOn) {
            isCurrentSongIndicatorOn = true;
        }
        if(currentlyPlayedSongArtistPos == -1) {
            currentlyPlayedSongArtistPos = position;
        } else if(currentlyPlayedSongArtistPos != -1 && currentlyPlayedSongArtistPos != position) {
            recentlyPlayedSongArtistPos = currentlyPlayedSongArtistPos;
            currentlyPlayedSongArtistPos = position;
        }
        if(currentlyPlayedSongArtist.isEmpty()) {
            currentlyPlayedSongArtist = artist.getArtist();
        } else if(!currentlyPlayedSongArtist.isEmpty() && !currentlyPlayedSongArtist.equals(artist.getArtist())) {
            recentlyPlayedSongArtist = currentlyPlayedSongArtist;
            currentlyPlayedSongArtist = artist.getArtist();
        }
    }
    
    public OnUpdateCurrentSong onUpdateCurrentSong = new OnUpdateCurrentSong() {
        
        @Override
        public void onUpdate(Model _artist, int position) {
            ArtistsModel artist = (ArtistsModel) _artist;
            if(currentSong.getCurrentSong() != null) {
                if(!isCurrentSongIndicatorOn) {
                    isCurrentSongIndicatorOn = true;
                }
                showCurrentSongIndicator(artist, position);
            }
        }
        
        @Override
        public void updateCurrentSong(MediaItem song) {
            isCurrentSongIndicatorOn = true;
            currentSong.setCurrentSong(song);
            String artistTitle = currentSong.getCurrentSong().mediaMetadata.artist.toString();
            if(artistTitles.contains(artistTitle)) {
                int artistPos = artistTitles.indexOf(artistTitle);
                notifyItemChanged(artistPos);
                notifyDataSetChanged();
            }
        }
        
    };
}
