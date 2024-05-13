package com.itsmcodez.echomusic.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.itsmcodez.echomusic.MainActivity;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.databinding.FragmentNpMd3Binding;
import com.itsmcodez.echomusic.databinding.LayoutNowPlayingQueueSheetBinding;

public class NPMD3Fragment extends Fragment {
    private FragmentNpMd3Binding binding;
    
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
        
        return binding.getRoot();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.binding = null;
    }
}
