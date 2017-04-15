package com.ewise.moneyapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ewise.moneyapp.MoneyAppApp;
import com.ewise.moneyapp.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by SilmiNawaz on 9/4/17.
 */
public class Settings {

    private static final String TAG = "Settings";

    static final String SETTINGS_USER_NAME = "com.ewise.moneyapp.username.{profilename}";
    static final String SETTINGS_USER_EMAIL = "com.ewise.moneyapp.email.{profilename}";

    private String FILE_SIGNON_USERS = "signon_users.dat";

    private EncryptedPin encryptedPin;
    private HashMap<String, EncryptedPin> encryptedPinMap;
    private SignOnUsers signOnUsers=null;
    //private Context context;

    private static Settings settings = null;


    private Settings(Context context){
        //this.context = context;
        encryptedPin = new EncryptedPin(context, null, null);//UNUSED
        encryptedPinMap = new HashMap<>();
        loadSignOnUsers(context);
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
                String signOnUserString = PdvApiResults.toJsonString(signOnUsers);
                outputStream.write(signOnUserString.getBytes());
                outputStream.close();
                return  true;
            }
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

        MoneyAppApp app = (MoneyAppApp) activity.getApplication();
        String signonUserId=app.signonUserId;
        SignOnSystem signOnSystem=app.signOnSystem;

        for (SignonUser user: signOnUsers.users){
            if (user.system.equals(signOnSystem) && user.id.equals(signonUserId)){
                return user;
            }
        }

        return null;

    }

    //file based implementation supporting multiple users

    /**
     * Get a SignonUser record of a user who has signed on to the app before
     * <p>
     * Get a SignonUser record of a user who has signed on to the app before
     * by passing the supported signon system (e.g. GOOGLE, FACEBOOK, EWISE etc.)
     * and the corresponding unique ID of the user in that system
     * If a new user is logging on then call #addUpdateUser() to ake sure its created
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
    public synchronized boolean addUpdateUser(Activity activity, SignonUser signonUser) {

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
                //user exists and has logged in before.. lets update their details
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

    //callto addd profile , after adding the user
    public synchronized boolean removeProfileFromActiveUser(Activity activity, String profileName){

        try {
            boolean removed=false;
            MoneyAppApp app = (MoneyAppApp) activity.getApplication();
            if (signOnUsers != null) {
                if (signOnUsers.users != null) {
                    Iterator<SignonUser> userIterator = signOnUsers.users.iterator();
                    while (userIterator.hasNext()) {
                        SignonUser user = userIterator.next();
                        if (user.profiles != null) {
                            Iterator<SignonProfile> profileIterator = user.profiles.iterator();
                            while (profileIterator.hasNext()) {
                                SignonProfile profile = profileIterator.next();
                                if (profile.name.equals(profileName)) {
                                    synchronized (this) {
                                        profileIterator.remove();
                                        removed=saveSignOnUsers(activity);
                                        return removed;
                                    }
                                }
                            }
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
    @Deprecated    public String getUserName(Activity activity) {
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
