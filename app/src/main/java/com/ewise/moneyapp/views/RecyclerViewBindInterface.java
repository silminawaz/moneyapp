package com.ewise.moneyapp.views;

/**
 * Created by SilmiNawaz on 1/9/16.
 *
 * RecyclerViewBindInterface
 * -------------------------
 * RecyclerViewBindInterface should be implemented to bind any data object to a view
 * Extend the RecyclerViewItemLayoutView custom view (extended from RelativeLayout) when implementing a specific view
 */
public interface RecyclerViewBindInterface <T> {

    public void bind(T bindDataObject);
}
