/**
 *  Quan Duc Loc CE140037
 */

package com.group6.noteapp.view;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Class to implements Recycler View's "On Item Click Listener" event
 */
public class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
    private GestureDetector gestureDetector;                // gesture detector
    private RecyclerViewClickListener clickListener;        // click listener

    /**
     * Constructor
     * @param context context
     * @param recyclerView recycler view
     * @param clickListener click listener
     */
    public RecyclerViewTouchListener(Context context, final RecyclerView recyclerView, final RecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            /**
             * Notify when a tap occurs
             * @param motionEvent motion event
             * @return true
             */
            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }

            /**
             * Call children's on long click when long click is detected
             * @param motionEvent motion event
             */
            @Override
            public void onLongPress(MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }
            }
        });
    }

    /**
     * Call children's on click when a touch event is detected
     * @param recyclerView recycler view
     * @param motionEvent motion event
     * @return false
     */
    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(motionEvent)) {
            clickListener.onClick(child, recyclerView.getChildPosition(child));
        }
        return false;
    }

    /**
     * Do nothing on touch event
     * @param recyclerView recycler view
     * @param motionEvent motion event
     */
    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        // do nothing
    }

    /**
     * Do nothing when a child of Recycler View intercept touch events
     * @param disallowInterceptStatus disallow-intercept status
     */
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowInterceptStatus) {
        // do nothing
    }
}