package com.itsmcodez.echomusic.callbacks;
import android.view.View;
import com.itsmcodez.echomusic.markups.Model;

public interface OnClickEvents {
    
    @FunctionalInterface
    public interface OnItemClickListener {
        public void onItemClick(View view, Model model, int position);
    }
    
    @FunctionalInterface
    public interface OnItemLongClickListener {
        public void onItemLongClick(View view, Model model, int position);
    }
}
