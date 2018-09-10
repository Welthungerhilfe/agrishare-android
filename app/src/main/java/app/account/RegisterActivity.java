package app.account;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import app.c2.android.CustomViewPager;
import me.relex.circleindicator.CircleIndicator;

import static app.agrishare.Constants.KEY_TELEPHONE;

public class RegisterActivity extends BaseActivity {

    public static final int NUM_PAGES = 2;
    public CustomViewPager mPager;
    private PagerAdapter mPagerAdapter;

    public String firstname = "";
    public String lastname = "";
    public String emailaddress = "";
    public String telephone = "";
    public String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.page_bg_grey));
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        telephone = getIntent().getStringExtra(KEY_TELEPHONE);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager =  findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPagingEnabled(false);        //disable swipe in custom viewpager

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

    }

    public void enableSwipe(){
        mPager.setPagingEnabled(true);
    }

    public void disableFingerSwipe(){
        mPager.setPagingEnabled(false);        //disable swipe in custom viewpager
    }


    public void goBack(){
       /* if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            close();
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }*/

        close();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        goBack();
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
            if (position == 0)
                return new RegFormFragment();
            else if (position == 1)
                return new SMSVerificationFragment();
            else
                return new RegFormFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


}
