package app.bookings;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import app.agrishare.BaseActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.contact.ContactUsActivity;
import app.dao.Booking;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_BOOKING;

public class IncompleteServiceActivity extends BaseActivity {

    @BindView(R.id.message)
    public EditText message_edittext;

    @BindView(R.id.submit)
    public Button submit_button;

    Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incomplete_service);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Incomplete Service", R.drawable.button_back);
        ButterKnife.bind(this);
        booking = getIntent().getParcelableExtra(KEY_BOOKING);
        initViews();
    }

    private void initViews(){
        message_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkFields();

                    return true;
                }
                return false;
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
    }


    private void clearErrors(){
        message_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String message = message_edittext.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(message)) {
            message_edittext.setError(getString(R.string.error_field_required));
            focusView = message_edittext;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            submit_button.setVisibility(View.GONE);
            showLoader("Sending", "Please wait...");
            HashMap<String, String> query = new HashMap<String, String>();
            query.put("BookingId", String.valueOf(booking.Id));
            query.put("Message", message);
            getAPI("bookings/incomplete", query, fetchResponse);
        }

    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("INCOMPLETE SERVICE SUCCESS: "+ result.toString());
            MyApplication.closeBookingDetailActivity = true;
            showFeedbackWithButton(R.drawable.feedback_sent_400, "Done", getResources().getString(R.string.service_has_been_marked_as_incomplete));
            setCloseButton();
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR INCOMPLETE SERVICE: " + errorMessage);
            hideLoader();
            submit_button.setVisibility(View.VISIBLE);
            popToast(IncompleteServiceActivity.this, errorMessage);
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
