package app.map;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import app.c2.android.Utils;
import app.dao.Category;
import app.dao.Location;
import io.realm.RealmResults;

import static app.agrishare.Constants.KEY_CATEGORY;
import static app.agrishare.Constants.KEY_LOCATION;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    android.support.v7.app.AlertDialog.Builder alertDialogBuilder;

    Criteria criteria;
    double latitude = 0, longitude = 0;

    // The minimum distance to change Updates in meters
    //  private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    // The minimum time between updates in milliseconds
//    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private static final long MIN_TIME_BW_UPDATES = 0;


    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected boolean gps_enabled,network_enabled;

    Boolean isGPSEnabled = false, isNetworkEnabled = false;
    android.location.Location location; // location

    private GoogleMap map;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Map", R.drawable.button_back);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Utils.getNetworkConnection(MapActivity.this).equals("MOBILE")){
                if (Utils.getNetworkClass(MapActivity.this).equals("2G")){
                    ((TextView) findViewById(R.id.intro)).setText(getResources().getString(R.string.find_your_location_and_tap_on_it) + " " + getResources().getString(R.string.poor_network_connection_map_message));
                }
                else {
                    ((TextView) findViewById(R.id.intro)).setText(getResources().getString(R.string.find_your_location_and_tap_on_it));
                }
            }
            else if (Utils.getNetworkConnection(MapActivity.this).equals("NONE")) {
                ((TextView) findViewById(R.id.intro)).setText(getResources().getString(R.string.find_your_location_and_tap_on_it) + " " + getResources().getString(R.string.poor_network_connection_map_message));
            }
            else {
                ((TextView) findViewById(R.id.intro)).setText(getResources().getString(R.string.find_your_location_and_tap_on_it));
            }


        }
    };

    private void initViews(){
        if (ContextCompat.checkSelfPermission(MapActivity.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MapActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            initMap();
        }
        else {
            askForLocationPermissions();
        }

    }

    public void initMap(){
        showLoader("Initiating Map", "Please Wait");
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detail_map);
        mapFrag.getMapAsync(this);
    }

    public void askForLocationPermissions(){
        if (ContextCompat.checkSelfPermission(MapActivity.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)  {

            ActivityCompat.requestPermissions(MapActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("PERMISSIONS", "");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    Log.d("PERMISSION GRANTED", "");
                    initMap();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Log.d("PERMISSION GRANTED", "NOT");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(PermissionsActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        zoomInOnMyLocation();
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                returnResult(point);
            }
        });
        hideLoader();
    }

    private void zoomInOnMyLocation(){
        Log("zoomInOnMyLocation: LOCATION: " + latitude + " : " + longitude);
      //  if (isCurrentLocationCoordinatesAvailable()) {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(-19.0154, 29.1549));
        builder.include(new LatLng(-16.5220, 28.8509));
        builder.include(new LatLng(-18.9758, 32.6504));
            LatLngBounds bounds = builder.build();

            int padding = Utils.convertDPtoPx(32, MapActivity.this); // offset from edges of the map in pixels
            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    map.animateCamera(cu);
                }
            });
       // }
    }

    private void returnResult(LatLng point) {
        Location selectedLocation = new Location("", point.latitude, point.longitude);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(KEY_LOCATION, selectedLocation);
        setResult(Activity.RESULT_OK, returnIntent);
        closeKeypad();
        goBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        goBack();
    }

}
