package com.itsmcodez.echomusic.fragments;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import android.view.ActionMode;
import android.view.Window;
import android.view.WindowManager;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.adapters.SongsAdapter;
import com.itsmcodez.echomusic.databinding.FragmentSongsBinding;
import com.itsmcodez.echomusic.models.SongsModel;
import com.itsmcodez.echomusic.viewmodels.SongsViewModel;
import java.util.ArrayList;

public class SongsFragment extends Fragment {
    private FragmentSongsBinding binding;
    private SongsAdapter songsAdapter;
    private SongsViewModel songsViewModel;
    private ActionMode actionMode;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songsViewModel = new ViewModelProvider(this).get(SongsViewModel.class);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Bind to views
        binding = FragmentSongsBinding.inflate(inflater, container, false);
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), RecyclerView.VERTICAL, false));
        
        songsViewModel.getAllSongs().observe(getViewLifecycleOwner(), new Observer<ArrayList<SongsModel>>() {
                @Override
                public void onChanged(ArrayList<SongsModel> allSongs) {
                	songsAdapter = new SongsAdapter(container.getContext(), getLayoutInflater(), allSongs);
                    binding.recyclerView.setAdapter(songsAdapter);
                    
                    songsAdapter.setOnItemClickListener((view, _song, position) -> {
                            // If multiple selection is toggled
                            if(songsAdapter.isSelectModeOn()) {
                                songsAdapter.addIndexToSelection(position, view);
                                actionMode.setTitle(String.valueOf(songsAdapter.getSelectedIndices().size() + " selected"));
                                if(songsAdapter.getSelectedIndices().size() == 0) {
                                	actionMode.finish();
                                }
                            	return;
                            }
                    });
                    
                    songsAdapter.setOnItemLongClickListener((view, _song, position) -> {
                            // toggle multiple selection
                            songsAdapter.toggleSelectionMode(position, true, view);
                            startActionMode();
                            actionMode.setTitle(String.valueOf(songsAdapter.getSelectedIndices().size() + " selected"));
                            if(songsAdapter.getSelectedIndices().size() == 0) {
                            	actionMode.finish();
                            }
                            return true;
                    });
                }
        });
        
        return binding.getRoot();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.binding = null;
    }
    
    /* Multiple selection ActionMode logic */
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback(){
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_fragment_songs_multiselect, menu);
            return true;
        }
    
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }
    
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            songsAdapter.clearSelection();
            mode = null;
        }
    };
    
    public void startActionMode() {
    	actionMode = getActivity().startActionMode(actionModeCallback);
    }
}
