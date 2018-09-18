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
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import app.dao.EquipmentService;
import butterknife.BindView;
import butterknife.ButterKnife;

import static app.agrishare.Constants.KEY_ENABLE_TEXT;
import static app.agrishare.Constants.KEY_EQUIPMENT_SERVICE;

public class ServiceFormActivity extends BaseActivity {

    int mobile = 0;

    EquipmentService service;

    @BindView(R.id.dollar_per_unit)
    public TextView dollar_per_unit_textview;

    @BindView(R.id.unit)
    public TextView unit_textview;

    @BindView(R.id.ploughing_switch)
    public Switch ploughing_switch;

    @BindView(R.id.mobile_container)
    public RelativeLayout mobile_container;

    @BindView(R.id.total_volume)
    public EditText total_volume_edittext;

    @BindView(R.id.hours_required_per_hectare)
    public EditText hours_required_per_hectare_edittext;

    @BindView(R.id.hire_cost)
    public EditText hire_cost_edittext;

    @BindView(R.id.fuel_cost)
    public EditText fuel_cost_edittext;

    @BindView(R.id.minimum_quantity)
    public EditText minimum_quantity_edittext;

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
        ButterKnife.bind(this);
        service = getIntent().getParcelableExtra(KEY_EQUIPMENT_SERVICE);
        setNavBar(service.title, R.drawable.button_back);
        initViews();
    }

    private void initViews(){
        ((TextView) findViewById(R.id.enable_text)).setText(getIntent().getStringExtra(KEY_ENABLE_TEXT));

        if (service.parent_category_id == 1){
            unit_textview.setText(getResources().getString(R.string.ha));
            dollar_per_unit_textview.setText(getResources().getString(R.string.dollar_per_ha));

            mobile_container.setVisibility(View.GONE);
            (findViewById(R.id.total_volume_container)).setVisibility(View.GONE);

            setEdittextListeners(hours_required_per_hectare_edittext);
            setEdittextListeners(hire_cost_edittext);
            setEdittextListeners(fuel_cost_edittext);
            setEdittextListeners(minimum_quantity_edittext);
            setEdittextListeners(distance_charge_edittext);
            setEdittextListeners(maximum_distance_edittext);
        }
        else if (service.parent_category_id == 2){
            unit_textview.setText(getResources().getString(R.string.bags));
            dollar_per_unit_textview.setText(getResources().getString(R.string.dollar_per_bag));

            mobile_container.setVisibility(View.GONE);
            (findViewById(R.id.fuel_container)).setVisibility(View.GONE);
            (findViewById(R.id.total_volume_container)).setVisibility(View.VISIBLE);


            setEdittextListeners(total_volume_edittext);
            setEdittextListeners(hours_required_per_hectare_edittext);
            setEdittextListeners(hire_cost_edittext);
            setEdittextListeners(minimum_quantity_edittext);
            setEdittextListeners(distance_charge_edittext);
            setEdittextListeners(maximum_distance_edittext);
        }
        else if (service.parent_category_id == 3){
            unit_textview.setText(getResources().getString(R.string.bags));
            dollar_per_unit_textview.setText(getResources().getString(R.string.dollar_per_bag));

            mobile_container.setVisibility(View.VISIBLE);
            (findViewById(R.id.fuel_container)).setVisibility(View.GONE);
            (findViewById(R.id.total_volume_container)).setVisibility(View.GONE);

            setEdittextListeners(hours_required_per_hectare_edittext);
            setEdittextListeners(hire_cost_edittext);
            setEdittextListeners(minimum_quantity_edittext);
            setEdittextListeners(distance_charge_edittext);
            setEdittextListeners(maximum_distance_edittext);
        }


        if (!service.hours_required_per_hectare.isEmpty()) {
            hours_required_per_hectare_edittext.setText(service.hours_required_per_hectare);
            hire_cost_edittext.setText(service.hire_cost);
            fuel_cost_edittext.setText(service.fuel_cost);
            minimum_quantity_edittext.setText(service.minimum_field_size);
            distance_charge_edittext.setText(service.distance_charge);
            maximum_distance_edittext.setText(service.maximum_distance);
        }

        if (service.enabled) {
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
                    service.enabled = true;
                    enableViews();
                }
                else {
                    service.enabled = false;
                    disableViews();
                }
                checkIfAllFieldsAreFilledIn();
            }
        });

        (findViewById(R.id.mobile_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(ServiceFormActivity.this, findViewById(R.id.mobile_container));
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_mobile_options);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.yes:
                                    mobile = 1;
                                    ((TextView) findViewById(R.id.mobile)).setText(getResources().getString(R.string.yes_is_mobile));
                                    checkIfAllFieldsAreFilledIn();
                                    break;
                                case R.id.no:
                                    mobile = 2;
                                    ((TextView) findViewById(R.id.mobile)).setText(getResources().getString(R.string.no_it_is_not_mobile));
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

        if (service.enabled)
            checkIfAllFieldsAreFilledIn();
        else
            disableSubmitButton(submit_button);
    }

    private void disableViews(){
        (findViewById(R.id.mobile_container)).setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
        total_volume_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
        hours_required_per_hectare_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
        hire_cost_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
        fuel_cost_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
        minimum_quantity_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
        distance_charge_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
        maximum_distance_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));

        total_volume_edittext.setEnabled(false);
        hours_required_per_hectare_edittext.setEnabled(false);
        hire_cost_edittext.setEnabled(false);
        fuel_cost_edittext.setEnabled(false);
        minimum_quantity_edittext.setEnabled(false);
        distance_charge_edittext.setEnabled(false);
        maximum_distance_edittext.setEnabled(false);
    }

    private void enableViews(){
        (findViewById(R.id.mobile_container)).setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));
        total_volume_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));
        hours_required_per_hectare_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));
        hire_cost_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));
        fuel_cost_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));
        minimum_quantity_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));
        distance_charge_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));
        maximum_distance_edittext.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));

        total_volume_edittext.setEnabled(true);
        hours_required_per_hectare_edittext.setEnabled(true);
        hire_cost_edittext.setEnabled(true);
        fuel_cost_edittext.setEnabled(true);
        minimum_quantity_edittext.setEnabled(true);
        distance_charge_edittext.setEnabled(true);
        maximum_distance_edittext.setEnabled(true);
    }

    private void clearErrors(){
        total_volume_edittext.setError(null);
        hours_required_per_hectare_edittext.setError(null);
        hire_cost_edittext.setError(null);
        fuel_cost_edittext.setError(null);
        minimum_quantity_edittext.setError(null);
        distance_charge_edittext.setError(null);
        maximum_distance_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String hours_required_per_hectare = hours_required_per_hectare_edittext.getText().toString();
        String hire_cost = hire_cost_edittext.getText().toString();
        String fuel_cost = fuel_cost_edittext.getText().toString();
        String minimum_field_size = minimum_quantity_edittext.getText().toString();
        String distance_charge = distance_charge_edittext.getText().toString();
        String maximum_distance = maximum_distance_edittext.getText().toString();
        String total_volume = total_volume_edittext.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (service.enabled) {

            if (service.parent_category_id == 1){
                if (TextUtils.isEmpty(fuel_cost)) {
                    fuel_cost_edittext.setError(getString(R.string.error_field_required));
                    focusView = fuel_cost_edittext;
                    cancel = true;
                }
            }
            else if (service.parent_category_id == 2){
                if (TextUtils.isEmpty(total_volume)) {
                    total_volume_edittext.setError(getString(R.string.error_field_required));
                    focusView = total_volume_edittext;
                    cancel = true;
                }
            }
            else if (service.parent_category_id == 3){
                if (mobile == 0) {
                    popToast(ServiceFormActivity.this, "Please specify if the Service is mobile or not.");
                }
            }

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

            if (TextUtils.isEmpty(minimum_field_size)) {
                minimum_quantity_edittext.setError(getString(R.string.error_field_required));
                focusView = minimum_quantity_edittext;
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
            service.enabled = ploughing_switch.isChecked();
            service.hours_required_per_hectare = hours_required_per_hectare;
            service.hire_cost = hire_cost;
            service.fuel_cost = fuel_cost;
            service.minimum_field_size = minimum_field_size;
            service.distance_charge = distance_charge;
            service.maximum_distance = maximum_distance;
            service.total_volume_in_tonne = total_volume;
            service.mobile = mobile == 1;
            returnResult();
        }

    }

    public void returnResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(KEY_EQUIPMENT_SERVICE, service);
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
        if (service.enabled) {
            if (service.parent_category_id == 1) {
                if (!hours_required_per_hectare_edittext.getText().toString().isEmpty() && !hire_cost_edittext.getText().toString().isEmpty()
                        && !fuel_cost_edittext.getText().toString().isEmpty() && !minimum_quantity_edittext.getText().toString().isEmpty()
                        && !distance_charge_edittext.getText().toString().isEmpty() && !maximum_distance_edittext.getText().toString().isEmpty()) {
                    enableSubmitButton(submit_button);
                } else
                    disableSubmitButton(submit_button);
            }
            else if (service.parent_category_id == 2) {
                if (!total_volume_edittext.getText().toString().isEmpty() && !hours_required_per_hectare_edittext.getText().toString().isEmpty() && !hire_cost_edittext.getText().toString().isEmpty()
                        && !minimum_quantity_edittext.getText().toString().isEmpty()
                        && !distance_charge_edittext.getText().toString().isEmpty() && !maximum_distance_edittext.getText().toString().isEmpty()) {
                    enableSubmitButton(submit_button);
                } else
                    disableSubmitButton(submit_button);
            }
            else if (service.parent_category_id == 3) {
                if (mobile != 0 && !hours_required_per_hectare_edittext.getText().toString().isEmpty() && !hire_cost_edittext.getText().toString().isEmpty()
                        && !minimum_quantity_edittext.getText().toString().isEmpty()
                        && !distance_charge_edittext.getText().toString().isEmpty() && !maximum_distance_edittext.getText().toString().isEmpty()) {
                    enableSubmitButton(submit_button);
                } else
                    disableSubmitButton(submit_button);
            }
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