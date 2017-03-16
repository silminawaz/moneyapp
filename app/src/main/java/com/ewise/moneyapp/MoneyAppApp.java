package com.ewise.moneyapp;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ewise.android.pdv.api.PdvApi;
import com.ewise.android.pdv.api.PdvApiImpl;
import com.ewise.android.pdv.api.callbacks.PdvApiCallback;
import com.ewise.android.pdv.api.model.PromptEntry;
import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.StatusCode;
import com.ewise.android.pdv.api.model.UserProviderEntry;
import com.ewise.android.pdv.api.model.provider.Group;
import com.ewise.android.pdv.api.model.provider.Institution;
import com.ewise.android.pdv.api.model.provider.Providers;
import com.ewise.android.pdv.api.model.response.GetPromptsData;
import com.ewise.android.pdv.api.model.response.GetPromptsResponse;
import com.ewise.android.pdv.api.model.response.GetUserProfileData;
import com.ewise.android.pdv.api.model.response.GetUserProfileResponse;
import com.ewise.android.pdv.api.util.ConnectivityReceiver;
import com.ewise.moneyapp.Utils.PdvApiName;
import com.ewise.moneyapp.Utils.PdvApiRequestParams;
import com.ewise.moneyapp.Utils.PdvApiRequestQueue;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvApiStatus;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;
import com.ewise.moneyapp.Utils.PdvConnectivityStatus;
import com.rogansoft.remotelogger.DebugHelper;
import com.rogansoft.remotelogger.RemoteLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dagger.ObjectGraph;

import static java.util.Arrays.asList;

/**
 * Created by SilmiNawaz on 5/9/16.
 */
public class MoneyAppApp extends Application {
    private static final String TAG = MoneyAppApp.class.getName();
    public static final String DEFAULT_MM_HOST = "https://qa-50-wmm.ewise.com/api/";
    public static final String DEFAULT_SWAN_HOST = "https://qaswan.ewise.com/";
    public static final String EWISEDEMO = "com.ewise.android.pdv.EwiseSharedPref";
    public static final String INSTCODE_DRAWABLE_PREFIX = "INSTCODE_";

    static final int ACCOUNT_DETAILS_ACTIVITY = 1;
    static final int ADD_PROVIDER_LIST_REQUEST = 2;
    static final int ADD_PROVIDER_PROMPTS_REQUEST = 3;

    public PdvApi pdvApi;
    public WebView pdvWebView;
    public boolean loggedOnToPdv;
    public PdvConnectivityStatus pdvConnectivityStatus;
    public PdvApiRequestQueue   pdvApiRequestQueue = null;
    public GetUserProfileData   userProfileData = null;
    public Providers providerData = null;
    public HashMap<String, String> instCodeToGroupMap = null;
    Handler threadHandler = new Handler();

    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler(){

        public void uncaughtException(Thread thread, Throwable e){
            Log.e("MoneyApp", "uncaught exception: ", e );

            defaultUncaughtExceptionHandler.uncaughtException(thread, e);
        }
    };

    @Override
    public void onCreate(){
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

                //todo: handle destruction of main activity the PdvApi uses the WebView from MainActivity
                //      so if its destroyed ad recreated, the callback references will leak and also refer to the
                //      already destroyed activity, and hence never get called

            }
        });


        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);

        final String mmHost = DEFAULT_MM_HOST;
        final String swanHost = DEFAULT_SWAN_HOST;
        pdvApi = new PdvApiImpl(swanHost, mmHost);
        loggedOnToPdv = false;
        pdvConnectivityStatus = PdvConnectivityStatus.UNKNOWN;
        pdvApiRequestQueue = new PdvApiRequestQueue();
        userProfileData = new GetUserProfileData();
        providerData = new Providers();
        instCodeToGroupMap = new HashMap<String, String>();
        setupAppConfig();

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

        pdvApi.config(config);
    }


    public String getIMEI(){
        Context context = getApplicationContext();
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;

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
                            Log.d("MOneyAppapp", "calling notifyConnectivitySuccess");
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
                        PdvApiResults results = new PdvApiResults();
                        results.pdvApiName = PdvApiName.GET_INSTITUTIONS;
                        results.callBackCompleted = true;
                        results.providers = response;
                        if (response.getStatus().equals(StatusCode.STATUS_SUCCESS)){
                            providerData = results.providers.getData();
                            updateProviderHashMap();
                            callback.onGetInstitutionsSuccess(results);
                        }
                        else {
                            results.callBackError=true;
                            callback.onGetInstitutionsFail(results);
                        }
                    }
                });
            }
        };

        new Thread (runPdvGetInstitutions).start();
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

        new Thread (runPdvGetUserProfiles).start();
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

    //todo: remove unused code
    public static WebView WebView (Activity activity) {
        WindowManager windowManager = (WindowManager) activity.getSystemService(activity.WINDOW_SERVICE);
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
