package com.ewise.moneyapp;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.animation.Animator;
import android.widget.TextView;
import android.widget.Toast;

import com.ewise.android.pdv.api.PdvApi;
import com.ewise.android.pdv.api.callbacks.CallbackStatus;
import com.ewise.android.pdv.api.callbacks.PdvApiCallback;
import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.StatusCode;
import com.ewise.android.pdv.api.model.consent.ConsentAppVO;
import com.ewise.android.pdv.api.model.consent.ConsentServiceResponse;
import com.ewise.android.pdv.api.model.consent.ConsentSvcVO;
import com.ewise.android.pdv.api.model.consent.ConsentUpdateRequest;
import com.ewise.android.pdv.api.model.response.GetPromptsData;
import com.ewise.moneyapp.Utils.PdvApiName;
import com.ewise.moneyapp.Utils.PdvApiRequestParams;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvApiStatus;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;
import com.ewise.moneyapp.service.PdvAcaBoundService;

import org.xwalk.core.XWalkView;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.Synchronized;


public class MainActivity extends AppCompatActivity
        implements AccountsFragment.OnFragmentInteractionListener, PdvConnectivityCallback
{

    public interface FragmentUpdateListener {
        enum LoginStatus{
            LOGIN_STATUS_UNKNOWN,
            LOGIN_STATUS_FAILED,
            LOGIN_STATUS_SUCCESS,
            LOGIN_STATUS_RETRY;
        }
        public void onLoginFailed();
        public void onLoginSuccess();
        public void onLoginRetry();
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

    private void notifyLoginStatusToAttachedFragments(FragmentUpdateListener.LoginStatus loginStatus){
        activityLoginStatus = loginStatus;
        if(loginStatus.equals(FragmentUpdateListener.LoginStatus.LOGIN_STATUS_SUCCESS)){
            //notify all fragments of login success
            if (providerFragmentUpdateListener!=null) { providerFragmentUpdateListener.onLoginSuccess(); }
            if (accountsFragmentUpdateListener!=null) { accountsFragmentUpdateListener.onLoginSuccess(); }
            if (networthFragmentUpdateListener!=null) { networthFragmentUpdateListener.onLoginSuccess(); }
            if (budgetsFragmentUpdateListener!=null) { budgetsFragmentUpdateListener.onLoginSuccess(); }
        }
        else if (loginStatus.equals(FragmentUpdateListener.LoginStatus.LOGIN_STATUS_FAILED))
        {
            //notify all fragments of login failure
            if (providerFragmentUpdateListener!=null) { providerFragmentUpdateListener.onLoginFailed(); }
            if (accountsFragmentUpdateListener!=null) { accountsFragmentUpdateListener.onLoginFailed(); }
            if (networthFragmentUpdateListener!=null) { networthFragmentUpdateListener.onLoginFailed(); }
            if (budgetsFragmentUpdateListener!=null) { budgetsFragmentUpdateListener.onLoginFailed(); }
        }
        else if (loginStatus.equals(FragmentUpdateListener.LoginStatus.LOGIN_STATUS_RETRY))
        {
            //notify all fragments of login in progress
            if (providerFragmentUpdateListener!=null) { providerFragmentUpdateListener.onLoginRetry(); }
            if (accountsFragmentUpdateListener!=null) { accountsFragmentUpdateListener.onLoginRetry(); }
            if (networthFragmentUpdateListener!=null) { networthFragmentUpdateListener.onLoginRetry(); }
            if (budgetsFragmentUpdateListener!=null) { budgetsFragmentUpdateListener.onLoginRetry(); }
        }
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

    int loginRetryCount=0;
    FragmentUpdateListener.LoginStatus activityLoginStatus = FragmentUpdateListener.LoginStatus.LOGIN_STATUS_UNKNOWN;

    public void resetLoginRetryCount() {
        this.loginRetryCount = 0;
        this.notifyLoginStatusToAttachedFragments(FragmentUpdateListener.LoginStatus.LOGIN_STATUS_RETRY);
    }

    public int getLoginRetryCount() {
        return loginRetryCount;
    }

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

    //PdvApiUpdateAccountRequest updateAccountRequestFragment;
    //private static final String TAG_ACCOUNT_REQUEST_FRAGMENT = "update_account_request_fragment";

    private Runnable        pdvApiRequestRunnable = new Runnable() {
        @Override
        public void run() {
            MoneyAppApp app = (MoneyAppApp) getApplication();

            if (app.pdvLoginStatus.isLoggedOffFromPdv() && (loginRetryCount < MoneyAppApp.MAX_AUTO_LOGIN_RETRY_COUNT)){
                loginToPDV();
                loginRetryCount++;
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
            //PdvApiRequestParams requestParams = PdvApiResults.objectFromString(sRequestParams, PdvApiRequestParams.class);

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


    public void checkPdvConnectivity()
    {
        MoneyAppApp app = (MoneyAppApp)getApplication();
        app.checkConnectivity(this,MoneyAppApp.DEFAULT_SWAN_HOST,this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTheme(R.style.AppTheme_NoActionBar); //remove the splash theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fabLayout1= (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2= (LinearLayout) findViewById(R.id.fabLayout2);
        fabLayout3= (LinearLayout) findViewById(R.id.fabLayout3);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        FloatingActionButton fab2= (FloatingActionButton) findViewById(R.id.fab2);
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fabBGLayout=findViewById(R.id.fabBGLayout);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoneyAppApp app = (MoneyAppApp)getApplication();

                if (app.pdvLoginStatus.isLoggedOnToPdv() && !app.pdvApiRequestQueue.isRequestInProgress()){
                        startActivityForResult(new Intent(MainActivity.this, AddInstitutionActivity.class), MoneyAppApp.ADD_PROVIDER_LIST_REQUEST);
                }
                else if (!app.pdvApiRequestQueue.isRequestInProgress())
                {
                    Toast.makeText(getApplicationContext(), R.string.pdvapi_not_loggedin, Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), R.string.pdvapi_request_inprogress, Toast.LENGTH_LONG).show();
                }

            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
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

        MoneyAppApp myApp = ((MoneyAppApp) getApplication());
        pdvApiResults = new PdvApiResults();
        PdvApi pdvApi = myApp.getPdvApi();
        myApp.pdvWebView = (WebView) findViewById(R.id.ewise_webview);
//**XWALK**        myApp.pdvWebView = (XWalkView) findViewById(R.id.ewise_webview);

        try {
            pdvApi.apiInit(getApplicationContext(), myApp.pdvWebView);
            myApp.checkConnectivity(this, MoneyAppApp.DEFAULT_SWAN_HOST, this);
            //if successfully connected, the callback will handle the login
            pdvApiRequestRunnable.run();
        } catch (Exception e) {
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            String sObjString = myApp.pdvApi.toString();
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, sObjString);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        Intent intent= new Intent(this, PdvAcaBoundService.class);
        bindService(intent, pdvAcaServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(pdvApiCallbackMessageReceiver,
                new IntentFilter("pdv-aca-bound-service-callback"));

        LocalBroadcastManager.getInstance(this).registerReceiver(pdvApiServiceErrorMessageReceiver,
                new IntentFilter("pdv-aca-service-error"));

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
        //unbindService(pdvAcaServiceConnection);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //todo: remove debug code
        Log.d("MainActivity", "onActivityResult() - requestCode=" + Integer.toString(requestCode));

        //if the result from the add provider form is returned
        if ((data !=null) && (requestCode == MoneyAppApp.ADD_PROVIDER_LIST_REQUEST)){
            String jsonPromptsData = data.getStringExtra("promptsData");
            String instCode = data.getStringExtra("instCode");
            String instName = data.getStringExtra("instName");

            GetPromptsData promptsData = PdvApiResults.objectFromString(jsonPromptsData, GetPromptsData.class);

            setDataFetchingStatus(true, null);

            Log.d("MainActivity-instCode", PdvApiResults.toJsonString(promptsData));


            MoneyAppApp app = (MoneyAppApp) getApplication();

            app.addNewProvider(instCode, instName, promptsData);

        }
    }

    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
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
        notifyLoginStatusToAttachedFragments(FragmentUpdateListener.LoginStatus.LOGIN_STATUS_FAILED);

        setDataFetchingStatus(false, null);

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

                Toast.makeText(MainActivity.this, getString(R.string.pdvapi_not_connected), Toast.LENGTH_LONG);
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
        notifyLoginStatusToAttachedFragments(FragmentUpdateListener.LoginStatus.LOGIN_STATUS_FAILED);

        Log.d("MainActivity", "onGetInstitutionsFail() - END");

    }

    @Override
    public void onGetInstitutionsSuccess(PdvApiResults results){

        Log.d("MainActivity", "onGetInstitutionsSuccess() - START");

        setDataFetchingStatus(false, null);
        ((MoneyAppApp)getApplication()).checkConnectivity(this, MoneyAppApp.DEFAULT_SWAN_HOST, this);

        //notify reloads for institutions
        Intent intent = new Intent("pdv-on-get-user-profile-success");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        ((MoneyAppApp)getApplication()).pdvLoginStatus.notifyLoggedOnToPdv();
        notifyLoginStatusToAttachedFragments(FragmentUpdateListener.LoginStatus.LOGIN_STATUS_SUCCESS);

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
    //END - PdvConnectivityCallbacks interface implementations

    public void displayLongToastMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG);
    }

    public void displayShortToastMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT);
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
    }



    private void showFABMenu(){
        isFABOpen=true;

        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabBGLayout.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.animate().rotationBy(-180);


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
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 2) {
                //return the AccountFragment class
                //return AccountFragment.newInstance (position + 1);
                return AccountsFragment_.newInstance();
            }
            else if (position == 0){
                //todo: return the providers fragment
                return ProvidersFragment.newInstance(position);

            }
            else if (position == 1){
                return NetworthFragment.newInstance(position);
            }
            else if (position == 3){
                return BudgetsFragment.newInstance(position);
            }
            else {
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
                case 0:
                    return getString(R.string.section_name_providers);
                case 1:
                    return getString(R.string.section_name_networth);
                case 2:
                    return getString(R.string.section_name_accounts);
                case 3:
                    return getString(R.string.section_name_spending);
            }
            return null;
        }
    }
}
