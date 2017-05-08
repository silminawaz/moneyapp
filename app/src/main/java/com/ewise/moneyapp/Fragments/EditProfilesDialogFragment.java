package com.ewise.moneyapp.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ewise.moneyapp.MainActivity;
import com.ewise.moneyapp.MoneyAppApp;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.Utils.Base64ImageConverter;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.Settings;
import com.ewise.moneyapp.Utils.SignonProfile;
import com.ewise.moneyapp.Utils.SignonUser;
import com.ewise.moneyapp.adapters.ProfileSelectImageViewAdapter;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 1/4/17.
 */
public class EditProfilesDialogFragment extends DialogFragment {

    public static final String TAG = "EditProfilesDialogFr...";

    SignonProfile profileEntry=null;


    ImageView imageProfilesLogo;
    Button saveButton;
    Button deleteButton;
    Button cancelButton;
    ProgressBar progressBar;
    EditText profileNameEditText;
    EditText profileDescriptionEditText;
    ProfileSelectImageViewAdapter profileImageRecyclerViewAdapter;
    RecyclerView profileImageRecyclerView;
    Settings settings;
    boolean isEditExistingProfile=false;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static EditProfilesDialogFragment newInstance(SignonProfile signonProfile) {
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
            isEditExistingProfile=true;
        }


        View v = inflater.inflate(R.layout.edit_profiles_dialog, container, false);

        saveButton = (Button) v.findViewById(R.id.btnProfilesSave);
        deleteButton = (Button) v.findViewById(R.id.btnProfilesDelete);;
        cancelButton = (Button) v.findViewById(R.id.btnProfilesCancel);;
        profileNameEditText = (EditText) v.findViewById(R.id.editTextProfileName);
        profileDescriptionEditText = (EditText) v.findViewById(R.id.editTextProfileDesc);
        profileImageRecyclerView = (RecyclerView) v.findViewById(R.id.profileImageRecyclerView);
        imageProfilesLogo = (ImageView) v.findViewById(R.id.imageProfilesLogo);

        progressBar = (ProgressBar) v.findViewById(R.id.profilesSaveProgressBar);

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

        if (profileEntry!=null) {
            profileNameEditText.setText(profileEntry.name);
            profileNameEditText.setEnabled(false);
            profileDescriptionEditText.setText(profileEntry.description);
            Base64ImageConverter.setImageFromBase64(imageProfilesLogo, profileEntry.base64Image);
        }

        progressBar.setVisibility(View.GONE);

        profileImageRecyclerViewAdapter = new ProfileSelectImageViewAdapter(R.array.ewise_profile_images, imageProfilesLogo, getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        profileImageRecyclerView.setLayoutManager(layoutManager);
        profileImageRecyclerView.setAdapter(profileImageRecyclerViewAdapter);


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
                progressBar.setVisibility(View.VISIBLE);
                String message=getProfileNameValidationMessage();
                if (!message.isEmpty()){
                    profileNameEditText.setError(message);
                }
                else {
                    if (isAdded()) {
                        if (profileEntry == null) {
                            profileEntry = new SignonProfile();
                            profileEntry.name = profileNameEditText.getText().toString();
                        }

                        profileEntry.description = profileDescriptionEditText.getText().toString();
                        try {
                            profileEntry.base64Image = Base64ImageConverter.fromBitmapToBase64(Base64ImageConverter.fromDrawableToBitmap(imageProfilesLogo.getDrawable(), getActivity()));
                        }
                        catch (Base64ImageConverter.UnsupportedDrawableException drawableException) {
                            Toast.makeText(getActivity(), drawableException.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (isEditExistingProfile)
                            ((MainActivity) getActivity()).saveSignonProfile(profileEntry);
                        else
                            ((MainActivity) getActivity()).addNewSignonProfile(profileEntry);

                        dismissAllowingStateLoss();

                    }

                }
                progressBar.setVisibility(View.GONE);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isAdded()) {
                    if (profileEntry!=null) {
                        progressBar.setVisibility(View.VISIBLE);
                        Log.d(TAG, "deleteButton.setOnClickListener.onClick()");

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ProgressDialog p=null;
                                p = new ProgressDialog(getActivity());
                                p.setMessage(getString(R.string.profile_delete_loading_message));
                                p.setIndeterminate(true);
                                p.show();
                                MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
                                if (p!=null){
                                    if (p.isShowing()) {
                                        p.hide();
                                    }
                                }

                                ((MainActivity) getActivity()).deleteSignonProfile(profileEntry);
                                dismissAllowingStateLoss();
                                progressBar.setVisibility(View.GONE);

                            }
                        });

                        builder.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                progressBar.setVisibility(View.GONE);

                            }
                        });


                        builder.setMessage(getString(R.string.profile_delete_confirmation_message))
                                .setTitle(getString(R.string.profile_delete_dialog_title));

                        AlertDialog deleteDialog=builder.create();
                        deleteDialog.show();

                    }
                }
            }
        });

    }

    public String getProfileNameValidationMessage()
    {
        String message=null;
        if (isAdded()){
            String profileName=profileNameEditText.getText().toString();
            message = SignonProfile.validateProfileName(profileName, getActivity());
            if (!message.isEmpty()) return message;

            if (!isEditExistingProfile) {
                //so far so good - check if there is a profile by this name
                SignonUser activeUser = settings.getActiveUser(getActivity());
                if (activeUser.doesProfileExist(profileName)) {
                    message = getString(R.string.profile_name_validation_profile_exists);
                    return message;
                }
            }

        }
        else{
            message=getString(R.string.profile_name_validation_page_not_added);
        }

        return message;
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
