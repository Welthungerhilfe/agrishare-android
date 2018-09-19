package app.account;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
import app.dao.User;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_IS_LOOKING;
import static app.agrishare.Constants.PREFS_TOKEN;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class SMSVerificationFragment extends BaseFragment {

    EditText code_edittext;
    Button submit_button;

    boolean isLooking = true;

    public SMSVerificationFragment() {
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

    SMSVerificationFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sms_verification, container, false);
        fragment = this;
        initViews();
        return rootView;
    }

    private void initViews(){
        code_edittext = rootView.findViewById(R.id.code);
        code_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkFields();

                    return true;
                }
                return false;
            }
        });

        submit_button = rootView.findViewById(R.id.submit);
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
            query.put("UserId", String.valueOf(MyApplication.currentUser.Id));
            query.put("Code", code);
            getAPI("register/code/verify", query, fetchResponse);
        }

    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("SMS VERIFICATION SUCCESS: "+ result.toString());

            hideLoader();
            MyApplication.currentUser = new User(result.optJSONObject("User"));

            MyApplication.token = MyApplication.currentUser.AuthToken;
            SharedPreferences.Editor editor = MyApplication.prefs.edit();
            editor.putString(PREFS_TOKEN, MyApplication.token);
            editor.commit();

            showFeedbackWithButton(R.drawable.welcome_400, "Welcome " + MyApplication.currentUser.FirstName, "Registration is now complete and you can now proceed to your dashboard.");
            setProceedButton();
            setInterestsView();
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR SMS VERIFICATION: " + errorMessage);
            hideLoader();
            submit_button.setVisibility(View.VISIBLE);
            if (getActivity() != null)
                popToast(getActivity(), errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    public void setProceedButton(){
        ((Button) rootView.findViewById(R.id.feedback_retry)).setText(getActivity().getResources().getString(R.string.proceed));
        rootView.findViewById(R.id.feedback_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra(KEY_IS_LOOKING, isLooking);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                        getActivity().finish();
                    }
                }
            }
        });
    }

    private void setInterestsView(){
        ((TextView) rootView.findViewById(R.id.feedback_form_text)).setText(getResources().getString(R.string.i_am_looking));
        rootView.findViewById(R.id.feedback_form_field_container).setVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.feedback_form_field_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getActivity(), rootView.findViewById(R.id.feedback_form_text));
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_language_options);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.looking:
                                    isLooking = true;
                                    ((TextView) rootView.findViewById(R.id.feedback_form_text)).setText(getResources().getString(R.string.i_am_looking));
                                    break;
                                case R.id.selling:
                                    isLooking = false;
                                    ((TextView) rootView.findViewById(R.id.feedback_form_text)).setText(getResources().getString(R.string.i_am_selling));
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
