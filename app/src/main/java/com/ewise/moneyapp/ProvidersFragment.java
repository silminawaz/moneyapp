package com.ewise.moneyapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.UserProviderEntry;
import com.ewise.android.pdv.api.model.response.AccountsResponse;
import com.ewise.android.pdv.api.model.response.TransactionsResponse;
import com.ewise.moneyapp.Utils.PdvApiName;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;
import com.ewise.moneyapp.adapters.ProviderItemViewAdapter;
import com.ewise.moneyapp.data.ProviderPopupMenuItemData;
import com.ewise.moneyapp.service.PdvAcaBoundService;
import com.google.android.gms.vision.text.Line;

import java.util.ArrayList;
import java.util.List;

/**
     * A placeholder fragment containing a simple view.
     */
    public class ProvidersFragment extends Fragment implements MainActivity.FragmentUpdateListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private static final String TAG = "ProvidersFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";

    ListView providerList;
    LinearLayout welcomeLayout;
    Button addProviderButton;

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

        Log.d(TAG, "onCreateView() - START");
        View rootView = inflater.inflate(R.layout.fragment_providers, container, false);
        providerList = (ListView) rootView.findViewById(R.id.providerList);
        welcomeLayout = (LinearLayout) rootView.findViewById(R.id.providerWelcomeLayout);
        addProviderButton = (Button) rootView.findViewById(R.id.addProviderButton);

        //if there are any legitimate providers , we can hide the welcome layout.
        MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();
        welcomeLayout.setVisibility(View.GONE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(pdvApiOnGetUserProfileSuccessMessageReceiver,
                new IntentFilter("pdv-on-get-user-profile-success"));

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(pdvApiOnStopMessageReceiver,
                new IntentFilter("pdv-aca-stop-callback"));

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(pdvApiOnAddingNewProvider,
                new IntentFilter("pdv-api-adding-new-provider"));

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(pdvApiCallbackMessageReceiver,
                new IntentFilter("pdv-aca-bound-service-callback"));

        //set the fragment listener
        ((MainActivity)getActivity()).setProviderFragmentListener(this);


        Log.d(TAG, "onCreateView() - END");

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

        final ProviderPopupMenuItemData[] popupMenuItemsStopSyncSetVerify = {
                new ProviderPopupMenuItemData(R.drawable.ic_sync, getString(R.string.provider_menu_setverify_title)),
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


        final ListAdapter popupMenuAdapterStopSyncSetVerify = new ArrayAdapter<ProviderPopupMenuItemData>(getContext(),
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                popupMenuItemsStopSyncSetVerify){
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(popupMenuItemsStopSyncSetVerify[position].icon, 0, 0, 0);

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

                    if (((MoneyAppApp)getActivity().getApplication()).getInstituionIdSyncStatus(provider.getIid()).equals(getResources().getString(R.string.pdvapi_sync_status_message_in_progress_setverify))){
                        new AlertDialog.Builder(getActivity())
                                .setTitle(provider.getDesc() + " (" + provider.getUid() + ")")
                                .setAdapter(popupMenuAdapterStopSyncSetVerify, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        //...
                                        Toast.makeText(getActivity(), "You Clicked : " + popupMenuItemsStopSyncSetVerify[item].text, Toast.LENGTH_SHORT).show();
                                        PopupMenuClicked (provider, popupMenuItemsStopSyncSetVerify, item);
                                    }
                                }).show();
                    }
                    else
                    {
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
            providers = new ArrayList<>(app.userProfileData.getUserprofile());
        }
        else
        {
            providers = new ArrayList<>();
        }
        providerAdapter = new ProviderItemViewAdapter(getContext(), providers);
        providerList.setAdapter(providerAdapter);
        welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);

        addProviderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();

                if (app.pdvLoginStatus.isLoggedOnToPdv() && !app.pdvApiRequestQueue.isRequestInProgress()){
                    ((MainActivity) getActivity()).startAddProviderActivity();
                }
                else if (!app.pdvApiRequestQueue.isRequestInProgress())
                {
                    Toast.makeText(getContext(), R.string.pdvapi_not_loggedin, Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getContext(), R.string.pdvapi_request_inprogress, Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    @Override
    public void onPause(){
        Log.d(TAG, "onPause() - START");

        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(pdvApiCallbackMessageReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(pdvApiOnStopMessageReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(pdvApiOnGetUserProfileSuccessMessageReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(pdvApiOnAddingNewProvider);
        Log.d(TAG, "onPause() - END");

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
            //edit the provider - show the edit provider dialogFragment
            showEditProviderDialog(provider);


        } else if (itemString.equals(getString(R.string.provider_menu_delete_title))) {
            //delete the provider - alert user for a confirmation first
            showRemoveProviderDialog(provider);

            //todo: refresh the providers, networth and accounts etc to ensure you get a correct portfolio

        } else if (itemString.equals(getString(R.string.provider_menu_stop_title))) {
            //stop pending sync request
            MainActivity activity = (MainActivity) getActivity();
            PdvAcaBoundService service = activity.pdvAcaBoundService;
            if (activity.pdvAcaServiceIsBound && service!=null){
                app.pdvApiRequestQueue.stopSync(provider.getIid(), app.pdvApi, service, getContext());
            }
        }
        else if (itemString.equals(getString(R.string.provider_menu_setverify_title))) {
            //edit the provider - show the edit provider dialogFragment
            showOTPDialog(provider);


        }
     }

    public void showOTPDialog(UserProviderEntry providerEntry){
        Log.d (TAG, "showOTPDialog instId=" + providerEntry.getIid());


        MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();

        DialogFragment otpFragment = null;
        TransactionsResponse transactionsResponse = app.pdvApiRequestQueue.getRequestForInstitution(providerEntry.getIid()).results.transactions;
        AccountsResponse accountsResponse = app.pdvApiRequestQueue.getRequestForInstitution(providerEntry.getIid()).results.accounts;

        if (transactionsResponse!=null) {
            //Handle OTP/Captcha for updateTransactions
            otpFragment = EwiseOTPFragment.newInstance(transactionsResponse.getInstId(),
                    String.format(transactionsResponse.getMessage() + " %s.",
                            app.getUserProviderEntry(transactionsResponse.getInstId()).getDesc()),
                    null  //todo: implement base64Image of captcha in v0.5.n
            );

        }
        else if (accountsResponse!=null) {
            //Handle OTP/Captcha for updateAccounts
            otpFragment = EwiseOTPFragment.newInstance(accountsResponse.getInstId(),
                    String.format(accountsResponse.getMessage() + " %s.",
                            app.getUserProviderEntry(accountsResponse.getInstId()).getDesc()),
                    null  //todo: implement base64Image of captcha in v0.5.n
            );

        }

        if (otpFragment!=null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("OTP_DIALOG");
            if (prev != null) {
                ft.remove(prev);
            }

            otpFragment.show(getActivity().getSupportFragmentManager(), "OTP_DIALOG");
        }

    }

    public void showEditProviderDialog(UserProviderEntry providerEntry){
        //mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("edit_institution_prompts_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = EditProviderDialogFragment.newInstance(providerEntry);
        newFragment.show(ft, "edit_institution_prompts_dialog");
    }


    private void showRemoveProviderDialog(final UserProviderEntry providerEntry){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        // Add the buttons
        builder.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Toast.makeText(getActivity(), getString(R.string.delete_provider_toast_message) + " " + providerEntry.getDesc(), Toast.LENGTH_SHORT).show();
                MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();
                app.pdvRemoveInstitution(providerEntry.getIid(), (MainActivity)getActivity());

            }
        });
        builder.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                // do nothing - just close this dialog and go back to previous activity
                dialog.cancel();
            }
        });


        MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();

        builder.setMessage(R.string.delete_provider_alert_message)
                .setTitle(getString(R.string.delete_provider_alert_title)+" "+providerEntry.getDesc())
                .setIcon(getResources().getIdentifier(app.getProviderGroupId(providerEntry.getIid()), "drawable", getActivity().getPackageName()));


        AlertDialog dialog = builder.create();

        dialog.show();

    }



    @Override
    public void refreshFragmentUI(){

        Log.d("AccountsFragment", "refreshFragment()");

        if (isAdded()) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //accounts retrieval failed
                    updatePageData();
                }
            });
        }
    }

    public void updatePageData(){

        //reset data fetching if there isn't any more requests
        MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
        if (app.userProfileData.getUserprofile()!=null){
            providerAdapter.swapData(new ArrayList<>(app.userProfileData.getUserprofile()));
            welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);
        }

    }


    private BroadcastReceiver pdvApiOnGetUserProfileSuccessMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //reset data fetching if there isn't any more requests
            MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
            if (app.userProfileData.getUserprofile()!=null){
                providerAdapter.swapData(new ArrayList<>(app.userProfileData.getUserprofile()));
                welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);
            }
        }
    };

    private BroadcastReceiver pdvApiOnStopMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //check if an error or success was received and handle it
            String stringResults = intent.getStringExtra("stringResults");
            PdvApiResults results = PdvApiResults.objectFromString(stringResults, PdvApiResults.class);
            Log.d(TAG, "Received Broadcast message pdv-aca-stop-callback");

            providerAdapter.notifyDataSetChanged();
        }
    };

    private BroadcastReceiver pdvApiOnAddingNewProvider = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received Broadcast message pdv-api-adding-new-provider");
            providerAdapter.notifyDataSetChanged();
            MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();
            welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);
        }
    };

    private BroadcastReceiver pdvApiCallbackMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String apiName = intent.getStringExtra("apiName");
            String callbackStatus = intent.getStringExtra("callbackStatus");
            String sRequestParams = intent.getStringExtra("requestParams");
            String sResults = intent.getStringExtra("results");

            Log.d (TAG, "pdvApiCallbackMessageReceiver.onReceive() apiName:" + apiName);
            Log.d (TAG, "pdvApiCallbackMessageReceiver.onReceive() callbackStatus:" + callbackStatus);
            Log.d (TAG, "pdvApiCallbackMessageReceiver.onReceive() requestParams:" + sRequestParams);
            Log.d (TAG, "pdvApiCallbackMessageReceiver.onReceive() results:" + sResults);

            //todo: process the results
            providerAdapter.notifyDataSetChanged();

        }
    };

}