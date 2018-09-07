package app.account;

import android.app.Activity;
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

import java.util.Calendar;
import java.util.HashMap;

import app.agrishare.BaseFragment;
import app.agrishare.R;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class SMSVerificationFragment extends BaseFragment {

    EditText code_edittext;
    Button submit_button;

    int gender_id = 0;
    String dob = "";

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
