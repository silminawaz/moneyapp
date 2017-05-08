package com.ewise.moneyapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ewise.moneyapp.MoneyAppApp;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.data.ProviderLastUpdated;
import com.ewise.moneyapp.data.ProviderLastUpdatedList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 9/4/17.
 */
public class Settings {

    private static final String TAG = "Settings";

    static final String SETTINGS_USER_NAME = "com.ewise.moneyapp.username.{profilename}";
    static final String SETTINGS_USER_EMAIL = "com.ewise.moneyapp.email.{profilename}";
    static final String SHARED_PREFERENCES_PROVIDER_LASTUPDATED = "providerlastupdated";
    static final String SETTINGS_PROVIDER_LAST_UPDATE_PREFIX = "provider.lastupdated";

    private String FILE_SIGNON_USERS = "signon_users.dat";

    private EncryptedPin encryptedPin;
    private HashMap<String, EncryptedPin> encryptedPinMap;
    private ProviderLastUpdatedList providerLastUpdatedList;
    private SignOnUsers signOnUsers=null;
    private SignonUser activeUser=null;
    private SignonProfile activeProfile=null;
    //private Context context;

    private static Settings settings = null;


    private Settings(Context context){
        //this.context = context;
        encryptedPin = new EncryptedPin(context, null, null);//UNUSED
        encryptedPinMap = new HashMap<>();
        providerLastUpdatedList = null;
        loadSignOnUsers(context);
        loadProviderLastUpdatedList(context);
    }

    public static Settings getInstance(Context context){
        if (settings==null){
            settings = new Settings(context);
        }
        return settings;
    }

    public SignOnUsers getSignOnUsers() {
        return signOnUsers;
    }

    public void loadProviderLastUpdatedList(Context context){
        SharedPreferences preferences=context.getSharedPreferences(SHARED_PREFERENCES_PROVIDER_LASTUPDATED, Context.MODE_PRIVATE);
        //TODO: STOPPED HERE **SN** TODO Last updated for providers
    }

    private void loadSignOnUsers(Context context){

        try {
            FileInputStream fis = context.openFileInput(FILE_SIGNON_USERS);
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder jsonDataStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                jsonDataStrBuilder.append(inputStr);
            }
            streamReader.close();
            fis.close();

            String signOnUsersJsonString = jsonDataStrBuilder.toString();
            signOnUsers = PdvApiResults.objectFromString(jsonDataStrBuilder.toString(), SignOnUsers.class);
        }
        catch (FileNotFoundException fnfe){
            //this is a legit exception that is expected to happen the first time the user login, or i the user data is cleared
            synchronized (this) {
                saveSignOnUsers(context);
                signOnUsers = null;
            }
        }
        catch (IOException ioe){
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(ioe.getClass().getName(), ioe.getMessage(), sMethod, FileInputStream.class.getName(),ioe, context);
        }
        catch (Exception e){
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, FileInputStream.class.getName(), e, context);
        }
    }


    private synchronized boolean saveSignOnUsers(Context context){
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(FILE_SIGNON_USERS, Context.MODE_PRIVATE);//overwrite the file
            if (signOnUsers!=null){
                Log.d(TAG, "saveSignOnUsers() - **SAVING FILE_SIGNON_USERS**");
                String signOnUserString = PdvApiResults.toJsonString(signOnUsers);
                outputStream.write(signOnUserString.getBytes());
                outputStream.close();
                return  true;
            }
            Log.d(TAG, "saveSignOnUsers() - **NOT SAVING signOnUsers==null**");
            return false;
        }
        catch (IOException ioe){
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(ioe.getClass().getName(), ioe.getMessage(), sMethod, FileInputStream.class.getName(),ioe,context);
            return false;
        }
        catch (Exception e) {
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, FileInputStream.class.getName(), e, context);
            return false;
        }
    }


    public EncryptedPin getActiveUserEncryptedPin(Activity activity){

        MoneyAppApp app = (MoneyAppApp) activity.getApplication();
        SignOnSystem signOnSystem=app.signOnSystem;
        String signonUserID=app.signonUserId;
        String signonUserPinKey = EncryptedPin.PIN_KEY_ALIAS_PREFIX + "." + signOnSystem.toString() + "." + signonUserID;
        if (!encryptedPinMap.containsKey(signonUserPinKey)){
            encryptedPinMap.put(signonUserPinKey, new EncryptedPin(activity, signOnSystem, signonUserID));
        }
        return encryptedPinMap.get(signonUserPinKey);
    }

    public EncryptedPin getNewUserEncryptedPin(Activity activity, SignOnSystem signOnSystem, String signonUserID){

        String signonUserPinKey = EncryptedPin.PIN_KEY_ALIAS_PREFIX + "." + signOnSystem.toString() + "." + signonUserID;
        if (!encryptedPinMap.containsKey(signonUserPinKey)){
            encryptedPinMap.put(signonUserPinKey, new EncryptedPin(activity, signOnSystem, signonUserID));
        }
        return encryptedPinMap.get(signonUserPinKey);
    }


    //file based implementation supporting multiple users
    public SignonUser getActiveUser(Activity activity) {

        if (signOnUsers==null) return null;

        if (activeUser==null) {

            MoneyAppApp app = (MoneyAppApp) activity.getApplication();
            String signonUserId = app.signonUserId;
            SignOnSystem signOnSystem = app.signOnSystem;

            for (SignonUser user : signOnUsers.users) {
                if (user.system.equals(signOnSystem) && user.id.equals(signonUserId)) {
                    activeUser=user;
                    break;
                }
            }
        }

        return activeUser;

    }

    public void setActiveUser(String signOnSystemId, String signonUserId){
        for (SignonUser user : signOnUsers.users) {
            if (user.system.toString().equals(signOnSystemId) && user.id.equals(signonUserId)) {
                activeUser=user;
                break;
            }
        }
    }



    public boolean setActiveProfile(String profileName, Activity activity){
        SignonUser user=getActiveUser(activity);
        if (user!=null) {
            for (SignonProfile profile : user.profiles) {
                if (profile.name.trim().toLowerCase().equals(profileName.trim().toLowerCase())) {
                    activeProfile = profile;
                    break;
                }
            }
            if (activeProfile != null) {
                if (activeProfile.name.trim().toLowerCase().equals(profileName.trim().toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }


    public boolean setDefaultActiveProfile(Activity activity){
        SignonUser user=getActiveUser(activity);
        if (user!=null) {
            activeProfile = user.profiles.get(0);
            return true;
        }

        return false;
    }


    public @Nullable SignonProfile getActiveProfile(Activity activity){
        if (activeProfile==null){
            if (!setDefaultActiveProfile(activity)){
                return null;
            }
        }

        return activeProfile;

    }

    /*
    * This method is used to get the active users identifier. the identifier is used to call the Pdv.setUser() API and consists of
    * Signon System name, Signon Userid and the selected Signon Profile. Must call "Settings.setActiveUser(), Settings.setActiveProfile()" before calling getActiveUserId
    * otherwise you will get a "null" pointer in return.
     */
    public @Nullable String getActiveUserId (Activity activity){
        String activeUserId=null;
        if (activeUser!=null){
            if (activeProfile!=null){
                activeUserId = activeUser.system.toString() + "." + activeUser.id + "." + activeProfile.name;
            }
            else
            {
                Log.e(TAG, "getActiveUserId(): Must call Settings.setActiveProfile() before you call getActiveUserId()");
            }
        }
        else
        {
            Log.e(TAG, "getActiveUserId(): Must call Settings.setActiveUser() followed by Settings.setActiveProfile() before you call getActiveUserId()");
        }
        return activeUserId;
    }

    //file based implementation supporting multiple users

    /**
     * Get a SignonUser record of a user who has signed on to the app before
     * <p>
     * Get a SignonUser record of a user who has signed on to the app before
     * by passing the supported signon system (e.g. GOOGLE, FACEBOOK, EWISE etc.)
     * and the corresponding unique ID of the user in that system
     * If a new user is logging on then call #loginUser() to ake sure its created
     * <p>
     *
     * @param  activity calling Activity
     * @param  signOnSystem SignOnSystem name (enum). e.g. GOOGLE, FACEBOOK, EWISE, etc.
     * @param  signonUserId Unique ID of the user in the SignOnSystem
     *
     * @return SignonUser object corresponding to the existing user
     */
    private SignonUser getUser(Activity activity, SignOnSystem signOnSystem, String signonUserId) {

        if (signOnUsers==null) return null;

        MoneyAppApp app = (MoneyAppApp) activity.getApplication();

        for (SignonUser user: signOnUsers.users){
            if (user.system.equals(signOnSystem) && user.id.equals(signonUserId)){
                return user;
            }
        }

        return null;

    }

    //file based implementation supporting multiple users
    //call this the first time a user login and there is no other user
    /**
     * Call this method to add a new user or update an existing user
     * <p>
     * Should be called after each time the user Signs In successfully to the system to ensure that
     * either the user is registered (if new) or updated with latest profile data (if existing)
     * <p>
     *
     * @param  activity calling Activity
     * @param  signonUser The SignonUser object that consists of the latest profile details of the signed on user
     *
     * @return boolean true if user was added or updated
     */
    public synchronized boolean loginUser(Activity activity, SignonUser signonUser) {

        try {

            SignonUser user = getUser(activity, signonUser.system, signonUser.id);
            if (user == null)
            {
                synchronized (this) {
                    //either there has been previous logins but this is a new user...
                    if (signOnUsers==null)
                    {
                        signOnUsers= new SignOnUsers();
                    }

                    signOnUsers.users.add(signonUser);

                }
            }
            else
            {
                //user exists and has logged in before.. lets update their details first
                //update the user
                user.base64Image = signonUser.base64Image;
                user.email = signonUser.email;
                user.name = signonUser.name;
                user.imageURLPath = signonUser.imageURLPath;
                user.firstName = signonUser.firstName;
                user.middleName = signonUser.middleName;
                user.lastName = signonUser.lastName;
                //todo:update profiles later...
                if (user.profiles.size()==0){
                    //no profiles - set the default profile in case it's missing
                    //if there are already profiles, then we leave it.
                    user.profiles.add(signonUser.profiles.get(0));
                }
                //user.profiles profiles are not updated this way
            }

            //save the users file
            boolean saved;
            synchronized (this) {
                saved = saveSignOnUsers(activity);
            }

            //now that the login user is saved we can set it as active and set the default active profile
            setActiveUser(signonUser.system.toString(), signonUser.id);
            setDefaultActiveProfile(activity);

            return saved;
        }
        catch (Exception e){
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, FileInputStream.class.getName(),e, activity);
            return false;
        }

    }


    public boolean doesProfileExist (Activity activity, String profileName) {

        try {
            SignonUser user = getActiveUser(activity);
            for (SignonProfile profile : user.profiles) {
                if (profile.name.toLowerCase().equals(profileName.toLowerCase())) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, FileInputStream.class.getName(), e, activity);
            return false;
        }
    }

    /**
     * Call this method to add a new application profile to the currently active user
     * <p>
     * Should be called whenever the user requests for a new application profile to be created
     * An application profile allows the user to maintain mulitple partitions within the application
     * allowing the user to create for example "Personal" , "Combined", "Family", "Child", "Business" profiles to view the
     * data separately but within the same login without having to logout of the app and login again
     * <p>
     *
     * @param  activity calling Activity
     * @param  profile The new profile to add to the active users profiles
     *
     * @return boolean true if user was added or updated
     */
    public synchronized boolean addNewProfileToActiveUser(Activity activity, SignonProfile profile){
        try{
            boolean saved=false;
            if (!doesProfileExist(activity, profile.name)){
                getActiveUser(activity).profiles.add(profile);
                synchronized (this){
                    saved=saveSignOnUsers(activity);
                }
            }
            return saved;
        }
        catch (Exception e){
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, FileInputStream.class.getName(),e, activity);
            return false;
        }

    }



    /**
     * Call this method to save an existing application profile to the currently active user
     * <p>
     * Should be called whenever the user requests to save an existing application profile
     * An application profile allows the user to maintain mulitple partitions within the application
     * allowing the user to create for example "Personal" , "Combined", "Family", "Child", "Business" profiles to view the
     * data separately but within the same login without having to logout of the app and login again
     * <p>
     *
     * @param  activity calling Activity
     * @param  profile The new profile to add to the active users profiles
     *
     * @return boolean true if user was added or updated
     */
    public synchronized boolean saveExistingProfileToActiveUser(Activity activity, SignonProfile profile){
        try{
            boolean saved=false;
            if (getActiveUser(activity).saveProfileData(profile)) {
                synchronized (this) {
                    saved = saveSignOnUsers(activity);
                }
            }
            return saved;
        }
        catch (Exception e){
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, FileInputStream.class.getName(),e, activity);
            return false;
        }

    }

    //callto addd profile , after adding the user
    public synchronized boolean removeProfileFromActiveUser(Activity activity, String profileName){

        try {
            boolean removed=false;
            MoneyAppApp app = (MoneyAppApp) activity.getApplication();
            if (signOnUsers != null) {
                if (signOnUsers.users != null) {
                    //TODO: this code will remove the first users
                    SignonUser user = getActiveUser(activity);
                    if (user.profiles != null) {
                        if (user.profiles.size()>1) {
                            Iterator<SignonProfile> profileIterator = user.profiles.iterator();
                            while (profileIterator.hasNext()) {
                                SignonProfile profile = profileIterator.next();
                                if (profile.name.equals(profileName)) {
                                    synchronized (this) {
                                        profileIterator.remove();
                                        removed = saveSignOnUsers(activity);
                                        return removed;
                                    }
                                }
                            }
                        }
                        else
                        {
                            Log.d(TAG, "Cannot delete profile because only one profile exists");
                        }
                    }
                }
            }

            return false;
        }
        catch (Exception e){
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, FileInputStream.class.getName(),e, activity);
            return false;
        }

    }

    @Deprecated
    public String getActiveUserEmail(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.contains(SETTINGS_USER_EMAIL)){
            return sharedPref.getString(SETTINGS_USER_EMAIL, "");
        }

        return "";

    }

    @Deprecated
    public boolean setUserEmail(String userName, String profileName, Activity activity){
        if (userName!=null) {
            if (userName.length()>0) {
                SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(SETTINGS_USER_EMAIL, userName);
                editor.apply();
                return true;
            }
        }
        return false;
    }

    /*
    * @deprecated   This method is deprecated because it only supports single user per app, now multiple users allowed </br>
    *               use {@link #getSignonUserEncryptedPin()} instead
    *
    * <blockquote>
    * <pre>
    * Settings.getInstance().getSignonUserEncryptedPin(activity)
    * </pre> </blockquote>
    *
 */
    @Deprecated
    public EncryptedPin getEncryptedPin() {
        return encryptedPin;
    }

    /*
* @deprecated   This method is deprecated because it only supports single user per app, now multiple users allowed </br>
*               use {@link #getSignonUser()} instead
*
* <blockquote>
* <pre>
* Settings.getInstance().getSignonUser(activity)
* </pre> </blockquote>
*
 */
    @Deprecated
    public String getUserName(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.contains(SETTINGS_USER_NAME)){
            return sharedPref.getString(SETTINGS_USER_NAME, "");
        }

        return "";

    }


    @Deprecated
    public boolean setUserName(String userName, String profileName, Activity activity){
        if (userName!=null) {
            if (userName.length()>0) {
                SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(SETTINGS_USER_NAME, userName);
                editor.apply();
                return true;
            }
        }
        return false;
    }


    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString, Exception e, Context context) {
        String sFormat = context.getString(R.string.exception_format_string);
        Log.e(TAG, String.format(sFormat, eType, eMethod, eMessage, eObjectString));
        e.printStackTrace();
    }
}
