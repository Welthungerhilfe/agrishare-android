package app.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.Locale;

import app.agrishare.MainActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import static app.agrishare.Constants.PREFS_CURRENT_LANGUAGE;
import static app.agrishare.Constants.PREFS_CURRENT_LANGUAGE_LOCALE_NAME;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.language)
    public TextView language_textview;

    @BindView(R.id.terms)
    public TextView terms_textview;

    @BindView(R.id.get_started)
    public Button get_started_button;


    Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initViews();
        setLanguage();

/*
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
*/

        if (!MyApplication.token.isEmpty()) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
        }

    }

    private void initViews(){
        terms_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(SplashActivity.this, PrivacyPolicyActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
                }
            }
        });

        get_started_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(SplashActivity.this, CellNumberActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
                }
            }
        });

        language_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(SplashActivity.this, language_textview);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_language_options);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.english:
                                    setChosenLanguage(1, "en");
                                    break;
                                case R.id.shona:
                                    setChosenLanguage(2, "sn");
                                    break;
                                case R.id.ndebele:
                                    setChosenLanguage(3, "nd");
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            }
        });

    }

    private void setLanguage(){

        if (MyApplication.current_language == 1){
            language_textview.setText("ENGLISH");
        }
        else if (MyApplication.current_language == 2){
            language_textview.setText("SHONA");
        }
        else if (MyApplication.current_language == 3){
            language_textview.setText("NDEBELE");
        }

        Configuration configuration = getResources().getConfiguration();
        if (!configuration.locale.getLanguage().equals(MyApplication.current_language_locale_name))
            setLocale(MyApplication.current_language_locale_name);
    }

    private void setChosenLanguage(int language_id, String language_string_id){
        MyApplication.current_language = language_id;
        MyApplication.current_language_locale_name = language_string_id;
        SharedPreferences.Editor editor = MyApplication.prefs.edit();
        editor.putInt(PREFS_CURRENT_LANGUAGE, MyApplication.current_language);
        editor.putString(PREFS_CURRENT_LANGUAGE_LOCALE_NAME, MyApplication.current_language_locale_name);
        editor.commit();

        setLanguage();
    }

    public void setLocale(String localeName) {
        myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        recreate();
    }

}
