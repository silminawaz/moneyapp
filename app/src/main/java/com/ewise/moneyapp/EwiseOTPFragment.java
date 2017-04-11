package com.ewise.moneyapp;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;

import com.ewise.android.pdv.api.PdvApi;
import com.ewise.moneyapp.Utils.PdvApiRequestQueue;

/**
 * Created by aaron on 11/23/16.
 */
public class EwiseOTPFragment extends DialogFragment {

    private static final String INSTID_PARAM = "instId";
    private static final String MESSAGE = "message";
    private static final String BASE64_PARAM = "base64Image";
    private String instId;
    private String message;
    private String base64Image;

    public static EwiseOTPFragment newInstance(String instId, String message) {
        EwiseOTPFragment frag = new EwiseOTPFragment();
        Bundle args = new Bundle();
        args.putString(INSTID_PARAM, instId);
        args.putString(MESSAGE, message);
        frag.setArguments(args);
        return frag;
    }


    public static EwiseOTPFragment newInstance(String instId, String message, String base64Image) {
        Log.d ("EwiseOTPFragment", "newInstance() - start");

        EwiseOTPFragment frag = new EwiseOTPFragment();
        Bundle args = new Bundle();
        args.putString(INSTID_PARAM, instId);
        args.putString(MESSAGE, message);
        if (base64Image!=null) {
            args.putString(BASE64_PARAM, base64Image);
        }
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d ("EwiseOTPFragment", "onCreateDialog() - start");

        final PdvApi pdvApi = ((MoneyAppApp) getActivity().getApplication()).getPdvApi();
        instId = getArguments().getString(INSTID_PARAM);
        message = getArguments().getString(MESSAGE);
        base64Image = getArguments().getString(BASE64_PARAM);

        final EditText input = new EditText(getActivity());
        final TableLayout layout = new TableLayout(getActivity());
        final ImageView imageView = new ImageView(getActivity());
        layout.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        layout.setGravity(Gravity.CENTER);

        String sTitle = getString(R.string.dialog_otp_text);

        byte[] decodedString = null;
        if (base64Image != null) {
            decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        }

        if(decodedString != null ) {
            sTitle = getString(R.string.dialog_captcha_text);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Bitmap newbitMap = Bitmap.createScaledBitmap(bitmap, 450, 450, true);
            imageView.setImageBitmap(newbitMap);
            layout.addView(imageView);
        }

        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        layout.addView(input);
        return new AlertDialog.Builder(getActivity())
                .setTitle(sTitle)
                .setMessage(message)
                .setView(layout)
                .setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                int i = layout.getChildCount();
                                MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
                                app.setVerifyOTP(instId, input.getText().toString());

                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
    }


}
