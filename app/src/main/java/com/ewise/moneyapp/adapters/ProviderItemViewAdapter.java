package com.ewise.moneyapp.adapters;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import com.ewise.moneyapp.Utils.PdvApiRequestQueue;

import java.util.List;

/**
 * Created by SilmiNawaz on 12/3/17.
 */
public class ProviderItemViewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<UserProviderEntry> mItems;

    public ProviderItemViewAdapter(Context context, List<UserProviderEntry> items) {
        mContext = context;
        mItems = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        MoneyAppApp app = (MoneyAppApp) mContext.getApplicationContext();

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
            providerUid = mContext.getApplicationContext().getString(R.string.provider_credentials_not_on_device_message);
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

        providerIcon.setImageResource(app.getInstitutionIconResourceId(providerEntry.getIid()));

        providerSyncStatus.setText(syncStatus);
        if (syncStatus.equals(mContext.getApplicationContext().getString(R.string.pdvapi_sync_status_message_in_progress))){
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

    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = mContext.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
