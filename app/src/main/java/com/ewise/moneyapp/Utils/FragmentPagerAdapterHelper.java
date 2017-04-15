package com.ewise.moneyapp.Utils;

import android.accounts.Account;
import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.ewise.moneyapp.MainActivity;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.ReportIssueFragment;
import com.ewise.moneyapp.adapters.HelpMenuPagerAdapter;
import com.ewise.moneyapp.adapters.PermissionsMenuPagerAdapter;
import com.ewise.moneyapp.adapters.RemovableFragmentPagerAdapter;
import com.ewise.moneyapp.adapters.SectionsPagerAdapter;
import com.ewise.moneyapp.adapters.SettingsMenuPagerAdapter;
import com.ewise.moneyapp.adapters.TransferMenuPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SilmiNawaz on 15/4/17.
 */
public class FragmentPagerAdapterHelper {

    public static final String TAG = "FragmentPagerAdapterH..";

    public static synchronized ViewPager setViewPagerToAdapter (ViewPager viewPager, Activity activity, FragmentManager fm, Class pagerAdapterClass, boolean keepIfExisting){

        Log.d(TAG, "setViewPagerToAdapter() : pagerAdapterClass="+pagerAdapterClass.getName()+" : keepIfExisting=" + Boolean.toString(keepIfExisting));
        if (keepIfExisting){
            if (viewPager.getAdapter()!=null) {
                if (viewPager.getAdapter().getClass().equals(pagerAdapterClass)) {
                    Log.d(TAG, "setViewPagerToAdapter() : **NO CHANGE IN VIEW PAGER ADAPTER**");
                    return viewPager; //do nothing if we are currently on same adapter
                }
            }
        }

        if (viewPager.getAdapter()!=null)
        {
            //clear it
            PagerAdapter adapter = viewPager.getAdapter();
            Log.d(TAG, "setViewPagerToAdapter() : viewPager.getAdapter().getClass.getName()="+adapter.getClass().getName());
            if (adapter instanceof RemovableFragmentPagerAdapter){

                ((RemovableFragmentPagerAdapter)adapter).removeAllItems(viewPager);
                adapter=null;
                viewPager.setAdapter(null); //viewpager adapter to kill itself
                Log.d(TAG, "setViewPagerToAdapter() : viewPager.setAdapter(null)");

            }
            else
            {
                Log.d(TAG, "setViewPagerToAdapter() :**INVALID FRAGMENT CLASS**");
                return null; /*must be using RemovableFragmentPagerAdapter class */
            }
        }

        //todo: create a new section pager adapter with the title array
        try {


            //viewPager.setOffscreenPageLimit(1);
            viewPager.setAdapter((RemovableFragmentPagerAdapter)
                    Class.forName(pagerAdapterClass.getName())
                            .getConstructor(FragmentManager.class, List.class)
                            .newInstance(fm, getTitleList(pagerAdapterClass, activity)));
            viewPager.getAdapter().notifyDataSetChanged();
            Log.d(TAG, "setViewPagerToAdapter() : viewPager.getAdapter().getPageTitle(0)=" + viewPager.getAdapter().getPageTitle(0));
            return viewPager;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public static List<String> getTitleList (Class pagerAdapterClass, Activity activity){
        String resourceName = pagerAdapterClass.getName().replaceAll("com.ewise.moneyapp.adapters.", "");
        Log.d(TAG, "getTitleList() : resourceName=" + resourceName);
        int resId = activity.getResources().getIdentifier(resourceName, "array", activity.getPackageName());
        Log.d(TAG, "getTitleList() : resId=" + Integer.toString(resId));
        return new ArrayList<>(Arrays.asList(activity.getResources().getStringArray(resId)));
    }

}
