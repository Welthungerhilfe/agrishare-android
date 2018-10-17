package app.account;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.Utils;
import app.dao.SplashPage;

import static app.agrishare.Constants.PREFS_CURRENT_LANGUAGE;
import static app.agrishare.Constants.PREFS_CURRENT_LANGUAGE_LOCALE_NAME;

/**
 * Created by ernestnyumbu on 17/10/2018.
 */

public class IntroFragment extends BaseFragment {


    ViewPager viewPager;
    LinearLayout footer_container;
    ImageView arrowImageView;
    TextView language_textview;
    Locale myLocale;

    public boolean isAlternateScreen = false;

    private SplashAdapter adapter;
    private ArrayList<SplashPage> pageList;
    int loop_position = 0;

    public IntroFragment() {
        mtag = "introductionfragment";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    Fragment fragment;
    IntroFragment introFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (isAlternateScreen)
            rootView = inflater.inflate(R.layout.fragment_introduction_small_screen, container, false);
        else
            rootView = inflater.inflate(R.layout.fragment_introduction, container, false);
        fragment = this;
        introFragment = this;
        initViews();
        setLanguage();
        return rootView;
    }

    private void initViews(){

        footer_container = rootView.findViewById(R.id.footer_container);
        arrowImageView = rootView.findViewById(R.id.arrow);
        language_textview = rootView.findViewById(R.id.language);


        if (isAlternateScreen){
            arrowImageView.setVisibility(View.GONE);
            footer_container.setVisibility(View.VISIBLE);

        }
        else {
            if (!Utils.isScreenDiagonalInchesGreaterThan(((SplashActivity) getActivity()).CUT_OFF_INCHES, getActivity())){
                footer_container.setVisibility(View.GONE);
                arrowImageView.setVisibility(View.VISIBLE);
                doBounceAnimation(arrowImageView);
                arrowImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        {
                            if (((SplashActivity) getActivity()).mPager.getCurrentItem() < ((SplashActivity) getActivity()).NUM_PAGES - 1){
                                ((SplashActivity) getActivity()).mPager.setCurrentItem(((SplashActivity) getActivity()).mPager.getCurrentItem() + 1);
                            }
                        }
                    }
                });
            }
            else {
                arrowImageView.setVisibility(View.GONE);
            }

            viewPager = rootView.findViewById(R.id.pager);
            pageList = new ArrayList<SplashPage>();

            SplashPage page1 = new SplashPage();
            page1.id = 1;
            page1.subtitle = getResources().getString(R.string.welcome_to);
            page1.title = getResources().getString(R.string.app_name);
            page1.intro = getResources().getString(R.string.splash_intro);
            page1.action = getResources().getString(R.string.learn_more);
            pageList.add(page1);

            SplashPage page2 = new SplashPage();
            page2.id = 2;
            page2.subtitle = getResources().getString(R.string.app_name);
            page2.title = getResources().getString(R.string.seeking);
            page2.intro = getResources().getString(R.string.splash_seeking_intro);
            page2.action = getResources().getString(R.string.next);
            pageList.add(page2);

            SplashPage page3 = new SplashPage();
            page3.id = 3;
            page3.subtitle = getResources().getString(R.string.app_name);
            page3.title = getResources().getString(R.string.offering);
            page3.intro = getResources().getString(R.string.splash_offering_intro);
            page3.action = getResources().getString(R.string.back);
            pageList.add(page3);

            adapter = new SplashAdapter(getActivity(), pageList, introFragment);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(0);
        }


        (rootView.findViewById(R.id.terms)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(getActivity(), PrivacyPolicyActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
                }
            }
        });

        (rootView.findViewById(R.id.register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(getActivity(), RegisterActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
                }
            }
        });

        (rootView.findViewById(R.id.login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
                }
            }
        });


        (rootView.findViewById(R.id.language_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getActivity(), language_textview);
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

    public void gotoNext(){
        if (viewPager.getCurrentItem() + 1 < pageList.size())
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    public void gotoPrevious(){
        if (viewPager.getCurrentItem() > 0)
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    private void setLanguage(){

        if (MyApplication.current_language == 0){
            language_textview.setText(getResources().getString(R.string.choose_language));
        }
        else if (MyApplication.current_language == 1){
            language_textview.setText("English");
        }
        else if (MyApplication.current_language == 2){
            language_textview.setText("Shona");
        }
        else if (MyApplication.current_language == 3){
            language_textview.setText("Ndebele");
        }

        if (MyApplication.current_language > 0) {
            Configuration configuration = getResources().getConfiguration();
            if (!configuration.locale.getLanguage().equals(MyApplication.current_language_locale_name))
                setLocale(MyApplication.current_language_locale_name);
            language_textview.setTextColor(getResources().getColor(android.R.color.black));
        }
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
        if (isAlternateScreen){
            Intent intent = new Intent(getActivity(), SplashActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        else
            getActivity().recreate();
    }

    private void doBounceAnimation(View targetView) {
        ObjectAnimator animY = ObjectAnimator.ofFloat(targetView, "translationY", -10f, 0f);
        animY.setDuration(1000);//1sec
        animY.setInterpolator(new BounceInterpolator());
        animY.setRepeatCount(100);
        animY.start();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }



    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onStop() {
        super.onStop();

    }

}
