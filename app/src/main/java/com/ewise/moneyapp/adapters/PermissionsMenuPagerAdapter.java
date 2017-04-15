package com.ewise.moneyapp.adapters;

/**
 * Created by SilmiNawaz on 14/4/17.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ewise.moneyapp.PermissionsFragment;
import com.ewise.moneyapp.R;

import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class PermissionsMenuPagerAdapter extends RemovableFragmentPagerAdapter {


    public PermissionsMenuPagerAdapter(FragmentManager fm, List<String> pagetitleList) {
        super(fm, pagetitleList);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        return PermissionsFragment.newInstance(position);

    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence pageTitle = super.getPageTitle(position).toString();
        if (pageTitle.toString().equals("")) {
            return "Permissions";
        }

        return "";
    }
}

