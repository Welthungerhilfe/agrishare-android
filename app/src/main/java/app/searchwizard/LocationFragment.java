package app.searchwizard;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
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

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.DatePickerWithMinFragment;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class LocationFragment extends BaseFragment {


    final int MY_LOCATION_PERMISSIONS_REQUEST = 2001;

    TextView start_date_textview;
    Button submit_button;

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
        if (place != null){
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

        if (place == null) {
            popToast(getActivity(), getActivity().getString(R.string.please_set_a_location));
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {

            ((SearchActivity) getActivity()).query.put("Latitude", String.valueOf(place.getLatLng().latitude));
            ((SearchActivity) getActivity()).query.put("Longitude", String.valueOf(place.getLatLng().longitude));

            MyApplication.searchQuery.Latitude = place.getLatLng().latitude;
            MyApplication.searchQuery.Longitude = place.getLatLng().longitude;
            MyApplication.searchQuery.Location = place.getName().toString();

            if (((SearchActivity) getActivity()).mPager.getCurrentItem() < ((SearchActivity) getActivity()).NUM_PAGES - 1){
                ((SearchActivity) getActivity()).mPager.setCurrentItem(((SearchActivity) getActivity()).mPager.getCurrentItem() + 1);
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
        ((TextView) rootView.findViewById(R.id.location)).setText(getResources().getString(R.string.location));
        ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(R.color.grey_for_text));
    }

    private void showFetchingLocationTextView(){
        ((TextView) rootView.findViewById(R.id.location)).setText(getResources().getString(R.string.fetching_current_location));
        ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(R.color.grey_for_text));
    }

    private void updateSelectedLocationTextView(){
        ((TextView) rootView.findViewById(R.id.location)).setText(place.getName());
        ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(android.R.color.black));

        checkIfAllFieldsAreFilledIn();
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
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
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
