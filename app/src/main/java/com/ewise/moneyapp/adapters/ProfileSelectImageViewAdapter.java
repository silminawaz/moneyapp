package com.ewise.moneyapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ArrayRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ewise.moneyapp.R;

import java.util.List;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 20/4/17.
 */
public class ProfileSelectImageViewAdapter extends RecyclerView.Adapter<ProfileSelectImageViewAdapter.ViewHolder>{

    private static final String TAG = "ProfileSelectImageVi...";

    private int[] mProfileImageResourceIdList;
    private int mSelectedItemPosition;
    private Context mContext;
    private ImageView mProfileImageToSet;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;

        public ViewHolder(ImageView v) {
            super(v);
            mImageView=v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProfileSelectImageViewAdapter(@ArrayRes int imgArrayResId, ImageView profileImageToSet, Context context) {
        mContext=context;
        mSelectedItemPosition=-1;
        mProfileImageToSet=profileImageToSet;
        TypedArray typedArray = mContext.getResources().obtainTypedArray(imgArrayResId);
        Log.d(TAG, "ProfileSelectImageViewAdapter()  - mProfileImageResourceIdList.getIndexCount()="+typedArray.length());


        int len = typedArray.length();
        mProfileImageResourceIdList = new int[len];
        for (int i = 0; i < len; i++) {
            mProfileImageResourceIdList[i] = typedArray.getResourceId(i, 0);
            Log.d(TAG, "ProfileSelectImageViewAdapter()  - typedArray.getResourceId("+Integer.toString(i)+", 0)="+Integer.toString(mProfileImageResourceIdList[i]));

        }
        typedArray.recycle();
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ProfileSelectImageViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profiles_select_image_item_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...


        ViewHolder vh = new ViewHolder(v);



        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if (mSelectedItemPosition==position){
            Log.d(TAG, "onBindViewHolder(): - mSelectedItemPosition==position **SET SELECTED IMAGE**");

            // change the curent position image
            Resources r = mContext.getResources();
            Drawable[] layers = new Drawable[2];
            layers[0] = ContextCompat.getDrawable(mContext, mProfileImageResourceIdList[position]);
            layers[1] = ContextCompat.getDrawable(mContext, R.drawable.ewise_default_checkmark);
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            holder.mImageView.setImageDrawable(layerDrawable);
            mSelectedItemPosition = position;
            mProfileImageToSet.setImageResource(mProfileImageResourceIdList[position]);

        }
        else{
            Log.d(TAG, "onBindViewHolder(): - mSelectedItemPosition!=position **SET REGULAR IMAGE**");

            holder.mImageView.setImageResource(mProfileImageResourceIdList[position]);
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "ImageView.onClick(): -  **CLICKED ON IMAGE**");
                mSelectedItemPosition=holder.getAdapterPosition();
                notifyDataSetChanged();

            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mProfileImageResourceIdList.length;
    }


    public int getSelectedImageResourceId(){
        if (mSelectedItemPosition>=0)
            return mProfileImageResourceIdList[mSelectedItemPosition];
        else
            return 0;
    }


}
