package app.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.HashMap;

import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.CustomViewPager;

import static app.agrishare.Constants.DASHBOARD;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class DashboardFragment extends BaseFragment {

    TabLayout tabLayout;
    CustomViewPager viewPager;

    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initViews();
        return rootView;
    }


    private void initViews(){

        collapsingToolbarLayout = rootView.findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setScrimVisibleHeightTrigger(988);
        collapsingToolbarLayout.setTitle("AgriShare");
     //   collapsingToolbarLayout.setTitleEnabled(false);


        AppBarLayout appBarLayout = rootView.findViewById(R.id.app_bar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int pos = Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange();

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    //  Collapsed

                }
                else
                {
                    //Expanded


                }
            }
        });


        appBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
/*
        (rootView.findViewById(R.id.settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }
        });

        (rootView.findViewById(R.id.followers_layout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowingOrFollowersActivity.class);
                intent.putExtra(KEY_USER, MyApplication.currentUser);
                intent.putExtra(KEY_Followers, true);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }
        });

        (rootView.findViewById(R.id.following_layout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowingOrFollowersActivity.class);
                intent.putExtra(KEY_USER, MyApplication.currentUser);
                intent.putExtra(KEY_Followers, false);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }
        });*/

        createTabs();
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

    //Tabs
    public static int int_items = 2 ;

    private void createTabs(){
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (CustomViewPager) rootView.findViewById(R.id.viewpager);
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
                case 0 : return new SeekingFragment();
                case 1 : return new SeekingFragment();
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
                    return "Seeking";
                case 1 :
                    return "Offering";
            }
            return null;
        }


    }

}
