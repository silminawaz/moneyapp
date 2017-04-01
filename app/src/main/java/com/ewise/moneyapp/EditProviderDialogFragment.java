package com.ewise.moneyapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ewise.android.pdv.api.model.PromptEntry;
import com.ewise.android.pdv.api.model.UserProviderEntry;
import com.ewise.android.pdv.api.model.provider.Institution;
import com.ewise.android.pdv.api.model.response.GetPromptsData;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;
import com.ewise.moneyapp.data.PdvAccountResponse;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

/**
 * Created by SilmiNawaz on 1/4/17.
 */
public class EditProviderDialogFragment extends DialogFragment implements PdvConnectivityCallback {


    UserProviderEntry providerEntry;
    GetPromptsData promptsData;
    LinearLayout editProviderPromptsLayout;
    LinearLayout linearlayoutGetPromptsMsg;
    LinearLayout editProviderAccountsLayout;
    Button saveButton;
    Button cancelButton;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static EditProviderDialogFragment newInstance(UserProviderEntry providerEntry) {
        EditProviderDialogFragment f = new EditProviderDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        String providerString = PdvApiResults.toJsonString(providerEntry);
        args.putString("providerString", providerString);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String providerString = getArguments().getString("providerString");
        providerEntry = PdvApiResults.objectFromString(providerString, UserProviderEntry.class);

        View v = inflater.inflate(R.layout.edit_institution_prompts_dialog, container, false);
        View tv = v.findViewById(R.id.text);

        TextView instName = (TextView) v.findViewById(R.id.textInstitutionName);
        instName.setText(providerEntry.getDesc());

        MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();

        if (!providerEntry.getDesc().equals("")) {
            instName.setText(providerEntry.getDesc());
        }
        else{
            //this is to handle a bug in the SDK where the provider entry description is not set after call to setCredential()
            Institution inst = app.getInstitution(providerEntry.getIid());
            if (inst!=null){
                instName.setText(inst.getInstDesc());
            }
        }

        editProviderPromptsLayout = (LinearLayout) v.findViewById(R.id.editProviderPromptsLayout);
        linearlayoutGetPromptsMsg = (LinearLayout) v.findViewById(R.id.linearlayoutGetPromptsMsg);
        editProviderAccountsLayout = (LinearLayout) v.findViewById(R.id.editProviderAccountsLayout);

        // Watch for button clicks.
        cancelButton = (Button)v.findViewById(R.id.btnEditProviderCancel);
        saveButton = (Button)v.findViewById(R.id.btnEditProviderSave);
        saveButton.setVisibility(View.VISIBLE);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.

                Log.d("EditProviderDialogFr...", "cancelButton.setOnClickListener.onClick()");
                //call main activity method to close the dialog
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                Log.d("EditProviderDialogFr...", "saveButton.setOnClickListener.onClick()");
                saveCredentials();
            }
        });



        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //todo: retrieve the credentials (call pdv api getCredential() - and send the data to the dialog
        MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();

        //populate the accounts for this provider
        populateAccountsForUserProvider();

        if (!app.pdvApiRequestQueue.isRequestInProgress()) {
            if (!app.pdvApiRequestQueue.isRequestPending()) {
                app.pdvGetPrompts(app.getInstCodeFromInstId(providerEntry.getIid()), this);
            }
        }

    }


    public void saveCredentials() {
        String sEmpty = "";
        boolean isValid = true;
        if (editProviderPromptsLayout != null) {
            if (editProviderPromptsLayout.getChildCount()>1){
                for (PromptEntry promptEntry : promptsData.getPrompts()) {
                    for (int i=0; i<editProviderPromptsLayout.getChildCount(); i++){
                        View v = editProviderPromptsLayout.getChildAt(i);
                        if (v instanceof EditText) {
                            TextView t = (TextView)v;
                            PromptEntry editTextPromptsEntry = (PromptEntry) t.getTag();
                            if (editTextPromptsEntry.getIndex().equals(promptEntry.getIndex())) {
                                String sValue = t.getText().toString();
                                sValue = sValue.trim();
                                if (sValue.equals(sEmpty)){
                                    isValid=false;
                                }
                                promptEntry.setValue(t.getText().toString());
                                break;
                            }
                        }
                    }
                    if (!isValid){
                        break;
                    }
                }

                if (isValid){
                    MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
                    app.pdvSetCredential(providerEntry.getIid(), promptsData.getPrompts(), this);
                }
            }
        }
    }

    public void populateAccountsForUserProvider(){

        if (editProviderAccountsLayout!=null){
            MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
            List<PdvAccountResponse.AccountsObject> accounts = app.getAccountsForUserProvider (providerEntry.getIid());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((ViewGroup.LayoutParams.MATCH_PARENT), (ViewGroup.LayoutParams.WRAP_CONTENT));
            if (accounts!=null){
                for (PdvAccountResponse.AccountsObject account: accounts){
                    editProviderAccountsLayout.addView(getAccountTextView(layoutParams, account));
                }
            }
            else {
                editProviderAccountsLayout.addView(getAccountTextView(layoutParams, null));
            }
        }
    }

    private TextView getAccountTextView(LinearLayout.LayoutParams layoutParams, PdvAccountResponse.AccountsObject account){
        TextView accountTextView = new TextView(getContext());
        layoutParams.gravity = Gravity.END;
        accountTextView.setLayoutParams(layoutParams);
        accountTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.ewise_secondary_text_size));
        if (account!=null) {
            accountTextView.setText(account.accountName + " : " + account.balance);
        }
        else{
            accountTextView.setText(getString(R.string.edit_provider_account_not_found_text));
        }
        accountTextView.setGravity(Gravity.END);
        return accountTextView;
    }


    //Begin: PdvConnectivityCallback implementation
    @Override
    public void onPdvConnected()
    {

    }

    @Override
    public void onPdvDisconnected()
    {

    }

    @Override
    public void onGetInstitutionsFail(PdvApiResults results)
    {

    }

    @Override
    public void onGetInstitutionsSuccess(PdvApiResults results)
    {

    }

    @Override
    public void onGetPromptsSuccess(PdvApiResults results)
    {
        Log.d("EditProviderDialogFr...", "onGetPromptsSuccess() : results=" + PdvApiResults.toJsonString(results));
        promptsData = results.prompts.getData();

        Runnable addPromptsToUI = new Runnable() {
            @Override
            public void run() {

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((ViewGroup.LayoutParams.MATCH_PARENT), (ViewGroup.LayoutParams.WRAP_CONTENT));

                for (PromptEntry promptEntry : promptsData.getPrompts()){
                    if (editProviderPromptsLayout!=null) {
                        editProviderPromptsLayout = PdvApiResults.getViewsForPromptsEntry(promptEntry, editProviderPromptsLayout, editProviderPromptsLayout.getChildCount(), layoutParams, getActivity());
                        linearlayoutGetPromptsMsg.setVisibility(View.GONE);
                        saveButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        if (getActivity()!=null) {
            Log.d("EditProviderDialogFr...", "about to run addPromptsToUI runnable");
            getActivity().runOnUiThread(addPromptsToUI);
        }

        //get credentials now and map the values on callback success
        MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
        app.pdvGetCredential(providerEntry.getIid(), this);


    }

    @Override
    public void onGetPromptsFail(PdvApiResults results)
    {
        Log.d("EditProviderDialogFr...", "onGetPromptsFail() : results=" + PdvApiResults.toJsonString(results));


    }

    @Override
    public void onGetUserProfileSuccess(PdvApiResults results)
    {

    }

    @Override
    public void onGetUserProfileFail(PdvApiResults results)
    {

    }

    @Override
    public void onRestoreAccountsComplete(String instId)
    {

    }

    @Override
    public void onRestoreAccountsAllComplete()
    {

    }

    @Override
    public void onRestoreTransactionsAllComplete(PdvApiResults results)
    {

    }

    @Override
    public void onRestoreTransactionsFail(PdvApiResults results)
    {

    }

    @Override
    public void onGetCredentialSuccess(PdvApiResults results)
    {
        Log.d("EditProviderDialogFr...", " () : results=" + PdvApiResults.toJsonString(results));
        promptsData = results.credential.getData();

        Runnable setPromptValues = new Runnable() {
            @Override
            public void run() {
                if (promptsData!=null) {
                    if (editProviderPromptsLayout!=null) {
                        for (PromptEntry promptEntry : promptsData.getPrompts()) {
                            for (int i=0; i<editProviderPromptsLayout.getChildCount(); i++){
                                View v = editProviderPromptsLayout.getChildAt(i);
                                if (v instanceof EditText) {
                                    TextView t = (TextView)v;
                                    PromptEntry editTextPromptsEntry = (PromptEntry) t.getTag();
                                    if (editTextPromptsEntry.getIndex().equals(promptEntry.getIndex())) {
                                        t.setText(promptEntry.getValue());
                                        if (t.getText().toString().equals(providerEntry.getUid()) && editTextPromptsEntry.getIndex().equals(1)){
                                            //if the first edit text and the value is the same as UID , then disable the field for input
                                            t.setEnabled(false);
                                            Log.d("EditProviderDialogFr...", " () : disabling uid field");
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        linearlayoutGetPromptsMsg.setVisibility(View.GONE);
                        saveButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        if (getActivity()!=null) {
            Log.d("EditProviderDialogFr...", "about to run addPromptsToUI runnable");
            getActivity().runOnUiThread(setPromptValues);
        }
    }

    @Override
    public void onGetCredentialFail(PdvApiResults results)
    {
        Log.d("EditProviderDialogFr...", "onGetCredentialFail() : results=" + PdvApiResults.toJsonString(results));

    }

    @Override
    public void onSetCredentialSuccess(PdvApiResults results)
    {
        Log.d("EditProviderDialogFr...", "onSetCredentialSuccess() : results=" + PdvApiResults.toJsonString(results));

    }

    @Override
    public void onSetCredentialFail(PdvApiResults results)
    {
        Log.d("EditProviderDialogFr...", "onSetCredentialFail() : results=" + PdvApiResults.toJsonString(results));

    }
    //End: PdvConnectivityCallback implementation


    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = getActivity().getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }

    private void generalExceptionHandler(Exception e, String eMethod, String eObjectString) {
        String sFormat = getActivity().getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, e.getClass().getName(), eMethod, e.getMessage(), eObjectString));
    }
}
