package com.ewise.moneyapp;

import android.content.Context;

import android.media.Image;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Currency;
import java.util.Locale;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.provider.Group;
import com.ewise.android.pdv.api.model.provider.Institution;
import com.ewise.android.pdv.api.model.provider.Providers;
import com.ewise.moneyapp.Utils.PdvApiResults;

import com.ewise.moneyapp.views.RecyclerViewBindInterface;
import com.ewise.moneyapp.views.RecyclerViewItemLayoutView;

/**
 * Created by SilmiNawaz on 23/2/17.
 */


@EViewGroup(R.layout.institutionobject_item)
public class AddInstitutionItemView extends RecyclerViewItemLayoutView<Institution> implements RecyclerViewBindInterface<Institution> {

// TODO : Change and replace the views bound to this layout - below are just examples - you must change this to be the correct view type and id

    @ViewById(R.id.accountinstitutionicon)
    ImageView accountinstitutionicon;

    @ViewById(R.id.accountInstitutionName)
    TextView accountInstitutionName;


    public AddInstitutionItemView(Context context) {
        super(context);
    }

    public void bind(Institution dataObject) {

        //todo: add institution icon for each institution using
        //accountinstitutionicon.setImageResource(getInstitutionIconResourceId (dataObject.getInstCode()));
        accountInstitutionName.setText(dataObject.getInstDesc());

    }

    public int getInstitutionIconResourceId (String instCode)
    {
        //todo: the image resources should be retrieved from a server and mapped at runtime instead of this approach of ysing the resource drawables

        String drawableName = instCode;
        int resID = getResources().getIdentifier(drawableName , "drawable", getContext().getPackageName());

        if (resID == 0){
            drawableName = getResources().getString(R.string.default_institution_icon_resource_name);
            resID = getResources().getIdentifier(drawableName , "drawable", getContext().getPackageName());
        }

        return resID;
    }

}
