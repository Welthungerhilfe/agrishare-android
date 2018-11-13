package app.account;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import app.agrishare.BaseActivity;
import app.agrishare.MainActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.dao.MiniUser;
import app.dao.User;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_IS_LOOKING;
import static app.agrishare.Constants.KEY_USER;
import static app.agrishare.Constants.KEY_UserId;
import static app.agrishare.Constants.PREFS_TOKEN;

public class SMSVerificationActivity extends BaseActivity {

    EditText code_edittext;
    Button submit_button;

    MiniUser miniUser;
    boolean isLooking = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsverification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (MyApplication.token.isEmpty()) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.page_bg_grey));
            setSupportActionBar(toolbar);
            setNavBar("", R.drawable.back_button);
        }
        else {
            setSupportActionBar(toolbar);
            setNavBar("Verification", R.drawable.button_back);
        }
        miniUser = getIntent().getParcelableExtra(KEY_USER);
        initViews();
    }

    private void initViews(){
        code_edittext = findViewById(R.id.code);
        code_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkFields();

                    return true;
                }
                return false;
            }
        });

        submit_button = findViewById(R.id.submit);
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
        code_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String code = code_edittext.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(code)) {
            code_edittext.setError(getString(R.string.error_field_required));
            focusView = code_edittext;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {

            submit_button.setVisibility(View.GONE);
            showLoader("Verifying", "Please wait...");

            HashMap<String, String> query = new HashMap<String, String>();
            query.put("UserId", String.valueOf(miniUser.Id));
            query.put("Code", code);
            getAPI("register/code/verify", query, fetchResponse);
        }

    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("SMS VERIFICATION SUCCESS: "+ result.toString());

            hideLoader();
            if (MyApplication.token.isEmpty()) {
                MyApplication.currentUser = new User(result.optJSONObject("User"));

                MyApplication.token = MyApplication.currentUser.AuthToken;
                SharedPreferences.Editor editor = MyApplication.prefs.edit();
                editor.putString(PREFS_TOKEN, MyApplication.token);
                editor.commit();

                showFeedbackWithButton(R.drawable.welcome_400, "Welcome " + MyApplication.currentUser.FirstName, "Registration is now complete and you can now proceed to your dashboard.");
                setProceedButton();
                setInterestsView();
            }
            else {
                MyApplication.currentUser = new User(result.optJSONObject("User"));
                showFeedbackWithButton(R.drawable.feedbacksuccess, getResources().getString(R.string.done), "Your number has been successfully updated");
                setCloseButton();
            }
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR SMS VERIFICATION: " + errorMessage);
            hideLoader();
            submit_button.setVisibility(View.VISIBLE);
            popToast(SMSVerificationActivity.this, errorMessage);
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

    public void setProceedButton(){
        ((Button) findViewById(R.id.feedback_retry)).setText(getResources().getString(R.string.proceed));
        findViewById(R.id.feedback_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(SMSVerificationActivity.this, MainActivity.class);
                    intent.putExtra(KEY_IS_LOOKING, isLooking);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                    finish();
                }
            }
        });
    }

    private void setInterestsView(){
        ((TextView) findViewById(R.id.feedback_form_text)).setText(getResources().getString(R.string.i_am_looking));
        findViewById(R.id.feedback_form_field_container).setVisibility(View.VISIBLE);
        (findViewById(R.id.feedback_form_field_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(SMSVerificationActivity.this, findViewById(R.id.feedback_form_text));
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_interests_options);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.looking:
                                    isLooking = true;
                                    ((TextView) findViewById(R.id.feedback_form_text)).setText(getResources().getString(R.string.i_am_looking));
                                    break;
                                case R.id.selling:
                                    isLooking = false;
                                    ((TextView) findViewById(R.id.feedback_form_text)).setText(getResources().getString(R.string.i_am_offering));
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
