package com.ewise.moneyapp;

import android.accounts.Account;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ewise.moneyapp.Utils.Settings;
import com.ewise.moneyapp.Utils.SignOnSystem;
import com.ewise.moneyapp.Utils.SignOnUsers;
import com.ewise.moneyapp.Utils.SignonUser;
import com.ewise.moneyapp.Utils.SignonProfile;
import com.ewise.moneyapp.views.PinEntryTextView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>,
        GoogleApiClient.OnConnectionFailedListener
{

    public static final String TAG = "LoginActivity";
    public static final String DEFAULT_SIGNON_PROFILE = "default";
    private static final int RC_SIGN_IN = 9001;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    @Deprecated
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mStatusTextView;
    private TextView mLoginName;
    private TextView mLoginEmail;
    private ImageView mLoginImage;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private SignInButton sign_in_button;
    private Button sign_out_button;
    private Button disconnect_button;

    private Button login_to_app;

    private PinEntryTextView txtPinEntry;
    private PinEntryTextView txtPinEntry2;

    private boolean mustReEnterPIN;
    Settings settings;
    private SignonUser activeUser=null;
    private SignonProfile defaultProfile=null;  //for now we will use a default profile


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mStatusTextView = (TextView) findViewById(R.id.loginStatusTextView) ;

        mLoginName = (TextView) findViewById(R.id.loginName);
        mLoginEmail = (TextView) findViewById(R.id.loginEmail);
        mLoginImage  = (ImageView) findViewById(R.id.loginImage);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Set the dimensions of the sign-in button.
        sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);
        sign_in_button.setSize(SignInButton.SIZE_WIDE);
        sign_in_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        sign_out_button = (Button) findViewById(R.id.sign_out_button);
        sign_out_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        disconnect_button = (Button) findViewById(R.id.disconnect_button);
        disconnect_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                revokeAccess();
            }
        });

        login_to_app = (Button) findViewById(R.id.login_to_app);
        login_to_app.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginToApp();
            }
        });



        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        txtPinEntry = (PinEntryTextView) findViewById(R.id.txtPinEntry);
        txtPinEntry2 = (PinEntryTextView) findViewById(R.id.txtPinEntry2);

        txtPinEntry.setOnEditorActionListener(new PinEntryTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    // Some logic here.
                    if (v.getText().length()<((PinEntryTextView)v).getMaxLength())
                    {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.pinentry_maxlength_error_message, Integer.toString(((PinEntryTextView)v).getMaxLength())), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false; // Focus will do whatever you put in the logic.
                }
                if ((i == EditorInfo.IME_ACTION_DONE)||(i==EditorInfo.IME_ACTION_GO)) {
                    // Some logic here.
                    if (v.getText().length()<((PinEntryTextView)v).getMaxLength())
                    {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.pinentry_maxlength_error_message, Integer.toString(((PinEntryTextView)v).getMaxLength())), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    else{
                        loginToApp();
                    }
                    return false; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });


        txtPinEntry2.setOnEditorActionListener(new PinEntryTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    // Some logic here.
                    if (v.getText().length()<((PinEntryTextView)v).getMaxLength())
                    {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.pinentry_maxlength_error_message, Integer.toString(((PinEntryTextView)v).getMaxLength())), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false; // Focus will do whatever you put in the logic.
                }
                if ((i == EditorInfo.IME_ACTION_DONE)||(i==EditorInfo.IME_ACTION_GO)) {
                    // Some logic here.
                    if (v.getText().length()<((PinEntryTextView)v).getMaxLength())
                    {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.pinentry_maxlength_error_message, Integer.toString(((PinEntryTextView)v).getMaxLength())), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    else{
                        loginToApp();
                    }
                    return false; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });


        settings = Settings.getInstance(this);
        activeUser = new SignonUser();
        defaultProfile = new SignonProfile();
        defaultProfile.name=DEFAULT_SIGNON_PROFILE;


        mustReEnterPIN = settings.getEncryptedPin().mustReEnterPIN(this);
        mustReEnterPIN = true;


        //todo: remove after testing
        //mustReEnterPIN=true;


        if (!mustReEnterPIN){
            //only display confirm PIN field if user must re enter the PIN
            findViewById(R.id.txtPinEntryLabel2).setVisibility(View.GONE);
            txtPinEntry2.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroy(){
        if (mProgressDialog!=null) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog(null);
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            setGoogleActiveUser(acct);
            mStatusTextView.setText(getString(R.string.login_status_text_signed_in_google, acct.getDisplayName()));
            mLoginName.setText(acct.getGivenName() + " " + acct.getFamilyName());
            mLoginEmail.setText(acct.getEmail());

            /*
            try{

            URL url = new URL(acct.getPhotoUrl().getPath());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            mLoginImage.setImageBitmap(bmp);
            }
            catch (Exception e){
                Log.e(TAG, "error getting google photo : exception = " + e.getMessage());

            }
            */

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    private void setGoogleActiveUser(GoogleSignInAccount acct){
        activeUser.system=SignOnSystem.SIGN_ON_SYSTEM_GOOGLE;
        activeUser.profiles.add(defaultProfile);
        activeUser.id=acct.getId();
        activeUser.name=acct.getDisplayName();
        activeUser.firstName=acct.getGivenName();
        activeUser.middleName="";
        activeUser.lastName=acct.getFamilyName();
        activeUser.email=acct.getEmail();
        activeUser.imageURLPath=acct.getPhotoUrl().getPath();
    }

    private void clearActiveUser(){
        activeUser.system=SignOnSystem.SIGN_ON_SYSTEM_UNKNOWN;
        activeUser.profiles.clear();
        activeUser.id="";
        activeUser.name="";
        activeUser.firstName="";
        activeUser.middleName="";
        activeUser.lastName="";
        activeUser.email="";
        activeUser.imageURLPath="";
        activeUser.base64Image="";
        activeUser.setEncryptedPIN(null);
    }

    // [START signIn]
    private void signIn() {
        Log.d(TAG, "signIn to google");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]


    private void showProgressDialog(@Nullable String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            if (message==null) {
                mProgressDialog.setMessage(getString(R.string.login_google_dialog_loading));
            }
            else{
                mProgressDialog.setMessage(message);
            }
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        setMustReEnterPIN(signedIn);
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.login_status_text_signed_out_google);
            mLoginName.setText(R.string.login_status_text_signed_out_google);
            clearActiveUser();
            mLoginEmail.setText("");

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }


    private void setMustReEnterPIN(boolean signedIn){
        if (signedIn)
        {
            mustReEnterPIN = settings.getNewUserEncryptedPin(this, activeUser.system, activeUser.id).mustReEnterPIN(this);
            if (mustReEnterPIN){
                //only display confirm PIN field if user must re enter the PIN
                findViewById(R.id.txtPinEntryLabel2).setVisibility(View.VISIBLE);
                txtPinEntry2.setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.txtPinEntryLabel2).setVisibility(View.GONE);
                txtPinEntry2.setVisibility(View.GONE);
            }
        }
        else
        {
            mustReEnterPIN = true;
            //only display confirm PIN field if user must re enter the PIN
            findViewById(R.id.txtPinEntryLabel2).setVisibility(View.VISIBLE);
            txtPinEntry2.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
        mStatusTextView.setText(getString(R.string.login_status_text_fail_google, connectionResult.getErrorMessage()));

    }

    public void loginToApp(){

        boolean canLoginToApp = false;
        String loginErrorMessage = null;

        if (mustReEnterPIN){
            //check the input PINS are correct
            if (isReEnterPINValid()){
                //PIN is valid.. lets try to save it
                //canLoginToApp = settings.getEncryptedPin().savePIN(txtPinEntry.getText().toString(), this);  //@deprecated
                canLoginToApp = settings.getNewUserEncryptedPin(this, activeUser.system, activeUser.id).savePIN(txtPinEntry.getText().toString(), this);
            }
        }
        else
        {
            if (isInputPINValid()) {
                //todo: validate PIN
                //canLoginToApp = settings.getEncryptedPin().validatePIN(txtPinEntry.getText().toString(), this);  //@deprecated
                canLoginToApp = settings.getActiveUserEncryptedPin(this).savePIN(txtPinEntry.getText().toString(), this);
                if (!canLoginToApp) {
                    loginErrorMessage = getString(R.string.pinentry_invalid_pin);
                }
            }
        }

        if (canLoginToApp){
            settings.addUpdateUser(this, activeUser); //add (if new user) or update the active user to the system
            ((MoneyAppApp)getApplication()).setAppLoggedIn(activeUser.system, activeUser.id);
            finish();
        }
        else
        {
            Toast.makeText(this, loginErrorMessage, Toast.LENGTH_LONG).show();
        }
    }

    public boolean isInputPINValid(){
        String errorMessage=null;
        //PIN 1 must be 4 digits
        if (!(txtPinEntry.getText().length()==txtPinEntry.getMaxLength())){
            errorMessage = getString(R.string.pinentry_maxlength_error_message, Integer.toString(txtPinEntry.getMaxLength()));
        }

        if (errorMessage!=null){
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public boolean isReEnterPINValid(){
        String errorMessage=null;
        //PIN 1 must be 4 digits
        if (!(txtPinEntry.getText().length()==txtPinEntry.getMaxLength())){
            errorMessage = getString(R.string.pinentry_maxlength_error_message, Integer.toString(txtPinEntry.getMaxLength()));
        }
        //PIN 2 must be 4 digits
        else if (!(txtPinEntry2.getText().length()==txtPinEntry.getMaxLength())) {
            errorMessage = getString(R.string.pinentry_maxlength_error_message, Integer.toString(txtPinEntry2.getMaxLength()));
        }
        //PIIN 1 must be equal to PIN2
        else if (!txtPinEntry.getText().toString().equals(txtPinEntry2.getText().toString())){
            errorMessage = getString(R.string.pinentry_pin_not_matching);
        }

        if (errorMessage!=null){
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    @Deprecated
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    @Deprecated
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Deprecated
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @Deprecated
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    @Deprecated
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    @Deprecated
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @Deprecated
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Deprecated
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Deprecated
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
        try {
            // Simulate network access.
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
    }

    @Deprecated
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Deprecated
    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @Deprecated
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                ((MoneyAppApp)getApplication()).setAppLoggedIn(SignOnSystem.SIGN_ON_SYSTEM_EWISE, null);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    //END: UNUSED CODE SAMPLE
}

