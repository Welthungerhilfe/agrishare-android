package app.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import app.agrishare.BaseActivity;
import app.agrishare.MainActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.dao.User;
import okhttp3.Response;

public class EditProfileActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener  {

    EditText phone_edittext, fname_edittext, lname_edittext, email_edittext;
    TextView dob_textview, gender_textview;
    Button submit_button;

    int gender_id = 0;
    String dob = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Edit Profile", R.drawable.button_back);
        initViews();
    }

    private void initViews(){
        phone_edittext = findViewById(R.id.phone);
        fname_edittext = findViewById(R.id.fname);
        lname_edittext = findViewById(R.id.lname);
        email_edittext = findViewById(R.id.email);
        dob_textview = findViewById(R.id.dob);
        gender_textview = findViewById(R.id.gender);
        submit_button = findViewById(R.id.submit);
        gender_textview.setTextColor(getResources().getColor(android.R.color.black));
        dob_textview.setTextColor(getResources().getColor(android.R.color.black));

        String telephone = MyApplication.currentUser.Telephone;
        phone_edittext.setText(telephone.substring(2));
        fname_edittext.setText(MyApplication.currentUser.FirstName);
        lname_edittext.setText(MyApplication.currentUser.LastName);
        email_edittext.setText(MyApplication.currentUser.EmailAddress);
        dob = MyApplication.currentUser.DateOfBirth.replace("T00:00:00", "");
        dob_textview.setText(dob);

        if (MyApplication.currentUser.GenderId == 1){
            gender_id = 1;
            gender_textview.setText("Male");
        }
        else {
            gender_id = 2;
            gender_textview.setText("Female");
        }

        (findViewById(R.id.dob_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Calendar maxDate = Calendar.getInstance();
                    maxDate.set(2012, 1, 1);
                    maxDate.add(Calendar.YEAR, -16);
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            EditProfileActivity.this,
                            now.get(Calendar.YEAR), // Initial year selection
                            now.get(Calendar.MONTH), // Initial month selection
                            now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                    );
                    dpd.setMaxDate(maxDate);
                    dpd.show(getFragmentManager(), "DOBpickerdialog");
                }

            }
        });

        (findViewById(R.id.gender_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(EditProfileActivity.this, findViewById(R.id.gender_container));
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
                                    checkIfAllFieldsAreFilledIn();
                                    break;
                                case R.id.female:
                                    gender_id = 2;
                                    gender_textview.setText("Female");
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


        setEdittextListeners(phone_edittext);
        setEdittextListeners(fname_edittext);
        setEdittextListeners(lname_edittext);
        setEdittextListeners(email_edittext);
        checkIfAllFieldsAreFilledIn();
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
        if (getPhoneNumberInEditText().length() == 10  && !fname_edittext.getText().toString().isEmpty()
                && !lname_edittext.getText().toString().isEmpty()  && !email_edittext.getText().toString().isEmpty()
                && !dob.isEmpty() && gender_id != 0){
            enableSubmitButton();
        }
        else
            disableSubmitButton();
    }

    private void disableSubmitButton(){
        submit_button.setEnabled(false);
        submit_button.setBackgroundColor(getResources().getColor(R.color.grey_for_text));
    }

    private void enableSubmitButton(){
        submit_button.setEnabled(true);
        submit_button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;

        String month = (monthOfYear+1) + "";
        if ((monthOfYear+1) < 10)
            month = "0" + (monthOfYear+1);

        if (view.getTag().equals("DOBpickerdialog")) {
            dob_textview.setText(date);
            dob = year + "-" + month + "-" + dayOfMonth;
            checkIfAllFieldsAreFilledIn();
        }

    }

    private void clearErrors(){
        phone_edittext.setError(null);
        fname_edittext.setError(null);
        lname_edittext.setError(null);
        email_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String phone = getPhoneNumberInEditText();
        String fname = fname_edittext.getText().toString();
        String lname = lname_edittext.getText().toString();
        String email = email_edittext.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(phone)) {
            phone_edittext.setError(getString(R.string.error_field_required));
            focusView = phone_edittext;
            cancel = true;
        }

        if (phone.length() < 10) {
            phone_edittext.setError(getString(R.string.phone_number_too_short));
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

        if (gender_id == 0) {
            popToast(EditProfileActivity.this, "Please select your gender");
            cancel = true;
        }

        if (dob.isEmpty()) {
            popToast(EditProfileActivity.this, "Please select your gender");
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            submit_button.setVisibility(View.GONE);
            showLoader("Saving Profile", "Please wait...");

            HashMap<String, Object> query = new HashMap<String, Object>();
            query.put("FirstName", fname);
            query.put("LastName", lname);
            query.put("EmailAddress", email);
            query.put("Telephone", phone);
            query.put("DateOfBirth", dob);
            query.put("GenderId", String.valueOf(gender_id));
            postAPI("profile/update", query, fetchResponse);
        }
    }

    private String getPhoneNumberInEditText(){
        String phone = phone_edittext.getText().toString();
        phone = "07" + phone;
        return phone;
    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("UPDATE PROFILE SUCCESS: "+ result.toString());

            hideLoader();
            MyApplication.currentUser = new User(result.optJSONObject("User"));
            showFeedbackWithButton(R.drawable.feedbacksuccess, "Done", "Your profile has been updated.");
            setCloseButton();
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR UPDATE PROFILE: " + errorMessage);
            hideLoader();
            submit_button.setVisibility(View.VISIBLE);
            popToast(EditProfileActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    public void setCloseButton(){
        ((Button) (findViewById(R.id.feedback_retry))).setText(getResources().getString(R.string.close));
        findViewById(R.id.feedback_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    goBack();
                }
            }
        });
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
