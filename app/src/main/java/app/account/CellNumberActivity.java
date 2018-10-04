package app.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.dao.MiniUser;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_TELEPHONE;
import static app.agrishare.Constants.KEY_USER;

public class CellNumberActivity extends BaseActivity {

    @BindView(R.id.phone)
    public EditText phone_edittext;

    @BindView(R.id.submit)
    public Button submit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_number);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.page_bg_grey));
        setSupportActionBar(toolbar);
        setNavBar("", R.drawable.grey_close);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        phone_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        phone_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String phone = getPhoneNumberInEditText();

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

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            submit_button.setVisibility(View.GONE);
            showLoader("Checking", "Please wait...");
            HashMap<String, String> query = new HashMap<String, String>();
            query.put("Telephone", phone);
            getAPI("register/telephone/lookup", query, fetchResponse);

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
            Log("TELEPHONE LOOK UP SUCCESS: "+ result.toString());

            hideLoader();
            MiniUser miniUser = new MiniUser(result.optJSONObject("User"));

            Intent intent = new Intent(CellNumberActivity.this, LoginActivity.class);
            intent.putExtra(KEY_USER, miniUser);
            intent.putExtra(KEY_TELEPHONE, getPhoneNumberInEditText());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            finish();

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR TELEPHONE LOOK UP: " + errorMessage);
            hideLoader();
            submit_button.setVisibility(View.VISIBLE);
            if (errorMessage.equals("Please register to continue")){
                Intent intent = new Intent(CellNumberActivity.this, RegisterActivity.class);
                intent.putExtra(KEY_TELEPHONE, getPhoneNumberInEditText());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                finish();
            }
            else {
                popToast(CellNumberActivity.this, errorMessage);
            }
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
                close();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        close();
    }

}
