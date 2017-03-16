package com.ewise.moneyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ewise.android.pdv.api.PdvApi;
import com.ewise.android.pdv.api.model.PromptEntry;
import com.ewise.android.pdv.api.model.provider.GroupedInstitution;
import com.ewise.android.pdv.api.model.response.GetPromptsData;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;
import com.ewise.moneyapp.Utils.PdvConnectivityStatus;

import java.util.ArrayList;
import java.util.List;

public class AddInstitutionPromptsActivity extends AppCompatActivity implements PdvConnectivityCallback {

    static int DYNAMIC_PROMPT_FILEDS_START_INDEX = 3;

    LinearLayout addInstitutionPromptsLayout;
    TextView    textInstitutionName;
    ImageView   imageInstitutionLogo;
    TextView    addInstitutionText;
    Button      btnAddProvider;

    GroupedInstitution groupedInstitution;
    GetPromptsData promptsData;
    Context context;
    List<PromptEntry>  credVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_institution_prompts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        credVals = new ArrayList<>();

        textInstitutionName = (TextView) findViewById(R.id.textInstitutionName);
        imageInstitutionLogo = (ImageView) findViewById(R.id.imageInstitutionLogo);
        addInstitutionPromptsLayout = (LinearLayout) findViewById(R.id.addInstitutionPromptsLayout);
        addInstitutionText = (TextView) findViewById(R.id.addInstitutionText);
        btnAddProvider = (Button)findViewById(R.id.btnAddProvider);

        btnAddProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addInstitution();
            }
        });


        btnAddProvider.setVisibility(View.GONE);

        Intent intent = getIntent();
        String objJsonString = intent.getStringExtra("com.wise.moneyapp.AddInstitutionPromptsActivity.GroupedInstitution");
        groupedInstitution = PdvApiResults.objectFromString(objJsonString, GroupedInstitution.class);

        String instName = groupedInstitution.getInstDesc();
        textInstitutionName.setText(instName);
        int resID = getResources().getIdentifier(groupedInstitution.getGroupId(), "drawable", getBaseContext().getPackageName());
        Log.d("AddInstResID", Integer.toString(resID));
        //imageInstitutionLogo.setImageResource(getResources().getIdentifier(groupedInstitution.getGroupId(), "drawable", getBaseContext().getPackageName()));

        addInstitutionText.setText(String.format(getString(R.string.add_provider_text),instName,instName));

        context = this;

        MoneyAppApp myApp = (MoneyAppApp) getApplication();
        PdvApi pdvApi = myApp.getPdvApi();
        myApp.pdvWebView = (WebView) findViewById(R.id.ewise_webview);
        try {
            pdvApi.apiInit(getApplicationContext(), myApp.pdvWebView);

            //what do I want to do now?  - call getPrompts
            if (myApp.pdvConnectivityStatus != PdvConnectivityStatus.SUCCESS) {
                myApp.checkConnectivity(this, MoneyAppApp.DEFAULT_SWAN_HOST, this);
            } else
            {
                onPdvConnected();
            }

            if (myApp.loggedOnToPdv){
                //Toast.makeText(getApplicationContext(), R.string.pdvapi_get_prompts_message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            String sObjString = myApp.pdvApi.toString();
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, sObjString);
        }


        myApp.pdvGetPrompts(groupedInstitution.getInstCode(), this);


    }

    @Override
    protected void onStart(){
        super.onStart();


    }

    private void addInstitution(){

        credVals.clear(); //remove any previous values
        String sEmpty = "";
        boolean isValid = true;
        boolean isPasswordValid = true;

        int nViews = addInstitutionPromptsLayout.getChildCount();
        for (int i=0; i<nViews; i++){
            if (addInstitutionPromptsLayout.getChildAt(i).getClass().equals(EditText.class)){
                EditText editText = (EditText) addInstitutionPromptsLayout.getChildAt(i);
                PromptEntry promptEntry = (PromptEntry) editText.getTag();
                if (promptEntry != null){
                    if (promptEntry.getIndex()==1 || promptEntry.getType()==1){
                        String sValue = editText.getText().toString();
                        sValue = sValue.trim();
                        if (sValue.equals(sEmpty)){
                            isValid=false;
                        }
                    }
                    promptEntry.setValue(editText.getText().toString());
                    credVals.add(promptEntry);
                }
            }
            else if (addInstitutionPromptsLayout.getChildAt(i).getClass().equals(CheckBox.class)){
                CheckBox checkBox = (CheckBox) addInstitutionPromptsLayout.getChildAt(i);
                PromptEntry promptEntry = (PromptEntry) checkBox.getTag();
                if (promptEntry != null){
                    promptEntry.setValue(checkBox.isChecked()?"true":"false");
                    credVals.add(promptEntry);
                }
            }
        }

        Log.d("CREDVALS", credVals.toString());

        promptsData.setPrompts(credVals);

        showAlertDialog(isValid);

    }

    @Override
    public void finish()
    {
        Log.d("PROMPTS", "Finish() called");

        super.finish();

    }

    private void showAlertDialog(boolean isValid){

        String retVal = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        if (isValid) {
            // Add the buttons
            builder.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Intent resultIntent = new Intent();
                    String promptsDataString = PdvApiResults.toJsonString(promptsData);
                    resultIntent.putExtra("promptsData", promptsDataString);
                    resultIntent.putExtra("instCode", groupedInstitution.getInstCode());
                    resultIntent.putExtra("instName", groupedInstitution.getInstDesc());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            });
            builder.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    // do nothing - just close this dialog and go back to previous activity
                    dialog.cancel();
                }
            });


            builder.setMessage(R.string.add_provider_alert_message)
                    .setTitle(R.string.add_provider_alert_title)
                    .setIcon(getResources().getIdentifier(groupedInstitution.getGroupId(), "drawable", getBaseContext().getPackageName()));
        }
        else{
            // Add the buttons
            builder.setNegativeButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    // do nothing - just close this dialog and go back to previous activity
                    dialog.cancel();
                }
            });


            builder.setMessage(R.string.add_provider_invalid_message)
                    .setTitle(R.string.add_provider_alert_title)
                    .setIcon(getResources().getIdentifier(groupedInstitution.getGroupId(), "drawable", getBaseContext().getPackageName()));
        }





        AlertDialog dialog = builder.create();

        dialog.show();

    }


    //Begin: PdvConnectivityCallback Interface implementations
    @Override
    public void onPdvConnected(){

    }

    @Override
    public void onPdvDisconnected(){

    }

    @Override
    public void onGetPromptsFail(PdvApiResults results){



    }

    @Override
    public void onGetPromptsSuccess(PdvApiResults results){

        promptsData = results.prompts.getData();

        Runnable addPromptsToUI = new Runnable() {
            @Override
            public void run() {

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((ViewGroup.LayoutParams.MATCH_PARENT), (ViewGroup.LayoutParams.WRAP_CONTENT));

                for (PromptEntry promptEntry : promptsData.getPrompts()){
                    addInstitutionPromptsLayout = PdvApiResults.getViewsForPromptsEntry(promptEntry, addInstitutionPromptsLayout, addInstitutionPromptsLayout.getChildCount()-2, layoutParams, context);
                }

                LinearLayout getPromptsMsgView = (LinearLayout) findViewById(R.id.linearlayoutGetPromptsMsg);
                getPromptsMsgView.setVisibility(View.GONE);
                btnAddProvider.setVisibility(View.VISIBLE);
            }
        };

        runOnUiThread(addPromptsToUI);
    }

    @Override
    public void onGetInstitutionsFail(PdvApiResults results){

        btnAddProvider.setVisibility(View.GONE);
    }

    @Override
    public void onGetInstitutionsSuccess(PdvApiResults results) {

    }

    @Override
    public void onGetUserProfileSuccess(PdvApiResults results){

    }

    @Override
    public void onGetUserProfileFail(PdvApiResults results){

    }




    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }

}
