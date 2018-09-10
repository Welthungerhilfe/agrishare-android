package app.account;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import app.agrishare.BaseActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.dao.User;
import okhttp3.Response;

public class NotificationPreferencesActivity extends BaseActivity {

    Switch sms_switch, push_notification_switch, email_switch;
    Button submit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Notifications Preferences", R.drawable.button_back);
        initViews();
    }

    private void initViews(){
        sms_switch = findViewById(R.id.sms_switch);
        push_notification_switch = findViewById(R.id.push_notifications_switch);
        email_switch = findViewById(R.id.email_switch);
        submit_button = findViewById(R.id.submit);

        try {
            JSONObject prefsObject = new JSONObject(MyApplication.currentUser.NotificationPreferences);
            if (prefsObject.optBoolean("SMS"))
                sms_switch.setChecked(true);
            else
                sms_switch.setChecked(false);

            if (prefsObject.optBoolean("PushNotifications"))
                push_notification_switch.setChecked(true);
            else
                push_notification_switch.setChecked(false);

            if (prefsObject.optBoolean("Email"))
                email_switch.setChecked(true);
            else
                email_switch.setChecked(false);
        } catch (JSONException ex){
            Log.d("JSONException", ex.getMessage());
        }

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    submit_button.setVisibility(View.GONE);
                    showLoader("Updating Preferences", "Please wait...");
                    HashMap<String, String> query = new HashMap<String, String>();
                    query.put("SMS", sms_switch.isChecked() + "");
                    query.put("PushNotifications", push_notification_switch.isChecked() + "");
                    query.put("Email", email_switch.isChecked() + "");
                    getAPI("profile/preferences/notifications/update", query, fetchResponse);
                }

            }
        });

    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("UPDATE NOTIFICATIONS SUCCESS: "+ result.toString());

            hideLoader();
            MyApplication.currentUser = new User(result.optJSONObject("User"));
            showFeedbackWithButton(R.drawable.feedbacksuccess, "Done", "Your notification preferences have been updated.");
            setCloseButton();
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR UPDATE NOTIFICATIONS: " + errorMessage);
            hideLoader();
            submit_button.setVisibility(View.VISIBLE);
            popToast(NotificationPreferencesActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    public void setCloseButton(){
        ((Button) (findViewById(R.id.feedback_retry))).setText("CLOSE");
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
