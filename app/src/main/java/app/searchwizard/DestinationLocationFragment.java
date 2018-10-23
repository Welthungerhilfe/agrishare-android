package app.searchwizard;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.dao.Location;
import app.map.MapActivity;
import app.search.SearchResultsActivity;
import okhttp3.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static app.agrishare.Constants.KEY_LOCATION;
import static app.agrishare.Constants.KEY_SEARCH_QUERY;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class DestinationLocationFragment extends BaseFragment {

    final int MY_LOCATION_PERMISSIONS_REQUEST = 3001;
    final int CHOOSE_LOCATION_FROM_MAP_REQUEST_CODE = 3002;

    Location selectedLocation;
    TextView start_date_textview;
    Button submit_button;

    Place place;
    PlaceLikelihoodBufferResponse likelyPlaces;

    public DestinationLocationFragment() {
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

    DestinationLocationFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_destination_location_form, container, false);
        fragment = this;
        initViews();
        ((SearchActivity) getActivity()).mPager.setPagingEnabled(true);   //enable swipe in custom viewpager
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
                ((SearchActivity) getActivity()).query.put("DestinationLatitude", String.valueOf(place.getLatLng().latitude));
                ((SearchActivity) getActivity()).query.put("DestinationLongitude", String.valueOf(place.getLatLng().longitude));

                MyApplication.searchQuery.DestinationLatitude = place.getLatLng().latitude;
                MyApplication.searchQuery.DestinationLongitude = place.getLatLng().longitude;
                MyApplication.searchQuery.DestinationLocation = place.getName().toString();

                Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
                intent.putExtra(KEY_SEARCH_QUERY, ((SearchActivity) getActivity()).query);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }
            else if (selectedLocation != null) {
                ((SearchActivity) getActivity()).query.put("DestinationLatitude", String.valueOf(selectedLocation.Latitude));
                ((SearchActivity) getActivity()).query.put("DestinationLongitude", String.valueOf(selectedLocation.Longitude));

                MyApplication.searchQuery.DestinationLatitude = selectedLocation.Latitude;
                MyApplication.searchQuery.DestinationLongitude = selectedLocation.Longitude;
                MyApplication.searchQuery.DestinationLocation = selectedLocation.Title;

                Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
                intent.putExtra(KEY_SEARCH_QUERY, ((SearchActivity) getActivity()).query);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }

            ((SearchActivity) getActivity()).mPager.setPagingEnabled(true);   //enable swipe in custom viewpager
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
        ((TextView) rootView.findViewById(R.id.location)).setText(getResources().getString(R.string.to));
        ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(R.color.grey_for_text));
    }

    private void showFetchingLocationTextView(){
        ((TextView) rootView.findViewById(R.id.location)).setText(getResources().getString(R.string.fetching_current_location));
        ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(R.color.grey_for_text));
    }

    private void updateSelectedLocationTextView(){
        ((TextView) rootView.findViewById(R.id.location)).setText(place.getName());
        ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(android.R.color.black));
        selectedLocation = null;
        checkIfAllFieldsAreFilledIn();
    }

    private void showFetchingLocationFromMapTextView(){
        ((TextView) rootView.findViewById(R.id.location)).setText(getResources().getString(R.string.fetching_location_details));
        ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(R.color.grey_for_text));
    }

    private void showFetchingLocationFromMapFailedTextView(){
        ((TextView) rootView.findViewById(R.id.location)).setText(getResources().getString(R.string.failed_to_fetch_location_details));
        ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(R.color.grey_for_text));
    }

    private void updateSelectedLocationTextViewFromMapData(){
        ((TextView) rootView.findViewById(R.id.location)).setText(selectedLocation.Title);
        ((TextView) rootView.findViewById(R.id.location)).setTextColor(getResources().getColor(android.R.color.black));
        place = null;
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
        GetMapLocationDetailsRequest task = new GetMapLocationDetailsRequest(fetchLocationDataResponse);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, coordinates);
    }

    AsyncResponse fetchLocationDataResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("MAP DESTINATION LOCATION DETAIL SUCCESS"+ result.toString() + "");
            if (result.optJSONArray("results").length() > 0) {
                selectedLocation.Title = result.optJSONArray("results").optJSONObject(0).optString("name");
            }
            else {
                selectedLocation.Title = "Could not fetch location title";
            }
            updateSelectedLocationTextViewFromMapData();
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("MAP DESTINATION LOCATION DETAIL ERROR:  " + errorMessage);
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
