package app.agrishare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import app.account.SplashActivity;
import app.c2.android.AsyncResponse;
import app.category.CategoryActivity;
import app.category.CategoryAdapter;
import app.dao.Category;
import app.equipment.AddEquipmentActivity;
import app.manage.BookingDetailActivity;
import app.manage.FilteredEquipmentListActivity;
import okhttp3.Response;

import static app.agrishare.Constants.*;

public class MainActivity extends BaseActivity {

    public boolean shouldAutoNavigateToSpecificSearchFragment = false;
    public int searchTabToOpen = 0;
    private int PLAY_SERVICES_RESOLUTION_REQUEST = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        refreshCategories();
        openTab();
        if (MyApplication.isChangingLanguage)
            MyApplication.isChangingLanguage = false;
        else
            checkIntents();

        if(!MyApplication.isDeviceRegisteredOnOurServer){
            registerDeviceWithOurServer();
        }

     //   sendEventToServer(LAUNCH_EVENT, 0, "", 1, false);
        playServicesCheck();
    }

    private void playServicesCheck(){
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                //prompt the dialog to update google play
                googleAPI.getErrorDialog(this,result,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
        }
        else{
            //google play up to date
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        setIntent(intent);
        checkIntents();
    }

    public void checkIntents(){
    //    if(getIntent().hasExtra(KEY_NOTIFICATION_ID)) {
            if (MyApplication.notificationManager != null)
                MyApplication.notificationManager.cancel(getIntent().getIntExtra(KEY_NOTIFICATION_ID, 0));
  //      }

        if(getIntent().hasExtra(KEY_BOOKING_ID)){
            long id = getIntent().getLongExtra(KEY_BOOKING_ID, 0);
            if (id != 0) {
                Intent intent = new Intent(MainActivity.this, BookingDetailActivity.class);
                intent.putExtra(KEY_BOOKING_ID, id);
                intent.putExtra(KEY_FROM_NOTIFICATION, true);
                intent.putExtra(KEY_SEEKER, getIntent().getBooleanExtra(KEY_SEEKER, false));
                intent.putExtra(KEY_REVIEW_NOTIFICATION, getIntent().getBooleanExtra(KEY_REVIEW_NOTIFICATION, false));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }
        }
      /*  else if(getIntent().hasExtra(KEY_UserId)){
            long id = getIntent().getLongExtra(KEY_UserId, 0);
            if (id != 0) {
                Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
                intent.putExtra(KEY_ID, id);
                intent.putExtra(KEY_FROM_NOTIFICATION, true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }
        }*/
    }

    public void registerDeviceWithOurServer(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String mToken = instanceIdResult.getToken();
                Log("onSuccess MainActivity Token: " + mToken);
                HashMap<String, String> query = new HashMap<String, String>();
                query.put("Token", mToken);
                getAPI("device/register", query, fetchResponse);
            }
        });

    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("SUCCESS DEVICE REG: "+ result.toString());

            MyApplication.isDeviceRegisteredOnOurServer = true;
            SharedPreferences.Editor editor = MyApplication.prefs.edit();
            editor.putBoolean(PREFS_IS_DEVICE_REGISTERED_ON_OUR_SERVER, MyApplication.isDeviceRegisteredOnOurServer);
            editor.commit();

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("DEVICE REG: "+ "WITH OUR OWN SERVER FAILED: " + errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };


    private void openTab(){
        TabFragment tabFragment = TabFragment.newInstance(1, "Tabs");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, tabFragment)
                .commit();

        if (MyApplication.hasJustChangedLanguageInProfile) {
            MyApplication.hasJustChangedLanguageInProfile = false;
            //if menu_settings havent been configured then show menu_settings tab.
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 1000ms
                    //HACK: wait half a second for tabLayout and viewPager to get initialized
                    MyApplication.tabLayout.setScrollPosition(2,0f,true);
                    MyApplication.viewPager.setCurrentItem(2);

                }
            }, 1000);
        }
    }


    private void refreshCategories(){
        HashMap<String, String> query = new HashMap<String, String>();
        getAPI("categories/list", query, fetchCategoriesResponse);
    }

    AsyncResponse fetchCategoriesResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("MAIN ACTVITY CATEGORIES SUCCESS: "+  result.toString() + "");

            JSONArray list = result.optJSONArray("List");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    new Category(list.optJSONObject(i), true);
                }
            }

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("MAIN ACTVITY CATEGORIES ERROR:  " + errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

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


        MyApplication.closeSearchModuleAndGoHome = false;
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
