package app.manage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.agrishare.BaseActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.c2.android.Utils;
import app.dao.Listing;
import app.equipment.AddEquipmentActivity;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_CATEGORY_ID;
import static app.agrishare.Constants.KEY_ID;
import static app.agrishare.Constants.KEY_PAGE_INDEX;
import static app.agrishare.Constants.KEY_PAGE_SIZE;

public class FilteredEquipmentListActivity extends BaseActivity {

    ManageEquipmentAdapter adapter;
    ArrayList<Listing> listingsList;

    View loadingmore_footerView;

    int pageSize = 10;
    int pageIndex = 0;
    Boolean loadMore = false;
    Boolean isFetchingOldContent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_equipment_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String title = "Filtered Equipment";

        if (getIntent().getLongExtra(KEY_CATEGORY_ID, 0) == 1)
            title = getString(R.string.tractors);
        else if (getIntent().getLongExtra(KEY_CATEGORY_ID, 0) == 2)
            title = getString(R.string.lorries);
        else if (getIntent().getLongExtra(KEY_CATEGORY_ID, 0) == 3)
            title = getString(R.string.processing);
        setNavBar(title, R.drawable.button_back);
        initViews();
    }

    private void initViews(){
        loadingmore_footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_loading_more, null, false);
        Glide.with(FilteredEquipmentListActivity.this).load(R.raw.dots).into((ImageView) loadingmore_footerView.findViewById(R.id.imageview));
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
                            if (listingsList != null && listingsList.size() > 9) {
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
        pageIndex = 0;

        adapter = null;
        listingsList = new ArrayList<>();

        showLoader("Fetching Equipment", "Please wait...");
        HashMap<String, String> query = new HashMap<String, String>();
        query.put(KEY_CATEGORY_ID, String.valueOf(getIntent().getLongExtra(KEY_CATEGORY_ID, 0)));
        query.put(KEY_PAGE_SIZE, pageSize + "");
        query.put(KEY_PAGE_INDEX, pageIndex + "");
        getAPI("listings", query, fetchResponse);
    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("FILTERED EQUIPMENT SUCCESS: "+ result.toString());

            hideLoader();
            refreshComplete();
            JSONArray list = result.optJSONArray("List");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    Listing listing = new Listing(list.optJSONObject(i));
                    if (!listingsList.contains(listing))
                        listingsList.add(listing);
                }

                loadMore = (size == pageSize);

                if (adapter == null) {
                    adapter = new ManageEquipmentAdapter(FilteredEquipmentListActivity.this, listingsList, FilteredEquipmentListActivity.this);
                    listview.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                    listview.setAdapter(adapter);
                }
            }
            else {
                showFeedbackWithButton(R.drawable.feedback_empty, getResources().getString(R.string.empty), getResources().getString(R.string.you_have_not_added_any_equipment));
                setAddButton();
            }

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("FILTERED EQUIPMENT ERROR:  " + errorMessage);
            showFeedbackWithButton(R.drawable.feedback_error, getResources().getString(R.string.error), getResources().getString(R.string.please_make_sure_you_have_working_internet));
            setRefreshButton();
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
        query.put(KEY_CATEGORY_ID, String.valueOf(getIntent().getLongExtra(KEY_CATEGORY_ID, 0)));
        query.put(KEY_PAGE_INDEX, pageIndex + "");
        query.put(KEY_PAGE_SIZE, pageSize + "");
        isFetchingOldContent = true;
        getAPI("listings", query, fetchMoreOldContentResponse);
    }

    AsyncResponse fetchMoreOldContentResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("FILTERED EQUIPMENT MORE OLD SUCCESS: " + result.toString());
            isFetchingOldContent = false;

            JSONArray list = result.optJSONArray("List");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    Listing listing = new Listing(list.optJSONObject(i));
                    if (!listingsList.contains(listing))
                        listingsList.add(listing);
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
            Log("ERROR FILTERED EQUIPMENT MORE OLD "+ errorMessage);
            isFetchingOldContent = false;
            listview.removeFooterView(loadingmore_footerView);
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };


    public void setRefreshButton(){
        ((Button) findViewById(R.id.feedback_retry)).setText(getResources().getString(R.string.retry));
        findViewById(R.id.feedback_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    refresh();
                }
            }
        });
    }

    public void setAddButton(){
        ((Button) findViewById(R.id.feedback_retry)).setText(getResources().getString(R.string.add));
        findViewById(R.id.feedback_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showAddEquipment();
                }
            }
        });
    }

    private void showAddEquipment(){
        Intent intent = new Intent(FilteredEquipmentListActivity.this, AddEquipmentActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.refreshEquipmentTab){
            MyApplication.refreshEquipmentTab = false;
            refresh();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                goBack();
                return true;
            case R.id.add:
                Intent intent = new Intent(FilteredEquipmentListActivity.this, AddEquipmentActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

}
