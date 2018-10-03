package app.search;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;

import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.dao.SearchQuery;
import app.dao.Service;
import app.services.SelectServiceActivity;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static app.agrishare.Constants.KEY_ID;
import static app.agrishare.Constants.KEY_SEARCH_QUERY;
import static app.agrishare.Constants.KEY_SERVICE;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class LorriesSearchFormFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener  {

    EditText numbers_of_bags_edittext;
    Button submit_button;

    int LOCATION_REQUEST_CODE = 1000;
    int SERVICE_REQUEST_CODE = 1001;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2000;
    final int MY_LOCATION_PERMISSIONS_REQUEST = 2001;

    Service service;
    String location_id = "";
    String renting_for = "";
    String start_date = "";
    long ForId = 0;

    Place place;
    PlaceLikelihoodBufferResponse likelyPlaces;

    LorriesSearchFormFragment fragment;

    public LorriesSearchFormFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_lorries_search_form, container, false);
        fragment = this;
        initViews();
        return rootView;
    }

    private void initViews(){
        numbers_of_bags_edittext = rootView.findViewById(R.id.number_of_bags);
        submit_button = rootView.findViewById(R.id.submit);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    checkFields();
                }
            }
        });


        (rootView.findViewById(R.id.renting_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getActivity(), rootView.findViewById(R.id.arrow_down));
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_renting_options);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.me:
                                    ForId = 0;
                                    renting_for = "me";
                                    ((TextView) rootView.findViewById(R.id.rent_for)).setText("Me");
                                    break;
                                case R.id.a_friend:
                                    ForId = 1;
                                    renting_for = "a_friend";
                                    ((TextView) rootView.findViewById(R.id.rent_for)).setText("A friend");
                                    break;
                                case R.id.a_group:
                                    ForId = 2;
                                    renting_for = "a_group";
                                    ((TextView) rootView.findViewById(R.id.rent_for)).setText("A group");
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            }
        });

        (rootView.findViewById(R.id.location)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showLocationsPopupMenu();
                }
            }
        });

        (rootView.findViewById(R.id.location_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showLocationsPopupMenu();
                }
            }
        });

        (rootView.findViewById(R.id.service)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    closeKeypad();
                    openSelectService();
                }
            }
        });

        (rootView.findViewById(R.id.service_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    closeKeypad();
                    openSelectService();
                }
            }
        });

        (rootView.findViewById(R.id.start_date_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            fragment,
                            now.get(Calendar.YEAR), // Initial year selection
                            now.get(Calendar.MONTH), // Initial month selection
                            now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                    );
                    dpd.show(getActivity().getFragmentManager(), "DOBpickerdialog");
                }

            }
        });
    }

    private void showLocationsPopupMenu(){
        closeKeypad();
        //creating a popup menu
        PopupMenu popup = new PopupMenu(getActivity(), rootView.findViewById(R.id.arrow));
        popup.inflate(R.menu.menu_location_options);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.use_current_location:
                        attemptFetchCurrentLocation();
                        break;
                    case R.id.find_location:
                        findPlace();
                        break;
                }
                return false;
            }
        });
        //displaying the popup
        popup.show();
    }

    private void openSelectService(){
        long category_id = 2;
        Intent intent = new Intent(getActivity(), SelectServiceActivity.class);
        intent.putExtra(KEY_ID, category_id);
        startActivityForResult(intent, SERVICE_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
    }

    private void clearErrors(){
        numbers_of_bags_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String field_size = numbers_of_bags_edittext.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(field_size)) {
            numbers_of_bags_edittext.setError(getString(R.string.error_field_required));
            focusView = numbers_of_bags_edittext;
            cancel = true;
        }

        if (service == null) {
            popToast(getActivity(), "Please select a service");
            cancel = true;
        }

        if (place == null) {
            popToast(getActivity(), "Please select location");
            cancel = true;
        }

        if (start_date.isEmpty()) {
            popToast(getActivity(), "Please select a Start Date");
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            HashMap<String, String> query = new HashMap<String, String>();
            query.put("CategoryId", String.valueOf(2));
            query.put("ServiceId", String.valueOf(service.Id));
            query.put("Latitude", String.valueOf(place.getLatLng().latitude));
            query.put("Longitude", String.valueOf(place.getLatLng().longitude));
            query.put("StartDate", start_date);
            query.put("Size", field_size);
            query.put("IncludeFuel", "false");
            query.put("Mobile", "true");

            //temporarily store search parameters
            MyApplication.searchQuery = new SearchQuery();
            MyApplication.searchQuery.ForId = ForId;
            MyApplication.searchQuery.CategoryId = 1;
            MyApplication.searchQuery.Service = service;
            MyApplication.searchQuery.Latitude = place.getLatLng().latitude;
            MyApplication.searchQuery.Longitude = place.getLatLng().longitude;
            MyApplication.searchQuery.StartDate = start_date;
            MyApplication.searchQuery.Size = Double.parseDouble(field_size);
            MyApplication.searchQuery.Location = place.getName().toString();
            MyApplication.searchQuery.Mobile =  true;

            Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
            intent.putExtra(KEY_SEARCH_QUERY, query);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);

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

    public void findPlace() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    //   .setFilter(typeFilter)
                    .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
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
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;

        String month = (monthOfYear+1) + "";
        if ((monthOfYear+1) < 10)
            month = "0" + (monthOfYear+1);

        if (view.getTag().equals("DOBpickerdialog")) {
            ((TextView) rootView.findViewById(R.id.start_date)).setText(date);
            ((TextView) rootView.findViewById(R.id.start_date)).setTextColor(getResources().getColor(android.R.color.black));
            start_date = year + "-" + month + "-" + dayOfMonth + "T00:00:00";
            //  checkIfAllFieldsAreFilledIn();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log("ON ACTIVITY RESULT: " + requestCode);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(getActivity(), data);
                updateSelectedLocationTextView();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.d("PLACES RESPONSE ERROR", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if (requestCode == LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                location_id = data.getStringExtra("location_id");
                String location_title = data.getStringExtra("location_title");
                ((TextView) rootView.findViewById(R.id.location)).setText(location_title);
            }else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if (requestCode == SERVICE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                service = data.getParcelableExtra(KEY_SERVICE);
                if (service != null) {
                    ((TextView) rootView.findViewById(R.id.service)).setText(service.Title);
                    if (getActivity() != null)
                        ((TextView) rootView.findViewById(R.id.service)).setTextColor(getActivity().getResources().getColor(android.R.color.black));
                }
            }else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

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
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }
        closeKeypad();
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

    //Feedback - need custom to avoid class

    public void showFeedback(int iconResourceId, String title, String message) {
        rootView.findViewById(R.id.feedback_).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_activity).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_icon).setVisibility(View.VISIBLE);
        ((ImageView)rootView.findViewById(R.id.feedback_icon)).setImageResource(iconResourceId);
        ((TextView)rootView.findViewById(R.id.feedback_title)).setText(title);
        ((TextView)rootView.findViewById(R.id.feedback_message)).setText(message);
        rootView.findViewById(R.id.feedback_retry).setVisibility(View.GONE);
    }


    public void showFeedbackWithButton(int iconResourceId, String title, String message) {
        rootView.findViewById(R.id.feedback_).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_activity).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_icon).setVisibility(View.VISIBLE);
        ((ImageView)rootView.findViewById(R.id.feedback_icon)).setImageResource(iconResourceId);
        ((TextView)rootView.findViewById(R.id.feedback_title)).setText(title);
        ((TextView)rootView.findViewById(R.id.feedback_message)).setText(message);
        rootView.findViewById(R.id.feedback_retry).setVisibility(View.VISIBLE);
    }

    public void hideFeedback() {
        rootView.findViewById(R.id.feedback_).setVisibility(View.GONE);
    }

    public void showLoader() {
        showLoader("", "");
    }

    public void showLoader(String title, String message) {
        rootView.findViewById(R.id.feedback_).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_activity).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_icon).setVisibility(View.GONE);
        ((TextView)rootView.findViewById(R.id.feedback_title)).setText(title);
        ((TextView)rootView.findViewById(R.id.feedback_message)).setText(message);
        rootView.findViewById(R.id.feedback_retry).setVisibility(View.GONE);
    }

    public void showLoader(String title, String message, int progress) {
        rootView.findViewById(R.id.feedback_).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_activity).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.VISIBLE);
        ((DonutProgress) rootView.findViewById(R.id.feedback_progress)).setProgress(progress);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_icon).setVisibility(View.GONE);
        ((TextView)rootView.findViewById(R.id.feedback_title)).setText(title);
        ((TextView)rootView.findViewById(R.id.feedback_message)).setText(message);
        rootView.findViewById(R.id.feedback_retry).setVisibility(View.GONE);
    }

    public void hideLoader() {
        rootView.findViewById(R.id.feedback_).setVisibility(View.GONE);
    }




}

