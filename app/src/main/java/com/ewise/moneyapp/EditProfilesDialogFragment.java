package com.ewise.moneyapp;

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
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;
import com.ewise.moneyapp.Utils.SignonProfile;
import com.ewise.moneyapp.data.PdvAccountResponse;

import java.util.List;

/**
 * Created by SilmiNawaz on 1/4/17.
 */
public class EditProfilesDialogFragment extends DialogFragment {


    SignonProfile profileEntry=null;


    ImageView imageInstitutionLogo;
    Button saveButton;
    Button deleteButton;
    Button cancelButton;
    ProgressBar progressBar;
    EditText profileNameEditText;
    EditText profileDescriptionEditText;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static EditProfilesDialogFragment newInstance(SignonProfile signonProfile) {
        EditProfilesDialogFragment f = new EditProfilesDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();

        String signonProfileString = "";
        if (signonProfile!=null) {
            signonProfileString=PdvApiResults.toJsonString(signonProfile);
            args.putString("signonProfileString", signonProfileString);
            f.setArguments(args);
        }

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String signonProfileString=null;
        if (getArguments()!=null) {
            signonProfileString = getArguments().getString("signonProfileString");
            profileEntry = PdvApiResults.objectFromString(signonProfileString, SignonProfile.class);
        }


        View v = inflater.inflate(R.layout.edit_profiles_dialog, container, false);

        saveButton = (Button) v.findViewById(R.id.btnProfilesSave);
        deleteButton = (Button) v.findViewById(R.id.btnProfilesDelete);;
        cancelButton = (Button) v.findViewById(R.id.btnProfilesCancel);;
        profileNameEditText = (EditText) v.findViewById(R.id.editTextProfileName);
        profileDescriptionEditText = (EditText) v.findViewById(R.id.editTextProfileDesc);


        progressBar = (ProgressBar) v.findViewById(R.id.profilesSaveProgressBar);


        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (profileEntry!=null) {
            profileNameEditText.setText(profileEntry.name);
            profileDescriptionEditText.setText(profileEntry.description);
        }

        progressBar.setVisibility(View.GONE);

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
                if (isAdded()) {
                    if (profileEntry==null) {
                        profileEntry = new SignonProfile();
                        profileEntry.name=profileNameEditText.getText().toString();
                        profileEntry.description=profileDescriptionEditText.getText().toString();
                        ((MainActivity)getActivity()).addNewSignonProfile(profileEntry);
                    }
                    else
                    {
                        profileEntry.name=profileNameEditText.getText().toString();
                        profileEntry.description=profileDescriptionEditText.getText().toString();
                        ((MainActivity)getActivity()).saveSignonProfile(profileEntry);
                    }
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("EditProviderDialogFr...", "deleteButton.setOnClickListener.onClick()");
                ((MainActivity)getActivity()).deleteSignonProfile(profileEntry);
            }
        });

    }


    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = getActivity().getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }

    private void generalExceptionHandler(Exception e, String eMethod, String eObjectString) {
        String sFormat = getActivity().getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, e.getClass().getName(), eMethod, e.getMessage(), eObjectString));
    }
}
