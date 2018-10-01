package app.agrishare;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.util.DisplayMetrics;
import android.util.LruCache;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;

import app.c2.android.CustomViewPager;
import app.dao.SearchQuery;
import app.dao.Service;
import app.dao.User;
import app.database.Users;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.realm.Realm;
import io.realm.RealmResults;

import static app.agrishare.Constants.*;


/**
 * Created by ernestnyumbu on 26/01/16.
 */
public class MyApplication extends Application {

    public static final String ApiUrl = "https://api.agrishare.app/";
    public static final String BaseUrl = "https://api.agrishare.app/";
    public static final String HockeyAppId = "887229fb8f43425fafc67023ecb8dd09";
    public static final String DEBUG_TAG = "Agrishare";
    public static final Boolean DEBUG = true;               //Dont forget to set to FALSE before deployment

    public static SharedPreferences prefs;
    private static MyApplication mInstance;
    public static MyApplication getInstance()
    {
        return mInstance;
    }
    public static Typeface typeFace;
    public static Typeface boldtypeFace;
    public static Typeface lighttypeFace;
    public static Realm realm;
    public static GoogleCloudMessaging gcm;
    public static Boolean isFirstRun = true;
    public static Boolean hasShownDashboardIntro = false;
    public static String default_date= "1999-01-01T12:59:59";
    public static String token = "";
    public static int current_language = 0;
   // public static int current_language = 1;     //1 = english; 2 = shona; 3 = ndebele;
    public static String current_language_locale_name = "en";

    public static int custom_spinner_left_padding = 0;
    public static int custom_spinner_right_padding = 0;

    private static LruCache<String, Bitmap> mMemoryCache;
    public static DisplayMetrics metrics;

    public static User currentUser;

    public static String last_update= "";
    public static String last_notifications_update= "";

    public static NotificationManager notificationManager;

    public static TabLayout tabLayout;
    public static CustomViewPager viewPager;
    public static Boolean resetLastPage = false;
    public static ArrayList<String> tabsStackList;

    public static Boolean isDeviceRegisteredOnOurServer = false;

  //  public static User currentUser;

    public static Boolean refreshEquipmentTab = false;
    public static Boolean refreshManageSeekingTab = false;
    public static Boolean refreshManageOfferingTab = false;
    public static Boolean refreshMyAccountLocations = false;
    public static Boolean refreshMyAccountSpecies = false;
    public static Boolean refreshFeed = false;
    public static Boolean refreshCompetitions = false;
    public static Boolean attempToUploadPendingPosts = false;


    public static SearchQuery searchQuery;

    public static String query = "";


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        typeFace= Typeface.createFromAsset(getAssets(), "fonts/SourceSansProRegular.ttf");
        boldtypeFace= Typeface.createFromAsset(getAssets(), "fonts/SourceSansProBold.ttf");
        lighttypeFace= Typeface.createFromAsset(getAssets(), "fonts/SourceSansProLight.ttf");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        metrics = getResources().getDisplayMetrics();
        custom_spinner_left_padding = (int) (16 * metrics.density);
        custom_spinner_right_padding = (int) (16 * metrics.density);
        tabsStackList = new ArrayList<>();

        // Initialize Realm
        Realm.init(this);

      /*  RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1) // Must be bumped when the schema changes
                .deleteRealmIfMigrationNeeded()
                .build();   */

        // Open the default Realm for the UI thread.
        realm = Realm.getDefaultInstance();

        prefs = this.getSharedPreferences(PREFS_USER_DETAILS, Activity.MODE_PRIVATE);
        token = prefs.getString(PREFS_TOKEN, "");
        isDeviceRegisteredOnOurServer = prefs.getBoolean(PREFS_IS_DEVICE_REGISTERED_ON_OUR_SERVER, false);
        last_update = prefs.getString(PREFS_LAST_UPDATE, default_date);
        last_notifications_update = prefs.getString(PREFS_LAST_NOTIFICATIONS_UPDATE, default_date);
        current_language = prefs.getInt(PREFS_CURRENT_LANGUAGE, 0);
        current_language_locale_name = prefs.getString(PREFS_CURRENT_LANGUAGE_LOCALE_NAME, "en");
        hasShownDashboardIntro = prefs.getBoolean(PREFS_HAS_SHOWN_DASHBOARD_INTRO, false);

        if (!token.isEmpty()){
            RealmResults<Users> results = MyApplication.realm.where(Users.class)
                    .equalTo("AuthToken", token)
                    .findAll();

            if (results.size() > 0) {
                currentUser = new User(results.first());
            }
            else {
                token = "";
            }
        }

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/SourceSansProRegular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
    }

}
