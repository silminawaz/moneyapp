package com.ewise.moneyapp;

/**
 * Created by SilmiNawaz on 20/8/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.provider.Group;
import com.ewise.android.pdv.api.model.provider.GroupedInstitution;
import com.ewise.android.pdv.api.model.provider.Institution;
import com.ewise.android.pdv.api.model.provider.Providers;
import com.ewise.moneyapp.Utils.DataLoadCallBackInterface;
import com.ewise.moneyapp.Utils.PdvApiResults;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddInstitutionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    interface Listener {
        public void updateFragmentData(AddInstitutionFragment fragment);
    }

    private Listener listener;


    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_PROVIDER_GROUP = "provider_group";

    RecyclerView add_institution_recycler_view;

    AddInstitutionItemViewAdapter institutionItemViewAdapter;

    String groupId;
    int sectionNumber;


    public AddInstitutionFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AddInstitutionFragment newInstance(int sectionNumber, String strGroup) {
        AddInstitutionFragment fragment = new AddInstitutionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        Log.d("D2", strGroup);
        args.putString(ARG_PROVIDER_GROUP, strGroup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_add_institution, container, false);

        sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);


        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format_2, getArguments().getInt(ARG_SECTION_NUMBER)));

        add_institution_recycler_view = (RecyclerView) rootView.findViewById(R.id.add_institution_recycler_view);
        add_institution_recycler_view.setLayoutManager(new LinearLayoutManager(this.getContext()));
        institutionItemViewAdapter = new AddInstitutionItemViewAdapter(this.getContext());
        add_institution_recycler_view.setAdapter(institutionItemViewAdapter);

        String strGroup = getArguments().getString(ARG_PROVIDER_GROUP);
        Log.d("D3", strGroup);
        Group group = (Group) PdvApiResults.objectFromString(strGroup, Group.class);
        setInstitutionData(group);

        return rootView;
    }

    public boolean setInstitutionData(Group group){

        try
        {
            List<Institution> institutionList = group.getInstitutions();
            List<GroupedInstitution> groupedInstitutionList = new ArrayList<>();
            for (Institution institution : institutionList){
                GroupedInstitution groupedInstitution = new GroupedInstitution();
                groupedInstitution.setInstCode(institution.getInstCode());
                groupedInstitution.setInstDesc(institution.getInstDesc());
                groupedInstitution.setGroupId(group.getGroupId());
                groupedInstitution.setGroupDesc(group.getGroupDesc());
                groupedInstitutionList.add(groupedInstitution);
            }
            institutionItemViewAdapter.swapData(groupedInstitutionList);
            return true;
        }
        catch (Exception e){
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            String sObjString = group.toString();
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, sObjString);
        }

        return false;
    }


    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}