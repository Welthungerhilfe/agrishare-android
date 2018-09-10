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
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_TELEPHONE;
import static app.agrishare.Constants.KEY_USER;
import static app.agrishare.Constants.PREFS_TOKEN;

public class LoginActivity extends BaseActivity {

    MiniUser miniUser;
    String telephone = "";

    @BindView(R.id.welcome)
    public TextView welcome_textview;

    @BindView(R.id.pin)
    public EditText pin_edittext;

    @BindView(R.id.submit)
    public Button submit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.page_bg_grey));
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        setNavBar("", R.drawable.button_back);
        miniUser = getIntent().getParcelableExtra(KEY_USER);
        telephone = getIntent().getStringExtra(KEY_TELEPHONE);
        initViews();
    }

    private void initViews() {
        welcome_textview.setText("Welcome " + miniUser.FirstName);
        pin_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkFields();

                    return true;
                }
                return false;
            }
        });

        (findViewById(R.id.forgot)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(LoginActivity.this, ForgotPinActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
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
    }

    private void clearErrors(){
        pin_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String pin = pin_edittext.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(pin)) {
            pin_edittext.setError(getString(R.string.error_field_required));
            focusView = pin_edittext;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            submit_button.setVisibility(View.GONE);
            showLoader("Logging in", "Please wait...");
            HashMap<String, String> query = new HashMap<String, String>();
            query.put("Telephone", telephone);
            query.put("PIN", pin);
            getAPI("login", query, fetchResponse);
        }

    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("LOGIN SUCCESS: "+ result.toString());

            hideLoader();
            MyApplication.currentUser = new User(result.optJSONObject("Auth").optJSONObject("User"));

            if (MyApplication.currentUser.AuthToken != null && !MyApplication.currentUser.AuthToken.isEmpty() && !MyApplication.currentUser.AuthToken.equals("null")) {
                MyApplication.token = MyApplication.currentUser.AuthToken;
                SharedPreferences.Editor editor = MyApplication.prefs.edit();
                editor.putString(PREFS_TOKEN, MyApplication.token);
                editor.commit();
            }

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            finish();

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR LOGIN: " + errorMessage);
            hideLoader();
            submit_button.setVisibility(View.VISIBLE);
            popToast(LoginActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

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
