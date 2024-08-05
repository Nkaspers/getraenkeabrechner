package com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection;

import android.view.View;

import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemSelectionSnapHelper extends LinearSnapHelper {
    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
//        final View currentView = findSnapView(layoutManager);
//        if (currentView == null) {
//            return RecyclerView.NO_POSITION;
//        }
//
//        int currentPosition = layoutManager.getPosition(currentView);
//        if (currentPosition == RecyclerView.NO_POSITION) {
//            return RecyclerView.NO_POSITION;
//        }
//
//        int targetPosition = currentPosition;
//
//        // Use layoutManager to determine the direction and calculate the target position
//        targetPosition += (velocityX > 0) ? 6 : -6;
//
//        final int itemCount = layoutManager.getItemCount();
//        targetPosition = Math.max(0, Math.min(targetPosition, itemCount - 1));
//
//        return targetPosition;
        return  super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
    }

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
       return super.findSnapView(layoutManager);
    }
}