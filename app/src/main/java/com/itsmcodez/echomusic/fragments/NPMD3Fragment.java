package com.itsmcodez.echomusic.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.itsmcodez.echomusic.MainActivity;
import com.itsmcodez.echomusic.R;
import com.itsmcodez.echomusic.databinding.FragmentNpMd3Binding;

public class NPMD3Fragment extends Fragment {
    private FragmentNpMd3Binding binding;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Bind to views
        binding = FragmentNpMd3Binding.inflate(inflater, container, false);
        
        // Toolbar
        binding.toolbar.setBackInvokedCallbackEnabled(true);
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        binding.toolbar.setNavigationOnClickListener(view -> {
                getActivity().navigateUpTo(new Intent(container.getContext(), MainActivity.class));
        });
        
        return binding.getRoot();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.binding = null;
    }
}
