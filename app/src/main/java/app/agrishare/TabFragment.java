package app.agrishare;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import app.account.ProfileFragment;
import app.c2.android.CustomViewPager;
import app.c2.android.Utils;
import app.dashboard.DashboardFragment;

import static app.agrishare.Constants.*;


/**
 * Created by ernestnyumbu on 27/1/17.
 */
public class TabFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    static String title;
    public static int int_items = 4 ;
    int last_page_selected = 0;
    private static final int MY_PERMISSIONS_REQUEST = 1;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TabFragment newInstance(int sectionNumber, String _title) {
        title = _title;
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TabFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x =  inflater.inflate(R.layout.tab_layout,null);
        MyApplication.tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        if (!Utils.isScreenDiagonalInchesGreaterThan(4.1, getActivity())) {
            MyApplication.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        else {
            if (!Utils.isScreenDiagonalInchesGreaterThan(5.59, getActivity())){
                MyApplication.tabLayout.getLayoutParams().height = Utils.convertDPtoPx(58, getActivity());
                MyApplication.tabLayout.requestLayout();
            }
        }
        MyApplication.viewPager = (CustomViewPager) x.findViewById(R.id.viewpager);
        MyApplication.viewPager.setPagingEnabled(false);        //disable swipe in custom viewpage
        /**
         *Set an Apater for the View Pager
         */
        MyApplication.viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        MyApplication.viewPager.setOffscreenPageLimit(3);
        MyApplication.viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;        //disable swiping between tabs
            }
        });
        MyApplication.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                View view = getActivity().getCurrentFocus();
                if(view != null) {
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */


        MyApplication.tabLayout.post(new Runnable() {
            @Override
            public void run() {
                MyApplication.tabLayout.setupWithViewPager(MyApplication.viewPager);

                MyApplication.tabLayout.getTabAt(0).setCustomView(getTabIconView(R.drawable.tab_icons_home_on_120));
                MyApplication.tabLayout.getTabAt(1).setCustomView(getTabIconView(R.drawable.tab_icons_explore_off_120));
                MyApplication.tabLayout.getTabAt(2).setCustomView(getTabIconView(R.drawable.tab_icons_notifications_off_120));
                MyApplication.tabLayout.getTabAt(3).setCustomView(getTabIconView(R.drawable.tab_icons_account_off_120));

                MyApplication.tabLayout.setOnTabSelectedListener(
                        new TabLayout.ViewPagerOnTabSelectedListener(MyApplication.viewPager) {

                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                if (tab.getPosition() != 2)
                                    super.onTabSelected(tab);
                                switch (tab.getPosition()){
                                    case 0:
                                        tab.getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_icons_home_on_120);
                                        last_page_selected = tab.getPosition();
                                        break;
                                    case 1:
                                        tab.getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_icons_explore_on_120);
                                        last_page_selected = tab.getPosition();
                                        break;
                                    case 2:
                                        tab.getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_icons_notifications_on_120);
                                        last_page_selected = tab.getPosition();
                                        break;
                                    case 3:
                                        tab.getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_icons_account_on_120);
                                        last_page_selected = tab.getPosition();
                                        break;
                                    default:
                                        break;
                                }
                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {
                                super.onTabUnselected(tab);
                                switch (tab.getPosition()){
                                    case 0:
                                        tab.getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_icons_home_off_120);
                                        break;
                                    case 1:
                                        tab.getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_icons_explore_off_120);
                                        break;
                                    case 2:
                                        tab.getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_icons_notifications_off_120);
                                        break;
                                    case 3:
                                        tab.getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_icons_account_off_120);
                                        break;
                                    default:
                                        break;
                                }
                            }

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {
                                super.onTabReselected(tab);
                            }
                        }
                );

            }
        });
        return x;
    }

    public View getTabIconView(int drawable){
        if (getActivity() != null) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.customtab, null);
            view.findViewById(R.id.icon).setBackgroundResource(drawable);
            return view;
        }
        else
            return null;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }

   /*     if (MyApplication.resetLastPage) {
            MyApplication.resetLastPage = false;
            MyApplication.viewPager.setCurrentItem(last_page_selected);
        }*/

    }

   /* @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            if (MyApplication.resetLastPage) {
                MyApplication.resetLastPage = false;
                MyApplication.viewPager.setCurrentItem(last_page_selected);
            }
        }
    }*/


    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        private int[] imageResId = {};

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return new DashboardFragment();
                case 1 : return new DashboardFragment();
                case 2 : return new DashboardFragment();
                case 3 : return new ProfileFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

         /*   switch (position){
                case 0 :
                    return "Feeds";
                case 1 :
                    return "Designers";
                case 2 :
                    return "My Look";
                case 3 :
                    return "Stores";
            }
            return null; */
       /*     Drawable image = ContextCompat.getDrawable(getActivity(), imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;  */
            return null;
        }


    }
}
