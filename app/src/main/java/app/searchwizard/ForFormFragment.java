package app.searchwizard;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import app.account.ForgotPinActivity;
import app.account.LoginActivity;
import app.account.RegisterActivity;
import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.c2.android.DatePickerFragment;
import app.dao.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

import static app.agrishare.Constants.SEEKING;
import static app.agrishare.Constants.TAB_FOR;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class ForFormFragment extends BaseFragment {


    public ForFormFragment() {
        mtag = "name";
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

    ForFormFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_for_form, container, false);
        fragment = this;
        initViews();
        return rootView;
    }

    private void initViews(){

        (rootView.findViewById(R.id.me_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    updateForId(0);
                }
            }
        });

        (rootView.findViewById(R.id.friend_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    updateForId(1);
                }
            }
        });

        (rootView.findViewById(R.id.group_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    updateForId(2);
                }
            }
        });

    }

    private void updateForId(int forId){
        ((SearchActivity) getActivity()).query.put("For", String.valueOf(forId));
        MyApplication.searchQuery.ForId = forId;

        if (((SearchActivity) getActivity()).mPager.getCurrentItem() < ((SearchActivity) getActivity()).NUM_PAGES - 1){
            ((SearchActivity) getActivity()).mPager.setCurrentItem(((SearchActivity) getActivity()).mPager.getCurrentItem() + 1);
        }
    }



    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }

        if (((SearchActivity) getActivity()).tabsStackList.contains(TAB_FOR))
            ((SearchActivity) getActivity()).tabsStackList.remove(TAB_FOR);
        ((SearchActivity) getActivity()).tabsStackList.add(TAB_FOR);

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

}
