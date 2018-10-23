package app.searchwizard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.search.SearchResultsActivity;

import static app.agrishare.Constants.KEY_SEARCH_QUERY;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class IncludeFuelFragment extends BaseFragment {


    public IncludeFuelFragment() {
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

    IncludeFuelFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_fuel_form, container, false);
        fragment = this;
        initViews();
        ((SearchActivity) getActivity()).mPager.setPagingEnabled(true);   //enable swipe in custom viewpager
        return rootView;
    }

    private void initViews(){

        (rootView.findViewById(R.id.yes_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    updateIncludeFuel(true);
                }
            }
        });

        (rootView.findViewById(R.id.yes_fuel_must_be_supplied)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    updateIncludeFuel(true);
                }
            }
        });

        (rootView.findViewById(R.id.no_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    updateIncludeFuel(false);
                }
            }
        });

        (rootView.findViewById(R.id.no_i_will_supply_my_own_fuel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    updateIncludeFuel(false);
                }
            }
        });
    }

    private void updateIncludeFuel(boolean includeFuel){
        ((SearchActivity) getActivity()).mPager.setPagingEnabled(true);   //enable swipe in custom viewpager

        ((SearchActivity) getActivity()).query.put("IncludeFuel", includeFuel + "");
        MyApplication.searchQuery.IncludeFuel = includeFuel;

        Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
        intent.putExtra(KEY_SEARCH_QUERY, ((SearchActivity) getActivity()).query);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
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
