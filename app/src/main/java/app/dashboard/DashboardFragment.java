package app.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;

import static app.agrishare.Constants.DASHBOARD;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class DashboardFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }

        if (MyApplication.tabsStackList.contains(DASHBOARD))
            MyApplication.tabsStackList.remove(DASHBOARD);
        MyApplication.tabsStackList.add(DASHBOARD);
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
