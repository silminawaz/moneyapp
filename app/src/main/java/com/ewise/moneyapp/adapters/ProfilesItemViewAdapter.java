package com.ewise.moneyapp.adapters;

import android.app.Activity;
import android.content.Context;
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
import com.ewise.moneyapp.Utils.PdvApiRequestParams;
import com.ewise.moneyapp.Utils.Settings;
import com.ewise.moneyapp.Utils.SignonProfile;

import java.util.List;

/**
 * Created by SilmiNawaz on 12/3/17.
 */
public class ProfilesItemViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<SignonProfile> mItems;

    public ProfilesItemViewAdapter(Context context, List<SignonProfile> items) {
        this.context = context;
        mItems = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        SignonProfile entry = (SignonProfile) getItem(position);

        profileName.setText(entry.name);
        profileDescription.setText(entry.description);

        return convertView;
    }

    public void swapData(List<SignonProfile> newData){
        Log.d("**TXN**LOAD***", String.format("ListAdapter swapping data size : %s", String.format("%d",newData.size())));

        mItems.clear();
        mItems.addAll(newData);
        notifyDataSetChanged();
    }


    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
