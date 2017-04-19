package com.ewise.moneyapp;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.animation.Animator;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ewise.android.pdv.api.PdvApi;
import com.ewise.android.pdv.api.callbacks.PdvApiCallback;
import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.StatusCode;
import com.ewise.android.pdv.api.model.UserProviderEntry;
import com.ewise.android.pdv.api.model.consent.ConsentAppVO;
import com.ewise.android.pdv.api.model.consent.ConsentServiceResponse;
import com.ewise.android.pdv.api.model.consent.ConsentSvcVO;
import com.ewise.android.pdv.api.model.consent.ConsentUpdateRequest;
import com.ewise.android.pdv.api.model.response.AccountsResponse;
import com.ewise.android.pdv.api.model.response.GetPromptsData;
import com.ewise.android.pdv.api.model.response.TransactionsResponse;
import com.ewise.moneyapp.Fragments.EditProfilesDialogFragment;
import com.ewise.moneyapp.Fragments.EditProviderDialogFragment;
import com.ewise.moneyapp.Fragments.EwiseOTPFragment;
import com.ewise.moneyapp.Utils.FragmentPagerAdapterHelper;
import com.ewise.moneyapp.Utils.OnHomePressedListener;
import com.ewise.moneyapp.Utils.PdvApiName;
import com.ewise.moneyapp.Utils.PdvApiRequestParams;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;
import com.ewise.moneyapp.Utils.Settings;
import com.ewise.moneyapp.Utils.SignonProfile;
import com.ewise.moneyapp.Utils.SignonUser;
import com.ewise.moneyapp.adapters.SectionsPagerAdapter;
import com.ewise.moneyapp.adapters.SettingsMenuPagerAdapter;
import com.ewise.moneyapp.adapters.BillsMenuPagerAdapter;
import com.ewise.moneyapp.adapters.HelpMenuPagerAdapter;
import com.ewise.moneyapp.adapters.PermissionsMenuPagerAdapter;
import com.ewise.moneyapp.adapters.ReportIssueMenuPagerAdapter;
import com.ewise.moneyapp.adapters.TransferMenuPagerAdapter;
import com.ewise.moneyapp.service.PdvAcaBoundService;

import java.util.List;


public class MainActivity extends AppCompatActivity
        implements PdvConnectivityCallback, OnHomePressedListener
{
    private static final String TAG = "MainActivity";

    //private static final int TAB_POSITION_OTHER     = 4;


    enum LogoutReason {
        LOGOUT_REASON_BACKPRESSED,
        LOGOUT_REASON_HOMEPRESSED,
        LOGOUT_REASON_HOMELONGPRESSED,
        LOGOUT_REASON_MENUPRESSED
    }

    private DrawerLayout drawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private int navMenuItemId=0;

    //
    // public HomeWatcher homeWatcher = null;
    public AlertDialog logOutDialog=null;


    public interface FragmentUpdateListener {
        void refreshFragmentUI();
    }

    //todo: add a listener for each fragment that needs to be updated by the MainActivity
    FragmentUpdateListener providerFragmentUpdateListener=null;
    FragmentUpdateListener networthFragmentUpdateListener=null;
    FragmentUpdateListener accountsFragmentUpdateListener=null;
    FragmentUpdateListener budgetsFragmentUpdateListener=null;

    public void setProviderFragmentListener(FragmentUpdateListener listener){
        providerFragmentUpdateListener = listener;
    }

    public void setNetworthFragmentListener(FragmentUpdateListener listener){
        networthFragmentUpdateListener = listener;
    }


    public void setAccountsFragmentListener(FragmentUpdateListener listener){
        accountsFragmentUpdateListener = listener;
    }

    public void setBudgetsFragmentListener(FragmentUpdateListener listener){
        budgetsFragmentUpdateListener = listener;
    }


    private void refreshAttachedFragments(){

        if (providerFragmentUpdateListener!=null) { providerFragmentUpdateListener.refreshFragmentUI(); }
        if (accountsFragmentUpdateListener!=null) { accountsFragmentUpdateListener.refreshFragmentUI(); }
        if (networthFragmentUpdateListener!=null) { networthFragmentUpdateListener.refreshFragmentUI(); }
        if (budgetsFragmentUpdateListener!=null) { budgetsFragmentUpdateListener.refreshFragmentUI(); }

    }


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

//    private FloatingActionButton fab;
            //, fab1, fab2, fab3;
    TabLayout               tabLayout;
    FrameLayout             progress_overlay;
    //FrameLayout             main_activity_content;
    FrameLayout             menu_fragment_container;
    TextView                progressText;
    LinearLayout            loginErrorLayout;
    TextView                loginErrorText;
    Button                  loginRetryButton;

    LinearLayout            loginToAppErrorLayout;
    TextView                loginToAppErrorText;
    Button                  loginToAppRetryButton;

    private Animation       fab_open,fab_close,rotate_forward,rotate_backward;
    LinearLayout            fabLayout1, fabLayout2, fabLayout3;
    View                    fabBGLayout;
    boolean                 isFABOpen=false;
    public PdvApiResults    pdvApiResults;
    Handler                 handler = new Handler();
    Handler                 pdvApiRequestHandler = new Handler();

    public PdvAcaBoundService pdvAcaBoundService;
    public boolean pdvAcaServiceIsBound = false;
    public boolean isPdvConnected = false;

    ImageView imagePdvConnected;
    boolean checkConnectivity;


    private ServiceConnection pdvAcaServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PdvAcaBoundService.PdvAcaServiceBinder binder = (PdvAcaBoundService.PdvAcaServiceBinder) iBinder;
            pdvAcaBoundService = binder.getService();
            pdvAcaServiceIsBound = true;
            Log.d("MainActivity", "PdvAcaBoundService -> onServiceConnected()");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            pdvAcaServiceIsBound = false;
            Log.d("MainActivity", "PdvAcaBoundService -> onServiceDisconnected()");

        }
    };


    private Runnable        pdvApiRequestRunnable = new Runnable() {
        @Override
        public void run() {
            MoneyAppApp app = (MoneyAppApp) getApplication();

            if (app.isAppLoggedIn()) {

                if (app.pdvLoginStatus.isLoggedOffFromPdv() && !app.isPdvLoginFailed()) {
                    loginToPDV();
                }

                if (app.pdvLoginStatus.isLoggedOnToPdv()) {
                    if (!app.pdvApiRequestQueue.isRequestInProgress()) {
                        Log.d("PdvApiRequestRunnable", "no requests in progress");
                        PdvApiRequestParams requestParams = app.pdvApiRequestQueue.getNextRequestToExecute();
                        if (requestParams != null && pdvAcaServiceIsBound) {
                            Log.d("PdvApiRequestRunnable", "request available to execute");
                            pdvApiExecuteRequest(requestParams);
                            setDataFetchingStatus(true, null);
                        } else {
                            setDataFetchingStatus(false, null);

                        }
                    } else {
                        setDataFetchingStatus(true, null);
                    }
                }
                pdvApiRequestHandler.postDelayed(pdvApiRequestRunnable, 5000); //run every 5 seconds
            }
        }
    };

    private BroadcastReceiver pdvApiCallbackMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String apiName = intent.getStringExtra("apiName");
            String callbackStatus = intent.getStringExtra("callbackStatus");
            String sRequestParams = intent.getStringExtra("requestParams");
            String sResults = intent.getStringExtra("results");

            Log.d ("MainActivity", "pdvApiCallbackMessageReceiver.onReceive() apiName:" + apiName);
            Log.d ("MainActivity", "pdvApiCallbackMessageReceiver.onReceive() callbackStatus:" + callbackStatus);
            Log.d ("MainActivity", "pdvApiCallbackMessageReceiver.onReceive() requestParams:" + sRequestParams);
            Log.d ("MainActivity", "pdvApiCallbackMessageReceiver.onReceive() results:" + sResults);


            //reset data fetching if there isn't any more requests
            MoneyAppApp app = (MoneyAppApp) getApplication();
            if (!app.pdvApiRequestQueue.isRequestInProgress()){
                Log.d ("MainActivity", "pdvApiCallbackMessageReceiver.onReceive() - no more requests in progress");
                setDataFetchingStatus(false, null);

            }


            PdvApiResults results = PdvApiResults.objectFromString(sResults, PdvApiResults.class);

            //Handle OTP
            //todo: process the results
            if (callbackStatus.equals(StatusCode.STATUS_VERIFY)) {
                Log.d ("MainActivity", "pdvApiCallbackMessageReceiver.onReceive() - StatusCode = verify - OTP required");

                DialogFragment otpFragment = null;
                if (results.transactions!=null) {
                    //Handle OTP/Captcha for updateTransactions
                    otpFragment = EwiseOTPFragment.newInstance(results.transactions.getInstId(),
                            String.format(results.transactions.getMessage() + " %s.",
                                    app.getUserProviderEntry(results.transactions.getInstId()).getDesc()),
                            null  //todo: implement base64Image of captcha in v0.5.n
                    );

                }
                else if (results.accounts!=null) {
                    //Handle OTP/Captcha for updateAccounts
                    otpFragment = EwiseOTPFragment.newInstance(results.accounts.getInstId(),
                            String.format(results.accounts.getMessage() + " %s.",
                                    app.getUserProviderEntry(results.accounts.getInstId()).getDesc()),
                            null  //todo: implement base64Image of captcha in v0.5.n
                    );

                }

                if (otpFragment!=null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    Fragment prev = getSupportFragmentManager().findFragmentByTag("OTP_DIALOG");
                    if (prev != null) {
                        Log.d("MainActivity", "pdvApiCallbackMessageReceiver() - Removing previous OTP_DIALOG");
                        ft.remove(prev);
                    }

                    Log.d("MainActivity", "pdvApiCallbackMessageReceiver() - showing OTP_DIALOG");
                    otpFragment.show(getSupportFragmentManager(), "OTP_DIALOG");
                }

            }

            //if this response is related to adding a new provider, we need to reload the user profile to take the new profile data from the server
            //originally we used a temporary UserProviderEntry during adding of the new provider
            if (apiName.equals(PdvApiName.UPDATE_ACCOUNTS_WITH_NEW_CREDENTIALS.toString())){
                //reload only if the callback was a success and no errors
                if (!results.callBackError && results.callBackAllComplete) {
                    app.pdvGetUserProfile(MainActivity.this);
                }
            }
        }
    };

    private BroadcastReceiver pdvApiServiceErrorMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String title = intent.getStringExtra("title");
            final String message = intent.getStringExtra("message");

            Log.d("MainActivity", "pdvApiServiceErrorMessageReceiver.onReceive() title:" + title);
            Log.d("MainActivity", "pdvApiServiceErrorMessageReceiver.onReceive() message:" + message);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showErrorAlertDialog(title, message);
                }
            });
        }

    };

    private void showErrorAlertDialog(String title, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                // do nothing - just close this dialog and go back to previous activity
                dialog.cancel();
            }
        });
        builder.setMessage(message)
                .setTitle(title);
                //.setIcon(getResources().getIdentifier(groupedInstitution.getGroupId(), "drawable", getBaseContext().getPackageName()));
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void setDataFetchingStatus(final boolean status, final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status){
                    findViewById(R.id.linearLayoutFetchingData).setVisibility(View.VISIBLE);
                    findViewById(R.id.progressBarRunning).setVisibility(View.VISIBLE);
                    ((ImageView)findViewById(R.id.imagePDVConnected)).setImageResource(R.drawable.ewise_pdv_refresh_material_white);
                    if (message!=null){
                        ((TextView)findViewById(R.id.progressBarText)).setText(message);
                    }
                    else{
                        ((TextView)findViewById(R.id.progressBarText)).setText(getString(R.string.pdv_api_fetching_data));
                    }
                }
                else{
                    findViewById(R.id.linearLayoutFetchingData).setVisibility(View.GONE);
                }
            }
        });
    }

    public void setProgressStatus(final boolean status, final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status){
                    progress_overlay.setVisibility(View.VISIBLE);
                    if (message!=null){
                        progressText.setText(message);
                    }
                    else{
                        progressText.setText(getString(R.string.pdv_api_fetching_data));
                    }
                }
                else{
                    progress_overlay.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setAppLoginStatus(final boolean status, final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status){
                    //login success
                    Log.d("MainActivity", "setAppLoginStatus: SUCCESS");
                    loginToAppErrorLayout.setVisibility(View.GONE);
                    loginToAppErrorText.setText(message != null ? message : "");
                }
                else{
                    //login failed
                    Log.d("MainActivity", "setAppLoginStatus: FAIL");
                    loginToAppErrorLayout.setVisibility(View.VISIBLE);
                    loginToAppErrorText.setText(message);
                    progress_overlay.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void checkPdvConnectivity()
    {
        MoneyAppApp app = (MoneyAppApp)getApplication();
        app.checkConnectivity(this,MoneyAppApp.DEFAULT_SWAN_HOST,this);
    }

    @Override
    public void onBackPressed() {
        logoutFromApp(LogoutReason.LOGOUT_REASON_BACKPRESSED);
    }

    @Override
    public void onHomePressed()
    {
        Log.d("MainActivity", "onHomePressed()");
        /* todo: **SN** too much friction to user on home and long press
        if (MainActivity.this!=null){
            if (!MainActivity.this.isFinishing()) {
                logoutFromApp(LogoutReason.LOGOUT_REASON_HOMEPRESSED);
            }
        }
        */

    }

    @Override
    public void onHomeLongPressed()
    {
        Log.d("MainActivity", "onHomeLongPressed()");
        /* todo: **SN** too much friction to user on home and long press
        if (MainActivity.this!=null){
            if (!MainActivity.this.isFinishing()) {
                logoutFromApp(LogoutReason.LOGOUT_REASON_HOMELONGPRESSED);
            }
        }
        */


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTheme(R.style.AppTheme_NoActionBar); //remove the splash theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MoneyAppApp myApp = ((MoneyAppApp) getApplication());


        //homeWatcher = new HomeWatcher(this);
        //homeWatcher.setOnHomePressedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.navigation_menu_icon);
        setSupportActionBar(toolbar);

        // Find our drawer view
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);



        // Setup drawer view
        setupDrawerContent(nvDrawer);

        //menu_fragment_container = (FrameLayout) findViewById(R.id.menu_fragment_container);
        //menu_fragment_container.setVisibility(View.GONE);

        //main_activity_content = (FrameLayout) findViewById(R.id.main_activity_content);
        //menu_fragment_container.setVisibility(View.VISIBLE);


        progress_overlay = (FrameLayout) findViewById(R.id.progress_overlay);
        progressText = (TextView) findViewById(R.id.progressText);

        loginErrorLayout = (LinearLayout) findViewById(R.id.loginErrorLayout);
        loginErrorText = (TextView) findViewById(R.id.loginErrorText);
        loginRetryButton = (Button) findViewById((R.id.loginRetryButton));

        loginRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProgressStatus(true, getString(R.string.pdv_api_login_to_pdv));
                ((MoneyAppApp)getApplication()).retryPdvLogin();
                loginErrorLayout.setVisibility(View.GONE);
            }
        });


        loginToAppErrorLayout = (LinearLayout) findViewById(R.id.loginToAppErrorLayout);
        loginToAppErrorText = (TextView) findViewById(R.id.loginToAppErrorText);
        loginToAppRetryButton = (Button) findViewById((R.id.loginToAppRetryButton));

        loginToAppRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginToAppErrorLayout.setVisibility(View.GONE);
                startLoginActivity();
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(1);
        //mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager = FragmentPagerAdapterHelper.setViewPagerToAdapter(mViewPager, this, getSupportFragmentManager(), SectionsPagerAdapter.class, false);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);




//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fabLayout1= (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2= (LinearLayout) findViewById(R.id.fabLayout2);
        fabLayout3= (LinearLayout) findViewById(R.id.fabLayout3);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fabAddProvider = (FloatingActionButton) findViewById(R.id.fab1);
        FloatingActionButton fabDoTransfer= (FloatingActionButton) findViewById(R.id.fab2);
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fabBGLayout=findViewById(R.id.fabBGLayout);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
                /* **SN** todo: remove
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
                */

            }
        });

        fabAddProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startAddProviderActivity();
                animateFAB();
            }

        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
            }
        });


        imagePdvConnected = (ImageView) findViewById(R.id.imagePDVConnected);

        imagePdvConnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoneyAppApp app = (MoneyAppApp)getApplication();
                if (!app.pdvLoginStatus.isLoggedOnToPdv() && !app.pdvLoginStatus.isLoginInProgress()){
                    //attempt to login


                }
            }
        });

        pdvApiResults = new PdvApiResults();
        PdvApi pdvApi = myApp.getPdvApi();
        myApp.pdvWebView = (WebView) findViewById(R.id.ewise_webview);
//**XWALK**        myApp.pdvWebView = (XWalkView) findViewById(R.id.ewise_webview);

        try {
            pdvApi.apiInit(getApplicationContext(), myApp.pdvWebView);
            myApp.checkConnectivity(this, MoneyAppApp.DEFAULT_SWAN_HOST, this);
            //if successfully connected, the callback will handle the login
            if (!myApp.isAppLoggedIn()) {
                startLoginActivity();
            }
            else {
                pdvApiRequestRunnable.run();
            }
        } catch (Exception e) {
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            String sObjString = myApp.pdvApi.toString();
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, sObjString);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem.getItemId());
                        menuItem.setChecked(true);
                        return true;
                    }
                });
    }


    public void selectDrawerItem(int navMenuItemId) {
        // Create a new fragment and specify the fragment to show based on nav item clicked

        if (mViewPager==null) {
            return;
        }

        switch(navMenuItemId) {
            case R.id.navHeaderSettingsIcon:
                showTabs(SettingsMenuPagerAdapter.class);
                mViewPager.setCurrentItem(SettingsMenuPagerAdapter.TAB_POSITION_SETTINGS);
                break;
            case R.id.navMenuAccounts:
                showTabs(SectionsPagerAdapter.class);
                mViewPager.setCurrentItem(SectionsPagerAdapter.TAB_POSITION_ACCOUNTS);
                refreshAttachedFragments();
                break;
            case R.id.navMenuProviders:
                showTabs(SectionsPagerAdapter.class);
                mViewPager.setCurrentItem(SectionsPagerAdapter.TAB_POSITION_PROVIDERS);
                refreshAttachedFragments();
                break;
            case R.id.navMenuNetworth:
                showTabs(SectionsPagerAdapter.class);
                mViewPager.setCurrentItem(SectionsPagerAdapter.TAB_POSITION_NETWORTH);
                refreshAttachedFragments();
                break;
            case R.id.navMenuBudgets:
                showTabs(SectionsPagerAdapter.class);
                mViewPager.setCurrentItem(SectionsPagerAdapter.TAB_POSITION_SPENDING);
                refreshAttachedFragments();
                break;
            case R.id.navMenuBills:
                showTabs(BillsMenuPagerAdapter.class);
                mViewPager.setCurrentItem(BillsMenuPagerAdapter.TAB_POSITION_BILLS);
                break;
            case R.id.navMenuTransfer:
                showTabs(TransferMenuPagerAdapter.class);
                mViewPager.setCurrentItem(TransferMenuPagerAdapter.TAB_POSITION_TRANSFER);
                break;
            case R.id.navMenuSendIssue:
                showTabs(ReportIssueMenuPagerAdapter.class);
                mViewPager.setCurrentItem(ReportIssueMenuPagerAdapter.TAB_POSITION_REPORTISSUE);
                break;
            case R.id.navMenuPermissions:
                showTabs(PermissionsMenuPagerAdapter.class);
                mViewPager.setCurrentItem(PermissionsMenuPagerAdapter.TAB_POSITION_PERMISSIONS);
                break;
            case R.id.navMenuLogout:
                logoutFromApp(LogoutReason.LOGOUT_REASON_MENUPRESSED);
                break;
            case R.id.navMenuHelp:
                showTabs(HelpMenuPagerAdapter.class);
                mViewPager.setCurrentItem(HelpMenuPagerAdapter.TAB_POSITION_HELP);
                break;
            default:
                break;
        }


        drawer.closeDrawers();
    }


    private void showTabs (Class pagerAdapterClass){
        Log.d(TAG, "showTabs() : pagerAdapterClass=" + pagerAdapterClass.getName());

        mViewPager = FragmentPagerAdapterHelper.setViewPagerToAdapter(mViewPager, this,getSupportFragmentManager(), pagerAdapterClass, false);

        if (mViewPager==null) return;

        // Set up the ViewPager with the sections adapter.
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        synchronized (mViewPager){
            mViewPager.notifyAll();
            mViewPager.getAdapter().notifyDataSetChanged();
        }

    }





        @Override
    protected void onResume() {

        super.onResume();

            MoneyAppApp app = (MoneyAppApp)getApplication();

            app.pdvWebView = (WebView) findViewById(R.id.ewise_webview);
            app.pdvApi.apiInit(this, app.pdvWebView);

            Intent intent= new Intent(this, PdvAcaBoundService.class);
        bindService(intent, pdvAcaServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(pdvApiCallbackMessageReceiver,
                new IntentFilter("pdv-aca-bound-service-callback"));

        LocalBroadcastManager.getInstance(this).registerReceiver(pdvApiServiceErrorMessageReceiver,
                new IntentFilter("pdv-aca-service-error"));

        if (app.isAppLoggedIn() && !app.isPdvLoginFailed()){
            this.progress_overlay.setVisibility(View.GONE);

            Settings settings = Settings.getInstance(this);
            if (settings.getSignOnUsers()!=null) {
                TextView user = (TextView) nvDrawer.getHeaderView(0).findViewById(R.id.navHeaderProfileNameTxt);
                SignonUser activeUser = settings.getActiveUser(this);
                if (user != null) {
                    user.setText(activeUser.name);
                }
                TextView email = (TextView) nvDrawer.getHeaderView(0).findViewById(R.id.navHeaderProfileEmailTxt);
                if (email != null) {
                    email.setText(activeUser.email);
                }
                //populate profiles spinner
                Spinner profileSpinner = (Spinner) nvDrawer.getHeaderView(0).findViewById(R.id.navHeaderProfileSpinner);
                String profileArray[] = new String[activeUser.profiles.size()];
                for (int i=0; i<activeUser.profiles.size(); i++){
                    profileArray[i] = activeUser.profiles.get(i).name.toLowerCase();
                }
                ArrayAdapter<String> profileAdapter = new ArrayAdapter<String>(this, R.layout.profile_spinner_item, profileArray);
                profileAdapter.setDropDownViewResource(R.layout.profile_spinner_dropdown_item);
                profileSpinner.setAdapter(profileAdapter);

                ImageView settingsImage= (ImageView) nvDrawer.getHeaderView(0).findViewById(R.id.navHeaderSettingsIcon);

                settingsImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectDrawerItem(R.id.navHeaderSettingsIcon);
                    }
                });
            }
        }

    }


    @Override
    protected void onStart(){
        super.onStart();
        //homeWatcher.startWatch();
        //Log.d("MainActivity", "onStop() : homeWatcher.startWatch()");

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(pdvAcaServiceConnection);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pdvApiCallbackMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pdvApiServiceErrorMessageReceiver);
    }

    @Override
    protected void onStop(){
        super.onStop();

        //homeWatcher.stopWatch();
        //Log.d("MainActivity", "onStop() : homeWatcher.stopWatch()");

        //unbindService(pdvAcaServiceConnection);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        MoneyAppApp app = (MoneyAppApp) getApplication();

        //todo: remove debug code
        Log.d("MainActivity", "onActivityResult() - requestCode=" + Integer.toString(requestCode));

        if (requestCode == MoneyAppApp.LOGIN_REQUEST) {
            //if login is successful, then allow the PDV login to continue...
            Log.d("MainActivity", "onActivityResult() - app.isAppLoggedIn()=" + Boolean.toString(app.isAppLoggedIn()));
            if (app.isAppLoggedIn()){

                setAppLoginStatus(true, null);
                pdvApiRequestRunnable.run();
            }
            else {
                //login failed
                setAppLoginStatus(false, getString(R.string.app_login_failed_message));
            }

        }

        //if the result from the add provider form is returned
        if ((data !=null) && (requestCode == MoneyAppApp.ADD_PROVIDER_LIST_REQUEST)){
            String jsonPromptsData = data.getStringExtra("promptsData");
            String instCode = data.getStringExtra("instCode");
            String instName = data.getStringExtra("instName");

            GetPromptsData promptsData = PdvApiResults.objectFromString(jsonPromptsData, GetPromptsData.class);

            setDataFetchingStatus(true, null);

            Log.d("MainActivity-instCode", PdvApiResults.toJsonString(promptsData));



            app.addNewProvider(instCode, instName, promptsData);

            Log.d("MainActivity", "onActivityResult() : **calling refreshAttachedFragments()**");
            refreshAttachedFragments();
        }
    }

    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }

    public boolean logoutFromApp(LogoutReason reason){
        MoneyAppApp app = (MoneyAppApp)getApplication();
        String message = getString(R.string.logout_dialog_message);
        boolean doLogOut = true;

        if (app.pdvApiRequestQueue.isRequestInProgress() || app.pdvApiRequestQueue.isRequestPending()){
            switch (reason){
                case LOGOUT_REASON_BACKPRESSED:
                    message = getString(R.string.logout_dialog_backpressed_sync_message);
                    break;
                case LOGOUT_REASON_HOMEPRESSED:
                    message = getString(R.string.logout_dialog_homepressed_sync_message);
                    //dont logout because screen wont stay for user to actually do anything, logout will get handled by the session timeout later
                    doLogOut=false;
                    break;
                case LOGOUT_REASON_HOMELONGPRESSED:
                    message = getString(R.string.logout_dialog_homelongpressed_sync_message);
                    //dont logout because screen wont stay for user to actually do anything, logout will get handled by the session timeout later
                    doLogOut=false;
                    break;
                case LOGOUT_REASON_MENUPRESSED:
                    message = getString(R.string.logout_dialog_menupressed_sync_message);
                    break;
                default:
                    break;
            }

        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (doLogOut){
            builder.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ProgressDialog p=null;
                    if (MainActivity.this!=null){
                        p = new ProgressDialog(MainActivity.this);
                        p.setMessage(getString(R.string.logout_loading_message));
                        p.setIndeterminate(true);
                        p.show();
                    }
                    MoneyAppApp app = (MoneyAppApp) getApplication();
                    app.setAppLoggedOff();
                    app.pdvLoginStatus.notifyLoggedOffFromPdv();
                    startLoginActivity();
                    //homeWatcher.stopWatch(); //no need to watch anymore
                    if (p!=null){
                        if (p.isShowing()) {
                            p.hide();
                        }
                    }

                }
            });
        }



        builder.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                // do nothing - just close this dialog and go back to previous activity
                dialog.cancel();
            }
        });

        builder.setMessage(message)
                .setTitle(getString(R.string.logout_dialog_title));

        if (logOutDialog!=null){
            if (logOutDialog.isShowing()){
                logOutDialog.cancel();
            }
        }

        logOutDialog = builder.create();
        logOutDialog.show();

        return true;

    }

    public void startLoginActivity(){

        if (!((MoneyAppApp) getApplication()).isAppLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, MoneyAppApp.LOGIN_REQUEST);
        }
    }

    public void startAddProviderActivity(){
        MoneyAppApp app = (MoneyAppApp)getApplication();

        if (app.pdvLoginStatus.isLoggedOnToPdv()
                && (!app.pdvApiRequestQueue.isRequestInProgress() || (!app.pdvApiRequestQueue.isRequestPending()))){
            startActivityForResult(new Intent(MainActivity.this, AddInstitutionActivity.class), MoneyAppApp.ADD_PROVIDER_LIST_REQUEST);
        }
        else if (!app.pdvApiRequestQueue.isRequestInProgress() || !app.pdvApiRequestQueue.isRequestPending())
        {
            Toast.makeText(getApplicationContext(), R.string.pdvapi_not_loggedin, Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), R.string.pdvapi_request_inprogress, Toast.LENGTH_LONG).show();
        }

    }

    private void loginToPDV()
    {
        Log.d("MainActivity", "loginToPDV() - START");
        Runnable login = new Runnable(){
            @Override
            public void run(){
                Log.d("MainActivity", "loginToPDV() : Runnable login - START");

                //String username = myApp.getIMEI();//todo: get imei number by allowing READ_PHONE_STATE permission
                //todo: remove hardcoded username (i.e. for production we need to allow a login user name on first time login and Persistent PIN for access)
                //      login first time should prevent an existing login from being re-used without proper authorisation - i.e. an authentication mechanism is needed - maybe a login TOKEN
                setDataFetchingStatus(true, getString(R.string.pdv_api_login_to_pdv));
                setProgressStatus(true, getString(R.string.pdv_api_login_to_pdv));
                String username = MoneyAppApp.DEFAULT_USERNAME;
                final MoneyAppApp myApp = (MoneyAppApp)getApplication();
                final PdvApi pdvApi = myApp.getPdvApi();
                myApp.pdvLoginStatus.notifyLogonInProgress();
                pdvApi.setUser(username, new PdvApiCallback<String>() {
                    @Override
                    public void result(final Response response) {
                        pdvApiResults.setUserResponse = response;
                        Log.d("SETUSER", response.getStatus());
                        if (StatusCode.STATUS_SUCCESS.equals(response.getStatus())) {
                            pdvApi.initialise(new PdvApiCallback.PdvApiInitialiseCallback() {
                                @Override
                                public void result(final String status) {
                                    pdvApiResults.initialiseStatus = status;
                                    pdvApiResults.callBackCompleted = true;
                                    Log.d("INIT", status);
                                    if (status.equals(StatusCode.STATUS_SUCCESS)){
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                notifyPdvLoginSuccess();
                                            }
                                        });
                                    }
                                    else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                notifyPdvLoginFail();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        else {
                            pdvApiResults.callBackCompleted = true;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    notifyPdvLoginFail();
                                }
                            });
                        }
                    }
                });

                Log.d("MainActivity", "loginToPDV() : Runnable login - END");

            }
        };

        new Thread(login).start();
    }

    public void notifyPdvLoginSuccess(){
        Log.d("MainActivity", "notifyPdvLoginSuccess() - START");
        //todo: whatever required when login is successful including UI updates
        String msg = getString(R.string.pdvapi_login_success_message);
        msg = msg + " - setUser(): " + pdvApiResults.setUserResponse.getStatus() + " : " + pdvApiResults.setUserResponse.getMessage() + " - initialise(): " + pdvApiResults.initialiseStatus;
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
        ((ImageView)findViewById(R.id.imagePDVConnected)).setImageResource(R.drawable.ewise_pdv_connected_material_white);//connected


        //check and set consent to avoid error
        //todo: remove this later when error is fixed! new version integrated
        final MoneyAppApp myApp = (MoneyAppApp)getApplication();


        //todo: remove this after xwalk integration - we just need to remove default consent because of the data sharing bug
        myApp.pdvApi.getConsent(new PdvApiCallback<ConsentServiceResponse>() {
            @Override
            public void result(Response<ConsentServiceResponse> response) {
                ConsentServiceResponse r = response.getData();
                List<ConsentAppVO> consentAppVOs = r.getConsents();
                String sAppConsentVOs = PdvApiResults.toJsonString(consentAppVOs);
                Log.d("GETCONSENT", "GET consentAppVOs=" + sAppConsentVOs);

                if (consentAppVOs!=null){
                    //set all conset to false;
                    boolean mustUpdateConsent = false;
                    for (ConsentAppVO cavo : consentAppVOs){
                        if (cavo.getGrantConsent().booleanValue()){
                            cavo.setGrantConsent(Boolean.FALSE);
                            List<ConsentSvcVO> csvoList = cavo.getServices();
                            for (ConsentSvcVO csvo: csvoList){
                                if (csvo.getGrantConsent().booleanValue()){
                                    csvo.setGrantConsent(Boolean.FALSE);
                                }
                            }
                            mustUpdateConsent = true;
                        }
                    }
                    if (mustUpdateConsent){
                        ConsentUpdateRequest cuRequest = new ConsentUpdateRequest();
                        cuRequest.setUserId(consentAppVOs.get(0).getUserId());
                        cuRequest.setAppVOs(consentAppVOs);
                        String sCuRequest = PdvApiResults.toJsonString(consentAppVOs);
                        Log.d("SETCONSENT", "SET ConsentUpdateRequest=" + sCuRequest);
                        myApp.pdvApi.setConsent(cuRequest, new PdvApiCallback<ConsentServiceResponse>() {
                            @Override
                            public void result(Response<ConsentServiceResponse> response) {
                                Log.d("MainActivity", "notifyPdvLoginSuccess() - setConsent status=" + response.getStatus());

                                ConsentServiceResponse r2 = response.getData();
                                List<ConsentAppVO> consentAppVOs2 = r2.getConsents();
                                String sAppConsentVOs2 = PdvApiResults.toJsonString(consentAppVOs2);
                                Log.d("SETCONSENT", "SET consentAppVOs=" + sAppConsentVOs2);
                            }
                        });
                    }
                }
            }
        });

        //if login is successful get the user profile
        setDataFetchingStatus(true, getString(R.string.pdv_api_fetch_user_profile));
        setProgressStatus(true, getString(R.string.pdv_api_fetch_user_profile));
        myApp.pdvGetUserProfile(this);

        Log.d("MainActivity", "notifyPdvLoginSuccess() - END");

    }

    public void notifyPdvLoginFail(){

        Log.d("MainActivity", "notifyPdvLoginFail() - START");

        //todo: whatever required when login is successful including UI updates
        String msg = getString(R.string.pdvapi_login_failed_message);
        msg = msg + " - setUser(): " + pdvApiResults.setUserResponse.getStatus() + " : " + pdvApiResults.setUserResponse.getMessage() + " - initialise(): " + pdvApiResults.initialiseStatus;
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
        ((ImageView)findViewById(R.id.imagePDVConnected)).setImageResource(R.drawable.ewise_pdv_disconnected_material_white);


        MoneyAppApp app = (MoneyAppApp)getApplication();
        app.pdvLoginStatus.notifyLoggedOffFromPdv();


        Log.d("MainActivity", "notifyPdvLoginFail(): **calling refreshAttachedFragments()**");
        refreshAttachedFragments();

        setDataFetchingStatus(false, null);
        setProgressStatus(true, getString(R.string.pdvapi_login_failed_message));
        app.setPdvLoginFailed();
        loginErrorText.setText(msg);
        loginErrorLayout.setVisibility(View.VISIBLE);


        Log.d("MainActivity", "notifyPdvLoginFail() - END");

    }


    //START - PdvConnectivityCallbacks interface implementations
    @Override
    public void onPdvConnected() {
        Log.d("MainActivity", "onPdvConnected() - START");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("MainActivity", "onPdvConnected() : Run() - START");
                isPdvConnected = true;

                ((ImageView)findViewById(R.id.imagePDVConnected)).setImageResource(R.drawable.ewise_pdv_connected_material_white);

            }
        });
        Log.d("MainActivity", "onPdvConnected() - END");

    }

    @Override
    public void onPdvDisconnected(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isPdvConnected = false;
                ((ImageView)findViewById(R.id.imagePDVConnected)).setImageResource(R.drawable.ewise_pdv_disconnected_material_white);

                Toast.makeText(MainActivity.this, getString(R.string.pdvapi_not_connected), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onGetInstitutionsFail(PdvApiResults results){
        Log.d("MainActivity", "onGetInstitutionsFail() - START");

        //do not call app.pdvGetInstitutions() more than once in MainActivity
        final String msg = getString(R.string.pdvapi_on_getinstitution_fail_message);
        String.format(msg, results.userProfile.getMessage());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayLongToastMessage(msg);
            }
        });
        ((MoneyAppApp)getApplication()).pdvLoginStatus.notifyLoggedOffFromPdv();

        Log.d("MainActivity", "onGetInstitutionsFail() : **calling refreshAttachedFragments()**");
        refreshAttachedFragments();

        Log.d("MainActivity", "onGetInstitutionsFail() - END");

    }

    @Override
    public void onGetInstitutionsSuccess(PdvApiResults results){

        Log.d("MainActivity", "onGetInstitutionsSuccess() - START");

        setDataFetchingStatus(true, getString(R.string.pdv_api_fetch_accounts));
        setProgressStatus(true, getString(R.string.pdv_api_fetch_accounts));


        //((MoneyAppApp)getApplication()).checkConnectivity(this, MoneyAppApp.DEFAULT_SWAN_HOST, this);

        //restore all accounts
        ((MoneyAppApp)getApplication()).pdvRestoreAllProviderAccounts(this);



        Log.d("MainActivity", "onGetInstitutionsSuccess() - END");

    }


    @Override
    public void onGetPromptsSuccess(PdvApiResults results){
        //not used
    }

    @Override
    public void onGetPromptsFail(PdvApiResults results){
        //not used
    }

    @Override
    public void onGetUserProfileSuccess(PdvApiResults results){

        Log.d("MainActivity", "onGetUserProfileSuccess() - START");
        //load institution data for later use
        setDataFetchingStatus(true, getString(R.string.pdv_api_fetch_institutions));
        setProgressStatus(true, getString(R.string.pdv_api_fetch_institutions));

        ((MoneyAppApp)getApplication()).pdvGetInstitutions(this);
        Log.d("MainActivity", "onGetUserProfileSuccess() - END");

    }

    @Override
    public void onGetUserProfileFail(PdvApiResults results){

        final String msg = getString(R.string.pdvapi_on_getuserprofile_fail_message);
        String.format(msg, results.userProfile.getMessage());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayLongToastMessage(msg);
            }
        });
    }

    @Override
    public void onRestoreAccountsComplete(String instId){

    }

    @Override
    public void onRestoreAccountsAllComplete(){

        setDataFetchingStatus(false, null);
        setProgressStatus(false, null);

        //notify reloads for institutions
        Intent intent = new Intent("pdv-on-get-user-profile-success");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        ((MoneyAppApp)getApplication()).pdvLoginStatus.notifyLoggedOnToPdv();
        Log.d("MainActivity", "onRestoreAccountsAllComplete() : **calling refreshAttachedFragments()**");
        refreshAttachedFragments();

    }

    @Override
    public void onRestoreAccountsNone()
    {
        setProgressStatus(false,null);
        setDataFetchingStatus(false,null);

        ((MoneyAppApp)getApplication()).pdvLoginStatus.notifyLoggedOnToPdv();
        Log.d("MainActivity", "onRestoreAccountsNone() : **calling refreshAttachedFragments()**");
        refreshAttachedFragments();

    }

    @Override
    public void onRestoreAccountsFail()
    {
        setProgressStatus(false,null);
        setDataFetchingStatus(false,null);

        ((MoneyAppApp)getApplication()).pdvLoginStatus.notifyLoggedOnToPdv();
        Log.d("MainActivity", "onRestoreAccountsFail() : **calling refreshAttachedFragments()**");
        refreshAttachedFragments();

    }

    @Override
    public void onRestoreTransactionsAllComplete(PdvApiResults results){}

    @Override
    public void onRestoreTransactionsFail(PdvApiResults results){}

    @Override
    public void onGetCredentialSuccess(PdvApiResults results) {
    }

    @Override
    public void onGetCredentialFail(PdvApiResults results) {

    }

    @Override
    public void onSetCredentialSuccess(PdvApiResults results)
    {

    }

    @Override
    public void onSetCredentialFail(PdvApiResults results)
    {

    }

    @Override
    public void onRemoveInstitutionSuccess(PdvApiResults results)
    {
        //todo: recall userprofile
        setDataFetchingStatus(true, getString(R.string.delete_provider_toast_message));
        MoneyAppApp app = (MoneyAppApp)getApplication();
        app.pdvGetUserProfile(this);


    }

    @Override
    public void onRemoveInstitutionFail(PdvApiResults results)
    {

    }

    //End: PdvConnectivityCallback implementation
    public void displayLongToastMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void displayShortToastMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //execute this method when there is anything in the request queue
    public void pdvApiExecuteRequest(PdvApiRequestParams requestParams){

        PdvApi pdvApi = ((MoneyAppApp)getApplication()).getPdvApi();

        if (requestParams.pdvApiName.equals(PdvApiName.UPDATE_ACCOUNTS_WITH_NEW_CREDENTIALS)){
            Log.d("pdvApiExecuteRequest()", "About to Update accounts with new credentials");
            if (pdvAcaServiceIsBound && pdvAcaBoundService!=null){
                //pdvAcaBoundService.updateAccounts(pdvApi, requestParams);
                pdvAcaBoundService.updateTransactionsNewCredentials(pdvApi, requestParams);
            }
        }
        else if (requestParams.pdvApiName.equals(PdvApiName.UPDATE_TRANSACTIONS)){
            Log.d("pdvApiExecuteRequest()", "About to Update transactions with existing credentials");
            if (pdvAcaServiceIsBound && pdvAcaBoundService!=null){
                pdvAcaBoundService.updateTransactions(pdvApi, requestParams);
            }
        }

        Log.d("MainActivity", "pdvApiExecuteRequest() : **calling refreshAttachedFragments()**");
        refreshAttachedFragments();
    }


    public void animateFAB(){

        if(isFABOpen){

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.startAnimation(rotate_backward);
            fabBGLayout.setVisibility(View.GONE);
            fabLayout1.setVisibility(View.GONE);
            fabLayout2.setVisibility(View.GONE);

            fabLayout1.startAnimation(fab_close);
            fabLayout2.startAnimation(fab_close);

            fabLayout2.animate().translationY(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if(!isFABOpen){

                        fabLayout1.setVisibility(View.GONE);
                        fabLayout2.setVisibility(View.GONE);

                    }

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            isFABOpen = false;

        } else {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fabBGLayout.setVisibility(View.VISIBLE);

            fab.startAnimation(rotate_forward);

            fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
            fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));

            fabLayout1.setVisibility(View.VISIBLE);
            fabLayout2.setVisibility(View.VISIBLE);

            fabLayout1.startAnimation(fab_open);
            fabLayout2.startAnimation(fab_open);

            isFABOpen = true;

        }
    }

    private void showFABMenu(){

        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.GONE);
        fabBGLayout.setVisibility(View.VISIBLE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.clearAnimation();
        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));

        isFABOpen=true;

    }

    private void closeFABMenu(){
        fabBGLayout.setVisibility(View.GONE);
        isFABOpen=false;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.animate().rotationBy(-180);
        fab.getPivotX();
        fab.clearAnimation();


        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){

                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);

                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == android.R.id.home){
            drawer.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAccountFragmentInteraction (Uri uri) {
        //empty for now
        return;
    }


    public void onAccountsFragmentInteraction (Uri uri) {
        //empty for now
        return;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    /*
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            if (position == TAB_POSITION_ACCOUNTS) {
                //return the AccountFragment class
                //return AccountFragment.newInstance (position + 1);
                return AccountsFragment_.newInstance();
            }
            else if (position == TAB_POSITION_PROVIDERS){
                //todo: return the providers fragment
                return ProvidersFragment.newInstance(position);

            }
            else if (position == TAB_POSITION_NETWORTH){
                return NetworthFragment.newInstance(position);
            }
            else if (position == TAB_POSITION_SPENDING){
                return BudgetsFragment.newInstance(position);
            }
            else{
                return PlaceholderFragment.newInstance(position + 1);
            }

        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case TAB_POSITION_PROVIDERS:
                    return getString(R.string.section_name_providers);
                case TAB_POSITION_NETWORTH:
                    return getString(R.string.section_name_networth);
                case TAB_POSITION_ACCOUNTS:
                    return getString(R.string.section_name_accounts);
                case TAB_POSITION_SPENDING:
                    return getString(R.string.section_name_spending);
            }
            return null;
        }

    }

*/


    public void addNewSignonProfile (SignonProfile signonProfile){

    }


    public void saveSignonProfile (SignonProfile signonProfile){

    }



    public void deleteSignonProfile (SignonProfile signonProfile){

    }


    public void showEditProfilesDialog(SignonProfile signonProfile){
        //mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("edit_profiles_dialog");
        if (prev != null) {
            EditProviderDialogFragment editProviderDialog = (EditProviderDialogFragment)prev;
            editProviderDialog.dismiss();
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        EditProfilesDialogFragment newFragment = EditProfilesDialogFragment.newInstance(signonProfile);
        newFragment.show(ft, "edit_institution_prompts_dialog");
    }

    public void showEditProviderDialog(UserProviderEntry providerEntry){
        //mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("edit_institution_prompts_dialog");
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




    public void showOTPDialog(UserProviderEntry providerEntry){
        Log.d (TAG, "showOTPDialog instId=" + providerEntry.getIid());


        MoneyAppApp app = (MoneyAppApp)getApplication();

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
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("OTP_DIALOG");
            if (prev != null) {
                EwiseOTPFragment prevFragment = (EwiseOTPFragment) prev;
                prevFragment.dismissAllowingStateLoss();
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            otpFragment.show(ft, "OTP_DIALOG");
        }

    }


}
