package com.ewise.moneyapp.Fragments;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ewise.android.pdv.api.model.PromptEntry;
import com.ewise.android.pdv.api.model.UserProviderEntry;
import com.ewise.android.pdv.api.model.provider.Institution;
import com.ewise.android.pdv.api.model.response.GetPromptsData;
import com.ewise.moneyapp.MoneyAppApp;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;
import com.ewise.moneyapp.data.PdvAccountResponse;

import java.util.List;

/**
 * Created by SilmiNawaz on 1/4/17.
 */
public class EditProviderDialogFragment extends DialogFragment implements PdvConnectivityCallback {


    UserProviderEntry providerEntry;
    GetPromptsData promptsData;

    ImageView imageInstitutionLogo;
    LinearLayout editProviderPromptsLayout;
    LinearLayout linearlayoutGetPromptsMsg;
    LinearLayout editProviderAccountsLayout;
    Button saveButton;
    Button cancelButton;
    ProgressBar editProviderSaveProgressBar;
    TextView editProviderAccountsText;
    ImageView editProviderAccountsInfoIcon;
    TextView editProviderHelpText;
    ImageView editProviderHelpIcon;
    TextView editProviderText;
    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static EditProviderDialogFragment newInstance(UserProviderEntry providerEntry) {
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

        MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();


        if (!providerEntry.getDesc().equals("")) {
            instName.setText(providerEntry.getDesc() + "\n" + providerEntry.getUid());
        }
        else{
            //this is to handle a bug in the SDK where the provider entry description is not set after call to setCredential()
            Institution inst = app.getInstitution(providerEntry.getIid());
            if (inst!=null){
                instName.setText(inst.getInstDesc() + "\n" + providerEntry.getUid());
            }
        }

        //instName.setCompoundDrawables(ContextCompat.getDrawable(getActivity(), app.getInstitutionIconResourceId(providerEntry.getIid())), null,null,null);


        editProviderPromptsLayout = (LinearLayout) v.findViewById(R.id.editProviderPromptsLayout);
        editProviderPromptsLayout.setVisibility(View.GONE);

        linearlayoutGetPromptsMsg = (LinearLayout) v.findViewById(R.id.linearlayoutGetPromptsMsg);
        editProviderAccountsLayout = (LinearLayout) v.findViewById(R.id.editProviderAccountsLayout);

        imageInstitutionLogo = (ImageView) v.findViewById(R.id.imageInstitutionLogo);
        imageInstitutionLogo.setImageResource(app.getInstitutionIconResourceId(providerEntry.getIid()));
        editProviderAccountsText = (TextView)  v.findViewById(R.id.editProviderAccountsText);
        editProviderAccountsText.setPaintFlags(editProviderAccountsText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        editProviderAccountsInfoIcon = (ImageView) v.findViewById(R.id.editProviderAccountsInfoIcon);
        editProviderHelpText = (TextView)  v.findViewById(R.id.editProviderHelpText);
        editProviderHelpText.setPaintFlags(editProviderAccountsText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        editProviderHelpIcon = (ImageView) v.findViewById(R.id.editProviderHelpIcon);
        editProviderText = (TextView)  v.findViewById(R.id.editProviderText);


        // Watch for button clicks.
        cancelButton = (Button)v.findViewById(R.id.btnEditProviderCancel);
        saveButton = (Button)v.findViewById(R.id.btnEditProviderSave);
        saveButton.setVisibility(View.VISIBLE);
        editProviderSaveProgressBar = (ProgressBar) v.findViewById(R.id.editProviderSaveProgressBar);
        editProviderSaveProgressBar.setVisibility(View.GONE);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.

                Log.d("EditProviderDialogFr...", "cancelButton.setOnClickListener.onClick()");
                dismissAllowingStateLoss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                Log.d("EditProviderDialogFr...", "saveButton.setOnClickListener.onClick()");
                saveCredentials();
            }
        });

        editProviderAccountsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("EditProviderDialogFr...", "editProviderAccountsText.setOnClickListener.onClick()");
                toggleDisplayAccounts();
            }
        });

        editProviderAccountsInfoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("EditProviderDialogFr...", "editProviderAccountsInfoIcon.setOnClickListener.onClick()");
                toggleDisplayAccounts();
            }
        });

        editProviderHelpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("EditProviderDialogFr...", "editProviderHelpText.setOnClickListener.onClick()");
                //toggleDisplayHelp();
            }
        });

        editProviderHelpIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("EditProviderDialogFr...", "editProviderHelpIcon.setOnClickListener.onClick()");
                //toggleDisplayHelp();
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
        //populateAccountsForUserProvider();


        if (!app.pdvApiRequestQueue.isRequestInProgress()) {
            if (!app.pdvApiRequestQueue.isRequestPending()) {
                app.pdvGetPrompts(app.getInstCodeFromInstId(providerEntry.getIid()), this);
            }
            else{
                Toast.makeText(getActivity(), getActivity().getString(R.string.edit_provider_try_again_text), Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getActivity(), getActivity().getString(R.string.edit_provider_try_again_text), Toast.LENGTH_LONG).show();
        }

    }


    public void toggleDisplayAccounts(){

        if(editProviderAccountsLayout.getVisibility()==View.VISIBLE){
            editProviderAccountsLayout.setVisibility(View.GONE);
            editProviderAccountsText.setText(getActivity().getString(R.string.edit_provider_account_show_text));
            //editProviderAccountsInfoIcon.setBackgroundColor(Color.TRANSPARENT);
        }
        else{
            editProviderAccountsLayout.setVisibility(View.VISIBLE);
            editProviderAccountsText.setText(getActivity().getString(R.string.edit_provider_account_hide_text));
            //editProviderAccountsInfoIcon.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.coloreWisePrimary));
        }
    }



    public void toggleDisplayHelp(){

        if(editProviderText.getVisibility()==View.VISIBLE){
            editProviderText.setVisibility(View.GONE);
            editProviderHelpText.setText(getActivity().getString(R.string.edit_provider_help_show_text));
            //editProviderHelpIcon.setBackgroundColor(Color.TRANSPARENT);
        }
        else{
            editProviderText.setVisibility(View.VISIBLE);
            editProviderHelpText.setText(getActivity().getString(R.string.edit_provider_help_hide_text));
            //editProviderHelpIcon.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.coloreWisePrimary));
        }
    }


    public void saveCredentials() {
        editProviderSaveProgressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);
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
                else
                {
                    editProviderSaveProgressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                }
            }
        }
    }

    public void populateAccountsForUserProvider(){

        if (editProviderAccountsLayout!=null){
            MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
            List<PdvAccountResponse.AccountsObject> accounts = app.getAccountsForUserProvider (providerEntry.getIid());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((ViewGroup.LayoutParams.MATCH_PARENT), (ViewGroup.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                    getActivity().getResources().getDimensionPixelSize(R.dimen.ewise_touch_icon_size_small),
                    getActivity().getResources().getDimensionPixelSize(R.dimen.ewise_touch_icon_size_small));
            LinearLayout.LayoutParams viewLayoutParams = new LinearLayout.LayoutParams(
                    (ViewGroup.LayoutParams.MATCH_PARENT),
                    getActivity().getResources().getDimensionPixelSize(R.dimen.ewise_vertical_margin));
            if (accounts!=null){
                for (PdvAccountResponse.AccountsObject account: accounts){
                    LinearLayout accountLayout = new LinearLayout(getContext());
                    accountLayout.setLayoutParams(layoutParams);
                    accountLayout.setOrientation(LinearLayout.VERTICAL);
                    //accountLayout.addView(getAccountImageView(imageLayoutParams, account));
                    accountLayout.addView(getAccountTextView(layoutParams, account));
                    View view = new View (getContext());
                    view.setLayoutParams(viewLayoutParams);
                    accountLayout.addView(view);
                    editProviderAccountsLayout.addView(accountLayout);
                }
            }
            else {
                editProviderAccountsLayout.addView(getAccountTextView(layoutParams, null));
            }
        }
    }

    private ImageView getAccountImageView(LinearLayout.LayoutParams layoutParams, PdvAccountResponse.AccountsObject account){
        ImageView accountImageView = new ImageView (getContext());
        accountImageView.setLayoutParams(layoutParams);
        //accountImageView.setImageResource(((MoneyAppApp)getActivity().getApplication()).getInstitutionIconResourceId(account.instId));
        float margin = getActivity().getResources().getDimension(R.dimen.ewise_horizontal_margin);
        accountImageView.setPadding(0, Math.round(margin), 0, 0);
        accountImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ewise_border));

        return accountImageView;
    }


    private TextView getAccountTextView(LinearLayout.LayoutParams layoutParams, PdvAccountResponse.AccountsObject account){
        TextView accountTextView = new TextView(getContext());
        //layoutParams.gravity = Gravity.END;
        accountTextView.setLayoutParams(layoutParams);
        accountTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.ewise_secondary_text_size));
        if (account!=null) {
            accountTextView.setText(account.accountName + " \n" + account.accountNumber + " \n" + account.balance + " " + account.currency);
        }
        else{
            accountTextView.setText(getString(R.string.edit_provider_account_not_found_text));
        }
        accountTextView.setGravity(Gravity.TOP);
        accountTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.coloreWiseMainTextBlack));
        float margin = getActivity().getResources().getDimension(R.dimen.ewise_horizontal_margin_large);
        accountTextView.setPadding(Math.round(margin), Math.round(margin), Math.round(margin), Math.round(margin));
        accountTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ewise_border));
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
                        //linearlayoutGetPromptsMsg.setVisibility(View.GONEcc);
                        //saveButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        if (isAdded()) {

            if (getActivity() != null) {
                Log.d("EditProviderDialogFr...", "about to run addPromptsToUI runnable");
                getActivity().runOnUiThread(addPromptsToUI);
            }
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
    public void onRestoreAccountsNone()
    {

    }

    @Override
    public void onRestoreAccountsFail()
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
                        editProviderPromptsLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        if (isAdded()) {
            if (getActivity() != null) {
                Log.d("EditProviderDialogFr...", "about to run addPromptsToUI runnable");
                getActivity().runOnUiThread(setPromptValues);
            }
        }
    }

    @Override
    public void onGetCredentialFail(PdvApiResults results)
    {
        if (isAdded()) {
            if (getActivity() != null) {

                Log.d("EditProviderDialogFr...", "onGetCredentialFail() : results=" + PdvApiResults.toJsonString(results));
                getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    linearlayoutGetPromptsMsg.setVisibility(View.GONE);
                                                    saveButton.setVisibility(View.VISIBLE);
                                                    editProviderPromptsLayout.setVisibility(View.VISIBLE);
                                                }
                });
            }
        }

    }

    @Override
    public void onSetCredentialSuccess(PdvApiResults results)
    {
        if (isAdded()) {
            Log.d("EditProviderDialogFr...", "onSetCredentialSuccess() : results=" + PdvApiResults.toJsonString(results));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    editProviderSaveProgressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    Toast.makeText(getActivity(), getString(R.string.edit_provider_save_button_success_text), Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    public void onSetCredentialFail(PdvApiResults results)
    {
        Log.d("EditProviderDialogFr...", "onSetCredentialFail() : results=" + PdvApiResults.toJsonString(results));

        if (isAdded()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    editProviderSaveProgressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    Toast.makeText(getActivity(), getString(R.string.edit_provider_save_button_fail_text), Toast.LENGTH_LONG).show();

                }
            });
        }
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

    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = getActivity().getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }

    private void generalExceptionHandler(Exception e, String eMethod, String eObjectString) {
        String sFormat = getActivity().getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, e.getClass().getName(), eMethod, e.getMessage(), eObjectString));
    }
}
