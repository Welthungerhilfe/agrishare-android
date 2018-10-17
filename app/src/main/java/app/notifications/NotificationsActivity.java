package app.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.c2.android.Utils;
import app.dao.Listing;
import app.dao.Notification;
import app.dashboard.NotificationAdapter;
import app.equipment.AddEquipmentActivity;
import app.manage.FilteredEquipmentListActivity;
import app.manage.ManageEquipmentAdapter;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_CATEGORY_ID;
import static app.agrishare.Constants.KEY_PAGE_INDEX;
import static app.agrishare.Constants.KEY_PAGE_SIZE;
import static app.agrishare.Constants.KEY_SEEKER;

public class NotificationsActivity extends BaseActivity {

    int pageIndex = 0;
    int pageSize = 10;
    Boolean loadMore = false;
    Boolean isFetchingOldContent = false;

    NotificationsAdapter adapter;
    ArrayList<Notification> notificationsList;
    View loadingmore_footerView;

    boolean isSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String title = "Notifications";

        if (getIntent().getBooleanExtra(KEY_SEEKER, false)) {
            isSeeking = true;
            title = getString(R.string.seeking_notifications);
        }
        else
            title = getString(R.string.offering_notifications);
        setNavBar(title, R.drawable.button_back);
        initViews();
    }

    private void initViews(){
        loadingmore_footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_loading_more, null, false);
        Glide.with(NotificationsActivity.this).load(R.raw.dots).into((ImageView) loadingmore_footerView.findViewById(R.id.imageview));
        listview = findViewById(R.id.list);
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) { }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log("FILTER ON SCROLL: " + "HIT");
                if (loadMore) {
                    if (listview.getAdapter() != null) {
                        if (Utils.isListViewScrolledAllTheWayDown(listview)) {
                            Log("FILTER END of list Last");
                            if (notificationsList != null && notificationsList.size() > 9) {
                                if (!isFetchingOldContent) {
                                    loadMoreOldContent();
                                }
                            }
                        }
                    }
                }
            }
        });
        swipeContainer = findViewById(R.id.refresher);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        refresh();
    }

    public void refresh(){
        adapter = null;
        notificationsList = new ArrayList<>();

        showLoader("Fetching Notifications", "Please wait...");
        HashMap<String, String> query = new HashMap<String, String>();
        query.put(KEY_PAGE_SIZE, pageSize + "");
        query.put(KEY_PAGE_INDEX, pageIndex + "");
        if (isSeeking)
            getAPI("notifications/seeking", query, fetchResponse);
        else
            getAPI("notifications/offering", query, fetchResponse);
    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("NOTIFICATIONS SUCCESS: "+ result.toString());

            hideLoader();
            refreshComplete();
            JSONArray list = result.optJSONArray("List");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    notificationsList.add(new Notification(list.optJSONObject(i), isSeeking));
                }

                loadMore = (size == pageSize);

                if (adapter == null) {
                    adapter = new NotificationsAdapter(NotificationsActivity.this, notificationsList, NotificationsActivity.this);
                    listview.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                    listview.setAdapter(adapter);
                }
            }
            else {
                showFeedbackWithButton(R.drawable.feedback_empty, getResources().getString(R.string.empty), getResources().getString(R.string.no_notifications));
                setRetryButton();
            }

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("NOTIFICATIONS ERROR:  " + errorMessage);
            showFeedbackWithButton(R.drawable.feedback_error, getResources().getString(R.string.error), getResources().getString(R.string.please_make_sure_you_have_working_internet));
            setRetryButton();
            refreshComplete();
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    private void loadMoreOldContent(){
        listview.addFooterView(loadingmore_footerView);
        pageIndex = pageIndex + 1;

        HashMap<String, String> query = new HashMap<String, String>();
        query.put(KEY_PAGE_INDEX, pageIndex + "");
        query.put(KEY_PAGE_SIZE, pageSize + "");
        isFetchingOldContent = true;
        if (isSeeking)
            getAPI("notifications/seeking", query, fetchMoreOldContentResponse);
        else
            getAPI("notifications/offering", query, fetchMoreOldContentResponse);
    }

    AsyncResponse fetchMoreOldContentResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("NOTIFICATIONS MORE OLD SUCCESS: " + result.toString());
            isFetchingOldContent = false;

            JSONArray list = result.optJSONArray("List");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    notificationsList.add(new Notification(list.optJSONObject(i), isSeeking));
                }

                loadMore = (size == pageSize);

                adapter.notifyDataSetChanged();
            }

            listview.removeFooterView(loadingmore_footerView);

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR NOTIFICATIONS: "+ errorMessage);
            isFetchingOldContent = false;
            listview.removeFooterView(loadingmore_footerView);
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    public void setRetryButton(){
        ((TextView)  findViewById(R.id.feedback_retry)).setText("REFRESH");
        findViewById(R.id.feedback_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    refresh();
                }
            }
        });
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

}
