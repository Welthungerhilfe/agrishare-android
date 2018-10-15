package app.manage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.agrishare.BaseFragment;
import app.agrishare.MainActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.dao.FAQ;
import app.dao.Listing;
import app.equipment.AddEquipmentActivity;
import app.faqs.FAQAdapter;
import app.faqs.FAQsActivity;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_PAGE_INDEX;
import static app.agrishare.Constants.KEY_PAGE_SIZE;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class ManageEquipmentFragment extends BaseFragment {

    ManageEquipmentAdapter adapter;
    ArrayList<Listing> listingsList;
    View headerView;

    int pageSize = 10;
    int pageIndex = 0;

    ManageEquipmentFragment fragment;

    public ManageEquipmentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_list_in_fragment, container, false);
        fragment = this;
        initViews();
        return rootView;
    }

    private void initViews(){
        listview = rootView.findViewById(R.id.list);
        swipeContainer = rootView.findViewById(R.id.refresher);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        refresh();
    }

    public void refresh(){
        if(headerView != null)
            listview.removeHeaderView(headerView);
        adapter = null;
        listingsList = new ArrayList<>();

        showLoader("Fetching Equipment", "Please wait...");
        HashMap<String, String> query = new HashMap<String, String>();
        query.put(KEY_PAGE_SIZE, pageSize + "");
        query.put(KEY_PAGE_INDEX, pageIndex + "");
        getAPI("listings", query, fetchResponse);
    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("EQUIPMENT SUCCESS", result.toString() + "");

            hideLoader();
            refreshComplete();
            JSONArray list = result.optJSONArray("List");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    listingsList.add(new Listing(list.optJSONObject(i)));
                }

                if (adapter == null) {
                    if (getActivity() != null) {
                        adapter = new ManageEquipmentAdapter(getActivity(), listingsList, getActivity());
                        listview.setAdapter(adapter);
                        headerView = (View) getLayoutInflater().inflate(R.layout.row_add_header, null);
                        (headerView.findViewById(R.id.add)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                {
                                    showAddEquipment();
                                }
                            }
                        });
                        listview.addHeaderView(headerView);
                    }
                } else {
                    adapter.notifyDataSetChanged();
                    listview.setAdapter(adapter);
                }
            }
            else {
                if (getActivity() != null) {
                    showFeedbackWithButton(R.drawable.feedback_empty, getResources().getString(R.string.empty), getResources().getString(R.string.you_have_not_added_any_equipment));
                    setAddButton();
                }
            }

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("EQUIPMENT ERROR:  " + errorMessage);
            showFeedbackWithButton(R.drawable.feedback_error, getResources().getString(R.string.error), getResources().getString(R.string.please_make_sure_you_have_working_internet));
            setRefreshButton();
            refreshComplete();
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    public void setRefreshButton(){
        ((Button) rootView.findViewById(R.id.feedback_retry)).setText(getResources().getString(R.string.retry));
        rootView.findViewById(R.id.feedback_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    refresh();
                }
            }
        });
    }

    public void setAddButton(){
        ((Button) rootView.findViewById(R.id.feedback_retry)).setText(getResources().getString(R.string.add));
        rootView.findViewById(R.id.feedback_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showAddEquipment();
                }
            }
        });
    }

    private void showAddEquipment(){
        Intent intent = new Intent(getActivity(), AddEquipmentActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
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

        if (MyApplication.refreshEquipmentTab){
            MyApplication.refreshEquipmentTab = false;
            refresh();
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

