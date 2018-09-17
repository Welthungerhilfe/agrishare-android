package app.equipment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import app.dao.Service;
import butterknife.BindView;
import butterknife.ButterKnife;

import static app.agrishare.Constants.KEY_ENABLE_TEXT;
import static app.agrishare.Constants.KEY_SERVICE;

public class ServiceFormActivity extends BaseActivity {

    Service ploughing;

    @BindView(R.id.ploughing_switch)
    public Switch ploughing_switch;

    @BindView(R.id.hours_required_per_hectare)
    public EditText hours_required_per_hectare_edittext;

    @BindView(R.id.hire_cost)
    public EditText hire_cost_edittext;

    @BindView(R.id.fuel_cost)
    public EditText fuel_cost_edittext;

    @BindView(R.id.minimum_field_size)
    public EditText minimum_field_size_edittext;

    @BindView(R.id.distance_charge)
    public EditText distance_charge_edittext;

    @BindView(R.id.maximum_distance)
    public EditText maximum_distance_edittext;

    @BindView(R.id.submit)
    public Button submit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ploughing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Service", R.drawable.button_back);
        ButterKnife.bind(this);
        ploughing = getIntent().getParcelableExtra(KEY_SERVICE);
        initViews();
    }

    private void initViews(){
        ((TextView) findViewById(R.id.enable_text)).setText(getIntent().getStringExtra(KEY_ENABLE_TEXT));

        if (ploughing == null)
            ploughing = new Service();
        else {
            hours_required_per_hectare_edittext.setText(ploughing.hours_required_per_hectare);
            hire_cost_edittext.setText(ploughing.hire_cost);
            fuel_cost_edittext.setText(ploughing.fuel_cost);
            minimum_field_size_edittext.setText(ploughing.minimum_field_size);
            distance_charge_edittext.setText(ploughing.distance_charge);
            maximum_distance_edittext.setText(ploughing.maximum_distance);
        }

        if (ploughing.enabled) {
            ploughing_switch.setChecked(true);
            enableViews();
        }
        else {
            ploughing_switch.setChecked(false);
            disableViews();
        }

        ploughing_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked){
                    ploughing.enabled = true;
                    enableViews();
                }
                else {
                    ploughing.enabled = false;
                    disableViews();
                }
                checkIfAllFieldsAreFilledIn();
            }
        });

        maximum_distance_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        setEdittextListeners(hours_required_per_hectare_edittext);
        setEdittextListeners(hire_cost_edittext);
        setEdittextListeners(fuel_cost_edittext);
        setEdittextListeners(minimum_field_size_edittext);
        setEdittextListeners(distance_charge_edittext);
        setEdittextListeners(maximum_distance_edittext);
        if (ploughing.enabled)
            checkIfAllFieldsAreFilledIn();
        else
            disableSubmitButton(submit_button);
    }

    private void disableViews(){
        hours_required_per_hectare_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
        hire_cost_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
        fuel_cost_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
        minimum_field_size_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
        distance_charge_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
        maximum_distance_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));

        hours_required_per_hectare_edittext.setEnabled(false);
        hire_cost_edittext.setEnabled(false);
        fuel_cost_edittext.setEnabled(false);
        minimum_field_size_edittext.setEnabled(false);
        distance_charge_edittext.setEnabled(false);
        maximum_distance_edittext.setEnabled(false);
    }

    private void enableViews(){
        hours_required_per_hectare_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));
        hire_cost_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));
        fuel_cost_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));
        minimum_field_size_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));
        distance_charge_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));
        maximum_distance_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));

        hours_required_per_hectare_edittext.setEnabled(true);
        hire_cost_edittext.setEnabled(true);
        fuel_cost_edittext.setEnabled(true);
        minimum_field_size_edittext.setEnabled(true);
        distance_charge_edittext.setEnabled(true);
        maximum_distance_edittext.setEnabled(true);
    }

    private void clearErrors(){
        hours_required_per_hectare_edittext.setError(null);
        hire_cost_edittext.setError(null);
        fuel_cost_edittext.setError(null);
        minimum_field_size_edittext.setError(null);
        distance_charge_edittext.setError(null);
        maximum_distance_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String hours_required_per_hectare = hours_required_per_hectare_edittext.getText().toString();
        String hire_cost = hire_cost_edittext.getText().toString();
        String fuel_cost = fuel_cost_edittext.getText().toString();
        String minimum_field_size = minimum_field_size_edittext.getText().toString();
        String distance_charge = distance_charge_edittext.getText().toString();
        String maximum_distance = maximum_distance_edittext.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (ploughing.enabled) {

            if (TextUtils.isEmpty(hours_required_per_hectare)) {
                hours_required_per_hectare_edittext.setError(getString(R.string.error_field_required));
                focusView = hours_required_per_hectare_edittext;
                cancel = true;
            }

            if (TextUtils.isEmpty(hire_cost)) {
                hire_cost_edittext.setError(getString(R.string.error_field_required));
                focusView = hire_cost_edittext;
                cancel = true;
            }

            if (TextUtils.isEmpty(fuel_cost)) {
                fuel_cost_edittext.setError(getString(R.string.error_field_required));
                focusView = fuel_cost_edittext;
                cancel = true;
            }

            if (TextUtils.isEmpty(minimum_field_size)) {
                minimum_field_size_edittext.setError(getString(R.string.error_field_required));
                focusView = minimum_field_size_edittext;
                cancel = true;
            }

            if (TextUtils.isEmpty(distance_charge)) {
                distance_charge_edittext.setError(getString(R.string.error_field_required));
                focusView = distance_charge_edittext;
                cancel = true;
            }

            if (TextUtils.isEmpty(maximum_distance)) {
                maximum_distance_edittext.setError(getString(R.string.error_field_required));
                focusView = maximum_distance_edittext;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            ploughing.enabled = ploughing_switch.isChecked();
            ploughing.hours_required_per_hectare = hours_required_per_hectare;
            ploughing.hire_cost = hire_cost;
            ploughing.fuel_cost = fuel_cost;
            ploughing.minimum_field_size = minimum_field_size;
            ploughing.distance_charge = distance_charge;
            ploughing.maximum_distance = maximum_distance;
            returnResult();
        }

    }

    public void returnResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(KEY_SERVICE, ploughing);
        setResult(Activity.RESULT_OK, returnIntent);
        closeKeypad();
        goBack();
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
        if (ploughing.enabled) {
            if (!hours_required_per_hectare_edittext.getText().toString().isEmpty() && !hire_cost_edittext.getText().toString().isEmpty()
                    && !fuel_cost_edittext.getText().toString().isEmpty() && !minimum_field_size_edittext.getText().toString().isEmpty()
                    && !distance_charge_edittext.getText().toString().isEmpty() && !maximum_distance_edittext.getText().toString().isEmpty()) {
                enableSubmitButton(submit_button);
            } else
                disableSubmitButton(submit_button);
        }
        else {
            enableSubmitButton(submit_button);
        }
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
