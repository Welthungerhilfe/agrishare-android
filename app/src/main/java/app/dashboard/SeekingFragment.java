package app.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.agrishare.BaseFragment;
import app.agrishare.R;
import okhttp3.Response;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class SeekingFragment extends BaseFragment {


    public SeekingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_list_recycler, container, false);
        initViews();
        return rootView;
    }

    private void initViews(){


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


    //Feedback - need custom to avoid class

    public void showFeedback(int iconResourceId, String title, String message) {
        rootView.findViewById(R.id.feedback_).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_activity).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_icon).setVisibility(View.VISIBLE);
        ((ImageView)rootView.findViewById(R.id.feedback_icon)).setImageResource(iconResourceId);
        ((TextView)rootView.findViewById(R.id.feedback_title)).setText(title);
        ((TextView)rootView.findViewById(R.id.feedback_message)).setText(message);
        rootView.findViewById(R.id.feedback_retry).setVisibility(View.GONE);
    }


    public void showFeedbackWithButton(int iconResourceId, String title, String message) {
        rootView.findViewById(R.id.feedback_).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_activity).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_icon).setVisibility(View.VISIBLE);
        ((ImageView)rootView.findViewById(R.id.feedback_icon)).setImageResource(iconResourceId);
        ((TextView)rootView.findViewById(R.id.feedback_title)).setText(title);
        ((TextView)rootView.findViewById(R.id.feedback_message)).setText(message);
        rootView.findViewById(R.id.feedback_retry).setVisibility(View.VISIBLE);
    }

    public void hideFeedback() {
        rootView.findViewById(R.id.feedback_).setVisibility(View.GONE);
    }

    public void showLoader() {
        showLoader("", "");
    }

    public void showLoader(String title, String message) {
        rootView.findViewById(R.id.feedback_).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_activity).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_icon).setVisibility(View.GONE);
        ((TextView)rootView.findViewById(R.id.feedback_title)).setText(title);
        ((TextView)rootView.findViewById(R.id.feedback_message)).setText(message);
        rootView.findViewById(R.id.feedback_retry).setVisibility(View.GONE);
    }

    public void showLoader(String title, String message, int progress) {
        rootView.findViewById(R.id.feedback_).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_activity).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.VISIBLE);
        ((DonutProgress) rootView.findViewById(R.id.feedback_progress)).setProgress(progress);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_icon).setVisibility(View.GONE);
        ((TextView)rootView.findViewById(R.id.feedback_title)).setText(title);
        ((TextView)rootView.findViewById(R.id.feedback_message)).setText(message);
        rootView.findViewById(R.id.feedback_retry).setVisibility(View.GONE);
    }

    public void hideLoader() {
        rootView.findViewById(R.id.feedback_).setVisibility(View.GONE);
    }




}

