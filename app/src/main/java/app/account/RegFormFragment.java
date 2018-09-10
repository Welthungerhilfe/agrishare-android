package app.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import app.agrishare.BaseFragment;
import app.agrishare.MainActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.dao.MiniUser;
import app.dao.User;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_TELEPHONE;
import static app.agrishare.Constants.KEY_USER;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class RegFormFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {

    EditText phone_edittext, fname_edittext, lname_edittext, email_edittext, pin_edittext;
    TextView dob_textview, gender_textview, i_have_read_textview;
    Button submit_button;
    CheckBox terms_checkBox;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_reg_form, container, false);
        fragment = this;
        initViews();
        return rootView;
    }

    private void initViews(){
        phone_edittext = rootView.findViewById(R.id.phone);
        if (getActivity() != null) {
            phone_edittext.setText(((RegisterActivity) getActivity()).telephone);
            phone_edittext.setEnabled(false);
        }
        fname_edittext = rootView.findViewById(R.id.fname);
        lname_edittext = rootView.findViewById(R.id.lname);
        email_edittext = rootView.findViewById(R.id.email);
        pin_edittext = rootView.findViewById(R.id.pin);
        dob_textview = rootView.findViewById(R.id.dob);
        gender_textview = rootView.findViewById(R.id.gender);
        submit_button = rootView.findViewById(R.id.submit);
        i_have_read_textview = rootView.findViewById(R.id.i_have_read_and_agreed);
        terms_checkBox = rootView.findViewById(R.id.terms_checkbox);
        terms_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    checkIfAllFieldsAreFilledIn();
               }
           }
        );

        i_have_read_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(getActivity(), PrivacyPolicyActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
                }
            }
        });

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
                                    gender_textview.setTextColor(getResources().getColor(android.R.color.black));
                                    checkIfAllFieldsAreFilledIn();
                                    break;
                                case R.id.female:
                                    gender_id = 2;
                                    gender_textview.setText("Female");
                                    gender_textview.setTextColor(getResources().getColor(android.R.color.black));
                                    checkIfAllFieldsAreFilledIn();
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


        disableSubmitButton();
        setEdittextListeners(phone_edittext);
        setEdittextListeners(fname_edittext);
        setEdittextListeners(lname_edittext);
        setEdittextListeners(email_edittext);
        setEdittextListeners(pin_edittext);
    }

    private void setEdittextListeners(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkIfAllFieldsAreFilledIn();
            }
        });
    }

    private void checkIfAllFieldsAreFilledIn(){
        if (terms_checkBox.isChecked() && !phone_edittext.getText().toString().isEmpty()  && !fname_edittext.getText().toString().isEmpty()
                && !lname_edittext.getText().toString().isEmpty()  && !email_edittext.getText().toString().isEmpty()  && !pin_edittext.getText().toString().isEmpty()
                && !dob.isEmpty() && gender_id != 0){
            enableSubmitButton();
        }
        else
            disableSubmitButton();
    }

    private void disableSubmitButton(){
        submit_button.setEnabled(false);
        submit_button.setBackgroundColor(getActivity().getResources().getColor(R.color.grey_for_text));
    }

    private void enableSubmitButton(){
        submit_button.setEnabled(true);
        submit_button.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;

        String month = (monthOfYear+1) + "";
        if ((monthOfYear+1) < 10)
            month = "0" + (monthOfYear+1);

        if (view.getTag().equals("DOBpickerdialog")) {
            dob_textview.setText(date);
            dob_textview.setTextColor(getResources().getColor(android.R.color.black));
            dob = year + "-" + month + "-" + dayOfMonth;
            checkIfAllFieldsAreFilledIn();
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
            submit_button.setVisibility(View.GONE);
            showLoader("Creating account", "Please wait...");

            HashMap<String, String> query = new HashMap<String, String>();
            query.put("FirstName", fname);
            query.put("LastName", lname);
            query.put("EmailAddress", email);
            query.put("Telephone", phone);
            query.put("PIN", pin);
            query.put("DateOfBirth", dob);
            query.put("GenderId", String.valueOf(gender_id));
            postAPI("register", query, fetchResponse);
        }
    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("REGISTER SUCCESS: "+ result.toString());

            hideLoader();
            MyApplication.currentUser = new User(result.optJSONObject("User"));

            if (((RegisterActivity) getActivity()).mPager.getCurrentItem() < ((RegisterActivity) getActivity()).NUM_PAGES - 1){
                ((RegisterActivity) getActivity()).mPager.setCurrentItem(((RegisterActivity) getActivity()).mPager.getCurrentItem() + 1);
            }
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR REGISTER: " + errorMessage);
            hideLoader();
            submit_button.setVisibility(View.VISIBLE);
            if (getActivity() != null)
                popToast(getActivity(), errorMessage);
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
}
