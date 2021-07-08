/**
 * Quan Duc Loc CE140037
 */

package com.group6.noteapp.view;

import android.view.View;

/**
 * Interface for Recycler View Click Listener
 */
public interface RecyclerViewClickListener {
    void onClick(View view, int position);      // on short click listener
    void onLongClick(View view, int position);  // on long click listener
}
