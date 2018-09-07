package app.agrishare;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.account.SplashActivity;

import static app.agrishare.Constants.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        openTab();
    }

    private void openTab(){
        TabFragment tabFragment = TabFragment.newInstance(1, "Tabs");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, tabFragment)
                .commit();
    }

    public void setActionBarTitle(String title)
    {
      /*  SpannableString s = new SpannableString(title);
        s.setSpan(new TypefaceSpan(MainActivity.this, "MontserratRegular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);    */

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);

    }

    public void hideActionBarLogoAndTitle(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
    }

    public void hideActionbar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void showActionbar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
    }

    @Override
    public void onResume(){
        super.onResume();

        if (MyApplication.token.isEmpty()){
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        if (MyApplication.tabsStackList.size() <= 1) {
            finish();
        }
        else {
            MyApplication.tabsStackList.remove(MyApplication.tabsStackList.size() - 1);

            int position_to_move_to = 0;

            String fragment_on_top_of_stack = MyApplication.tabsStackList.get(MyApplication.tabsStackList.size() - 1);

            if (fragment_on_top_of_stack.contains(DASHBOARD))
                position_to_move_to = 0;
            else if (fragment_on_top_of_stack.contains(SEARCH))
                position_to_move_to = 1;
            else if (fragment_on_top_of_stack.contains(MANAGE))
                position_to_move_to = 2;
            else if (fragment_on_top_of_stack.contains(PROFILE))
                position_to_move_to = 3;
            MyApplication.tabLayout.setScrollPosition(position_to_move_to,0f,true);
            MyApplication.viewPager.setCurrentItem(position_to_move_to);
        }
    }
}
