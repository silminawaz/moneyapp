package com.ewise.moneyapp.Fragments;

/**
 * Created by SilmiNawaz on 20/8/16.
 */

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ewise.moneyapp.MainActivity;
import com.ewise.moneyapp.MoneyAppApp;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.Utils.Settings;
import com.ewise.moneyapp.Utils.SignonProfile;
import com.ewise.moneyapp.Utils.SignonUser;
import com.ewise.moneyapp.adapters.ProfilesItemViewAdapter;
import com.ewise.moneyapp.data.AccountCardDataObject;
import com.ewise.moneyapp.data.AccountCardListDataObject;

import org.androidannotations.annotations.App;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends MoneyAppFragment {

    private static final String TAG = "SettingsFragment";

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

        ((MainActivity)getActivity()).enableProfileFab();

        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.settingsProfilesListLayout);
        TabLayout tab = (TabLayout)getActivity().findViewById(R.id.tabs);
        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.appbar);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        //layout.getLayoutParams().height=size.y-tab.getHeight()-appBarLayout.getHeight();

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


    //NOTE: setUserVisibleHint() is called when the fragment is no longer visible or becomes visible
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            //if (isAdded()) {

                if (isVisibleToUser) {
                    ((MainActivity)getActivity()).enableProfileFab();

                } else {
                    ((MainActivity)getActivity()).disableProfileFab();
                }
            //}
        }
    }

    //This method always runs on UI thread as it is called by refreshFragmentUI()
    public void updatePageData()
    {
        if (isAdded()) {
            Log.d(TAG, "updatePageData()");
            //there is no need to swap data because the adapter uses the profile data from the settings
            profilesItemViewAdapter.updateImageCache();
            profilesItemViewAdapter.notifyDataSetChanged();
        }
    }

}