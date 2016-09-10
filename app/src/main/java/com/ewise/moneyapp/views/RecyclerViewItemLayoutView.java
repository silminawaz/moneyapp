package com.ewise.moneyapp.views;

import android.content.Context;
import android.widget.RelativeLayout;

/**
 * Created by SilmiNawaz on 1/9/16.
 * RecyclerViewItemLayoutView is a parameterised abstract class that is used as a custom view
 * extend this class to for the specific custom views
 */
public abstract class RecyclerViewItemLayoutView<T> extends RelativeLayout implements RecyclerViewBindInterface<T> {

    public RecyclerViewItemLayoutView (Context context){
        super (context);
    }

    public abstract void bind(T bindDataObject);

    //add other generic methods common to all views

}
