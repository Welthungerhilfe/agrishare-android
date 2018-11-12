package app.searchwizard;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;

import static app.agrishare.Constants.TAB_FOR;
import static app.agrishare.Constants.TAB_QUANTITY;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class QuantityFragment extends BaseFragment {

    EditText size_edittext;
    Button submit_button;


    public QuantityFragment() {
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

    QuantityFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_quantity_form, container, false);
        fragment = this;
        initViews();
        return rootView;
    }

    private void initViews(){
        size_edittext = rootView.findViewById(R.id.size);
        size_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkFields();

                    return true;
                }
                return false;
            }
        });

        if (((SearchActivity) getActivity()).catergoryId == 1) {
            ((TextView) rootView.findViewById(R.id.description)).setText(getActivity().getResources().getString(R.string.what_is_the_field_size_in_hectares));
            size_edittext.setHint(getActivity().getResources().getString(R.string.field_size));
        }
        else if (((SearchActivity) getActivity()).catergoryId == 2) {
            ((TextView) rootView.findViewById(R.id.description)).setText(getActivity().getResources().getString(R.string.how_heavy_is_your_load_in_tonnes));
            size_edittext.setHint(getActivity().getResources().getString(R.string.weight));
        }
        else if (((SearchActivity) getActivity()).catergoryId == 3) {
            ((TextView) rootView.findViewById(R.id.description)).setText(getActivity().getResources().getString(R.string.how_many_bags_do_you_want_to_process));
            size_edittext.setHint(getActivity().getResources().getString(R.string.number_of_bags));
        }

        submit_button = rootView.findViewById(R.id.submit);
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
        size_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String quantity = size_edittext.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(quantity)) {
            size_edittext.setError(getString(R.string.error_field_required));
            focusView = size_edittext;
            cancel = true;
        }

        if (((SearchActivity) getActivity()).catergoryId == 1) {
            if (!quantity.isEmpty()) {

                Double field_size = Double.valueOf(quantity);
                if (field_size < 0.5) {
                    size_edittext.setError(getString(R.string.error_minimum_field_size_required_is_0_5ha));
                    focusView = size_edittext;
                    cancel = true;
                }
            }
        }
        else if (((SearchActivity) getActivity()).catergoryId == 2) {
            if (!quantity.isEmpty()) {

                Double field_size = Double.valueOf(quantity);
                if (field_size == 0) {
                    size_edittext.setError(getString(R.string.error_total_volume_has_to_be_greater_than_0));
                    focusView = size_edittext;
                    cancel = true;
                }
            }
        }

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {

            if (((SearchActivity) getActivity()).catergoryId == 2) {
                ((SearchActivity) getActivity()).query.put("TotalVolume", quantity);
            }
            else {
                ((SearchActivity) getActivity()).query.put("Size", quantity);
            }

            MyApplication.searchQuery.Size = Double.parseDouble(quantity);

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

        if (((SearchActivity) getActivity()).tabsStackList.contains(TAB_QUANTITY))
            ((SearchActivity) getActivity()).tabsStackList.remove(TAB_QUANTITY);
        ((SearchActivity) getActivity()).tabsStackList.add(TAB_QUANTITY);
    }

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

}
