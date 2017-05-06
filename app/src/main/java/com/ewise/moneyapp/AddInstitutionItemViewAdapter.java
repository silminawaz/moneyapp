package com.ewise.moneyapp;

import android.content.Context;
import android.view.ViewGroup;

//import com.ewise.android.pdv.api.model.provider.GroupedInstitution;
import com.ewise.moneyapp.data.GroupedInstitution;
import com.ewise.android.pdv.api.model.provider.Institution;
import com.ewise.moneyapp.views.RecyclerViewAdapterBase;
import com.ewise.moneyapp.views.RecyclerViewItemLayoutView;
import com.ewise.moneyapp.views.RecyclerViewWrapper;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

/**
 * Created by SilmiNawaz on 23/2/17.
 */

@EBean
public class AddInstitutionItemViewAdapter extends RecyclerViewAdapterBase<GroupedInstitution, AddInstitutionItemView> {

    @RootContext
    Context context;

    int groupIconResId;


    public void setGroupIconResId(int groupIconResId) {
        this.groupIconResId = groupIconResId;
    }

    public int getGroupIconResId() {
        return groupIconResId;
    }


    public AddInstitutionItemViewAdapter(Context context) {
        this.context = context;
        groupIconResId = R.drawable.uncategorized;
    }

    @Override
    protected AddInstitutionItemView onCreateItemView(ViewGroup parent, int viewType) {
        return AddInstitutionItemView_.build(context);
    }

}
