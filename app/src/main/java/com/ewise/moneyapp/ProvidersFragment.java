package com.ewise.moneyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.UserProviderEntry;
import com.ewise.moneyapp.Utils.PdvApiName;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;
import com.ewise.moneyapp.adapters.ProviderItemViewAdapter;
import com.ewise.moneyapp.data.ProviderPopupMenuItemData;
import com.ewise.moneyapp.service.PdvAcaBoundService;

import java.util.ArrayList;
import java.util.List;

/**
     * A placeholder fragment containing a simple view.
     */
    public class ProvidersFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    ListView providerList;

    private ProviderItemViewAdapter providerAdapter;



    public ProvidersFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProvidersFragment newInstance(int sectionNumber) {
        ProvidersFragment fragment = new ProvidersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_providers, container, false);
        providerList = (ListView) rootView.findViewById(R.id.providerList);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(pdvApiOnGetUserProfileSuccessMessageReceiver,
                new IntentFilter("pdv-on-get-user-profile-success"));

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(pdvApiOnStopMessageReceiver,
                new IntentFilter("pdv-aca-stop-callback"));

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(pdvApiOnAddingNewProvider,
                new IntentFilter("pdv-api-adding-new-provider"));

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(pdvApiCallbackMessageReceiver,
                new IntentFilter("pdv-aca-bound-service-callback"));

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        final ProviderPopupMenuItemData[] popupMenuItems = {
                new ProviderPopupMenuItemData(R.drawable.ic_sync, getString(R.string.provider_menu_refresh_title)),
                new ProviderPopupMenuItemData(R.drawable.ic_action_edit, getString(R.string.provider_menu_edit_title)),
                new ProviderPopupMenuItemData(R.drawable.ic_delete, getString(R.string.provider_menu_delete_title)),
        };


        final ProviderPopupMenuItemData[] popupMenuItemsNoSync = {
                new ProviderPopupMenuItemData(R.drawable.ic_action_edit, getString(R.string.provider_menu_edit_title)),
                new ProviderPopupMenuItemData(R.drawable.ic_delete, getString(R.string.provider_menu_delete_title)),
        };

        final ProviderPopupMenuItemData[] popupMenuItemsStopSync = {
                new ProviderPopupMenuItemData(R.drawable.ic_cancel, getString(R.string.provider_menu_stop_title)),
        };

        final ListAdapter popupMenuAdapter = new ArrayAdapter<ProviderPopupMenuItemData>(getContext(),
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                popupMenuItems){
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(popupMenuItems[position].icon, 0, 0, 0);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);
                return v;
            }
        };


        final ListAdapter popupMenuAdapterNoSync = new ArrayAdapter<ProviderPopupMenuItemData>(getContext(),
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                popupMenuItemsNoSync){
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(popupMenuItemsNoSync[position].icon, 0, 0, 0);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);
                return v;
            }
        };


        final ListAdapter popupMenuAdapterStopSync = new ArrayAdapter<ProviderPopupMenuItemData>(getContext(),
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                popupMenuItemsStopSync){
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(popupMenuItemsStopSync[position].icon, 0, 0, 0);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);
                return v;
            }
        };


        providerList.setClickable(true);

        providerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final UserProviderEntry provider = (UserProviderEntry) providerAdapter.getItem(i);
                if (provider.isFoundInDevice() &&
                        ((MoneyAppApp)getActivity().getApplication()).getInstituionIdSyncStatus(provider.getIid()).equals(getResources().getString(R.string.pdvapi_sync_status_message_ready))) {

                    new AlertDialog.Builder(getActivity())
                            .setTitle(provider.getDesc() + " (" + provider.getUid() + ")")
                            .setAdapter(popupMenuAdapter, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    //...
                                    Toast.makeText(getActivity(), "You Clicked : " + popupMenuItems[item].text, Toast.LENGTH_SHORT).show();
                                    PopupMenuClicked (provider, popupMenuItems, item);
                                }
                            }).show();
                }
                else if (!((MoneyAppApp)getActivity().getApplication()).getInstituionIdSyncStatus(provider.getIid()).equals(getResources().getString(R.string.pdvapi_sync_status_message_ready))) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(provider.getDesc() + " (" + provider.getUid() + ")")
                            .setAdapter(popupMenuAdapterStopSync, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    //...
                                    Toast.makeText(getActivity(), "You Clicked : " + popupMenuItemsStopSync[item].text, Toast.LENGTH_SHORT).show();
                                    PopupMenuClicked (provider, popupMenuItemsStopSync, item);
                                }
                            }).show();
                }
                else
                {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(provider.getDesc() + " (" + provider.getUid() + ")")
                            .setAdapter(popupMenuAdapterNoSync, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    //...
                                    Toast.makeText(getActivity(), "You Clicked : " + popupMenuItemsNoSync[item].text, Toast.LENGTH_SHORT).show();
                                    PopupMenuClicked (provider, popupMenuItemsNoSync, item);
                                }
                            }).show();
                }
            }

        });

        MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
        List<UserProviderEntry> providers = null;
        if (app.userProfileData.getUserprofile()!=null){
            providers = app.userProfileData.getUserprofile();
        }
        else
        {
            providers = new ArrayList<>();
        }
        providerAdapter = new ProviderItemViewAdapter(getContext(), providers);
        providerList.setAdapter(providerAdapter);
    }

    //method to handle user clicking the popup menu (Sync, Edit, Delete provider)
    private void PopupMenuClicked(UserProviderEntry provider, ProviderPopupMenuItemData[] itemData, int item) {

        String itemString = itemData[item].text;
        MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();
        if (itemString.equals(getString(R.string.provider_menu_refresh_title))) {
            //sync the provider
            ((MoneyAppApp)getActivity().getApplication()).syncExistingProvider(provider.getIid());
            providerAdapter.updateSyncStatus();

        } else if (itemString.equals(getString(R.string.provider_menu_edit_title))) {
            //edit the provider - show the edit provider activity


        } else if (itemString.equals(getString(R.string.provider_menu_delete_title))) {
            //delete the provider - alert user for a confirmation first

            //todo: refresh the providers, networth and accounts etc to ensure you get a correct portfolio

        } else if (itemString.equals(getString(R.string.provider_menu_stop_title))) {
            //stop pending sync request
            MainActivity activity = (MainActivity) getActivity();
            PdvAcaBoundService service = activity.pdvAcaBoundService;
            if (activity.pdvAcaServiceIsBound && service!=null){
                app.pdvApiRequestQueue.stopSync(provider.getIid(), app.pdvApi, service, getContext());
            }

            //todo: refresh the providers, networth and accounts etc to ensure you get a correct portfolio

        }
     }


    private BroadcastReceiver pdvApiOnGetUserProfileSuccessMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //reset data fetching if there isn't any more requests
            MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
            if (app.userProfileData.getUserprofile()!=null){
                providerAdapter.swapData(app.userProfileData.getUserprofile());
            }
        }
    };

    private BroadcastReceiver pdvApiOnStopMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //check if an error or success was received and handle it
            String stringResults = intent.getStringExtra("stringResults");
            PdvApiResults results = PdvApiResults.objectFromString(stringResults, PdvApiResults.class);
            Log.d("ProviderFragment", "Received Broadcast message pdv-aca-stop-callback");

            providerAdapter.updateSyncStatus();
        }
    };

    private BroadcastReceiver pdvApiOnAddingNewProvider = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ProviderFragment", "Received Broadcast message pdv-api-adding-new-provider");
            providerAdapter.updateSyncStatus();
        }
    };

    private BroadcastReceiver pdvApiCallbackMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String apiName = intent.getStringExtra("apiName");
            String callbackStatus = intent.getStringExtra("callbackStatus");
            String sRequestParams = intent.getStringExtra("requestParams");
            String sResults = intent.getStringExtra("results");

            Log.d ("ProvidersFragment", "pdvApiCallbackMessageReceiver.onReceive() apiName:" + apiName);
            Log.d ("ProvidersFragment", "pdvApiCallbackMessageReceiver.onReceive() callbackStatus:" + callbackStatus);
            Log.d ("ProvidersFragment", "pdvApiCallbackMessageReceiver.onReceive() requestParams:" + sRequestParams);
            Log.d ("ProvidersFragment", "pdvApiCallbackMessageReceiver.onReceive() results:" + sResults);

            //todo: process the results
            providerAdapter.updateSyncStatus();

        }
    };

}