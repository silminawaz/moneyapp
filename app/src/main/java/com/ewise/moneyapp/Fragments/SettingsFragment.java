package com.ewise.moneyapp.Fragments;

/**
 * Created by SilmiNawaz on 20/8/16.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.ewise.moneyapp.MainActivity;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.Utils.Settings;
import com.ewise.moneyapp.Utils.SignonProfile;
import com.ewise.moneyapp.Utils.SignonUser;
import com.ewise.moneyapp.adapters.ProfilesItemViewAdapter;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public ListView settingsProfileList=null;
    public Button settingsProfileAddBtn;

    private ProfilesItemViewAdapter profilesItemViewAdapter;


    public SettingsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SettingsFragment newInstance(int sectionNumber) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        settingsProfileList = (ListView) rootView.findViewById(R.id.settingProfilesList);
        settingsProfileAddBtn = (Button) rootView.findViewById(R.id.settingsProfileAddBtn);


        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        settingsProfileList.setClickable(true);

        settingsProfileList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> ListView, View view, int i, long l) {

                SignonProfile profile = (SignonProfile)profilesItemViewAdapter.getItem(i);
                if (isAdded()) {
                    ((MainActivity)getActivity()).showEditProfilesDialog(profile);
                }
            }
        });

        Settings settings = Settings.getInstance(getActivity());
        SignonUser activeUser = settings.getActiveUser(getActivity());
        List<SignonProfile> profileList = activeUser.profiles;
        profilesItemViewAdapter = new ProfilesItemViewAdapter(getActivity(), profileList);
        settingsProfileList.setAdapter(profilesItemViewAdapter);

        settingsProfileAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAdded()) {
                    ((MainActivity)getActivity()).showEditProfilesDialog(null);
                }
            }
        });
    }
}