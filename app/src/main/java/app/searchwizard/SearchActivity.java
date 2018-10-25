package app.searchwizard;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.HashMap;

import app.account.RegFormFragment;
import app.account.RegisterActivity;
import app.account.SMSVerificationFragment;
import app.agrishare.BaseActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.CustomViewPager;
import app.dao.SearchQuery;
import me.relex.circleindicator.CircleIndicator;

import static app.agrishare.Constants.DASHBOARD;
import static app.agrishare.Constants.KEY_CATEGORY_ID;
import static app.agrishare.Constants.MANAGE;
import static app.agrishare.Constants.PROFILE;
import static app.agrishare.Constants.SEARCH;

public class SearchActivity extends BaseActivity {

    public int NUM_PAGES = 6;
    public CustomViewPager mPager;
    private PagerAdapter mPagerAdapter;
    long catergoryId = 0;

    public HashMap<String, String> query;
    public static ArrayList<String> tabsStackList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabsStackList = new ArrayList<>();

        catergoryId = getIntent().getLongExtra(KEY_CATEGORY_ID, 0);

        query = new HashMap<String, String>();
        query.put("CategoryId", String.valueOf(catergoryId));

        MyApplication.searchQuery = new SearchQuery();
        MyApplication.searchQuery.CategoryId = catergoryId;

        if (catergoryId == 1) {
            setNavBar("Search Tractors", R.drawable.button_back);

            query.put("Mobile", "true");
            MyApplication.searchQuery.Mobile =  true;
        }
        else if (catergoryId == 2) {
            setNavBar("Search Lorries", R.drawable.button_back);
            NUM_PAGES = 5;

            query.put("Mobile", "true");
            query.put("IncludeFuel", "true");
            MyApplication.searchQuery.Mobile =  true;
            MyApplication.searchQuery.IncludeFuel =  true;

            query.put("Size", "0");
            MyApplication.searchQuery.Size = 0;

        }
        else if (catergoryId == 3) {
            setNavBar("Search Processors", R.drawable.button_back);

            query.put("IncludeFuel", "true");
            MyApplication.searchQuery.IncludeFuel =  true;
        }

        mPager =  findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPagingEnabled(false);        //disable swipe in custom viewpager
        mPager.setOffscreenPageLimit(NUM_PAGES - 1);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
    }
    
    private void closeActivity(){
        if (tabsStackList.size() <= 1) {
            goBack();
        }
        else {
            tabsStackList.remove(tabsStackList.size() - 1);

            int position_to_move_to = tabsStackList.size() - 1;
            mPager.setCurrentItem(position_to_move_to);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.closeSearchModuleAndGoHome){
            goBack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                closeActivity();
                return true;
            case R.id.home:
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        closeActivity();
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (catergoryId == 1) {
                if (position == 0)
                    return new ForFormFragment();
                else if (position == 1)
                    return new TypeOfServiceFragment();
                else if (position == 2)
                    return new QuantityFragment();
                else if (position == 3)
                    return new StartDateFragment();
                else if (position == 4)
                    return new LocationFragment();
                else if (position == 5)
                    return new IncludeFuelFragment();
                else
                    return new ForFormFragment();
            }
            else if (catergoryId == 2)  {
                if (position == 0)
                    return new ForFormFragment();
                else if (position == 1)
                    return new TypeOfServiceFragment();
                else if (position == 2)
                    return new StartDateFragment();
                else if (position == 3)
                    return new LocationFragment();
                else if (position == 4)
                    return new DestinationLocationFragment();
                else
                    return new ForFormFragment();
            }
            else {
                if (position == 0)
                    return new ForFormFragment();
                else if (position == 1)
                    return new TypeOfServiceFragment();
                else if (position == 2)
                    return new QuantityFragment();
                else if (position == 3)
                    return new StartDateFragment();
                else if (position == 4)
                    return new LocationFragment();
                else if (position == 5)
                    return new MobileFragment();
                else
                    return new ForFormFragment();
            }


        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


}
