package app.search;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;

import app.agrishare.BaseFragment;
import app.agrishare.R;
import app.dao.Service;
import app.location.SelectLocationActivity;
import app.services.SelectServiceActivity;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static app.agrishare.Constants.KEY_ID;
import static app.agrishare.Constants.KEY_LISTING;
import static app.agrishare.Constants.KEY_SEARCH_QUERY;
import static app.agrishare.Constants.KEY_SERVICE;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class TractorsSearchFormFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {

    EditText field_size_edittext;
    Button submit_button;

    int LOCATION_REQUEST_CODE = 1000;
    int SERVICE_REQUEST_CODE = 1001;

    Service service;
    String location_id = "";
    String renting_for = "";
    String start_date = "";

    TractorsSearchFormFragment fragment;

    public TractorsSearchFormFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_tractors_search_form, container, false);
        fragment = this;
        initViews();
        return rootView;
    }

    private void initViews(){
        field_size_edittext = rootView.findViewById(R.id.field_size);
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
                                    renting_for = "me";
                                    ((TextView) rootView.findViewById(R.id.rent_for)).setText("Me");
                                    break;
                                case R.id.a_friend:
                                    renting_for = "a_friend";
                                    ((TextView) rootView.findViewById(R.id.rent_for)).setText("A friend");
                                    break;
                                case R.id.a_group:
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

        (rootView.findViewById(R.id.location_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    closeKeypad();
                    Intent intent = new Intent(getActivity(), SelectLocationActivity.class);
                    startActivityForResult(intent, LOCATION_REQUEST_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                }
            }
        });

        (rootView.findViewById(R.id.service_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    closeKeypad();
                    Intent intent = new Intent(getActivity(), SelectServiceActivity.class);
                    intent.putExtra(KEY_ID, 1);
                    startActivityForResult(intent, SERVICE_REQUEST_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
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

    private void clearErrors(){
        field_size_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String field_size = field_size_edittext.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(field_size)) {
            field_size_edittext.setError(getString(R.string.error_field_required));
            focusView = field_size_edittext;
            cancel = true;
        }

        if (location_id.isEmpty()) {
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
        /*    HashMap<String, String> query = new HashMap<String, String>();
            query.put("CategoryId", "1");
            query.put("ServiceId", "");
            query.put("Latitude", email);
            query.put("Longitude", phone);
            query.put("StartDate", start_date);
            query.put("Size", field_size);


            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(KEY_SEARCH_QUERY, query);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);*/

        }
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
        if (requestCode == LOCATION_REQUEST_CODE) {
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
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
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

