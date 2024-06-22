package com.itsmcodez.echomusic.fragments;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import android.content.ComponentName;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsmcodez.echomusic.BaseApplication;
import com.itsmcodez.echomusic.PlayerActivity;
import com.itsmcodez.echomusic.adapters.NowPlayingQueueItemsAdapter;
import com.itsmcodez.echomusic.callbacks.OnPlayerStateChange;
import com.itsmcodez.echomusic.common.MediaItemsQueue;
import com.itsmcodez.echomusic.common.PlayerStateObserver;
import com.itsmcodez.echomusic.models.NowPlayingQueueItemsModel;
import com.itsmcodez.echomusic.models.PlaylistSongsModel;
import com.itsmcodez.echomusic.services.MusicService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.common.util.concurrent.ListenableFuture;
import com.itsmcodez.echomusic.MainActivity;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.databinding.FragmentNpMd3Binding;
import com.itsmcodez.echomusic.databinding.LayoutNowPlayingQueueSheetBinding;
import com.itsmcodez.echomusic.utils.MusicUtils;
import com.itsmcodez.echomusic.utils.PlaylistUtils;
import java.util.ArrayList;
import java.util.Collections;

public class NPMD3Fragment extends Fragment {
    private FragmentNpMd3Binding binding;
    private MediaController mediaController;
    private ListenableFuture<MediaController> controllerFuture;
    private OnPlayerStateChange playerStateCallback;
    private NowPlayingQueueItemsAdapter nowPlayingQueueItemsAdapter;
    
    @Override
    public void onStart() {
        super.onStart();
        SessionToken sessionToken = new SessionToken(getContext(), new ComponentName(getContext(), MusicService.class));
        controllerFuture = new MediaController.Builder(getContext(), sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
                
                if(controllerFuture.isDone()) {
                    try {
                        mediaController = controllerFuture.get();
                        // update UI if music is being played
                        if(mediaController != null && mediaController.getCurrentMediaItem() != null) {
                            updateUI(mediaController.getCurrentMediaItem());
                            binding.playPauseBt.setImageDrawable(mediaController.isPlaying() ? getContext().getDrawable(R.drawable.ic_pause_circle) : getContext().getDrawable(R.drawable.ic_play_circle));
                            updateProgress();
                        }
                    } catch(Exception err) {
                        err.printStackTrace();
                        mediaController = null;
                    }
                }
        }, MoreExecutors.directExecutor());
        
        // Player state callback
        playerStateCallback = new OnPlayerStateChange() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if(playbackState == ExoPlayer.STATE_READY) {
                    binding.playPauseBt.setImageDrawable(getContext().getDrawable(R.drawable.ic_pause_circle));
                } else binding.playPauseBt.setImageDrawable(getContext().getDrawable(R.drawable.ic_play_circle));
            }
            
            @Override
            public void onMediaItemTransition(MediaItem mediaItem, int reason) {
                updateUI(mediaItem);
                updateProgress();
            }
            
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if(isPlaying) {
                	binding.playPauseBt.setImageDrawable(getContext().getDrawable(R.drawable.ic_pause_circle));
                } else binding.playPauseBt.setImageDrawable(getContext().getDrawable(R.drawable.ic_play_circle));
            }
        };
        PlayerStateObserver.registerCallback(playerStateCallback);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        MediaController.releaseFuture(controllerFuture);
        PlayerStateObserver.unregisterCallback(playerStateCallback);
        playerStateCallback = null;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Bind to views
        binding = FragmentNpMd3Binding.inflate(inflater, container, false);
        
        // Now playing queue sheet
        BottomSheetBehavior<LinearLayout> nowPlayingQueueSheet = BottomSheetBehavior.from(binding.npQueueSheet.getRoot());
        nowPlayingQueueSheet.handleBackInvoked();
        binding.npQueueSheet.toggleBar.setOnClickListener(view -> {
                switch(nowPlayingQueueSheet.getState()){
                    case BottomSheetBehavior.STATE_HIDDEN : nowPlayingQueueSheet.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED : nowPlayingQueueSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED : {
                        nowPlayingQueueSheet.setPeekHeight(getActivity().getDisplay().getHeight() / 2);
                        nowPlayingQueueSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED : nowPlayingQueueSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
        });
        
        binding.npQueueSheet.navigateUpBt.setOnClickListener(view -> {
                if(nowPlayingQueueSheet.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                	nowPlayingQueueSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
        });
        
        // Now playing queue logic
        binding.npQueueSheet.recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), RecyclerView.VERTICAL, false));
        PlayerActivity.getNowPlayingQueueList().getAllSongs().observe(getViewLifecycleOwner(), new Observer<ArrayList<NowPlayingQueueItemsModel>>() {
                @Override
                public void onChanged(ArrayList<NowPlayingQueueItemsModel> songs) {
                    nowPlayingQueueItemsAdapter = new NowPlayingQueueItemsAdapter(getContext(), getLayoutInflater(), songs);
                    binding.npQueueSheet.recyclerView.setAdapter(nowPlayingQueueItemsAdapter);
                    
                    nowPlayingQueueItemsAdapter.setOnItemClickListener((view, _song, position) -> {
                            mediaController.seekTo(position, 0);
                    });
                    
                    nowPlayingQueueItemsAdapter.setOnRemoveItemClickListener((position) -> {
                            mediaController.removeMediaItem(position);
                            Toast.makeText(getContext(), getString(R.string.msg_remove_song_from_queue_success, MediaItemsQueue.getNowPlayingQueue().get(position).getTitle()), Toast.LENGTH_LONG).show();
                            MediaItemsQueue.getNowPlayingQueue().remove(position);
                            nowPlayingQueueItemsAdapter.notifyItemRemoved(position);
                            nowPlayingQueueItemsAdapter.notifyDataSetChanged();
                    });
                    
                    // Swap song item position logic
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                        new ItemTouchHelper.Callback(){
                            
                            @Override
                            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
                            }
                            
                            @Override
                            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder targetViewHolder) {
                                // Swap song position
                                int draggedItemIndex = viewHolder.getAdapterPosition();
                                int targetIndex = targetViewHolder.getAdapterPosition();
                                mediaController.moveMediaItem(draggedItemIndex, targetIndex);
                                // Update adapter's contents after swapping
                                Collections.swap(songs, draggedItemIndex, targetIndex);
                                recyclerView.getAdapter().notifyItemMoved(draggedItemIndex, targetIndex);
                                return true;
                            }
                            
                            @Override
                            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                                // TODO: implement this method
                            }
                            
                        }
                    );
                    itemTouchHelper.attachToRecyclerView(binding.npQueueSheet.recyclerView);
                }
        });
        
        // Toolbar
        binding.toolbar.setBackInvokedCallbackEnabled(true);
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        binding.toolbar.setNavigationOnClickListener(view -> {
                getActivity().navigateUpTo(new Intent(container.getContext(), MainActivity.class));
        });
        
        binding.toolbar.setOnMenuItemClickListener(item -> {
                
                if(item.getItemId() == R.id.queue_menu_item) {
                	if(nowPlayingQueueSheet.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        nowPlayingQueueSheet.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    }
                    return true;
                }
                
                return false;
        });
        
        // skipPreviousBt
        binding.skipPreviousBt.setOnClickListener(view -> {
                if(mediaController.getPlayWhenReady() || mediaController.getMediaItemCount() != 0) {
                	if(mediaController.hasPreviousMediaItem()) {
                		mediaController.seekToPreviousMediaItem();
                	}
                }
        });
        
        // playPauseBt
        binding.playPauseBt.setOnClickListener(view -> {
                if(mediaController.getPlayWhenReady() || mediaController.getMediaItemCount() != 0) {
                	if(mediaController.isPlaying()) {
                		mediaController.pause();
                	} else {
                        mediaController.play();
                    }
                }
        });
        
        // skipNextBt
        binding.skipNextBt.setOnClickListener(view -> {
                if(mediaController.getPlayWhenReady() || mediaController.getMediaItemCount() != 0) {
                	if(mediaController.hasNextMediaItem()) {
                		mediaController.seekToNextMediaItem();
                	}
                }
        });
        
        binding.seekBar.setOnSeekBarChangeListener(new AppCompatSeekBar.OnSeekBarChangeListener() {
                
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO: Implement this method
                }
                
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO: Implement this method
                }
                
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser) {
                        mediaController.seekTo(progress);
                    }
                }
                
        });
        
        binding.repeatBt.setOnClickListener(view -> {
                if(mediaController.getPlayWhenReady() || mediaController.getMediaItemCount() != 0) {
                	if(mediaController.getRepeatMode() == Player.REPEAT_MODE_ALL) {
                		mediaController.setRepeatMode(Player.REPEAT_MODE_ONE);
                        binding.repeatBt.setImageResource(R.drawable.ic_repeat);
                        Toast.makeText(getContext(), getString(R.string.msg_repeat_on), Toast.LENGTH_SHORT).show();
                	} else {
                        mediaController.setRepeatMode(Player.REPEAT_MODE_ALL);
                        binding.repeatBt.setImageResource(R.drawable.ic_repeat_off);
                        Toast.makeText(getContext(), getString(R.string.msg_repeat_off), Toast.LENGTH_SHORT).show();
                    }
                }
        });
        
        binding.shuffleBt.setOnClickListener(view -> {
                if(mediaController.getPlayWhenReady() || mediaController.getMediaItemCount() != 0) {
                	if(!mediaController.getShuffleModeEnabled()) {
                		mediaController.setShuffleModeEnabled(true);
                        binding.shuffleBt.setImageResource(R.drawable.ic_shuffle);
                        Toast.makeText(getContext(), getString(R.string.msg_shuffle_on), Toast.LENGTH_SHORT).show();
                	} else {
                        mediaController.setShuffleModeEnabled(false);
                        binding.shuffleBt.setImageResource(R.drawable.ic_shuffle_off);
                        Toast.makeText(getContext(), getString(R.string.msg_shuffle_off), Toast.LENGTH_SHORT).show();
                    }
                }
        });
        
        binding.detailsBt.setOnClickListener(view -> {
                AlertDialog dialog = new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.details)
                .setMessage(getContext().getString(R.string.song_details, mediaController.getCurrentMediaItem().mediaMetadata.title, mediaController.getCurrentMediaItem().mediaMetadata.displayTitle, mediaController.getCurrentMediaItem().mediaMetadata.artist, mediaController.getCurrentMediaItem().mediaMetadata.albumTitle, MusicUtils.getReadableDuration(mediaController.getDuration())))
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                })
                .create();
                dialog.show();
        });
        
        binding.favBt.setOnClickListener(view -> {
                if(MusicUtils.isFavouriteSong(Long.parseLong(mediaController.getCurrentMediaItem().mediaId))) {
                    MusicUtils.removeSongFromFavourites(Long.parseLong(mediaController.getCurrentMediaItem().mediaId));
                    binding.favBt.setImageResource(R.drawable.ic_heart_outline);
                	return;
                }
                PlaylistSongsModel playlistSong = new PlaylistSongsModel((String)mediaController.getCurrentMediaItem().mediaMetadata.displayTitle, (String)mediaController.getCurrentMediaItem().mediaMetadata.title,
                    (String)mediaController.getCurrentMediaItem().mediaMetadata.artist, String.valueOf(mediaController.getDuration()), (String)mediaController.getCurrentMediaItem().mediaMetadata.albumTitle, (String)mediaController.getCurrentMediaItem().mediaMetadata.description, mediaController.getCurrentMediaItem().mediaId);
                MusicUtils.addSongToFavourites(playlistSong);
                binding.favBt.setImageResource(R.drawable.ic_heart);
        });
        
        binding.setRingtoneBt.setOnClickListener(view -> {});
        
        return binding.getRoot();
    }
    
    private void updateUI(MediaItem mediaItem) {
        if(!binding.title.isSelected()) {
        	binding.title.setSelected(true);
        }
        if(!binding.artist.isSelected()) {
        	binding.artist.setSelected(true);
        }
        binding.title.setText(mediaItem.mediaMetadata.title);
        binding.artist.setText(mediaItem.mediaMetadata.artist);
        Glide.with(getContext()).load(mediaItem.mediaMetadata.artworkUri)
            .error(R.drawable.ic_music_note_outline)
            .into(binding.albumArtwork);
        if(MusicUtils.isFavouriteSong(Long.parseLong(mediaItem.mediaId))) {
            binding.favBt.setImageResource(R.drawable.ic_heart);
        } else {
            binding.favBt.setImageResource(R.drawable.ic_heart_outline);
        }
        if(mediaController.getRepeatMode() == Player.REPEAT_MODE_ALL) {
            binding.repeatBt.setImageResource(R.drawable.ic_repeat_off);
        } else {
            binding.repeatBt.setImageResource(R.drawable.ic_repeat);
        }
        if(mediaController.getShuffleModeEnabled()) {
            binding.shuffleBt.setImageResource(R.drawable.ic_shuffle);
        } else {
            binding.shuffleBt.setImageResource(R.drawable.ic_shuffle_off);
        }
    }
    
    private void updateProgress() {
        if(mediaController != null) {
            getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(binding != null && mediaController.getCurrentMediaItem() != null) {
                            // Player progress
                            String max = "0";
                            String progress = "0";
                            if(mediaController.getDuration() > 0 || mediaController.getCurrentPosition() > 0) {
                                max = String.valueOf(mediaController.getDuration());
                                progress = String.valueOf(mediaController.getCurrentPosition());
                            }
                            binding.seekBar.setMax(Integer.parseInt(max));
                            binding.duration.setText(mediaController.getDuration() <= 0 ? "00:00" : MusicUtils.getReadableDuration(mediaController.getDuration()));
                            binding.seekBar.setProgress(Integer.parseInt(progress));
                            binding.currentDuration.setText(MusicUtils.getReadableDuration(mediaController.getCurrentPosition()));
                        }
                        new Handler().postDelayed(this, 1000);
                    }
            });
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.binding = null;
    }
}
