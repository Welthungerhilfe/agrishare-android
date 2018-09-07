package app.account;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import app.agrishare.BaseFragment;
import app.agrishare.R;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class RegFormFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {

    EditText phone_edittext, fname_edittext, lname_edittext, email_edittext, pin_edittext;
    TextView dob_textview, gender_textview;
    Button submit_button;

    int gender_id = 0;
    String dob = "";

    public RegFormFragment() {
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

    RegFormFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_reg_form, container, false);
        fragment = this;
        initViews();
        return rootView;
    }

    private void initViews(){
        phone_edittext = rootView.findViewById(R.id.phone);
        fname_edittext = rootView.findViewById(R.id.fname);
        lname_edittext = rootView.findViewById(R.id.lname);
        email_edittext = rootView.findViewById(R.id.email);
        pin_edittext = rootView.findViewById(R.id.pin);
        dob_textview = rootView.findViewById(R.id.dob);
        gender_textview = rootView.findViewById(R.id.gender);
        submit_button = rootView.findViewById(R.id.submit);

        (rootView.findViewById(R.id.dob_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Calendar maxDate = Calendar.getInstance();
                    maxDate.set(2012, 1, 1);
                    maxDate.add(Calendar.YEAR, -16);
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            fragment,
                            now.get(Calendar.YEAR), // Initial year selection
                            now.get(Calendar.MONTH), // Initial month selection
                            now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                    );
                    dpd.setMaxDate(maxDate);
                    dpd.show(getActivity().getFragmentManager(), "DOBpickerdialog");
                }

            }
        });

        (rootView.findViewById(R.id.gender_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getActivity(), rootView.findViewById(R.id.gender_container));
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_gender_options);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.male:
                                    gender_id = 1;
                                    gender_textview.setText("Male");
                                    break;
                                case R.id.female:
                                    gender_id = 2;
                                    gender_textview.setText("Female");
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

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    checkFields();
                }

            }
        });

        (rootView.findViewById(R.id.close_icon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    getActivity().onBackPressed();
                }
            }
        });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;

        String month = (monthOfYear+1) + "";
        if ((monthOfYear+1) < 10)
            month = "0" + (monthOfYear+1);

        if (view.getTag().equals("DOBpickerdialog")) {
            dob_textview.setText(date);
            dob = year + "-" + month + "-" + dayOfMonth +"T23:59:59";
        }

    }

    private void clearErrors(){
        phone_edittext.setError(null);
        fname_edittext.setError(null);
        lname_edittext.setError(null);
        email_edittext.setError(null);
        pin_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String phone = phone_edittext.getText().toString();
        String fname = fname_edittext.getText().toString();
        String lname = lname_edittext.getText().toString();
        String email = email_edittext.getText().toString();
        String pin = pin_edittext.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(phone)) {
            phone_edittext.setError(getString(R.string.error_field_required));
            focusView = phone_edittext;
            cancel = true;
        }

        if (TextUtils.isEmpty(fname)) {
            fname_edittext.setError(getString(R.string.error_field_required));
            focusView = fname_edittext;
            cancel = true;
        }

        if (TextUtils.isEmpty(lname)) {
            lname_edittext.setError(getString(R.string.error_field_required));
            focusView = lname_edittext;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            email_edittext.setError(getString(R.string.error_field_required));
            focusView = email_edittext;
            cancel = true;
        }
        else if (!email.contains("@")){
            email_edittext.setError(getString(R.string.error_invalid_email));
            focusView = email_edittext;
            cancel = true;
        }

        if (TextUtils.isEmpty(pin)) {
            pin_edittext.setError(getString(R.string.error_field_required));
            focusView = pin_edittext;
            cancel = true;
        }

        if (gender_id == 0) {
            popToast(getActivity(), "Please select your gender");
            cancel = true;
        }

        if (dob.isEmpty()) {
            popToast(getActivity(), "Please select your gender");
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {

            /*((RegisterActivity) getActivity()).firstname = fname;
            ((RegisterActivity) getActivity()).lastname = lname;
            ((RegisterActivity) getActivity()).emailaddress = email;

            if (((RegisterActivity) getActivity()).mPager.getCurrentItem() < ((RegisterActivity) getActivity()).NUM_PAGES - 1){
                ((RegisterActivity) getActivity()).mPager.setCurrentItem(((RegisterActivity) getActivity()).mPager.getCurrentItem() + 1);
            }*/

            // submit_button.setVisibility(View.GONE);
            //  showLoader("Verifying", "Please wait...");

           /* HashMap<String, String> query = new HashMap<String, String>();
            query.put(KEY_USERNAME, username);
            query.put(KEY_PASSWORD, password);
            postAPI(POST_LOGIN, query, fetchResponse, null, null, null);*/
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
}
