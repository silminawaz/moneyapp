package com.ewise.moneyapp.adapters;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.UserProviderEntry;
import com.ewise.android.pdv.api.model.provider.Institution;
import com.ewise.moneyapp.MoneyAppApp;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.Utils.PdvApiRequestParams;
import com.ewise.moneyapp.Utils.PdvApiRequestQueue;

import java.util.List;

/**
 * Created by SilmiNawaz on 12/3/17.
 */
public class ProviderItemViewAdapter extends BaseAdapter {

    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<UserProviderEntry> mItems;
    private SparseBooleanArray mSelectedItemsIds;


    public ProviderItemViewAdapter(Activity activity, List<UserProviderEntry> items) {
        mActivity = activity;
        mItems = items;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSelectedItemsIds = new SparseBooleanArray();
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
            convertView = mInflater.inflate(R.layout.provider_list_item_2, parent, false);
        }


        ImageView providerIcon = (ImageView) convertView.findViewById(R.id.providerIcon);
        TextView providerName = (TextView) convertView.findViewById(R.id.providerName);
        TextView providerUsername = (TextView) convertView.findViewById(R.id.providerUsername);
        TextView providerMessage = (TextView) convertView.findViewById(R.id.providerMessage);
        TextView providerSyncStatus = (TextView) convertView.findViewById(R.id.providerSyncStatus);
        ProgressBar providerSyncProgressBar = (ProgressBar) convertView.findViewById(R.id.providerSyncProgressBar);


        MoneyAppApp app = (MoneyAppApp) mActivity.getApplication();

        UserProviderEntry providerEntry = (UserProviderEntry) getItem(position);

        if (!providerEntry.getDesc().equals("")) {
            providerName.setText(providerEntry.getDesc());
        }
        else{
            //this is to handle a bug in the SDK where the provider entry description is not set after call to setCredential()
            Institution inst = app.getInstitution(providerEntry.getIid());
            if (inst!=null){
                providerName.setText(inst.getInstDesc());
            }
        }
        String syncStatus = app.getInstituionIdSyncStatus(providerEntry.getIid());
        String providerUid = providerEntry.getUid();
        if (providerUid==null && !providerEntry.isFoundInDevice()){
            providerUid = mActivity.getString(R.string.provider_credentials_not_on_device_message);
            syncStatus = "";
        }
        providerUsername.setText(providerUid);

        //providerMessage - display any response messages in the request queue
        providerMessage.setText("");
        PdvApiRequestParams p = app.pdvApiRequestQueue.getRequestForInstitution(providerEntry.getIid());
        if (p!=null) {
            if (p.results!=null) {
                Response r = p.results.getResponse();
                if (r != null) {
                    providerMessage.setText(r.getMessage());
                } else {
                    providerMessage.setText("");
                }
            }
        }

        if (mSelectedItemsIds.get(position)) {
            Resources r = mActivity.getResources();
            Drawable[] layers = new Drawable[2];
            //layers[0] = r.getDrawable(R.drawable.t);
            layers[0] = ContextCompat.getDrawable(mActivity, app.getInstitutionIconResourceId(providerEntry.getIid()));
            //layers[1] = r.getDrawable(R.drawable.tt);
            layers[1] = ContextCompat.getDrawable(mActivity, R.drawable.ewise_default_checkmark);
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            providerIcon.setImageDrawable(layerDrawable);
        }
        else {

            providerIcon.setImageResource(app.getInstitutionIconResourceId(providerEntry.getIid()));
        }

        providerSyncStatus.setText(syncStatus);
        if (syncStatus.equals(mActivity.getString(R.string.pdvapi_sync_status_message_in_progress))){
            providerSyncProgressBar.setVisibility(View.VISIBLE);
        }
        else{
            providerSyncProgressBar.setVisibility(View.GONE);
        }


        return convertView;
    }

    public void swapData(List<UserProviderEntry> newData){
        Log.d("**TXN**LOAD***", String.format("RecyclerAdapter swapping data size : %s", String.format("%d",newData.size())));

        mItems.clear();
        mItems.addAll(newData);
        notifyDataSetChanged();
    }

    public void updateSyncStatus(){
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = mActivity.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
