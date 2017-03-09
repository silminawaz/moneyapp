package com.ewise.moneyapp;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
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
import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.StatusCode;
import com.ewise.android.pdv.api.model.provider.Providers;
import com.ewise.android.pdv.api.model.response.GetPromptsResponse;
import com.ewise.android.pdv.api.util.ConnectivityReceiver;
import com.ewise.moneyapp.Utils.PdvApiRequestParams;
import com.ewise.moneyapp.Utils.PdvApiRequestQueue;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;
import com.ewise.moneyapp.Utils.PdvConnectivityStatus;
import com.rogansoft.remotelogger.DebugHelper;
import com.rogansoft.remotelogger.RemoteLogger;

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

    static final int ACCOUNT_DETAILS_ACTIVITY = 1;
    static final int ADD_PROVIDER_LIST_REQUEST = 2;
    static final int ADD_PROVIDER_PROMPTS_REQUEST = 3;

    public PdvApi pdvApi;
    public WebView pdvWebView;
    public boolean loggedOnToPdv;
    public PdvConnectivityStatus pdvConnectivityStatus;
    public PdvApiRequestQueue   pdvApiRequestQueue = null;
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
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);

        final String mmHost = DEFAULT_MM_HOST;
        final String swanHost = DEFAULT_SWAN_HOST;
        pdvApi = new PdvApiImpl(swanHost, mmHost);
        loggedOnToPdv = false;
        pdvConnectivityStatus = PdvConnectivityStatus.UNKNOWN;
        pdvApiRequestQueue = new PdvApiRequestQueue();
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
                        results.callBackCompleted = true;
                        results.providers = response;
                        if (response.getStatus().equals(StatusCode.STATUS_SUCCESS)){
                            callback.onGetInstitutionsSuccess(results);
                        }
                        else {
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
                        results.callBackCompleted = true;
                        results.prompts = getPromptsResponse;
                        if (results.prompts.getStatus().equals(StatusCode.STATUS_SUCCESS)) {
                            callback.onGetPromptsSuccess(results);
                        } else {
                            callback.onGetPromptsFail(results);
                        }
                    }
                });
            }
        };

        new Thread (runPdvGetPrompts).start();
    }




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
