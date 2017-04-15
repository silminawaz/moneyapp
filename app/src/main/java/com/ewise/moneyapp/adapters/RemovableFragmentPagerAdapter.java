package com.ewise.moneyapp.adapters;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;

import com.ewise.moneyapp.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by SilmiNawaz on 15/4/17.
 */
public class RemovableFragmentPagerAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = "RemovableFragmentPag...";

    HashMap<String, Object> instantiatedObjectMap=null;
    List<String> pageTitleList=null;

    public RemovableFragmentPagerAdapter
            (FragmentManager fm,
             List<String> pageTitleList /*list must be sorted by position*/)
    {
        super(fm);
        instantiatedObjectMap = new HashMap<>();
        this.pageTitleList=pageTitleList;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        return null;

    }

    @Override
    public int getCount() {
        return 0;
    }

    public boolean removeItem (ViewGroup container, int position, Object object){
        //todo: implement RemovableFragmentPagerAdapter.removeItem(ViewGroup container, int position, Object object) later
        return false;
    }

    /* Once you call this method, the adapter must be set to null */
    public boolean removeAllItems(
            ViewGroup container /* must be the same ViewPager used previously  */){


        try {
            Log.d(TAG, "removeAllItems() - START");
            if (instantiatedObjectMap!=null){
                if (instantiatedObjectMap.size()>0){
                    Iterator it = instantiatedObjectMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry entry = (Map.Entry) it.next();
                        int position = Integer.parseInt((String) entry.getKey());
                        Object object = entry.getValue();
                        if (position>=0 && object!=null){
                            destroyItem(container, position, object);
                        }
                    }

                    //remove all items from the map
                    instantiatedObjectMap.clear();
                    //this.notifyDataSetChanged();
                    return true;
                }
            }
            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override public Object instantiateItem (ViewGroup container, int position){
        Object instantiatedObject = super.instantiateItem(container, position);
        this.instantiatedObjectMap.put(Integer.toString(position), instantiatedObject);
        Log.d(TAG, "instantiateItem() -this class=" + this.getClass().getName() +  " - instantiatedObject=" + instantiatedObject.getClass().getName());
        return instantiatedObject;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object){
        Log.d(TAG, "destroyItem() - START");

        super.destroyItem(container, position, object);

        //remoe related childfragments and fragments from fragment manager
        if (position <= getCount()) {
            //remove child fragments
            FragmentManager manager = ((Fragment) object).getChildFragmentManager();
            if (manager!=null) {
                //remove all fragments
                if (manager.getFragments() != null) {
                    List<Fragment> childFragments = manager.getFragments();
                    for (Iterator<Fragment> iterator = childFragments.iterator(); iterator.hasNext(); ) {
                        Fragment childFragment = iterator.next();
                        FragmentTransaction trans = childFragment.getFragmentManager().beginTransaction();
                        Log.d(TAG, "destroyItem() - Removing ChildFragment =" + childFragment.getClass().getName());
                        trans.remove(childFragment);
                        trans.commit();
                    }

                    //todo: begin: test code - checking if this removes any backstack references of dialogfragments
                    //manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    //end:
                }
            }

            manager = ((Fragment) object).getFragmentManager();
            if (manager!=null) {
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove((Fragment) object);
                Log.d(TAG, "destroyItem() - Removing Fragment =" + object.getClass().getName());
                trans.commit();
            }
        }

        //remove item from local map - comment this until we know if there are remaining undestroyed views.
        /*
        if (instantiatedObjectMap.containsKey(Integer.toString(position))) {
            synchronized (this) {
                instantiatedObjectMap.remove(Integer.toString(position));
            }
        }
        */
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (pageTitleList!=null){
            if (pageTitleList.size()<position){
                return pageTitleList.get(position);
            }
        }
        return "";
    }



}
