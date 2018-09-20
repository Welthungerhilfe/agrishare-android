package app.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import app.agrishare.BaseFragment;
import app.agrishare.MainActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.CustomViewPager;
import app.faqs.FAQsActivity;

import static app.agrishare.Constants.SEARCH;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class SearchFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener {

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
            toolbar.inflateMenu(R.menu.menu_faqs);
            toolbar.setOnMenuItemClickListener(this);
          //  ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
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

    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }

        if (((MainActivity) getActivity()).shouldAutoNavigateToSpecificSearchFragment) {
            int searchTabToOpen = ((MainActivity) getActivity()).searchTabToOpen;
            if (tabLayout != null && viewPager != null) {
                tabLayout.setScrollPosition(searchTabToOpen, 0f, true);
                viewPager.setCurrentItem(searchTabToOpen);
            }
            ((MainActivity) getActivity()).shouldAutoNavigateToSpecificSearchFragment = false;
        }


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
                    return getActivity().getString(R.string.tractors).toUpperCase();
                case 1 :
                    return getActivity().getString(R.string.lorries).toUpperCase();
                case 2 :
                    return getActivity().getString(R.string.processing).toUpperCase();
            }
            return null;
        }


    }

}
