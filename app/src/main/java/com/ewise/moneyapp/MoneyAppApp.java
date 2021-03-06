package com.ewise.moneyapp;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ewise.android.pdv.api.PdvApi;
import com.ewise.android.pdv.api.PdvApiImpl;
import com.ewise.android.pdv.api.callbacks.PdvApiCallback;
import com.ewise.android.pdv.api.model.AccountEntry;
import com.ewise.android.pdv.api.model.InstitutionAndAccounts;
import com.ewise.android.pdv.api.model.PromptEntry;
import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.StatusCode;
import com.ewise.android.pdv.api.model.UserProviderEntry;
import com.ewise.android.pdv.api.model.provider.Group;
import com.ewise.android.pdv.api.model.provider.Institution;
import com.ewise.android.pdv.api.model.provider.Providers;
import com.ewise.android.pdv.api.model.response.AccountsResponse;
import com.ewise.android.pdv.api.model.response.GetPromptsData;
import com.ewise.android.pdv.api.model.response.GetPromptsResponse;
import com.ewise.android.pdv.api.model.response.GetUserProfileData;
import com.ewise.android.pdv.api.model.response.GetUserProfileResponse;
import com.ewise.android.pdv.api.model.response.RemoveInstitutionResponse;
import com.ewise.android.pdv.api.model.response.TransactionsResponse;
import com.ewise.android.pdv.api.util.ConnectivityReceiver;
import com.ewise.moneyapp.APIDataMappers.PdvAccountDataMapper;
import com.ewise.moneyapp.Utils.PdvApiCallbackCounter;
import com.ewise.moneyapp.Utils.PdvApiName;
import com.ewise.moneyapp.Utils.PdvApiRequestParams;
import com.ewise.moneyapp.Utils.PdvApiRequestQueue;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvApiStatus;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;
import com.ewise.moneyapp.Utils.PdvConnectivityStatus;
import com.ewise.moneyapp.Utils.PdvLoginStatus;
import com.ewise.moneyapp.Utils.Settings;
import com.ewise.moneyapp.Utils.SignOnSystem;
import com.ewise.moneyapp.data.AccountCardListDataObject;
import com.ewise.moneyapp.data.CurrencyExchangeRates;
import com.ewise.moneyapp.data.DataUpdateType;
import com.ewise.moneyapp.data.GroupedInstitution;
import com.ewise.moneyapp.data.PdvAccountResponse;
import com.ewise.moneyapp.service.PdvAcaBoundService;
//import com.rogansoft.remotelogger.DebugHelper;
//import com.rogansoft.remotelogger.RemoteLogger;

import org.xwalk.core.XWalkView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import dagger.ObjectGraph;

import static java.util.Arrays.asList;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 5/9/16.
 */
public class MoneyAppApp extends Application {
    private static final String TAG = MoneyAppApp.class.getName();
    public static final String DEFAULT_USERNAME = "silmiandroid5demo5";
    public static final String DEFAULT_MM_HOST = "https://qa-50-wmm.ewise.com/api/";
    //public static final String DEFAULT_MM_HOST = "https://tandem-api.ewise.com/api/";
    public static final String DEFAULT_SWAN_HOST = "https://qaswan.ewise.com/";
    //public static final String DEFAULT_SWAN_HOST = "https://swan-tandem.ewise.com/";
    public static final String EWISEDEMO = "com.ewise.android.pdv.EwiseSharedPref";
    public static final String INSTCODE_DRAWABLE_PREFIX = "instcode_";


    //todo: implement inactivity timeout for the app login
    public static final int DEFAULT_APP_INACTIVITY_TIMEOUT = 180000; //30 minutes default timeout for the app for login

    static final int LOGIN_REQUEST = 1;
    static final int ACCOUNT_DETAILS_ACTIVITY = 2;
    static final int ADD_PROVIDER_LIST_REQUEST = 3;
    static final int ADD_PROVIDER_PROMPTS_REQUEST = 4;


    static final int MAINACTIVITY_FINISHING_NOTIFICATION_REQUEST_CODE = 999;

    static final int MAX_AUTO_LOGIN_RETRY_COUNT = 3;


    public PdvApi pdvApi;
    // REMOVE XWALK **XWALK**     public WebView pdvWebView;
    public XWalkView pdvWebView;
    public SignOnSystem signOnSystem=SignOnSystem.SIGN_ON_SYSTEM_UNKNOWN;
    public String signonUserId=null;
    public PdvLoginStatus pdvLoginStatus;
    public PdvConnectivityStatus pdvConnectivityStatus;
    public PdvApiRequestQueue   pdvApiRequestQueue = null;
    public GetUserProfileData   userProfileData = null;
    public Providers providerData = null;
    private GroupedInstitution addInstitutionSelected = null;
    private GetPromptsData promptsDataSelected = null;
    public HashMap<String, String> instCodeToGroupMap = null;
    public PdvAccountResponse pdvAccountResponse = null;
    Handler threadHandler = new Handler();


    private PdvApiCallbackCounter restoreAccountsCallBackCounter = null;
    private Date lastRestoredDateTime = null;

    private boolean pdvLoginFailed = false;
    private boolean appLoggedIn= false;

    private PdvAcaBoundService pdvAcaBoundService;
    private boolean pdvAcaServiceIsBound = false;


    public GroupedInstitution getAddInstitutionSelected() {
        return addInstitutionSelected;
    }

    public void setAddInstitutionSelected(GroupedInstitution addInstitutionSelected) {
        if (this.addInstitutionSelected==null){
            this.addInstitutionSelected=new GroupedInstitution();
        }
        this.addInstitutionSelected.setGroupId(addInstitutionSelected.getGroupId());
        this.addInstitutionSelected.setGroupDesc(addInstitutionSelected.getGroupDesc());
        this.addInstitutionSelected.setInstCode(addInstitutionSelected.getInstCode());
        this.addInstitutionSelected.setInstDesc(addInstitutionSelected.getInstDesc());
        this.addInstitutionSelected.setInstitutionIcon(addInstitutionSelected.getInstitutionIcon());
    }

    public GetPromptsData getPromptsDataSelected() {
        return promptsDataSelected;
    }

    public void setPromptsDataSelected(GetPromptsData promptsDataSelected) {
        if (this.promptsDataSelected==null){
            this.promptsDataSelected=new GetPromptsData();
        }
        this.promptsDataSelected.setInstId(promptsDataSelected.getInstId());
        List<PromptEntry> promptEntryList = new ArrayList<>();
        promptEntryList.addAll(promptsDataSelected.getPrompts());
        if (this.promptsDataSelected.getPrompts()!=null) this.promptsDataSelected.getPrompts().clear();
        this.promptsDataSelected.setPrompts(promptEntryList);
    }

    public boolean isPdvLoginFailed(){
        return pdvLoginFailed;
    }

    public void setPdvLoginFailed (){
        pdvLoginFailed=true;
    }

    public void retryPdvLogin(){
        pdvLoginFailed=false;
    }

    public boolean isAppLoggedIn(){
        return appLoggedIn;
    }

    public void setAppLoggedIn(SignOnSystem signOnSystem, String signonUserId){
        appLoggedIn=true;
        if (signonUserId==null){
            signonUserId=DEFAULT_USERNAME;
        }
        this.signOnSystem=signOnSystem;
        this.signonUserId=signonUserId;
    }

    public void setAppLoggedOff (){
        appLoggedIn=false;

        if (userProfileData!=null && userProfileData.getUserprofile()!=null){
            userProfileData.getUserprofile().clear();
        }

        if (pdvAccountResponse!=null){
            pdvAccountResponse.clearAccounts();
        }
    }



    public PdvAcaBoundService getPdvAcaBoundService(){
        if (pdvAcaServiceIsBound){
            return pdvAcaBoundService;
        }
        else{
            return null;
        }
    }

    public String getBaseCurrency(){

        //todo: implement base currency via configuration or service
        return getString(R.string.var_base_currency);
    }

    public AccountCardListDataObject getAccountCardListDO(Context context){

        if (pdvAccountResponse!=null){
            if (pdvAccountResponse.accounts!=null){
                return new AccountCardListDataObject(context, pdvAccountResponse, getBaseCurrency());
            }
        }
        return null;
    }

    private ServiceConnection pdvAcaServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PdvAcaBoundService.PdvAcaServiceBinder binder = (PdvAcaBoundService.PdvAcaServiceBinder) iBinder;
            pdvAcaBoundService = binder.getService();
            pdvAcaServiceIsBound = true;
            Log.d("MoneyAppApp", "PdvAcaBoundService -> onServiceConnected()");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            pdvAcaServiceIsBound = false;
            Log.d("MoneyAppApp", "PdvAcaBoundService -> onServiceDisconnected()");

        }
    };

    private void setLastRestoredDateTime(){
        if (lastRestoredDateTime==null){
            lastRestoredDateTime=new Date();
        }
        else {
            lastRestoredDateTime.setTime(System.currentTimeMillis());
        }
    }

    public boolean mustRestoreAccounts(){
        if (lastRestoredDateTime==null){
            return true;
        }
        else{
            if (getPdvAcaBoundService()!=null) {
                return getPdvAcaBoundService().isRefreshedAfter(lastRestoredDateTime);
            }
            else{
                return false;
            }
        }
    }

    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler(){

        public void uncaughtException(Thread thread, Throwable e){
            Log.e("MoneyApp", "uncaught exception: ", e );
            e.printStackTrace(); // not all Android versions will print the stack trace automatically
            Intent intent = new Intent ();
            //todo: to create send_log ativity to report crashes
            intent.setAction ("com.ewise.moneyapp.SEND_LOG");
            intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity (intent);

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);

            defaultUncaughtExceptionHandler.uncaughtException(thread, e);
        }
    };

    public void handleUncaughtException (Thread thread, Throwable e)
    {

        Intent intent = new Intent ();
        intent.setAction ("com.mydomain.SEND_LOG"); // see step 5.
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity (intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    @Override
    public void onCreate(){
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                Log.d("MoneyAppApp", "onActivityCreated() - Activity = " + activity.getClass().getName());
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d("MoneyAppApp", "onActivityStarted() - Activity = " + activity.getClass().getName());

            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.d("MoneyAppApp", "onActivityResumed() - Activity = " + activity.getClass().getName());
                if (activity.getClass().getName().equals(MainActivity.class.toString())){
                    MainActivity mainActivity = (MainActivity) activity;
                    //mainActivity.homeWatcher.startWatch();
                    if (mainActivity.logOutDialog !=null){
                        if (mainActivity.logOutDialog.isShowing()){
                            mainActivity.logOutDialog.hide();
                            mainActivity.logOutDialog.cancel();
                        }
                    }
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.d(TAG, "onActivityPaused() - Activity = " + activity.getClass().getName());

                if (activity.getClass().getName().equals(MainActivity.class.toString())){
                    MainActivity mainActivity = (MainActivity) activity;
                    if (mainActivity.isFinishing()) {
                        //mainActivity.homeWatcher.stopWatch();
                        if (mainActivity.logOutDialog != null) {
                            if (mainActivity.logOutDialog.isShowing()) {
                                mainActivity.logOutDialog.hide();
                                mainActivity.logOutDialog.cancel();
                            }
                        }

                        Log.d(TAG, "onActivityPaused() - MainActivity is FINISHING");


                        if (pdvApiRequestQueue.isRequestInProgress() || pdvApiRequestQueue.isRequestPending()) {
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                    .setContentTitle(activity.getString(R.string.mainactivity_finishing_notification_message))
                                    .setSmallIcon(R.drawable.ewise_marque_black)
                                    .setOngoing(true);
                            //Intent intent = new Intent(activity, null);
                            //PendingIntent pIntent = PendingIntent.getActivity(context, MAINACTIVITY_FINISHING_NOTIFICATION_REQUEST_CODE, intent, 0);
                            //builder.setContentIntent(pIntent);
                            NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            Notification notif = builder.build();
                            notif.flags |= Notification.PRIORITY_HIGH;
                            Log.d(TAG, "onActivityPaused() - MainActivity is FINISHING - sending notification");
                            mNotificationManager.notify(MAINACTIVITY_FINISHING_NOTIFICATION_REQUEST_CODE, notif);

                        }
                    }
                }

                //todo: if mainactivity is paused, then the BoundService calls cannot be handled by main activity
                //      so we need to handle the bradcast receivers here in application class and use whatever

            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d("MoneyAppApp", "onActivityStopped() - Activity = " + activity.getClass().getName());

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                Log.d("MoneyAppApp", "onActivitySaveInstanceState() - Activity = " + activity.getClass().getName());

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d("MoneyAppApp", "onActivityDestroyed() - Activity = " + activity.getClass().getName());

                //todo: handle destruction of main activity the PdvApi uses the WebView from MainActivity
                //      so if its destroyed ad recreated, the callback references will leak and also refer to the
                //      already destroyed activity, and hence never get called

            }
        });


        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.currentThread().setDefaultUncaughtExceptionHandler(handler);

        final String mmHost = DEFAULT_MM_HOST;
        final String swanHost = DEFAULT_SWAN_HOST;
        pdvApi = new PdvApiImpl(swanHost, mmHost);
        pdvLoginStatus=new PdvLoginStatus();
        pdvConnectivityStatus = PdvConnectivityStatus.UNKNOWN;
        pdvApiRequestQueue = new PdvApiRequestQueue();
        userProfileData = new GetUserProfileData();
        providerData = new Providers();
        instCodeToGroupMap = new HashMap<String, String>();
        pdvAccountResponse = new PdvAccountResponse();
        restoreAccountsCallBackCounter = new PdvApiCallbackCounter();

        Intent intent= new Intent(this, PdvAcaBoundService.class);
        bindService(intent, pdvAcaServiceConnection, Context.BIND_AUTO_CREATE);

        setupAppConfig();

        //setup the currency rates
        CurrencyExchangeRates rates = CurrencyExchangeRates.getInstance();
        rates.loadExchangeRatesFromJsonAssets(getApplicationContext());

    }



    public PdvApi getPdvApi() {
        return pdvApi;
    }

    public void setPdvApi(PdvApi pdvApi) {
        this.pdvApi = pdvApi;
    }

    private void setupAppConfig() {
        final List<String> issuerDns = asList("CN=RapidSSL SHA256 CA - G3, O=GeoTrust Inc., C=US");
        Map<PdvApi.Config, Object> config = new LinkedHashMap<>();
        config.put(
                PdvApi.Config.LIST_OF_ACCEPTED_ISSUERS,
                issuerDns);

        config.put (PdvApi.Config.ENABLE_WEBVIEW_CONFIGURATION, false);

        //config.put (PdvApi.Config.ENABLE_SEAGULL_LOGGING, false);
        //config.put (PdvApi.Config.ENABLE_ACA_LOGGING, false);



        pdvApi.config(config);
    }


    public String getIMEI(){
        Context context = getApplicationContext();
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;

    }

    public boolean isPdvConnected(Context context){
        Log.d(TAG, "Testing if connected to PDV server...");
        return ConnectivityReceiver.isConnected(context, MoneyAppApp.DEFAULT_SWAN_HOST);
    }

    public void checkConnectivity(final Context context, final String swanHost, final PdvConnectivityCallback callback) {
        Log.d(TAG, "Testing if connected to PDV server...");

        Runnable connectivityChecker = new Runnable() {
            @Override
            public void run() {
                if (ConnectivityReceiver.isConnected(context, swanHost)){
                    threadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("MoneyAppapp", "calling notifyConnectivitySuccess");
                            notifyConnectivitySuccess(callback);
                        }
                    });
                }
                else {
                    threadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyConnectivityFail(callback);
                        }
                    });
                }
            }
        };

        new Thread(connectivityChecker).start();
        //check to see if we know the status

    }

    public void notifyConnectivitySuccess(PdvConnectivityCallback callback){
        if (pdvConnectivityStatus!=PdvConnectivityStatus.SUCCESS){

            pdvConnectivityStatus = PdvConnectivityStatus.SUCCESS;

        }
        Log.d("MoneyAppApp", "Calling callback.onPdvConnected");
        callback.onPdvConnected();
    }

    public void notifyConnectivityFail(PdvConnectivityCallback callback){
        if (pdvConnectivityStatus!=PdvConnectivityStatus.ERROR){

            pdvConnectivityStatus = PdvConnectivityStatus.ERROR;

        }

        Log.d("MoneyAppApp", "Calling callback.onPdvDisconnected");
        callback.onPdvDisconnected();
    }

    public void pdvGetInstitutions(final PdvConnectivityCallback callback){
        Log.d(TAG, "Calling PdvGetInstitutions()");

        Runnable runPdvGetInstitutions = new Runnable() {
            @Override
            public void run() {
                pdvApi.getInstitutions(new PdvApiCallback<Providers>() {
                    @Override
                    public void result(Response<Providers> response) {

                        try {


                            PdvApiResults results = new PdvApiResults();
                            results.pdvApiName = PdvApiName.GET_INSTITUTIONS;
                            results.callBackCompleted = true;
                            results.providers = response;
                            if (response.getStatus().equals(StatusCode.STATUS_SUCCESS)) {
                                providerData = results.providers.getData();
                                updateProviderHashMap();
                                callback.onGetInstitutionsSuccess(results);
                            } else {
                                results.callBackError = true;
                                callback.onGetInstitutionsFail(results);
                            }
                        }
                        catch(Exception e){

                            Log.e("MoneyAppApp", "pdvGetInstitutions() - exception " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }
        };


        new Thread(runPdvGetInstitutions).start();

    }


    public void pdvGetPrompts (final String instCode, final PdvConnectivityCallback callback) {
        Log.d(TAG, "Calling pdvGetPrompts()");

        Runnable runPdvGetPrompts = new Runnable() {
            @Override
            public void run() {
                pdvApi.getPrompts(instCode, new PdvApiCallback.PdvApiGetPromptsCallback() {
                    @Override
                    public void result(GetPromptsResponse getPromptsResponse) {
                        PdvApiResults results = new PdvApiResults();
                        results.pdvApiName = PdvApiName.GET_PROMPTS;
                        results.callBackCompleted = true;
                        results.prompts = getPromptsResponse;
                        if (results.prompts.getStatus().equals(StatusCode.STATUS_SUCCESS)) {
                            callback.onGetPromptsSuccess(results);
                        } else {
                            results.callBackError=true;
                            callback.onGetPromptsFail(results);
                        }
                    }
                });
            }
        };

        new Thread (runPdvGetPrompts).start();
    }

    public void pdvGetUserProfile (final PdvConnectivityCallback callback) {
        Log.d(TAG, "Calling pdvGetUserProfile()");

        Runnable runPdvGetUserProfiles = new Runnable() {
            @Override
            public void run() {
                try {
                    pdvApi.getUserProfile(new PdvApiCallback.PdvApiGetUserProfileCallback() {
                        @Override
                        public void result(GetUserProfileResponse getUserProfileResponse) {
                            PdvApiResults results = new PdvApiResults();
                            results.pdvApiName = PdvApiName.GET_USER_PROFILE;
                            results.callBackCompleted = true;
                            results.userProfile = getUserProfileResponse;
                            if (results.userProfile.getStatus().equals(StatusCode.STATUS_SUCCESS)) {
                                userProfileData.setUserprofile(results.userProfile.getData().getUserprofile());
                                callback.onGetUserProfileSuccess(results);
                            } else {
                                callback.onGetUserProfileFail(results);
                            }
                        }
                    });
                }
                catch (Exception e){
                    Log.e("MoneyAppApp", "pdvGetUserProfile() - exception " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        try{
            new Thread (runPdvGetUserProfiles).start();
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("MoneyAppApp", "pdvGetUserProfile() - exception " + e.getMessage());
            PdvApiResults results = new PdvApiResults();
            results.callBackCompleted=false;
            results.callBackError=true;
            callback.onGetUserProfileFail(results);
        }
    }


    public void pdvGetCredential (final String instId, final PdvConnectivityCallback callback) {
        Log.d(TAG, "Calling pdvGetCredential()");

        Runnable runPdvGetCredential = new Runnable() {
            @Override
            public void run() {
                try {
                    pdvApi.getCredential(instId, new PdvApiCallback<GetPromptsData>() {
                        @Override
                        public void result(Response<GetPromptsData> response) {
                            PdvApiResults results = new PdvApiResults();
                            results.pdvApiName = PdvApiName.GET_CREDENTIAL;
                            results.callBackCompleted = true;
                            results.credential = response;
                            if (results.credential.getStatus().equals(StatusCode.STATUS_SUCCESS)) {
                                callback.onGetCredentialSuccess(results);
                            } else {
                                callback.onGetCredentialFail(results);
                            }
                        }
                    });
                }
                catch (Exception e){
                    Log.e("MoneyAppApp", "pdvGetCredential() - exception " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        new Thread (runPdvGetCredential).start();
    }


    public void pdvSetCredential (final String instId, final List<PromptEntry> prompts, final PdvConnectivityCallback callback) {
        Log.d(TAG, "Calling pdvGetCredential()");

        Runnable runPdvSetCredential = new Runnable() {
            @Override
            public void run() {
                try {
                    pdvApi.setCredential(instId,prompts, new PdvApiCallback<String>() {
                        @Override
                        public void result(Response<String> response) {
                            PdvApiResults results = new PdvApiResults();
                            results.pdvApiName = PdvApiName.SET_CREDENTIAL;
                            results.callBackCompleted = true;
                            results.setCredentialResponse = response;
                            if (results.setCredentialResponse.getStatus().equals(StatusCode.STATUS_SUCCESS)) {
                                callback.onSetCredentialSuccess(results);
                            } else {
                                callback.onSetCredentialFail(results);
                            }
                        }
                    });
                }
                catch (Exception e){
                    Log.e("MoneyAppApp", "pdvGetCredential() - exception " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        new Thread (runPdvSetCredential).start();
    }

    public void pdvRestoreAllProviderAccounts(final PdvConnectivityCallback callback){
        Log.d(TAG, "Calling pdvRestoreAllProviderAccounts() - START");

        if (restoreAccountsCallBackCounter.getValue()!=0){
            Log.d(TAG, "pdvRestoreAllProviderAccounts() - A restore is in progress, resetting counter");
            synchronized (this){
                restoreAccountsCallBackCounter.reset();
                callback.onRestoreAccountsAllComplete(); // complete the previous call
                setLastRestoredDateTime();
            }
        }

        if (userProfileData.getUserprofile() == null) {
            callback.onRestoreAccountsNone();
            return;
        }

        boolean noInstitutionsOnDevice=true;
        if (userProfileData.getUserprofile().size()>0) {
            for (UserProviderEntry p : userProfileData.getUserprofile()) {
                if (p.isFoundInDevice()) {
                    pdvRestoreAccounts(p.getIid(), callback);
                    noInstitutionsOnDevice=false;
                }
                else {
                    //callback.onRestoreAccountsNone();
                }
            }
        }
        else {
            callback.onRestoreAccountsNone();
        }

        if (noInstitutionsOnDevice) callback.onRestoreAccountsNone();

        Log.d(TAG, "Calling pdvRestoreAllProviderAccounts() - END");
    }

    public void pdvRestoreAccounts (final String instId, final PdvConnectivityCallback callback) {
        Log.d(TAG, "Calling pdvRestoreAccounts() - START");
        synchronized (this){
            restoreAccountsCallBackCounter.increment();
        }
        Runnable runPdvRestoreAccounts = new Runnable() {
            @Override
            public void run() {
                try {
                    pdvApi.restoreAccounts(instId, new PdvApiCallback.PdvApiAccountsCallback() {
                        @Override
                        public void result(AccountsResponse accountsResponse) {
                            PdvApiResults results = new PdvApiResults();
                            results.pdvApiName = PdvApiName.RESTORE_ACCOUNTS;
                            results.callBackCompleted = true;
                            results.accounts = accountsResponse;

                            //CHANGE: Ignoring "data" callback record by record
                            //if (results.accounts.getStatus().equals(StatusCode.STATUS_DATA)){
                            //    //save the data for the institution
                            //    if (pdvAccountResponse.addUpdateAccount(PdvAccountDataMapper.getMappedObject(results.accounts.getData(), getApplicationContext())).equals(DataUpdateType.DATA_UPDATE_TYPE_ERROR)){
                            //        Log.e("MoneyAppApp", "pdvRestoreAccounts() - Error adding or updating account in pdvAccountResponse. "
                            //                + " InstId=" + results.accounts.getData().getInstId()
                            //                + " | accountHash=" + results.accounts.getData().getAccountHash());
                            //    }
                            //}
                            //else
                            if (results.accounts.getStatus().equals(StatusCode.STATUS_COMPLETE)) {
                                List<String> instIdList = new ArrayList<String>();
                                instIdList.add(instId);
                                //add account list
                                for (AccountEntry accountEntry:results.accounts.getDataList()) {
                                    //save the data for the institution
                                    if (pdvAccountResponse.addUpdateAccount(PdvAccountDataMapper.getMappedObject(accountEntry, getApplicationContext())).equals(DataUpdateType.DATA_UPDATE_TYPE_ERROR)){
                                        Log.e("MoneyAppApp", "pdvRestoreAccounts() - Error adding or updating account in pdvAccountResponse. "
                                                + " InstId=" + results.accounts.getData().getInstId()
                                                + " | accountHash=" + results.accounts.getData().getAccountHash());
                                    }
                                }
                                callback.onRestoreAccountsComplete(accountsResponse.getInstId());
                                synchronized (this){
                                    restoreAccountsCallBackCounter.decrement();
                                }
                            }
                            else if (results.accounts.getStatus().equals(StatusCode.STATUS_ALL_COMPLETE)) {
                                if (restoreAccountsCallBackCounter.getValue() == 0) {
                                    pdvApiRequestQueue.resetAccountsMustBeRestored();

                                    callback.onRestoreAccountsAllComplete();
                                    setLastRestoredDateTime();
                                }
                            }
                            else if (results.accounts.getStatus().equals(StatusCode.STATUS_ERROR)) {
                                Log.e("MoneyAppApp", "pdvRestoreAccounts() - Error adding or updating account in pdvAccountResponse. "
                                        + " InstId=" + accountsResponse.getInstId()
                                        + " | errorType=" + accountsResponse.getErrorType()
                                        + " | message=" + accountsResponse.getMessage());
                                restoreAccountsCallBackCounter.decrement();
                                if (restoreAccountsCallBackCounter.getValue() == 0) {
                                    pdvApiRequestQueue.resetAccountsMustBeRestored();
                                    callback.onRestoreAccountsFail();
                                    setLastRestoredDateTime();
                                }
                            }
                        }
                    });
                }
                catch (Exception e){
                    Log.e("MoneyAppApp", "pdvRestoreAccounts() - exception - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        new Thread (runPdvRestoreAccounts).start();
        Log.d(TAG, "Calling pdvRestoreAccounts() - END");
    }


    public void pdvRestoreAccountTransactions (final String instId, final String accountHash, final String startDate, final String endDate, final PdvConnectivityCallback callback) {
        Log.d(TAG, "Calling pdvRestoreAccountTransactions() - START");

        Runnable runPdvRestoreAccountTransactions = new Runnable() {
            @Override
            public void run() {
                try {

                    InstitutionAndAccounts institutionAndAccounts = new InstitutionAndAccounts();
                    institutionAndAccounts.setInstitutionId(instId);
                    List<String> accountIds = new ArrayList<>();
                    accountIds.add(accountHash);
                    institutionAndAccounts.setAccountIds(accountIds);
                    List<InstitutionAndAccounts> institutionAndAccountsList = new ArrayList<>();
                    institutionAndAccountsList.add(institutionAndAccounts);

                    pdvApi.restoreTransactions(institutionAndAccountsList, startDate, endDate, new PdvApiCallback.PdvApiTransactionsCallback() {
                        @Override
                        public void result(TransactionsResponse transactionsResponse) {
                            PdvApiResults results = new PdvApiResults();
                            results.pdvApiName = PdvApiName.RESTORE_TRANSACTIONS;
                            results.callBackCompleted = true;
                            results.transactions = transactionsResponse;
                            if (results.transactions.getStatus().equals(StatusCode.STATUS_ALL_COMPLETE)) {
                                if (callback!=null) {
                                    callback.onRestoreTransactionsAllComplete(results);
                                }
                            }
                            else if (results.transactions.getStatus().equals(StatusCode.STATUS_ERROR)) {
                                results.callBackError=true;
                                Log.e("MoneyAppApp", "pdvRestoreAccountTransactions() - Error restoring transactions in pdvTransactionsResponse "
                                        + " InstId=" + transactionsResponse.getInstId()
                                        + " | errorType=" + transactionsResponse.getErrorType()
                                        + " | message=" + transactionsResponse.getMessage());
                                if (callback!=null) {
                                    callback.onRestoreTransactionsFail(results);
                                }
                            }
                        }
                    });
                }
                catch (Exception e){
                    Log.e("MoneyAppApp", "pdvRestoreAccountTransactions() - exception - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        new Thread (runPdvRestoreAccountTransactions).start();
        Log.d(TAG, "Calling runPdvRestoreAccountTransactions() - END");
    }


    public void pdvRemoveInstitution (final String instId, final PdvConnectivityCallback callback) {
        Log.d(TAG, "Calling pdvRemoveInstitution()");

        Runnable runPdvRemoveInstitution = new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> instList = new ArrayList<>();
                    instList.add(instId);
                    pdvApi.removeInstitutions(instList, new PdvApiCallback.PdvApiRemoveInsitutionsCallback() {
                        @Override
                        public void result(RemoveInstitutionResponse response) {
                            PdvApiResults results = new PdvApiResults();
                            results.pdvApiName = PdvApiName.REMOVE_INSTITUTION;
                            results.callBackCompleted = true;
                            results.removeInstitutionResponse = response;
                            if (results.removeInstitutionResponse.getStatus().equals(StatusCode.STATUS_SUCCESS)) {
                                //todo: restore the accounts again
                                //todo: restore the user profile again
                                lastRestoredDateTime=null;
                                callback.onRemoveInstitutionSuccess(results);
                            } else {
                                callback.onRemoveInstitutionFail(results);
                            }
                        }
                    });
                }
                catch (Exception e){
                    Log.e("MoneyAppApp", "pdvRemoveInstitution() - exception " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        new Thread (runPdvRemoveInstitution).start();
    }


    public void updateProviderHashMap(){
        instCodeToGroupMap.clear();
        for (Group group : providerData.getGroups()){
            for (Institution institution : group.getInstitutions()){
                instCodeToGroupMap.put(institution.getInstCode(), group.getGroupId());
            }
        }
    }

    //get InstCode from InstId "S101_1234_XXXXXXX"
    public String getInstCodeFromInstId(String instId){
        if (instId.length() > getResources().getInteger(R.integer.ewise_instid_instcode_end_index)+1){
            String instCode = instId.substring(getResources().getInteger(R.integer.ewise_instid_instcode_begin_index),
                    getResources().getInteger(R.integer.ewise_instid_instcode_end_index)+1);
            return instCode;
        }
        return null;
    }

    public Institution getInstitution(String instId){
        if (providerData!=null) {
            String groupId = getProviderGroupId(instId);
            String instCode = getInstCodeFromInstId(instId);
            for (Group group : providerData.getGroups()){
                if (group.getGroupId().equals(groupId)){
                    for (Institution inst : group.getInstitutions()){
                        if (inst.getInstCode().equals(instCode)){
                            return inst;
                        }
                    }
                }
            }
        }

        return null;
    }


    public String getProviderGroupId(String instId){

        String instCode = getInstCodeFromInstId(instId);
        if (instCode!=null) {
            //look for institution code in hashmap
            String groupId = instCodeToGroupMap.get(instCode);
            return groupId;
        }
        return null;
    }

    public int getInstitutionGroupIconResourceId (String instId){
        String groupId = getProviderGroupId(instId);
        if (groupId != null){
            groupId = groupId.toLowerCase();
            int resId = getResources().getIdentifier(groupId, "drawable", getPackageName());
            if (resId == 0){
                return R.drawable.rbanks;
            }
            else {
                return resId;
            }
        }

        return R.drawable.rbanks;
    }

    public int getInstitutionCodeIconResourceId (String instCode, String instGroup){

        //todo: implement retrieval of all implementaiton icons from server and then map it in memory
        //** DO NOT RETRIEVE ICONS ONE BY ONE BY INSTID FOR PERFORMANCE REASONS
        int resId = 0;
        //for now we just map each institution icon to "INSTCODE_9999" resource id in drawables
        if (instCode!=null){
            String instCodeResourceName = MoneyAppApp.INSTCODE_DRAWABLE_PREFIX + instCode;
            resId = getResources().getIdentifier(instCodeResourceName.toLowerCase() , "drawable", getPackageName());
            if (resId==0){
                //get default resource
                instCodeResourceName = MoneyAppApp.INSTCODE_DRAWABLE_PREFIX + instGroup.toLowerCase();
                resId = getResources().getIdentifier(instCodeResourceName.toLowerCase() , "drawable", getPackageName());
            }
            if(resId==0){
                resId= R.drawable.ewise_institutions_default;
            }
        }
        return resId;
    }

    public int getInstitutionIconResourceId (String instId){

        //todo: implement retrieval of all implementaiton icons from server and then map it in memory
        //** DO NOT RETRIEVE ICONS ONE BY ONE BY INSTID FOR PERFORMANCE REASONS
        int resId = 0;
        //for now we just map each institution icon to "INSTCODE_9999" resource id in drawables
        String instCode = getInstCodeFromInstId(instId);
        if (instCode!=null){
            String instCodeResourceName = MoneyAppApp.INSTCODE_DRAWABLE_PREFIX + instCode;
            resId = getResources().getIdentifier(instCodeResourceName , "drawable", getPackageName());
            if (resId==0){
                //get the group resource id
                resId = getInstitutionGroupIconResourceId(instId);
            }
        }
        return resId;
    }


    public String getInstituionIdSyncStatus (String instId){
        //find request sync
        PdvApiRequestParams requestParams = pdvApiRequestQueue.getPendingRequestForInstitution (instId);
        String syncStatus = getResources().getString(R.string.pdvapi_sync_status_message_ready);
        if (requestParams!=null) {
            switch (requestParams.pdvApiStatus) {
                case PDV_API_STATUS_NOTSTARTED:
                    syncStatus = getResources().getString(R.string.pdvapi_sync_status_message_not_started);
                    break;
                case PDV_API_STATUS_INPROGRESS:
                    syncStatus = getResources().getString(R.string.pdvapi_sync_status_message_in_progress);
                    if (pdvApiRequestQueue.getRequestForInstitution(instId).results!=null) {
                        if (pdvApiRequestQueue.getRequestForInstitution(instId).results.isMustShowOTP()) {
                            syncStatus = getResources().getString(R.string.pdvapi_sync_status_message_in_progress_setverify);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        return syncStatus;
    }




    /* *** add a new provider **
        Use this method to add a new provider
     */
    public void addNewProvider (String instCode, String instName, GetPromptsData promptsData){
        PdvApiRequestParams requestParams = new PdvApiRequestParams();
        requestParams.pdvApiName = PdvApiName.UPDATE_ACCOUNTS_WITH_NEW_CREDENTIALS; //trying update accounts
        List<String> instIds = new ArrayList<>();
        instIds.add(promptsData.getInstId());
        requestParams.updateParams.instIds = instIds;
        requestParams.updateParams.profileId = null;
        requestParams.updateParams.credPrompts = promptsData.getPrompts();
        pdvApiRequestQueue.add(requestParams); //add this request to the queue...

        //Add a temporary provider into the Local user profile list and notify the ProviderFragment to refresh
        UserProviderEntry e = new UserProviderEntry();
        e.setIid(promptsData.getInstId());
        e.setUid(promptsData.getPrompts().get(0).getValue());//default new one
        e.setDesc(instName);
        synchronized (this) {
            this.userProfileData.getUserprofile().add(e);
        }

        //notify the Provider Fragment to refresh itself
        Log.d("MoneyAppApp", "addNewProvider: About to send Broadcast message pdv-api-adding-new-provider");
        Intent intent = new Intent("pdv-api-adding-new-provider");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    public void syncExistingProvider (String instId){
        //todo: initiate the aggregation service to add accounts....
        PdvApiRequestParams requestParams = new PdvApiRequestParams();
        requestParams.pdvApiName = PdvApiName.UPDATE_TRANSACTIONS; //trying update accounts
        //set requestParams
        List<String> instIds = new ArrayList<>();
        instIds.add(instId);
        requestParams.updateParams.instIds = instIds;
        requestParams.updateParams.profileId = null;
        requestParams.updateParams.credPrompts = null;
        pdvApiRequestQueue.add(requestParams);
    }


    public boolean setUserProviderMessage(String instId, Response response){
        boolean updated = false;
        for (UserProviderEntry e : userProfileData.getUserprofile()){
            if (e.getIid().equals(instId)){
                synchronized (this){
                    e.setUid(response.getErrorType() + " : " + response.getMessage());
                    updated = true;
                }
            }
            if (updated) break;
        }
        return updated;
    }

    public UserProviderEntry getUserProviderEntry(String instId){
        for (UserProviderEntry e : userProfileData.getUserprofile()){
            if (e.getIid().equals(instId)){
                return e;
            }
        }
        return null;
    }

    public boolean isProviderFoundInDevice(){
        if (userProfileData.getUserprofile()!=null) {
            for (UserProviderEntry e : userProfileData.getUserprofile()) {
                if (e.isFoundInDevice()) {
                    return true;
                }
            }
        }

        Log.d("MoneyAppApp", "isProviderFoundInDevice() - returning FALSE");
        return false;

    }

    public List<PdvAccountResponse.AccountsObject> getAccountsForUserProvider (String instId){
        List<PdvAccountResponse.AccountsObject> accountsObjectList = null;
        if (this.pdvAccountResponse!=null){
            if (pdvAccountResponse.accounts!=null){
                accountsObjectList = new ArrayList<>();
                for (PdvAccountResponse.AccountsObject account: pdvAccountResponse.accounts){
                    if (account.instId.equals(instId)){
                        accountsObjectList.add(account);
                    }
                }
            }
        }
        return accountsObjectList;
    }


    public void setVerifyOTP(String instID, String OTP)
    {
        pdvApi.setVerify(instID, OTP);
        pdvApiRequestQueue.setVerify (instID);
    }


    //todo: remove unused code
    public static WebView WebView (Activity activity) {
        WindowManager windowManager = (WindowManager) activity.getSystemService(WINDOW_SERVICE);
        final WindowManager.LayoutParams params =
                new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;
        params.width = 0;
        params.height = 0;

        LinearLayout view = new LinearLayout(activity);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        WebView newWebView = new WebView(activity);
        view.addView(newWebView);
        newWebView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        windowManager.addView(view, params);

        return newWebView;
    }
}
