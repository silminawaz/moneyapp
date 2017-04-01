package com.ewise.moneyapp.Utils;

import android.util.Log;

/**
 * Created by SilmiNawaz on 21/3/17.
 */
public class PdvLoginStatus{

    private int loginStatus;

    public PdvLoginStatus(){
        notifyLoggedOffFromPdv();
    }

    public int toInt () { return loginStatus; }

    public boolean equals(int value) {return (loginStatus==value);}

    public boolean isLoggedOnToPdv()
    {
        if (loginStatus==2){
            return true;
        }
        return false;
    }

    public boolean isLoginInProgress()
    {
        if(loginStatus==1) {
            return true;
        }

        return false;
    }

    public boolean isLoggedOffFromPdv()
    {
        if(loginStatus==0) {
            return true;
        }

        return false;
    }

    public void notifyLoggedOffFromPdv()
    {
        synchronized (this) {
            Log.d("PdvLoginStatus", "notifyLoggedOffFromPdv() - setting to LoggedOff (0)");
            loginStatus = 0;
        }
    }

    public void notifyLogonInProgress()
    {
        synchronized (this) {
            Log.d("PdvLoginStatus", "notifyLogonInProgress() - setting to LogonInProgress (1)");
            loginStatus = 1;
        }
    }

    public void notifyLoggedOnToPdv()
    {
        synchronized (this) {
            Log.d("PdvLoginStatus", "notifyLoggedOnToPdv() - setting to LoggedOn (2)");
            loginStatus = 2;
        }
    }

}
