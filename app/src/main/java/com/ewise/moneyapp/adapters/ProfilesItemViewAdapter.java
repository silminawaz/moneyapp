package com.ewise.moneyapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.UserProviderEntry;
import com.ewise.android.pdv.api.model.provider.Institution;
import com.ewise.moneyapp.MoneyAppApp;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.Utils.Base64ImageConverter;
import com.ewise.moneyapp.Utils.PdvApiRequestParams;
import com.ewise.moneyapp.Utils.Settings;
import com.ewise.moneyapp.Utils.SignonProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 12/3/17.
 */
public class ProfilesItemViewAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater mInflater;
    private List<SignonProfile> mItems;
    private Bitmap[] mImageCache; //performance fix for list scrolling Todo: Apply this performance fix everywhere if it works

    public ProfilesItemViewAdapter(Activity activity, List<SignonProfile> items) {
        this.activity = activity;
        mItems = items;

        //performance fix for draggy listview scrolling is to cache the icon images in memory
        mImageCache = new Bitmap[mItems.size()];
        for (int i=0; i<mItems.size(); i++) {
            if ((mItems.get(i).base64Image!=null) && (mItems.get(i).base64Image.trim().length()>0)){
                mImageCache[i]=Base64ImageConverter.fromBase64ToBitmap(mItems.get(i).base64Image);
            }
        }
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null) {
            // Get view for row item
            convertView = mInflater.inflate(R.layout.settings_profiles_item, parent, false);
        }

        TextView profileName = (TextView) convertView.findViewById(R.id.settingsProfilesName);
        TextView profileDescription = (TextView) convertView.findViewById(R.id.settingsProfilesDesc);
        final ImageView profileIcon = (ImageView) convertView.findViewById(R.id.settingsProfilesIcon);

        final SignonProfile entry = (SignonProfile) getItem(position);

        profileName.setText(entry.name.toLowerCase());
        profileDescription.setText(entry.description);

        if (position<mImageCache.length){
            //we have only cached previous images... newly added may not be cached
            if (mImageCache[position] != null) {
                profileIcon.setImageBitmap(mImageCache[position]);
            }
        }


        return convertView;
    }

    public void swapData(List<SignonProfile> newData){
        Log.d("**TXN**LOAD***", String.format("ProfilesItemViewAdapter: swapping data size : %s", String.format("%d",newData.size())));

        mItems.clear();
        mItems.addAll(newData);

        mImageCache = new Bitmap[mItems.size()];
        for (int i=0; i<mItems.size(); i++) {
            if ((mItems.get(i).base64Image!=null) && (mItems.get(i).base64Image.trim().length()>0)){
                mImageCache[i]=Base64ImageConverter.fromBase64ToBitmap(mItems.get(i).base64Image);
            }
        }

        notifyDataSetChanged();
    }


    public void updateImageCache(){
        Log.d("**TXN**LOAD***", "ProfilesItemViewAdapter: updateImageCache()");

        mImageCache = new Bitmap[mItems.size()];
        for (int i=0; i<mItems.size(); i++) {
            if ((mItems.get(i).base64Image!=null) && (mItems.get(i).base64Image.trim().length()>0)){
                mImageCache[i]=Base64ImageConverter.fromBase64ToBitmap(mItems.get(i).base64Image);
            }
        }

        notifyDataSetChanged();
    }

    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = activity.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
