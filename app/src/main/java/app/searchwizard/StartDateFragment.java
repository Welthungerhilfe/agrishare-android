package app.searchwizard;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.DatePickerFragment;
import app.c2.android.DatePickerWithMinFragment;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class StartDateFragment extends BaseFragment {

    TextView start_date_textview;
    Button submit_button;

    String start_date = "";

    public StartDateFragment() {
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

    StartDateFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_start_date_form, container, false);
        fragment = this;
        initViews();
        return rootView;
    }

    private void initViews(){

        start_date_textview = rootView.findViewById(R.id.start_date);
        (rootView.findViewById(R.id.start_date_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    DatePickerWithMinFragment date = new DatePickerWithMinFragment();
                    Calendar calender = Calendar.getInstance();
                    Bundle args = new Bundle();
                    args.putInt("year", calender.get(Calendar.YEAR));
                    args.putInt("month", calender.get(Calendar.MONTH));
                    args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
                    date.setArguments(args);
                    date.setCallBack(ondate);
                    date.show(getActivity().getFragmentManager(), "DOBpickerdialog");
                }

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
        checkIfAllFieldsAreFilledIn();

    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;

            String month = (monthOfYear+1) + "";
            if ((monthOfYear+1) < 10)
                month = "0" + (monthOfYear+1);

            start_date_textview.setText(date);
            start_date_textview.setTextColor(getResources().getColor(android.R.color.black));
            start_date = year + "-" + month + "-" + dayOfMonth;
            checkIfAllFieldsAreFilledIn();
        }
    };

    private void checkIfAllFieldsAreFilledIn(){
        if (!start_date.isEmpty()){
            enableSubmitButton(submit_button);
        }
        else
            disableSubmitButton(submit_button);
    }

    private void clearErrors(){

    }

    public void checkFields() {
        closeKeypad();
        clearErrors();

        boolean cancel = false;
        View focusView = null;

        if (start_date.isEmpty()) {
            popToast(getActivity(), getActivity().getString(R.string.please_select_a_start_date));
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {

            ((SearchActivity) getActivity()).query.put("StartDate", start_date);
            MyApplication.searchQuery.StartDate = start_date;

            if (((SearchActivity) getActivity()).mPager.getCurrentItem() < ((SearchActivity) getActivity()).NUM_PAGES - 1){
                ((SearchActivity) getActivity()).mPager.setCurrentItem(((SearchActivity) getActivity()).mPager.getCurrentItem() + 1);
            }
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
