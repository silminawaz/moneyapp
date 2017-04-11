package com.ewise.moneyapp.Utils;

/**
 * Created by SilmiNawaz on 10/4/17.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class HomeWatcher {

    static final String TAG = "HomeWatcher";
    private Context mContext;
    private IntentFilter mFilter;
    private OnHomePressedListener mListener;
    private InnerRecevier mRecevier;
    private boolean mIsWatching;

    public HomeWatcher(Context context) {
        mContext = context;
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mIsWatching=false;
    }

    public void setOnHomePressedListener(OnHomePressedListener listener) {
        mListener = listener;
        mRecevier = new InnerRecevier();
    }

    public void startWatch() {
        if (!mIsWatching) {
            if (mRecevier != null) {
                mContext.registerReceiver(mRecevier, mFilter);
                mIsWatching = true;
            }
        }
    }

    public void stopWatch() {
        if(mIsWatching) {
            if (mRecevier != null) {
                mContext.unregisterReceiver(mRecevier);
                mIsWatching = false;
            }
        }
    }

    class InnerRecevier extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    Log.e(TAG, "action:" + action + ",reason:" + reason);
                    if (mListener != null) {
                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                            mListener.onHomePressed();
                        } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                            mListener.onHomeLongPressed();
                        }
                    }
                }
            }
        }
    }
}
