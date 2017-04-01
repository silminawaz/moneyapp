package com.ewise.moneyapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ewise.android.pdv.api.model.PromptEntry;
import com.ewise.android.pdv.api.model.UserProviderEntry;
import com.ewise.android.pdv.api.model.response.GetPromptsData;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;

/**
 * Created by SilmiNawaz on 1/4/17.
 */
public class EditProviderDialogFragment extends DialogFragment implements PdvConnectivityCallback {


    UserProviderEntry providerEntry;
    GetPromptsData promptsData;
    LinearLayout editProviderPromptsLayout;
    LinearLayout linearlayoutGetPromptsMsg;
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

        editProviderPromptsLayout = (LinearLayout) v.findViewById(R.id.editProviderPromptsLayout);
        linearlayoutGetPromptsMsg = (LinearLayout) v.findViewById(R.id.linearlayoutGetPromptsMsg);



        // Watch for button clicks.
        cancelButton = (Button)v.findViewById(R.id.btnEditProviderCancel);
        saveButton = (Button)v.findViewById(R.id.btnEditProviderSave);
        saveButton.setVisibility(View.VISIBLE);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.

                Log.d("EditProviderDialogFr...", "cancelButton.setOnClickListener.onClick()");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.

                Log.d("EditProviderDialogFr...", "saveButton.setOnClickListener.onClick()");
            }
        });



        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //todo: retrieve the credentials (call pdv api getCredential() - and send the data to the dialog
        MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
        if (!app.pdvApiRequestQueue.isRequestInProgress()) {
            if (!app.pdvApiRequestQueue.isRequestPending()) {
                app.pdvGetPrompts(app.getInstCodeFromInstId(providerEntry.getIid()), this);
            }
        }

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
                        editProviderPromptsLayout = PdvApiResults.getViewsForPromptsEntry(promptEntry, editProviderPromptsLayout, 0, layoutParams, getActivity());
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
        Log.d("EditProviderDialogFr...", "onSetCredentialSuccess() : results=" + PdvApiResults.toJsonString(results));
        promptsData = results.credential.getData();

        Runnable addPromptsToUI = new Runnable() {
            @Override
            public void run() {

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((ViewGroup.LayoutParams.MATCH_PARENT), (ViewGroup.LayoutParams.WRAP_CONTENT));

                for (PromptEntry promptEntry : promptsData.getPrompts()){
                    if (editProviderPromptsLayout!=null) {
                        editProviderPromptsLayout = PdvApiResults.getViewsForPromptsEntry(promptEntry, editProviderPromptsLayout, 0, layoutParams, getActivity());
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
    }

    @Override
    public void onGetCredentialFail(PdvApiResults results)
    {

    }

    @Override
    public void onSetCredentialSuccess(PdvApiResults results)
    {

    }

    @Override
    public void onSetCredentialFail(PdvApiResults results)
    {

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
