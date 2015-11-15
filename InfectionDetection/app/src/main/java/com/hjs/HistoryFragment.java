package com.hjs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by spero on 11/15/15.
 */
public class HistoryFragment extends Fragment {

    public static HistoryFragment newInstance(){
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    public HistoryFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.historyfragment,container,false);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
