package com.itsmcodez.echomusic.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.databinding.LayoutMaterialTextinputBinding;
import com.itsmcodez.echomusic.databinding.LayoutPlaylistItemBinding;
import com.itsmcodez.echomusic.fragments.PlaylistsFragment;
import com.itsmcodez.echomusic.models.PlaylistsModel;
import com.itsmcodez.echomusic.utils.ArtworkUtils;
import com.itsmcodez.echomusic.utils.MusicUtils;
import java.util.ArrayList;

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.PlaylistsViewHolder> {
    private LayoutPlaylistItemBinding binding;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<PlaylistsModel> playlists;
    private OnItemClickListener onItemClickListener;

    public PlaylistsAdapter(Context context, LayoutInflater inflater, ArrayList<PlaylistsModel> playlists) {
        this.context = context;
        this.inflater = inflater;
        this.playlists = playlists;
    }

    public static class PlaylistsViewHolder extends RecyclerView.ViewHolder {
        public CardView itemView;
        public ImageView albumArtwork, itemMenu;
        public TextView title, info;

        public PlaylistsViewHolder(LayoutPlaylistItemBinding binding) {
            super(binding.getRoot());
            this.itemView = binding.itemView;
            this.albumArtwork = binding.albumArtwork;
            this.itemMenu = binding.itemMenu;
            this.title = binding.title;
            this.info = binding.info;
        }
    }

    @Override
    public PlaylistsAdapter.PlaylistsViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        // Bind to views
        binding = LayoutPlaylistItemBinding.inflate(inflater, parent, false);
        return new PlaylistsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(PlaylistsAdapter.PlaylistsViewHolder viewHolder, int position) {
        PlaylistsModel playlist = playlists.get(position);
        
        viewHolder.title.setText(playlist.getTitle());
        if(playlist.getSongs() != null && playlist.getSongs().size() != 0) {
            var playlistDuration = playlist.getTotalDuration();
            var songsCount = playlist.getSongCount();
            viewHolder.info.setText(context.getResources().getQuantityString(R.plurals.playlist_songs_count, songsCount, songsCount) + MusicUtils.getReadableDuration(playlistDuration));
            viewHolder.albumArtwork.setImageURI(ArtworkUtils.getArtworkFrom(Long.parseLong(playlist.getSongs().get(playlist.getSongs().size() - 1).getAlbumId())));
            if(viewHolder.albumArtwork.getDrawable() == null) {
            	viewHolder.albumArtwork.setImageDrawable(context.getDrawable(R.drawable.ic_library_music_outline));
            }
        } else {
            var songsCount = 0;
            viewHolder.info.setText(context.getResources().getQuantityString(R.plurals.playlist_songs_count, songsCount, songsCount) + "00:00");
            viewHolder.albumArtwork.setImageDrawable(context.getDrawable(R.drawable.ic_library_music_outline));
        }
        
        viewHolder.itemView.setOnClickListener(view -> {
                if(onItemClickListener != null) {
                	onItemClickListener.onItemClick(view, playlist, position);
                }
        });
        
        viewHolder.itemMenu.setOnClickListener(view -> {
                PopupMenu menu = new PopupMenu(context, view);
                menu.inflate(R.menu.menu_playlist_item);
                // Check and restrict user from deleting Favourites playlist item
                if(playlist.getTitle().equals("Favourites") && position == 0) {
                	MenuItem deleteMenuItem = menu.getMenu().findItem(R.id.delete_playlist_menu_item);
                    MenuItem renameMenuItem = menu.getMenu().findItem(R.id.rename_playlist_menu_item);
                    deleteMenuItem.setEnabled(false);
                    renameMenuItem.setEnabled(false);
                }
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            
                            if(item.getItemId() == R.id.delete_playlist_menu_item) {
                                
                                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context)
                                .setTitle(R.string.delete_playlist_confirmation_dialog_title)
                                .setMessage(context.getString(R.string.msg_delete_playlist_rationale, playlist.getTitle()))
                                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                })
                                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, context.getString(R.string.msg_delete_playlist_success, playlist.getTitle()), Toast.LENGTH_LONG).show();
                                            PlaylistsFragment.deletePlaylistAt(position);
                                        }
                                });
                                dialog.show();
                                
                            	return true;
                            }
                            
                            if(item.getItemId() == R.id.rename_playlist_menu_item) {
                                
                            	LayoutMaterialTextinputBinding textInputBinding = LayoutMaterialTextinputBinding.inflate(inflater);
                                textInputBinding.textInputLayout.setHint(R.string.add_playlist_textinput_hint);
                                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context)
                                .setView(textInputBinding.getRoot())
                                .setTitle(R.string.rename)
                                .setMessage(context.getString(R.string.msg_rename_playlist_rationale, playlist.getTitle()))
                                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                })
                                .setPositiveButton(R.string.rename, new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            
                                            // check for null input 
                                            if(textInputBinding.textInput.getText().toString().trim().isEmpty()){
                                                Toast.makeText(context, R.string.add_playlist_dialog_empty_input_rationale, Toast.LENGTH_LONG).show();
                                            } else {
                                                String newPlaylistName = textInputBinding.textInput.getText().toString();
                                                Toast.makeText(context, context.getString(R.string.msg_rename_playlist_success, playlist.getTitle(), newPlaylistName), Toast.LENGTH_LONG).show();
                                                PlaylistsFragment.renamePlaylistAt(newPlaylistName, position);
                                            }
                                            
                                        }
                                });
                                dialog.show();
                                
                                return true;
                            }
                            
                            if(item.getItemId() == R.id.add_playlist_to_queue_menu_item) {
                            	return true;
                            }
                            
                            if(item.getItemId() == R.id.clear_playlist_menu_item) {
                                
                                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context)
                                .setTitle(R.string.clear_playlist)
                                .setMessage(context.getString(R.string.msg_clear_playlist_rationale, playlist.getTitle()))
                                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                })
                                .setPositiveButton(R.string.clear, new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            PlaylistsFragment.clearSongsFromPlaylistAt(position);
                                        }
                                });
                                dialog.show();
                                
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
        return playlists.size();
    }
    
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    	this.onItemClickListener = onItemClickListener;
    }
    
    @FunctionalInterface
    public interface OnItemClickListener {
        public void onItemClick(View view, PlaylistsModel playlist, int position);
    }
}
