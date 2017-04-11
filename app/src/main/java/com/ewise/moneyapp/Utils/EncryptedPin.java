package com.ewise.moneyapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

/**
 * Created by SilmiNawaz on 9/4/17.
 */
public class EncryptedPin {


    public static final String TAG = "EncryptedPin";
    public static final String DEFAULT_PIN_KEY_ALIAS = "com.ewise.moneyapp.loginpinkey";
    public static final String DEFAULT_PIN_KEY_SUBJECT = "CN=com.ewise.moneyapp, O=Android Authority";
    //public static final int DEFAULT_PIN_KEY_SERIAL_NO = 1;
    public static final int DEFAULT_PIN_KEY_EXPIRY_MONTHS = 9999; //always valid.

    private Context context=null;
    private KeyStore keyStore=null;

    public EncryptedPin(Context context){
        this.context=context;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

        }
        catch(Exception e){
            Log.e(TAG, "Constructor: EncryptedPin() : Unable to access Android Keystore. Exception=" + e.getMessage());
            Toast.makeText(context, "Unable to access Android Keystore. Exception=" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public boolean mustReEnterPIN(Activity activity){

        if (pinKeyExists()){
            if (!isPINKeyExpired()){
                return false;
            }
        }

        return true;
    }

    public boolean doesPINExist(Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.contains(DEFAULT_PIN_KEY_ALIAS)){
            String PINValue = sharedPref.getString(DEFAULT_PIN_KEY_ALIAS, null);
            if (PINValue!=null){
                if (PINValue.length()>0){
                    return true;
                }
            }
        }

        return false;
    }


    public boolean isPINExpired(Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.contains(DEFAULT_PIN_KEY_ALIAS)){
            String PINValue = sharedPref.getString(DEFAULT_PIN_KEY_ALIAS, null);
            if (PINValue!=null){
                if (PINValue.length()>0){
                    return isPINKeyExpired();
                }
            }
        }

        return false;
    }


    public boolean savePIN (String plainTextPin, Activity activity){
        boolean isPinSaved = false;

        if (isPINExpired(activity)){
            removePinKey();
        }

        if (!pinKeyExists()){
            createNewKeys();
        }

        //encrypt plaintextpin
        String encryptedPin = encryptPIN(plainTextPin);

        if (encryptedPin!=null) {
            if (encryptedPin.length()>0) {
                //save plaintext pin in shared preferences
                SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(DEFAULT_PIN_KEY_ALIAS, encryptedPin);
                editor.apply();
                return true;
            }
        }

        return false;
    }

    public boolean validatePIN (String plainTextPin, Activity activity){

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        String savedPin = sharedPref.getString(DEFAULT_PIN_KEY_ALIAS, null);
        if (savedPin!=null){
            String decryptedPin = decryptPIN(savedPin);

            if (plainTextPin.equals(decryptedPin)){
                return true;
            }
        }

        return false;
    }


    private boolean pinKeyExists(){

        try {

            if (keyStore.containsAlias(DEFAULT_PIN_KEY_ALIAS)){
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e){
            Log.e(TAG, "pinKeyExists() : Error accessing keystore : " + e.getMessage());
            return false;
        }

    }


    public boolean isPINKeyExpired(){
        if (pinKeyExists()) {
            try {
                X509Certificate cert = (X509Certificate) keyStore.getCertificate(DEFAULT_PIN_KEY_ALIAS);
                Date expiryDate = cert.getNotAfter();
                Calendar now = Calendar.getInstance();
                Date today = now.getTime();
                if (expiryDate.compareTo(today) > 0) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "pinKeyExpired() : Error accessing keystore : " + e.getMessage());
                return true;
            }
        }

        return true;
    }

    public boolean removePinKey(){
        if (pinKeyExists()){
            try {
                keyStore.deleteEntry(DEFAULT_PIN_KEY_ALIAS);
                return true;
            }
            catch(Exception e){
                Log.e(TAG, "removePinKey() : Error deleting pin key : " + e.getMessage());

                return false;
            }
        }

        return false;
    }

    private void createNewKeys() {
        try {

            if (!keyStore.containsAlias(DEFAULT_PIN_KEY_ALIAS)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.MONTH, DEFAULT_PIN_KEY_EXPIRY_MONTHS);
                if (Build.VERSION.SDK_INT<23) {

                    KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                            .setAlias(DEFAULT_PIN_KEY_ALIAS)
                            .setSubject(new X500Principal(DEFAULT_PIN_KEY_SUBJECT))
                            .setSerialNumber(BigInteger.ONE)
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime())
                            .build();
                    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                    generator.initialize(spec);

                    KeyPair keyPair = generator.generateKeyPair();
                }
                else if (Build.VERSION.SDK_INT>=23){
                    KeyGenParameterSpec.Builder spec = new KeyGenParameterSpec.Builder
                            (DEFAULT_PIN_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT);
                    spec.setDigests(KeyProperties.DIGEST_SHA256);
                    spec.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1);
                    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                    generator.initialize(spec.build());
                    KeyPair keyPair = generator.generateKeyPair();
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }


    private String encryptPIN(String plainTextPIN) {
        String encryptedPIN = "";
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(DEFAULT_PIN_KEY_ALIAS, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            // Encrypt the text
            if(plainTextPIN.isEmpty()) {
                return encryptedPIN;
            }

            Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            input.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, input);
            cipherOutputStream.write(plainTextPIN.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte [] vals = outputStream.toByteArray();
            encryptedPIN = Base64.encodeToString(vals, Base64.DEFAULT);

            return encryptedPIN;

        } catch (Exception e) {
            Toast.makeText(context, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
            Log.e(TAG, Log.getStackTraceString(e));
            return encryptedPIN;
        }

    }


    private String decryptPIN(String encryptedPIN) {
        String decryptedPIN="";
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(DEFAULT_PIN_KEY_ALIAS, null);

            //Begin: Bug fix
            //RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
            //Bug:  java.lang.ClassCastException: android.security.keystore.AndroidKeyStoreRSAPrivateKey cannot be cast to java.security.interfaces.RSAPrivateKey
            PrivateKey privateKey = (PrivateKey) privateKeyEntry.getPrivateKey();
            //Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            //End: Bug  fix 9/4/17

            output.init(Cipher.DECRYPT_MODE, privateKey);

            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(encryptedPIN, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            decryptedPIN = new String(bytes, 0, bytes.length, "UTF-8");

            return decryptedPIN;

        } catch (Exception e) {
            Toast.makeText(context, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
            Log.e(TAG, Log.getStackTraceString(e));
            return decryptedPIN;
        }
    }


}
