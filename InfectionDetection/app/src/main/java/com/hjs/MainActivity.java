package com.hjs;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.mashape.unirest.http.Unirest;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static String host = "http://52.4.123.82:8080/";

    private GoogleApiClient mGoogleApiClient;

    public static Location mLastLocation;

    private Drawer result;

    public static String uuid;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //View header = getLayoutInflater().inflate(R.layout.nav_header, null);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                //.withHeader(header)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Map").withIdentifier(0)
                                .withIcon(new IconDrawable(this, FontAwesomeIcons.fa_globe).actionBarSize()),
                        new PrimaryDrawerItem().withName("History").withIdentifier(1)
                                .withIcon(new IconDrawable(this, FontAwesomeIcons.fa_history).actionBarSize()),
                        new PrimaryDrawerItem().withName("Status").withIdentifier(2)
                                .withIcon(new IconDrawable(this, FontAwesomeIcons.fa_clipboard).actionBarSize()),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Settings").withIdentifier(3)
                                .withIcon(new IconDrawable(this, FontAwesomeIcons.fa_wrench).actionBarSize())
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        createSelectedFragment(drawerItem);
                        return true;
                    }
                })
                .build();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(sharedPreferences.contains("uuid")) {
            uuid = sharedPreferences.getString("uuid", null);
        } else {
            uuid = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(RegistrationIntentService.UUID_KEY, uuid).apply();
            register(uuid);
        }

        result.setSelection(0);

        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null).show();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("receive");
            }
        };

        if(!sharedPreferences.getBoolean(RegistrationIntentService.REGISTRATION_COMPLETE, false)) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        buildGoogleApiClient();
    }

    public void createSelectedFragment(IDrawerItem drawerItem){
        Fragment fragment = new Fragment();
        switch(drawerItem.getIdentifier()){
            case 0: fragment = MMapFragment.newInstance();break;
            case 1: fragment = HistoryFragment.newInstance();break;
            case 2: fragment = SurveyFragment.newInstance();break;
            case 3: fragment = SettingFragment.newInstance();break;
            default:break;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.viewer, fragment).commitAllowingStateLoss();
        result.closeDrawer();
    }

    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void getFineLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }

    private void getCoarseLocation() {
        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mLastLocation = locationManager.getLastKnownLocation(bestProvider);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void register(String uuid) {
        Unirest.post(host+"register").field("uuid", uuid).asStringAsync();
    }

    public void onSick(View v) {
        System.out.println("sick");
        Unirest.post(host+"update").field("uuid", uuid).field("sick", "1").asStringAsync();
    }

    public void onNotSick(View v) {
        Unirest.post(host+"update").field("uuid", uuid).field("sick", "0").asStringAsync();
    }

    public void onTrack(View v) {
        System.out.println();
        Unirest.post(host+"track").field("uuid", uuid).field("lat", mLastLocation.getLatitude()+"")
                .field("lng", mLastLocation.getLongitude() + "").asStringAsync();
    }

    public void onEnableTrack(View v) {
        Unirest.post(host+"allowtrack").field("uuid", uuid).field("allow", "true").asStringAsync();
    }

    public void onDisableTrack(View v) {
        Unirest.post(host+"allowtrack").field("uuid", uuid).field("allow", "false").asStringAsync();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
