package com.ewise.moneyapp.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.ewise.moneyapp.R;

import java.io.ByteArrayOutputStream;
/**
 * Created by SilmiNawaz on 20/4/17.
 */


public class Base64ImageConverter
{
    public static final String TAG = "Base64ImageConverter";


    public static boolean setImageFromBase64 (ImageView view, String base64Image){
        if (base64Image != null) {
            if(!base64Image.trim().isEmpty()) {
                Bitmap bm = Base64ImageConverter.fromBase64ToBitmap(base64Image);
                if (bm != null) {
                    view.setImageBitmap(bm);
                    return true;
                }
            }
        }

        return false;
    }

    public static @Nullable Bitmap fromDrawableResIDToBitmap (@DrawableRes int resId, Context context){

        if (resId<=0){
            return null;
        }

        Drawable drawable = ContextCompat.getDrawable(context, resId);

        if (!(drawable instanceof VectorDrawable)){
            Log.e(TAG, "***Unsupported drawable used. use Vector graphics for all icons and images***");
        }

        if (drawable==null) return null;

        return fromDrawableToBitmap(drawable, context);

    }


    public static Bitmap fromDrawableToBitmap (Drawable drawable, Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        //**WARNING** Always use 30dp or low res images when loading lists of images or you may run out of memory***
        try {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        catch (OutOfMemoryError outOfMemoryError){
            outOfMemoryError.printStackTrace();
            return null;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public static Bitmap fromBase64ToBitmap(String base64Str) throws IllegalArgumentException {
        try {
            byte[] decodedBytes = Base64.decode(
                    base64Str.substring(base64Str.indexOf(",") + 1),
                    Base64.DEFAULT
            );

            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String fromBitmapToBase64(Bitmap bitmap) {
        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static class UnsupportedDrawableException extends IllegalArgumentException{

        UnsupportedDrawableException(String message){
            super(message);
        }
    }

}