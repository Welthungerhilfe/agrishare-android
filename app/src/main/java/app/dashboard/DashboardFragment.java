package app.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import app.account.LoginActivity;
import app.agrishare.BaseFragment;
import app.agrishare.MainActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.CustomViewPager;
import app.equipment.AddEquipmentActivity;
import app.faqs.FAQsActivity;

import static app.agrishare.Constants.DASHBOARD;
import static app.agrishare.Constants.PREFS_CURRENT_LANGUAGE;
import static app.agrishare.Constants.PREFS_CURRENT_LANGUAGE_LOCALE_NAME;
import static app.agrishare.Constants.PREFS_HAS_SHOWN_DASHBOARD_INTRO;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class DashboardFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener  {

    TabLayout tabLayout;
    CustomViewPager viewPager;

    CollapsingToolbarLayout collapsingToolbarLayout;

    int intro_mode = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initViews();
        return rootView;
    }


    private void initViews(){
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_faqs);
        toolbar.setOnMenuItemClickListener(this);

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

        (rootView.findViewById(R.id.tractors)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).searchTabToOpen = 0;
                showSearchFragment();
            }
        });

        (rootView.findViewById(R.id.lorries)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).searchTabToOpen = 1;
                showSearchFragment();
            }
        });

        (rootView.findViewById(R.id.processing)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).searchTabToOpen = 2;
                showSearchFragment();
            }
        });

        createTabs();
        if (!MyApplication.hasShownDashboardIntro) {
            toggleIntro();
            MyApplication.hasShownDashboardIntro = true;
            SharedPreferences.Editor editor = MyApplication.prefs.edit();
            editor.putBoolean(PREFS_HAS_SHOWN_DASHBOARD_INTRO, MyApplication.hasShownDashboardIntro);
            editor.commit();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.faqs:
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), FAQsActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                }
                return true;
        }
        return false;
    }

    private void toggleIntro(){
        if (intro_mode == 0) {
            ((TextView) rootView.findViewById(R.id.intro_title)).setText(R.string.do_you_have_equipment);
            ((TextView) rootView.findViewById(R.id.intro_description)).setText(R.string.have_equipment_intro);
            ((TextView) rootView.findViewById(R.id.intro_option)).setText(R.string.do_you_need_equipment);
            ((Button) rootView.findViewById(R.id.intro_button)).setText(R.string.add_listing);
            (rootView.findViewById(R.id.intro_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddEquipmentActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
                    (rootView.findViewById(R.id.intro_container)).setVisibility(View.GONE);
                }
            });
            intro_mode = 1;
        }
        else {
            ((TextView) rootView.findViewById(R.id.intro_title)).setText(R.string.do_you_need_equipment);
            ((TextView) rootView.findViewById(R.id.intro_description)).setText(R.string.need_equipment_intro);
            ((TextView) rootView.findViewById(R.id.intro_option)).setText(R.string.do_you_have_equipment);
            ((Button) rootView.findViewById(R.id.intro_button)).setText(R.string.search);
            (rootView.findViewById(R.id.intro_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.tabLayout.setScrollPosition(1,0f,true);
                    MyApplication.viewPager.setCurrentItem(1);
                    (rootView.findViewById(R.id.intro_container)).setVisibility(View.GONE);
                }
            });
            intro_mode = 0;
        }
        (rootView.findViewById(R.id.intro_container)).setVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.intro_option)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleIntro();
            }
        });

    }

    private void showSearchFragment(){
        ((MainActivity) getActivity()).shouldAutoNavigateToSpecificSearchFragment = true;
        MyApplication.tabLayout.setScrollPosition(1,0f,true);
        MyApplication.viewPager.setCurrentItem(1);
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
