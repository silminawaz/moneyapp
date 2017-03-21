package com.ewise.moneyapp.Utils;

/**
 * Created by SilmiNawaz on 21/3/17.
 */
public class PdvLoginStatus{

    private int loginStatus;

    public PdvLoginStatus(){
        this.loginStatus=0;
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
            loginStatus = 0;
        }
    }

    public void notifyLogonInProgress()
    {
        synchronized (this) {
            loginStatus = 1;
        }
    }

    public void notifyLoggedOnToPdv()
    {
        synchronized (this) {
            loginStatus = 2;
        }
    }

}
