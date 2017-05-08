package com.ewise.moneyapp;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Currency;
import java.util.Locale;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.provider.Group;
//import com.ewise.android.pdv.api.model.provider.GroupedInstitution;
import com.ewise.moneyapp.data.GroupedInstitution;
import com.ewise.android.pdv.api.model.provider.Institution;
import com.ewise.android.pdv.api.model.provider.Providers;
import com.ewise.moneyapp.Utils.PdvApiResults;

import com.ewise.moneyapp.views.RecyclerViewBindInterface;
import com.ewise.moneyapp.views.RecyclerViewItemLayoutView;
import com.google.gson.Gson;


/**
 * Copyright (c) 2017 eWise Singapore. Created  on 23/2/17.
 */


@EViewGroup(R.layout.institutionobject_item)
public class AddInstitutionItemView extends RecyclerViewItemLayoutView<GroupedInstitution> implements RecyclerViewBindInterface<GroupedInstitution> {

// TODO : Change and replace the views bound to this layout - below are just examples - you must change this to be the correct view type and id

    static final int PICK_CONTACT_REQUEST = 1;

    @ViewById(R.id.accountinstitutionicon)
    ImageView accountinstitutionicon;

    @ViewById(R.id.accountInstitutionName)
    TextView accountInstitutionName;

    @ViewById(R.id.institutionLayout)
    RelativeLayout institutionlayout;

    public AddInstitutionItemView(Context context) {
        super(context);
    }

    public void bind(final GroupedInstitution dataObject) {

        this.institutionlayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startAddInstitutionPromptsActivity (dataObject);
            }
        }
        );

        if (dataObject.getInstitutionIcon()!=null)
            accountinstitutionicon.setImageBitmap(dataObject.getInstitutionIcon());
        else
            accountinstitutionicon.setImageResource(R.drawable.rbanks);

        accountInstitutionName.setText(dataObject.getInstDesc());
    }

    public int getInstitutionIconResourceId (String resourceName) {
        resourceName = resourceName.toLowerCase();
        int resID = getResources().getIdentifier(resourceName , "drawable", getContext().getPackageName());
        if (resID == 0){
            resID = R.drawable.rbanks; //default institution icon
        }

        return resID;
    }

    public void startAddInstitutionPromptsActivity(GroupedInstitution groupedInstitution){

        MoneyAppApp app = (MoneyAppApp) getContext().getApplicationContext();
        app.setAddInstitutionSelected(groupedInstitution);
        Intent intent= new Intent(getContext(), AddInstitutionPromptsActivity.class);
        Activity activity = (Activity) this.getContext();
        activity.startActivityForResult(intent, MoneyAppApp.ADD_PROVIDER_PROMPTS_REQUEST );

    }

}
