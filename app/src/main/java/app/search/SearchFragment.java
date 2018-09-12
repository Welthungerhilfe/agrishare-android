package app.search;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.about.AboutActivity;
import app.account.EditProfileActivity;
import app.account.ForgotPinActivity;
import app.account.NotificationPreferencesActivity;
import app.account.PrivacyPolicyActivity;
import app.account.SplashActivity;
import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.CustomViewPager;
import app.contact.ContactUsActivity;
import app.dashboard.DashboardFragment;
import app.dashboard.SeekingFragment;
import app.faqs.FAQsActivity;

import static app.agrishare.Constants.PREFS_TOKEN;
import static app.agrishare.Constants.PROFILE;
import static app.agrishare.Constants.SEARCH;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class SearchFragment extends BaseFragment {

    TabLayout tabLayout;
    CustomViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_search_parent, container, false);
        initViews();
        return rootView;
    }

    private void initViews(){
        setToolbar();
        createTabs();
    }

    private void setToolbar(){
        if (rootView != null){
            Toolbar toolbar = rootView.findViewById(R.id.toolbar);
            toolbar.setTitle("Search");
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
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
        setToolbar();

        if (MyApplication.tabsStackList.contains(SEARCH))
            MyApplication.tabsStackList.remove(SEARCH);
        MyApplication.tabsStackList.add(SEARCH);
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


    //Tabs
    public static int int_items = 3 ;

    private void createTabs(){
        tabLayout = rootView.findViewById(R.id.tabs);
        viewPager = rootView.findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);        //disable swipe in custom viewpager
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(1);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position)
        {
            switch (position) {
                case 0 : return new TractorsSearchFormFragment();
                case 1 : return new LorriesSearchFormFragment();
                case 2 : return new ProcessingSearchFormFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }

        /**
         *
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "TRACTORS";
                case 1 :
                    return "LORRIES";
                case 2 :
                    return "PROCESSING";
            }
            return null;
        }


    }

}
