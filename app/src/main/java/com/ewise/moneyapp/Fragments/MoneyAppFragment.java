package com.ewise.moneyapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ewise.moneyapp.MainActivity;
import com.ewise.moneyapp.MoneyAppApp;
import com.ewise.moneyapp.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoneyAppFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoneyAppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public abstract class MoneyAppFragment extends Fragment implements MainActivity.FragmentUpdateListener {

    private static final String TAG = "MoneyAppFragment";

    //refresh timeout required to ensure that the page refresh happens after
    // all pending fragment manager transactions are executed
    private static final long FRAGMENT_UI_REFRESH_TIMEOUT_MS = 1000;
    private static final long FRAGMENT_UI_REFRESH_WAIT_TIME_MS = 100;


    private OnFragmentInteractionListener mListener;

    public MoneyAppFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoneyAppFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment newInstance() {
        Fragment fragment = new Fragment();
        Bundle args = new Bundle();
        //fragment.setArguments(args);
        return fragment;
    }

    /*
    * Must override this method in deriving class
     */
    public abstract void updatePageData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //todo: read any arguments
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                ((MainActivity)getActivity()).disableProfileFab();
            }
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).disableProfileFab();
        ((MainActivity) getActivity()).setAttachedFragmentUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).resetAttachedFragmentUpdateListener(this);
    }

    @Override
    public void refreshFragmentUI(){

        //TODO: **SN** this code is buggy
        long timeout=0;
        synchronized (Thread.currentThread()) {
            while (!isAdded() && timeout <= FRAGMENT_UI_REFRESH_TIMEOUT_MS) {
                try {
                        timeout += FRAGMENT_UI_REFRESH_WAIT_TIME_MS;
                        Thread.currentThread().wait(timeout);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        if (isAdded()) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //accounts retrieval failed
                    updatePageData();
                }
            });
        }
    }

}
