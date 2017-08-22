package com.ewise.moneyapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ewise.moneyapp.R;
import com.ewise.moneyapp.Utils.Settings;
import com.ewise.moneyapp.Utils.SignonProfile;
import com.ewise.moneyapp.views.PinEntryTextView;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 30/5/17.
 */

public class ChangePinDialogFragment extends DialogFragment {

    public static final String TAG = "ChangePinDialogFr...";

    SignonProfile profileEntry=null;

    Button saveButton;
    Button cancelButton;
    ProgressBar progressBar;
    Settings settings;

    private PinEntryTextView txtPinEntryCurrent;
    private PinEntryTextView txtPinEntryNew;
    private PinEntryTextView txtPinEntryConfirmNew;


    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static ChangePinDialogFragment newInstance() {
        ChangePinDialogFragment f = new ChangePinDialogFragment();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.change_pin_dialog, container, false);

        saveButton = (Button) v.findViewById(R.id.btnChangePinSave);
        cancelButton = (Button) v.findViewById(R.id.btnChangePinCancel);

        txtPinEntryCurrent = (PinEntryTextView) v.findViewById(R.id.txtPinEntryCurrent);
        txtPinEntryNew = (PinEntryTextView) v.findViewById(R.id.txtPinEntryNew);
        txtPinEntryConfirmNew = (PinEntryTextView) v.findViewById(R.id.txtPinEntryConfirmNew);

        progressBar = (ProgressBar) v.findViewById(R.id.changePinSaveProgressBar);

        return v;
    }

    @Override
    public void onPause(){
        super.onPause();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        settings = Settings.getInstance(getActivity());

        progressBar.setVisibility(View.GONE);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.

                Log.d(TAG, "cancelButton.setOnClickListener.onClick()");
                dismissAllowingStateLoss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "saveButton.setOnClickListener.onClick()");
                progressBar.setVisibility(View.VISIBLE);
                String message=getChangePinValidationMessage();
                if (message!=null){
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
                else {
                    if (isAdded()) {
                        settings.getEncryptedPin(getActivity()).savePIN(txtPinEntryNew.getText().toString(), getActivity());
                        Toast.makeText(getActivity(), R.string.settings_changepin_success_message, Toast.LENGTH_LONG).show();
                        dismissAllowingStateLoss();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Nullable
    public String getChangePinValidationMessage()
    {
        String message=null;
        if (isAdded()){

            //1. validate current PIN
            message=isCurrentPINValid();
            if (message==null){
                message = isNewPINValid();
            }
        }
        else{
            message=getString(R.string.profile_name_validation_page_not_added);
        }

        return message;
    }



    @Nullable
    public String isCurrentPINValid(){
        //returns null if there is no error and pin is valid
        String message=null;
        //PIN 1 must be the equal to MaxLength of pinentry
        if (!(txtPinEntryCurrent.getText().length()==txtPinEntryCurrent.getMaxLength())){
            message = getString(R.string.pinentry_maxlength_error_message, Integer.toString(txtPinEntryCurrent.getMaxLength()));
        }
        else
        {
            //validate if the PIN is correct
            if (!settings.getEncryptedPin(getActivity()).validatePIN(txtPinEntryCurrent.getText().toString(),getActivity())) {
                message = getString(R.string.pinentry_invalid_pin);
            }
        }

        return message;
    }

    @Nullable
    public String isNewPINValid(){
        String message=null;
        //new pin length must be valid
        if (!(txtPinEntryNew.getText().length()==txtPinEntryNew.getMaxLength())){
            message = getString(R.string.pinentry_maxlength_error_message, Integer.toString(txtPinEntryNew.getMaxLength()));
        }
        //confirm new pin length must be valid
        else if (!(txtPinEntryConfirmNew.getText().length()==txtPinEntryConfirmNew.getMaxLength())) {
            message = getString(R.string.pinentry_maxlength_error_message, Integer.toString(txtPinEntryConfirmNew.getMaxLength()));
        }
        //new pin must be equal to confirm new pin
        else if (!txtPinEntryNew.getText().toString().equals(txtPinEntryConfirmNew.getText().toString())){
            message = getString(R.string.pinentry_pin_not_matching);
        }

        return message;
    }

}
