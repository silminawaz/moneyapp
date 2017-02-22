package com.ewise.moneyapp;


import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.animation.Animator;
import android.widget.Toast;

import com.ewise.android.pdv.api.PdvApi;
import com.ewise.android.pdv.api.PdvApiImpl;
import com.ewise.android.pdv.api.callbacks.PdvApiCallback;
import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.StatusCode;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvConnectivityStatus;

import java.lang.ref.WeakReference;
import java.net.URL;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity
        implements AccountsFragment.OnFragmentInteractionListener
{

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
    public PdvApiResults    pdvApiResults = new PdvApiResults();
    Handler                 handler = new Handler();

/*  // Implementing login using AsyncTask
    // This is not really necessary because the PDV API is in itself Async in nature so we can use separate threads to execute

    private class LoginToPdvTask extends AsyncTask<Object, Void, Void> {

        PdvApiResults results;  //param needed to update results on background thread
        boolean loggedOnToPdv = false;

        @Override
        protected void onPreExecute(){
            results = new PdvApiResults();
            Toast.makeText(getApplicationContext(), R.string.pdvapi_prelogin_message, Toast.LENGTH_LONG).show();
            Log.d("PDVAPI", getString(R.string.pdvapi_prelogin_message));

        }

        @Override
        protected Void doInBackground(Object... params){
            //String username = myApp.getIMEI();

            //      login first time should prevent an existing login from being re-used without proper authorisation - i.e. an authentication mechanism is needed - maybe a login TOKEN
            String username = "silmi";

            final PdvApi pdvApi = (PdvApi) params[0];
            pdvApi.setUser(username, new PdvApiCallback<String>() {
                @Override public void result(final Response response) {
                    results.setUserResponse = response;
                    Log.d("SETUSER", response.getStatus());
                    if (StatusCode.STATUS_SUCCESS.equals(response.getStatus())) {
                        pdvApi.initialise(new PdvApiCallback.PdvApiInitialiseCallback() {
                            @Override public void result(final String status) {
                                results.initialiseStatus = status;
                                results.callBackCompleted = true;
                                loggedOnToPdv = true;
                                Log.d("INIT", status);
                            }
                        });
                    }
                    else{
                        results.callBackCompleted = true;
                        loggedOnToPdv = false;
                    }
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            //wait until callback is completed
            while (!results.callBackCompleted){
                //Since login to PDV is essential to continue we will block the UI thread until the call back is completed
            }

            if (loggedOnToPdv) {
                ((MoneyAppApp)getApplication()).loggedOnToPdv = true;
            }

            pdvApiResults = results; //set the results accessible to the activity context

            String msg  = getString(R.string.pdvapi_postlogin_message);
            if (results.setUserResponse != null) {
                msg = msg + " - " + results.setUserResponse.getStatus() + " - " + results.initialiseStatus;
            }
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            Log.d("PDVAPI", msg);

        }

    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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

                if (((MoneyAppApp)getApplication()).loggedOnToPdv == true){
                        startActivity(new Intent(MainActivity.this, AddInstitutionActivity.class));
                }
                else
                {
                    Toast.makeText(getApplicationContext(), R.string.pdvapi_not_loggedin, Toast.LENGTH_LONG).show();
                }

            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });

        MoneyAppApp myApp = ((MoneyAppApp) getApplication());
        PdvApi pdvApi = myApp.getPdvApi();
        boolean loggedOnToPdv = myApp.loggedOnToPdv;
        myApp.pdvWebView = (WebView) findViewById(R.id.ewise_webview);

        try {
            pdvApi.apiInit(getApplicationContext(), myApp.pdvWebView);
            if (!myApp.loggedOnToPdv){
                loginToPDV();
            }
        } catch (Exception e) {
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            String sObjString = myApp.pdvApi.toString();
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, sObjString);
        }
    }

    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }


    private void loginToPDV()
    {

        Runnable login = new Runnable(){
            @Override
            public void run(){
                //String username = myApp.getIMEI();//todo: get imei number by allowing READ_PHONE_STATE permission
                //todo: remove hardcoded username (i.e. for production we need to allow a login user name on first time login and Persistent PIN for access)
                //      login first time should prevent an existing login from being re-used without proper authorisation - i.e. an authentication mechanism is needed - maybe a login TOKEN
                String username = "silmi";
                final MoneyAppApp myApp = (MoneyAppApp)getApplication();
                final PdvApi pdvApi = myApp.getPdvApi();
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
            }
        };

        new Thread(login).start();
    }

    public void notifyPdvLoginSuccess(){
        //todo: whatever required when login is successful including UI updates
        ((MoneyAppApp)getApplication()).loggedOnToPdv = true;
        String msg = getString(R.string.pdvapi_login_success_message);
        msg = msg + " - setUser(): " + pdvApiResults.setUserResponse.getStatus() + " : " + pdvApiResults.setUserResponse.getMessage() + " - initialise(): " + pdvApiResults.initialiseStatus;
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    public void notifyPdvLoginFail(){
        //todo: whatever required when login is successful including UI updates
        ((MoneyAppApp)getApplication()).loggedOnToPdv = false;
        String msg = getString(R.string.pdvapi_login_failed_message);
        msg = msg + " - setUser(): " + pdvApiResults.setUserResponse.getStatus() + " : " + pdvApiResults.setUserResponse.getMessage() + " - initialise(): " + pdvApiResults.initialiseStatus;
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
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
            if (position == 0) {
                //return the AccountFragment class
                //return AccountFragment.newInstance (position + 1);
                return AccountsFragment_.newInstance();
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
                    return getString(R.string.section_name_accounts);
                case 1:
                    return getString(R.string.section_name_spending);
                case 2:
                    return getString(R.string.section_name_goals);
                case 3:
                    return getString(R.string.section_name_peers);
            }
            return null;
        }
    }
}
