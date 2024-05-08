package com.itsmcodez.echomusic.callbacks;
import android.util.SparseBooleanArray;
import android.view.View;
import com.itsmcodez.echomusic.markups.Model;
import java.util.ArrayList;

public interface OnClickEvents {
    
    @FunctionalInterface
    public interface OnItemClickListener {
        public void onItemClick(View view, Model model, int position);
    }
    
    @FunctionalInterface
    public interface OnItemLongClickListener {
        public boolean onItemLongClick(View view, Model model, int position);
    }
    
    public interface OnMultiSelectListener {
        public ArrayList<Integer> getSelectedIndices();
        public SparseBooleanArray getSelectionMap();
        public void addIndexToSelection(int index, View view);
        public void toggleSelectionMode(int index, boolean isSelectModeOn, View view);
        public void clearSelection();
        
        @FunctionalInterface
        public interface OnSelectChangedListener {
            public void onSelectChanged(int index, boolean isActive, View view);
        }
    }
}
