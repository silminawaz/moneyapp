package com.ewise.moneyapp;

import android.content.Context;
import android.view.ViewGroup;

import com.ewise.android.pdv.api.model.provider.Institution;
import com.ewise.moneyapp.views.RecyclerViewAdapterBase;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by SilmiNawaz on 23/2/17.
 */

@EBean
public class AddInstitutionItemViewAdapter extends RecyclerViewAdapterBase<Institution, AddInstitutionItemView> {


    @RootContext
    Context context;

    public AddInstitutionItemViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected AddInstitutionItemView onCreateItemView(ViewGroup parent, int viewType) {
        return AddInstitutionItemView_.build(context);
    }

}
