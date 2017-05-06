package com.ewise.moneyapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

import android.widget.TextView;
import android.widget.Toast;

import com.ewise.android.pdv.api.PdvApi;
import com.ewise.android.pdv.api.model.PromptEntry;
import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.provider.Group;
import com.ewise.android.pdv.api.model.provider.Providers;
import com.ewise.android.pdv.api.model.response.GetPromptsData;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;
import com.ewise.moneyapp.Utils.PdvConnectivityStatus;

import org.xwalk.core.XWalkView;

import java.util.ArrayList;
import java.util.List;

public class AddInstitutionActivity extends AppCompatActivity implements PdvConnectivityCallback {

    PdvApiResults providerResults;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_institution);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }


    @Override
    protected void onResume() {
        super.onResume();

        // Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        //sn: todo: call getprompts api and setup the data for the view
        MoneyAppApp myApp = ((MoneyAppApp) getApplication());
        PdvApi pdvApi = myApp.getPdvApi();
        //REMOVE XWALK myApp.pdvWebView = (WebView) findViewById(R.id.ewise_webview);
        myApp.pdvWebView = (XWalkView) findViewById(R.id.ewise_webview);
        try {
            pdvApi.apiInit(getApplicationContext(), myApp.pdvWebView);

            //what do I want to do now?  - call getInstitutions
            if (myApp.pdvConnectivityStatus != PdvConnectivityStatus.SUCCESS) {
                myApp.checkConnectivity(this, MoneyAppApp.DEFAULT_SWAN_HOST, this);
            } else
            {
                onPdvConnected();
            }

            if (myApp.pdvLoginStatus.isLoggedOnToPdv()){
                Toast.makeText(getApplicationContext(), R.string.pdvapi_get_institutions_message, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            String sObjString = myApp.pdvApi.toString();
            generalExceptionHandler(e.getClass().getName(), sMethod, e.getMessage(), sObjString);
        }

        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                new String[]{
                        getString(R.string.provider_list_retrieving_message)
                }));




        myApp.pdvGetInstitutions(this);  //callbacks will handle the rest

    }

    //Begin: PdvConnectivityCallback Interface implementations
    @Override
    public void onPdvConnected(){

        //call getinstitutions API

    }

    @Override
    public void onPdvDisconnected(){

    }

    @Override
    public void onGetPromptsFail(PdvApiResults results){

    }

    @Override
    public void onGetPromptsSuccess(PdvApiResults results){

    }


    @Override
    public void onGetInstitutionsFail(PdvApiResults results){

        Log.d("PDVAPI", results.providers.toString());

        providerResults = results;

        Runnable getInstitutionFail = new Runnable() {
            @Override
            public void run() {
                String msg = "STATUS: ";
                msg = msg + providerResults.providers.getStatus() + " | ERRORTYPE: " + providerResults.providers.getErrorType() + " | MESSAGE: " + providerResults.providers.getMessage();
                Toast.makeText(AddInstitutionActivity.this, msg, Toast.LENGTH_LONG).show();
                Log.d("PDVAPI", msg);

                Spinner spinner = (Spinner) findViewById(R.id.spinner);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

                String providerList[] = new String[1];
                providerList[0] = providerResults.providers.getMessage();

                MyAdapter dataAdapter = new MyAdapter(toolbar.getContext(), providerList);
                spinner.setAdapter(dataAdapter);
            }
        };

        runOnUiThread(getInstitutionFail);

    }

    @Override
    public void onGetInstitutionsSuccess(PdvApiResults results){

        Log.d("PDVAPI", results.providers.getData().toString());

        providerResults = results;

        Runnable setProviderList = new Runnable() {
            @Override
            public void run() {
                List<Group> groups = providerResults.providers.getData().getGroups();
                String providerList[] = new String[groups.size()];
                int i=0;
                for (Group group : groups){
                    providerList[i] = group.getGroupDesc();
                    i++;
                }

                Spinner spinner = (Spinner) findViewById(R.id.spinner);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

                Log.d("PROVIDERS", Integer.toString(providerList.length));

                spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // When the given dropdown item is selected, show its contents in the
                        // container view.

                        //Group group = providerResults.providers.getData().getGroups().get(position);

                        //String strGroup = PdvApiResults.toJsonString(group);

                        //Log.d("D1", strGroup);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, AddInstitutionFragment.newInstance(position))
                                .commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });


                MyAdapter dataAdapter = new MyAdapter(toolbar.getContext(), providerList);
                spinner.setAdapter(dataAdapter);
            }
        };

        runOnUiThread(setProviderList);

    }

    @Override
    public void onGetUserProfileSuccess(PdvApiResults results){

    }

    @Override
    public void onGetUserProfileFail(PdvApiResults results){

    }

    @Override
    public void onRestoreAccountsComplete(String instId){

    }

    @Override
    public void onRestoreAccountsAllComplete(){

    }

    @Override
    public void onRestoreAccountsNone()
    {

    }

    @Override
    public void onRestoreAccountsFail()
    {

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

    }

    @Override
    public void onRemoveInstitutionFail(PdvApiResults results)
    {

    }

    //End: PdvConnectivityCallback implementation

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((data != null) && (requestCode == MoneyAppApp.ADD_PROVIDER_PROMPTS_REQUEST)){
            //String jsonPromptsData = data.getStringExtra("promptsData");
            //GetPromptsData promptsData = PdvApiResults.objectFromString(jsonPromptsData, GetPromptsData.class);
            //Log.d("AddInst-instCode", PdvApiResults.toJsonString(promptsData));
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_institution, menu);
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


    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_institution, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }


    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
