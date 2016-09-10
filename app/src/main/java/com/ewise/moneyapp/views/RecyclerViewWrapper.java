package com.ewise.moneyapp.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by SilmiNawaz on 30/8/16.
 * test
 */
public class RecyclerViewWrapper<V extends View> extends RecyclerView.ViewHolder {

    private V view;

    public RecyclerViewWrapper(V itemView) {
        super(itemView);
        view = itemView;
    }

    public V getView() {
        return view;
    }

}
