package com.hjs;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by spero on 11/14/15.
 */
public class MMapFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap googleMap;
    private SeekBar seekbar;
    private List<LatLng> data = null;
    private int hoursBefore = 0;

    public static MMapFragment newInstance(){
        MMapFragment fragment = new MMapFragment();
        return fragment;
    }

    public MMapFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mapfragment,container,false);
        seekbar = (SeekBar)v.findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                hoursBefore = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return v;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isAdded()){
                    SupportMapFragment supportMapFragment = SupportMapFragment.
                            newInstance(new GoogleMapOptions().mapType(GoogleMap.MAP_TYPE_HYBRID));
                    supportMapFragment.getMapAsync(MMapFragment.this);
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.map_container,supportMapFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        },400);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;

        googleMap.setMyLocationEnabled(true);

        Unirest.get(MainActivity.host+"points").field("lat", "42.732239").field("lng", "-73.6604785").field("rad", "10").field("time", "1447605882").asStringAsync(new Callback<String>() {
            @Override
            public void completed(HttpResponse<String> httpResponse) {
                /*String str = "[{\"lat\" : -37.1886, \"lng\" : 145.708 } ," +
                        "{\"lat\" : -37.8361, \"lng\" : 144.845 } ," +
                        "{\"lat\" : -38.4034, \"lng\" : 144.192 } ," +
                        "{\"lat\" : -38.7597, \"lng\" : 143.67 } ," +
                        "{\"lat\" : -36.9672, \"lng\" : 141.083 }]";*/
                try {
                    JSONArray json = new JSONArray(httpResponse.getBody());
                    data = new ArrayList<>();
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject o = json.getJSONObject(i);
                        data.add(new LatLng(o.getDouble("lat"), o.getDouble("lng")));
                        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                                .data(data)
                                .radius(30)
                                .opacity(0.4)
                                .build();
                        googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(UnirestException e) {

            }

            @Override
            public void cancelled() {

            }
        });
    }
}
