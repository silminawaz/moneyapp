package com.ewise.moneyapp.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ewise.android.pdv.api.model.UserProviderEntry;
import com.ewise.android.pdv.api.model.response.AccountsResponse;
import com.ewise.android.pdv.api.model.response.TransactionsResponse;
import com.ewise.moneyapp.MainActivity;
import com.ewise.moneyapp.MoneyAppApp;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.adapters.ProviderItemViewAdapter;
import com.ewise.moneyapp.data.ProviderPopupMenuItemData;
import com.ewise.moneyapp.service.PdvAcaBoundService;

//import org.apache.log4j.chainsaw.Main;
import org.xwalk.core.XWalkView;

import java.util.ArrayList;
import java.util.List;

/**
     * A placeholder fragment containing a simple view.
     */
    public class ProvidersFragment extends MoneyAppFragment implements MainActivity.FragmentUpdateListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private static final String TAG = "ProvidersFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final int CONTEXT_MENU_POSITION_SYNC = 0;
    private static final int CONTEXT_MENU_POSITION_STOPSYNC = 1;
    private static final int CONTEXT_MENU_POSITION_VERIFY = 2;
    private static final int CONTEXT_MENU_POSITION_EDIT = 3;
    private static final int CONTEXT_MENU_POSITION_DELETE = 4;


    ListView providerList;
    LinearLayout welcomeLayout;
    Button addProviderButton;

    private ProviderItemViewAdapter providerAdapter;

   // private List<Integer> selectedItemsList=null;
    android.view.ActionMode mActionMode=null;


    private AbsListView.MultiChoiceModeListener mMultiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int i, long l, boolean b) {
            // Capture total checked items
            Log.d(TAG, "onItemCheckedStateChanged() - position="+Integer.toString(i));

            final int checkedCount = providerList.getCheckedItemCount();
            // Set the CAB title according to total checked items
            actionMode.setTitle(Integer.toString(checkedCount));
            // Calls toggleSelection method from ListViewAdapter Class
            providerAdapter.toggleSelection(i);
            onSelectionChanged(actionMode, i);
            //mActionMode=actionMode;
        }

        @Override
        public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.menu_provider_context, menu);
            Log.d(TAG, "onCreateActionMode() - START");
            onSelectionChanged(actionMode, 0);
            mActionMode=actionMode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
            if (!isAdded())return false;
            MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();
            SparseBooleanArray selectedIds = providerAdapter.getSelectedIds();

            switch (menuItem.getItemId()) {
                case R.id.menu_context_sync:
                    //todo: call activity sync all selected items
                    Toast.makeText(getActivity(), "Sync", Toast.LENGTH_SHORT).show();
                    actionMode.finish(); // Action picked, so close the CAB
                    if (isAdded()) {
                        for (int i=0; i<selectedIds.size(); i++) {
                            UserProviderEntry provider = (UserProviderEntry) providerAdapter.getItem(selectedIds.keyAt(i));
                            app.syncExistingProvider(provider.getIid());
                        }
                        providerAdapter.updateSyncStatus();
                    }
                    return true;
                case R.id.menu_context_stopsync:
                    //todo: call activity stop sync of all selected items
                    Toast.makeText(getActivity(), "Stop sync", Toast.LENGTH_SHORT).show();
                    actionMode.finish(); // Action picked, so close the CAB
                    if (isAdded()){
                        MainActivity activity = (MainActivity) getActivity();
                        PdvAcaBoundService service = activity.pdvAcaBoundService;
                        if (activity.pdvAcaServiceIsBound && service!=null){
                            for (int i=0; i<selectedIds.size(); i++) {
                                UserProviderEntry provider = (UserProviderEntry) providerAdapter.getItem(selectedIds.keyAt(i));
                                app.pdvApiRequestQueue.stopSync(provider.getIid(), app.pdvApi, service, getContext());
                            }
                            providerAdapter.updateSyncStatus();
                        }
                    }
                    return true;

                case R.id.menu_context_submit_otp:
                    //todo: submit otp for selected item
                    Toast.makeText(getActivity(), "Submit OTP", Toast.LENGTH_SHORT).show();

                    actionMode.finish(); // Action picked, so close the CAB
                    if (isAdded()) {
                        for (int i=0; i<selectedIds.size(); i++) {
                            UserProviderEntry provider = (UserProviderEntry) providerAdapter.getItem(selectedIds.keyAt(i));
                            ((MainActivity) getActivity()).showOTPDialog(provider);
                        }
                        providerAdapter.updateSyncStatus();
                    }

                    return true;

                case R.id.menu_context_edit:
                    //todo: launch edit dialog for selected item (will be only 1)
                    Toast.makeText(getActivity(), "Edit", Toast.LENGTH_SHORT).show();

                    actionMode.finish();
                    if (isAdded()) {
                        for (int i=0; i<selectedIds.size(); i++) {
                            UserProviderEntry provider = (UserProviderEntry) providerAdapter.getItem(selectedIds.keyAt(i));
                            ((MainActivity)getActivity()).showEditProviderDialog(provider);
                        }
                        providerAdapter.updateSyncStatus();
                    }
                    return true;

                case R.id.menu_context_delete:
                    //todo: launch delete dialog for selecte item (will be only 1)
                    Toast.makeText(getActivity(), "Delete", Toast.LENGTH_SHORT).show();

                    actionMode.finish();
                    if (isAdded()) {
                        for (int i=0; i<selectedIds.size(); i++) {
                            UserProviderEntry provider = (UserProviderEntry) providerAdapter.getItem(selectedIds.keyAt(i));
                            showRemoveProviderDialog(provider);
                        }
                        providerAdapter.updateSyncStatus();
                    }
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode actionMode) {
            Log.d(TAG, "onCreateActionMode() - START");

            providerAdapter.removeSelection();
            mActionMode=null;

        }
    };

    

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
        //selectedItemsList=new ArrayList<>();
        View rootView = inflater.inflate(R.layout.fragment_providers, container, false);
        providerList = (ListView) rootView.findViewById(R.id.providerList);
        welcomeLayout = (LinearLayout) rootView.findViewById(R.id.providerWelcomeLayout);
        addProviderButton = (Button) rootView.findViewById(R.id.addProviderButton);

        //rootView.findViewById(R.id.providerFragmentTopLayout).setPadding(0,0,0,getActivity().findViewById(R.id.tabs).getHeight());


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
        //((MainActivity)getActivity()).setProviderFragmentListener(this);


        Log.d(TAG, "onCreateView() - END");

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().findViewById(R.id.providerFragmentTopLayout).setPadding(0, 0, 0, getActivity().findViewById(R.id.tabs).getHeight());

        MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
        List<UserProviderEntry> providers = null;
        if (app.userProfileData.getUserprofile()!=null){
            providers = new ArrayList<>(app.userProfileData.getUserprofile());
        }
        else
        {
            providers = new ArrayList<>();
        }
        providerAdapter = new ProviderItemViewAdapter(getActivity(), providers);
        providerList.setAdapter(providerAdapter);
        providerList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);

        providerList.setMultiChoiceModeListener(mMultiChoiceModeListener);


    }

    //NOTE: setUserVisibleHint() is called when the fragment is no longer visible or becomes visible
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                Log.d("MyFragment", "Not visible anymore.  clearing menu.");
                if (mActionMode!=null) {
                    mActionMode.getMenu().clear();
                    mActionMode.finish();
                    if (providerAdapter!=null) providerAdapter.removeSelection();
                }
                // TODO stop audio playback
            }
        }
    }


    public boolean onSelectionChanged(android.view.ActionMode actionMode, int position)
    {
        /*
        NOTE: the sync status are:
        1. ready to sync
        2. sync in progress
        3. Sync in progress (OTP required)
         */


        boolean showSync=true;
        boolean showStopSync=true;
        boolean showEdit=true;
        boolean showVerify=true;
        boolean showDelete=true;

        MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();

        Menu menu = actionMode.getMenu();

        SparseBooleanArray selectedIds = providerAdapter.getSelectedIds();

        if (isAdded()){
            if (selectedIds.size()>0){
            //turn off the drawer menu
                ((MainActivity)getActivity()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
            else
            {
                ((MainActivity)getActivity()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }

        if (selectedIds.size()==1){
            Log.d(TAG, "onSelectionChanged() - selectedIds.size()==1");

            if (!((UserProviderEntry)providerAdapter.getItem(selectedIds.keyAt(0))).isFoundInDevice())
            {
                //dont show sync if the provier is not in device - its being added , so its the first time the provider is being added
                showSync = false;

                //if ready to sync - show edit, delete

                if (!app.getInstituionIdSyncStatus(((UserProviderEntry)providerAdapter.getItem(selectedIds.keyAt(0))).getIid()).equals(getResources().getString(R.string.pdvapi_sync_status_message_ready))){
                    if (!app.getInstituionIdSyncStatus(((UserProviderEntry)providerAdapter.getItem(selectedIds.keyAt(0))).getIid()).equals(getResources().getString(R.string.pdvapi_sync_status_message_in_progress_setverify)))
                    {
                        //no otp pending - dont show otp
                        showVerify=false;
                    }
                    else{
                        //sync in progress - so no edit, delete
                        showEdit=false;
                        showDelete=false;
                    }
                }
                else{
                    //ready to sync
                    showVerify=false;
                    showStopSync=false;
                }
                //if OTP required - show OTP
                //if
            }
            else{ //found in device
                if (!app.getInstituionIdSyncStatus(((UserProviderEntry)providerAdapter.getItem(selectedIds.keyAt(0))).getIid()).equals(getResources().getString(R.string.pdvapi_sync_status_message_ready)))
                {
                    //sync in progress - dont show sync,edit or delete
                    showSync=false;
                    showEdit=false;
                    showDelete=false;
                    if (!app.getInstituionIdSyncStatus(((UserProviderEntry)providerAdapter.getItem(selectedIds.keyAt(0))).getIid()).equals(getResources().getString(R.string.pdvapi_sync_status_message_in_progress_setverify)))
                    {
                        //no otp pending - dont show otp
                        showVerify=false;
                    }
                }
                else
                {
                    //ready
                    showStopSync=false;
                    showVerify=false;
                }
            }
        }
        else if (selectedIds.size()>1) {
            //we will not show edit, delete and OTP
            showVerify=false;
            showEdit=false;
            showDelete=false;

            //we will only show "sync" and "stop sync"

            for (int i = 0; i < selectedIds.size(); i++) {

                UserProviderEntry provider = (UserProviderEntry) providerAdapter.getItem(selectedIds.keyAt(i));
                if (!provider.isFoundInDevice()){
                    //we dont show sync if any of the items are not found in device
                    showSync=false;
                }
                else {
                    //we also dont show sync if any of the items are already syncing
                    if (!app.getInstituionIdSyncStatus(provider.getIid()).equals(getResources().getString(R.string.pdvapi_sync_status_message_ready)))
                    {
                        showSync=false;
                    }
                    //we dont show stop sync if any of the item is in ready state
                    else
                    {
                        showStopSync=false;
                    }
                }

            }
        }

        if(selectedIds.size()>0) {
            menu.getItem(CONTEXT_MENU_POSITION_SYNC).setVisible(showSync);
            menu.getItem(CONTEXT_MENU_POSITION_STOPSYNC).setVisible(showStopSync);
            menu.getItem(CONTEXT_MENU_POSITION_VERIFY).setVisible(showVerify);
            menu.getItem(CONTEXT_MENU_POSITION_EDIT).setVisible(showEdit);
            menu.getItem(CONTEXT_MENU_POSITION_DELETE).setVisible(showDelete);
        }
        else{
            menu.getItem(CONTEXT_MENU_POSITION_SYNC).setVisible(false);
            menu.getItem(CONTEXT_MENU_POSITION_STOPSYNC).setVisible(false);
            menu.getItem(CONTEXT_MENU_POSITION_VERIFY).setVisible(false);
            menu.getItem(CONTEXT_MENU_POSITION_EDIT).setVisible(false);
            menu.getItem(CONTEXT_MENU_POSITION_DELETE).setVisible(false);
        }

        return true;
    }

    @Deprecated
    public void onSelectAction()
    {

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



        providerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(getActivity(), "clicked="+Integer.toString(view.getId()), Toast.LENGTH_SHORT).show();

                final UserProviderEntry provider = (UserProviderEntry) providerAdapter.getItem(i);
                    if (provider.isFoundInDevice() &&
                            ((MoneyAppApp) getActivity().getApplication()).getInstituionIdSyncStatus(provider.getIid()).equals(getResources().getString(R.string.pdvapi_sync_status_message_ready))) {

                        new AlertDialog.Builder(getActivity())
                                .setTitle(provider.getDesc() + " (" + provider.getUid() + ")")
                                .setAdapter(popupMenuAdapter, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        //...
                                        Toast.makeText(getActivity(), "You Clicked : " + popupMenuItems[item].text, Toast.LENGTH_SHORT).show();
                                        PopupMenuClicked(provider, popupMenuItems, item);
                                    }
                                }).show();
                    } else if (!((MoneyAppApp) getActivity().getApplication()).getInstituionIdSyncStatus(provider.getIid()).equals(getResources().getString(R.string.pdvapi_sync_status_message_ready))) {

                        if (((MoneyAppApp) getActivity().getApplication()).getInstituionIdSyncStatus(provider.getIid()).equals(getResources().getString(R.string.pdvapi_sync_status_message_in_progress_setverify))) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(provider.getDesc() + " (" + provider.getUid() + ")")
                                    .setAdapter(popupMenuAdapterStopSyncSetVerify, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int item) {
                                            //...
                                            Toast.makeText(getActivity(), "You Clicked : " + popupMenuItemsStopSyncSetVerify[item].text, Toast.LENGTH_SHORT).show();
                                            PopupMenuClicked(provider, popupMenuItemsStopSyncSetVerify, item);
                                        }
                                    }).show();
                        } else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(provider.getDesc() + " (" + provider.getUid() + ")")
                                    .setAdapter(popupMenuAdapterStopSync, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int item) {
                                            //...
                                            Toast.makeText(getActivity(), "You Clicked : " + popupMenuItemsStopSync[item].text, Toast.LENGTH_SHORT).show();
                                            PopupMenuClicked(provider, popupMenuItemsStopSync, item);
                                        }
                                    }).show();
                        }

                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(provider.getDesc() + " (" + provider.getUid() + ")")
                                .setAdapter(popupMenuAdapterNoSync, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        //...
                                        Toast.makeText(getActivity(), "You Clicked : " + popupMenuItemsNoSync[item].text, Toast.LENGTH_SHORT).show();
                                        PopupMenuClicked(provider, popupMenuItemsNoSync, item);
                                    }
                                }).show();
                    }
                }

        });



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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        Log.d(TAG, "onPause() - START");

        super.onCreateOptionsMenu(menu, inflater);

        if (mActionMode!=null){
            mActionMode.getMenu().clear();
        }

        //menu.clear();


        //fragment specific menu creation
    }

    @Override
    public void onPause(){
        Log.d(TAG, "onPause() - START");

        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(pdvApiCallbackMessageReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(pdvApiOnStopMessageReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(pdvApiOnGetUserProfileSuccessMessageReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(pdvApiOnAddingNewProvider);

        //clear any selected items
        //providerAdapter.removeSelection();
        if (mActionMode!=null){
            mActionMode.finish();
        }

        Log.d(TAG, "onPause() - END");

    }

    //method to handle user clicking the popup menu (Sync, Edit, Delete provider)
    @Deprecated
    private void PopupMenuClicked(UserProviderEntry provider, ProviderPopupMenuItemData[] itemData, int item) {

        String itemString = itemData[item].text;
        MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();
        if (itemString.equals(getString(R.string.provider_menu_refresh_title))) {
            try {
                //sync the provider
                if (isAdded()) {
                    //XWALK RMEMOVE WebView pdvWebView = (WebView) getActivity().findViewById(R.id.ewise_webview);
                    XWalkView pdvWebView = (XWalkView) getActivity().findViewById(R.id.ewise_webview);
                    if (pdvWebView != null) {
                        app.pdvWebView = pdvWebView;
                        app.pdvApi.apiInit(getActivity().getApplicationContext(), app.pdvWebView);
                        app.syncExistingProvider(provider.getIid());
                        providerAdapter.updateSyncStatus();
                    }
                }
            }
            catch (Exception e){
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }

        } else if (itemString.equals(getString(R.string.provider_menu_edit_title))) {
            //edit the provider - show the edit provider dialogFragment
            if (isAdded()) {
                ((MainActivity)getActivity()).showEditProviderDialog(provider);
            }


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
            if (isAdded()){
                ((MainActivity)getActivity()).showOTPDialog(provider);
            }


        }
     }

    @Deprecated
    public void showOTPDialog(UserProviderEntry providerEntry){
        Log.d (TAG, "showOTPDialog instId=" + providerEntry.getIid());


        MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();

        EwiseOTPFragment otpFragment = null;
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
                EwiseOTPFragment prevFragment = (EwiseOTPFragment) prev;
                prevFragment.dismissAllowingStateLoss();
                ft.remove(prev);
            }

            otpFragment.show(getActivity().getSupportFragmentManager(), "OTP_DIALOG");
        }

    }

    @Deprecated
    public void showEditProviderDialog(UserProviderEntry providerEntry){
        //mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("edit_institution_prompts_dialog");
        if (prev != null) {
            EditProviderDialogFragment editProviderDialog = (EditProviderDialogFragment)prev;
            editProviderDialog.dismiss();
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        EditProviderDialogFragment newFragment = EditProviderDialogFragment.newInstance(providerEntry);
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



    public void updatePageData(){

        if (isAdded()) {

            //reset data fetching if there isn't any more requests
            MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
            if (app.userProfileData.getUserprofile() != null) {
                providerAdapter.swapData(new ArrayList<>(app.userProfileData.getUserprofile()));
                welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);
            }
        }
    }


    private BroadcastReceiver pdvApiOnGetUserProfileSuccessMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (isAdded()) {

                //reset data fetching if there isn't any more requests
                MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
                if (app.userProfileData.getUserprofile() != null) {
                    providerAdapter.swapData(new ArrayList<>(app.userProfileData.getUserprofile()));
                    welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);
                }
            }
        }
    };

    private BroadcastReceiver pdvApiOnStopMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //check if an error or success was received and handle it
            if (isAdded()) {

                String stringResults = intent.getStringExtra("stringResults");
                PdvApiResults results = PdvApiResults.objectFromString(stringResults, PdvApiResults.class);
                Log.d(TAG, "Received Broadcast message pdv-aca-stop-callback");

                providerAdapter.notifyDataSetChanged();
            }
        }
    };

    private BroadcastReceiver pdvApiOnAddingNewProvider = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAdded()) {

                Log.d(TAG, "Received Broadcast message pdv-api-adding-new-provider");
                providerAdapter.notifyDataSetChanged();
                MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
                welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);
            }
        }
    };

    private BroadcastReceiver pdvApiCallbackMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAdded()) {

                String apiName = intent.getStringExtra("apiName");
                String callbackStatus = intent.getStringExtra("callbackStatus");
                String sRequestParams = intent.getStringExtra("requestParams");
                String sResults = intent.getStringExtra("results");

                Log.d(TAG, "pdvApiCallbackMessageReceiver.onReceive() apiName:" + apiName);
                Log.d(TAG, "pdvApiCallbackMessageReceiver.onReceive() callbackStatus:" + callbackStatus);
                Log.d(TAG, "pdvApiCallbackMessageReceiver.onReceive() requestParams:" + sRequestParams);
                Log.d(TAG, "pdvApiCallbackMessageReceiver.onReceive() results:" + sResults);

                //todo: process the results
                providerAdapter.notifyDataSetChanged();
            }

        }
    };

}