package app.searchwizard;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.c2.android.DatePickerWithMinFragment;
import app.c2.android.OkHttp;
import app.dao.Location;
import app.dao.MyCalendar;
import app.map.MapActivity;
import app.services.SelectServiceActivity;
import okhttp3.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static app.agrishare.Constants.KEY_ID;
import static app.agrishare.Constants.KEY_LOCATION;
import static app.agrishare.Constants.KEY_SERVICE;
import static app.agrishare.Constants.TAB_FOR;
import static app.agrishare.Constants.TAB_LOCATION;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class LocationFragment extends BaseFragment {

    final int MY_LOCATION_PERMISSIONS_REQUEST = 2001;
    final int CHOOSE_LOCATION_FROM_MAP_REQUEST_CODE = 2002;

    TextView start_date_textview;
    Button submit_button;

    Location selectedLocation;
    Place place;
    PlaceLikelihoodBufferResponse likelyPlaces;

    public LocationFragment() {
        mtag = "name";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    LocationFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_location_form, container, false);
        fragment = this;
        initViews();
        return rootView;
    }

    private void initViews(){

        start_date_textview = rootView.findViewById(R.id.start_date);
        (rootView.findViewById(R.id.my_location_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    checkIfLocationServicesIsEnabled();
                }

            }
        });

        (rootView.findViewById(R.id.my_current_location)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    checkIfLocationServicesIsEnabled();
                }

            }
        });

        (rootView.findViewById(R.id.choose_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    goToMap();
                }

            }
        });

        (rootView.findViewById(R.id.choose_textview)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    goToMap();
                }

            }
        });

        submit_button = rootView.findViewById(R.id.submit);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    checkFields();
                }

            }
        });
        checkIfAllFieldsAreFilledIn();

    }

    private void goToMap(){
        Intent intent = new Intent(getActivity(), MapActivity.class);
        startActivityForResult(intent, CHOOSE_LOCATION_FROM_MAP_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
    }

    private void checkIfLocationServicesIsEnabled(){
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(getActivity().getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getActivity().getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
        else {
            attemptFetchCurrentLocation();
        }
    }


    private void checkIfAllFieldsAreFilledIn(){
        if (place != null || selectedLocation != null){
            enableSubmitButton(submit_button);
        }
        else
            disableSubmitButton(submit_button);
    }

    private void clearErrors(){

    }

    public void checkFields() {
        closeKeypad();
        clearErrors();

        boolean cancel = false;
        View focusView = null;

        if (place == null && selectedLocation == null) {
            popToast(getActivity(), getActivity().getString(R.string.please_set_a_location));
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {

            if (place != null) {
                ((SearchActivity) getActivity()).query.put("Latitude", String.valueOf(place.getLatLng().latitude));
                ((SearchActivity) getActivity()).query.put("Longitude", String.valueOf(place.getLatLng().longitude));

                MyApplication.searchQuery.Latitude = place.getLatLng().latitude;
                MyApplication.searchQuery.Longitude = place.getLatLng().longitude;
                MyApplication.searchQuery.Location = place.getName().toString();

                if (((SearchActivity) getActivity()).mPager.getCurrentItem() < ((SearchActivity) getActivity()).NUM_PAGES - 1){
                    ((SearchActivity) getActivity()).mPager.setCurrentItem(((SearchActivity) getActivity()).mPager.getCurrentItem() + 1);
                }
            }
            else if (selectedLocation != null){

                ((SearchActivity) getActivity()).query.put("Latitude", String.valueOf(selectedLocation.Latitude));
                ((SearchActivity) getActivity()).query.put("Longitude", String.valueOf(selectedLocation.Longitude));

                MyApplication.searchQuery.Latitude = selectedLocation.Latitude;
                MyApplication.searchQuery.Longitude = selectedLocation.Longitude;
                MyApplication.searchQuery.Location = selectedLocation.Title;


                if (((SearchActivity) getActivity()).mPager.getCurrentItem() < ((SearchActivity) getActivity()).NUM_PAGES - 1){
                    ((SearchActivity) getActivity()).mPager.setCurrentItem(((SearchActivity) getActivity()).mPager.getCurrentItem() + 1);
                }
            }

        }

    }

    private void attemptFetchCurrentLocation(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            askForPermissions();
        }
    }

    public void askForPermissions(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)  {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_PERMISSIONS_REQUEST);
        }
    }

    public void getCurrentLocation(){
        showFetchingLocationTextView();
        PlaceDetectionClient placeDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        Task<PlaceLikelihoodBufferResponse> placeResult = placeDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                Log("AGRISHARE PLACE GET CURRENT: COMPLETE" + task.isSuccessful() + " - " +  task.isComplete() + " - " +  task.isCanceled());
                if (task.isSuccessful()) {
                    likelyPlaces = task.getResult();
                    float highest_likelihood = 0;
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        Log.d("AGRISHARE PLACES", String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                        if (placeLikelihood.getLikelihood() > highest_likelihood) {
                            place = placeLikelihood.getPlace();
                            highest_likelihood = placeLikelihood.getLikelihood();
                        }
                    }
                    updateSelectedLocationTextView();
                    //   likelyPlaces.release();
                }
                else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.failed_to_fetch_current_location), Toast.LENGTH_LONG).show();
                        place = null;
                        resetLocationTextView();
                    }
                }
            }

        });

    }

    private void resetLocationTextView(){
        if (getActivity() != null) {
            ((TextView) rootView.findViewById(R.id.location)).setText(getResources().getString(R.string.location));
            ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(R.color.grey_for_text));
        }
    }

    private void showFetchingLocationTextView(){
        if (getActivity() != null) {
            ((TextView) rootView.findViewById(R.id.location)).setText(getResources().getString(R.string.fetching_current_location));
            ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(R.color.grey_for_text));
        }
    }

    private void updateSelectedLocationTextView(){
        if (getActivity() != null) {
            ((TextView) rootView.findViewById(R.id.location)).setText(place.getName());
            ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(android.R.color.black));
            selectedLocation = null;
            checkIfAllFieldsAreFilledIn();
        }
    }

    private void showFetchingLocationFromMapTextView(){
        if (getActivity() != null) {
            ((TextView) rootView.findViewById(R.id.location)).setText(getResources().getString(R.string.fetching_location_details));
            ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(R.color.grey_for_text));
        }
    }

    private void showFetchingLocationFromMapFailedTextView(){
        if (getActivity() != null) {
            //just failed to get the title but we already have coordinates, so it's safe to proceed.
            ((TextView) rootView.findViewById(R.id.location)).setText(getResources().getString(R.string.location_successfully_marked));
            ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(android.R.color.black));
            place = null;
            checkIfAllFieldsAreFilledIn();
        }
    }

    private void updateSelectedLocationTextViewFromMapData(){
        if (getActivity() != null) {
            ((TextView) rootView.findViewById(R.id.location)).setText(selectedLocation.Title);
            ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(android.R.color.black));
            place = null;
            checkIfAllFieldsAreFilledIn();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("PERMISSIONS", "");
        switch (requestCode) {
            case MY_LOCATION_PERMISSIONS_REQUEST: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    Log.d("PERMISSION GRANTED", "");
                    getCurrentLocation();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Log.d("PERMISSION GRANTED", "NOT");


                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log("ON ACTIVITY RESULT: " + requestCode);
        if (requestCode == CHOOSE_LOCATION_FROM_MAP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                selectedLocation = data.getParcelableExtra(KEY_LOCATION);
                showFetchingLocationFromMapTextView();
                getLocationData(selectedLocation.Latitude + "," + selectedLocation.Longitude);
            }else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

    private void getLocationData(String coordinates){
        Log("COORDINATES TO FIND LOCATION DETAILS: "+ coordinates);
        GetMapLocationDetailsRequest task = new GetMapLocationDetailsRequest(fetchLocationDataResponse);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, coordinates);
    }

    AsyncResponse fetchLocationDataResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("MAP LOCATION DETAIL SUCCESS"+ result.toString() + "");
            if (selectedLocation != null) {
                if (result.optJSONArray("results").length() > 0) {
                    selectedLocation.Title = result.optJSONArray("results").optJSONObject(0).optString("name");
                    updateSelectedLocationTextViewFromMapData();
                } else {
                   // selectedLocation.Title = "Could not fetch location title";
                    showFetchingLocationFromMapFailedTextView();
                }
            }
            else {
                // do nothing. user probably selected "use my current location" option instead while getLocationData was running
                // which results in selectedLocation being null.
            }
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("MAP LOCATION DETAIL ERROR:  " + errorMessage);
            showFetchingLocationFromMapFailedTextView();
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }

        if (((SearchActivity) getActivity()).tabsStackList.contains(TAB_LOCATION))
            ((SearchActivity) getActivity()).tabsStackList.remove(TAB_LOCATION);
        ((SearchActivity) getActivity()).tabsStackList.add(TAB_LOCATION);
    }

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (likelyPlaces != null){
            likelyPlaces.release();
        }
    }

}
