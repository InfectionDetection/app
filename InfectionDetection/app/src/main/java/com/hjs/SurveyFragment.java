package com.hjs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.mashape.unirest.http.Unirest;

/**
 * Created by spero on 11/14/15.
 */
public class SurveyFragment extends Fragment {

    public static SurveyFragment newInstance(){
        SurveyFragment fragment = new SurveyFragment();
        return fragment;
    }

    public SurveyFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.surveyfragment,container,false);

        ((ImageButton)v.findViewById(R.id.survey_yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Unirest.post(MainActivity.host + "update").field("uuid", MainActivity.uuid).field("sick", "1").asStringAsync();
                Unirest.post(MainActivity.host+"track").field("uuid", MainActivity.uuid).field("lat", MainActivity.mLastLocation.getLatitude()+"")
                        .field("lng", MainActivity.mLastLocation.getLongitude() + "").asStringAsync();
                Unirest.post(MainActivity.host + "allowtrack").field("uuid",MainActivity. uuid).field("allow", ((CheckBox)v.findViewById(R.id.track_cb)).isSelected()+"").asStringAsync();
                AlertDialog.Builder builder = new AlertDialog.Builder(SurveyFragment.this.getContext(), R.style.MaterialBaseTheme_AlertDialog);
                builder.setTitle("Thanks!");
                builder.setMessage("Thank you for your input! Please let us know when you feel better!");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        });

        ((ImageButton)v.findViewById(R.id.survey_no)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Unirest.post(MainActivity.host+"update").field("uuid", MainActivity.uuid).field("sick", "0").asStringAsync();
                AlertDialog.Builder builder = new AlertDialog.Builder(SurveyFragment.this.getContext(), R.style.MaterialBaseTheme_AlertDialog);
                builder.setTitle("Thanks!");
                builder.setMessage("Thank for letting us know!");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        });

        return v;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
