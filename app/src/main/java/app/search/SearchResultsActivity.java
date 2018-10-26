package app.search;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import app.agrishare.BaseActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.c2.android.Utils;
import app.dao.FAQ;
import app.dao.Listing;
import app.dao.SearchResultListing;
import app.faqs.FAQAdapter;
import app.faqs.FAQsActivity;
import app.manage.FilteredEquipmentListActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_CATEGORY_ID;
import static app.agrishare.Constants.KEY_LISTING;
import static app.agrishare.Constants.KEY_PAGE_INDEX;
import static app.agrishare.Constants.KEY_PAGE_SIZE;
import static app.agrishare.Constants.KEY_SEARCH_QUERY;
import static app.agrishare.Constants.LAUNCH_EVENT;
import static app.agrishare.Constants.MATCH_EVENT;
import static app.agrishare.Constants.SEARCH_EVENT;

public class SearchResultsActivity extends BaseActivity {

    SearchResultsAdapter adapter;
    ArrayList<SearchResultListing> listingsList;
    ArrayList<SearchResultListing> displayList;

    int pageSize = 10;
    int pageIndex = 0;

    boolean hide_unavailable_services = false;
    boolean ordering_by_distance = true;

    Boolean loadMore = false;
    Boolean isFetchingOldContent = false;

    View loadingmore_footerView;

    @BindView(R.id.hide_checkbox)
    public CheckBox hide_checkbox;

    @BindView(R.id.order_by_container)
    public LinearLayout order_by_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Search Results", R.drawable.button_back);
        ButterKnife.bind(this);
        displayList = new ArrayList<>();
        initViews();
      //  sendEventToServer(SEARCH_EVENT, String.valueOf(MyApplication.currentUser.Id), MyApplication.currentUser.Gender,"",1, false);

    }

    private void initViews(){
        loadingmore_footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_loading_more, null, false);
        Glide.with(SearchResultsActivity.this).load(R.raw.dots).into((ImageView) loadingmore_footerView.findViewById(R.id.imageview));
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
        hide_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                   if (isChecked){
                       hide_unavailable_services = true;
                       displayData();
                   }
                   else {
                       hide_unavailable_services = false;
                       displayData();
                   }
               }
           }
        );

        order_by_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    ordering_by_distance = !ordering_by_distance;
                    displayData();
                    if (ordering_by_distance)
                        ((TextView) findViewById(R.id.order_by)).setText(getResources().getString(R.string.order_by_price));
                    else
                        ((TextView) findViewById(R.id.order_by)).setText(getResources().getString(R.string.order_by_distance));
                }
            }
        });
        refresh();
    }

    private void displayData(){
        hideFeedback();
        hideLoader();
        if (listingsList.size() > 0) {
            displayList.clear();
            int size = listingsList.size();
            for (int i = 0; i < size; i++) {
                if (hide_unavailable_services) {
                    if (listingsList.get(i).Available)
                        displayList.add(listingsList.get(i));
                } else
                    displayList.add(listingsList.get(i));
            }

            if (ordering_by_distance){
                sortByDistance();
            }
            else {
                sortByPrice();
            }

            loadMore = (size == pageSize);

            if (displayList.size() > 0) {
                if (adapter == null) {
                    adapter = new SearchResultsAdapter(SearchResultsActivity.this, displayList, SearchResultsActivity.this);
                    listview.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                    listview.setAdapter(adapter);
                }
            }
            else {
                showFeedback(R.drawable.feedback_empty, getResources().getString(R.string.empty), getResources().getString(R.string.there_are_no_available_services));
            }
        }
    }

    private void sortByDistance(){
        Collections.sort(displayList, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                SearchResultListing p1 = (SearchResultListing) o1;
                SearchResultListing p2 = (SearchResultListing) o2;
                if (Double.valueOf(p1.Distance).compareTo(Double.valueOf(p2.Distance)) < 0) {
                    return -1;
                } else if (Double.valueOf(p1.Distance).compareTo(Double.valueOf(p2.Distance)) > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    private void sortByPrice(){
        Collections.sort(displayList, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                SearchResultListing p1 = (SearchResultListing) o1;
                SearchResultListing p2 = (SearchResultListing) o2;
                if (Double.valueOf(p1.Price).compareTo(Double.valueOf(p2.Price)) < 0) {
                    return -1;
                } else if (Double.valueOf(p1.Price).compareTo(Double.valueOf(p2.Price)) > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    public void refresh(){
        pageIndex = 0;
        adapter = null;
        listingsList = new ArrayList<>();

        showLoader("Searching", "Please wait...");
        HashMap<String, String> query = (HashMap<String, String>) getIntent().getSerializableExtra(KEY_SEARCH_QUERY);
        query.put(KEY_PAGE_SIZE, String.valueOf(pageSize));
        query.put(KEY_PAGE_INDEX, String.valueOf(pageIndex));
        query.put("Sort", "Distance");
        getAPI("search", query, fetchResponse);

    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("SEARCH RESULTS SUCCESS", result.toString() + "");

            hideLoader();
            refreshComplete();
            JSONArray list = result.optJSONArray("List");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    listingsList.add(new SearchResultListing(list.optJSONObject(i)));
                }
                displayData();
             //   sendEventToServer(MATCH_EVENT, MyApplication.searchQuery.Service.Title, "","",1, false);

            }
            else {
                showFeedbackWithButton(R.drawable.feedback_empty, getResources().getString(R.string.empty), getResources().getString(R.string.no_results_found));
                setRefreshButton();
            }

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("SEARCH RESULTS ERROR:  " + errorMessage);
            showFeedbackWithButton(R.drawable.feedback_error, getResources().getString(R.string.error), getResources().getString(R.string.please_make_sure_you_have_working_internet) + " : " + errorMessage);
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
        isFetchingOldContent = true;

        HashMap<String, String> query = (HashMap<String, String>) getIntent().getSerializableExtra(KEY_SEARCH_QUERY);
        query.put(KEY_PAGE_SIZE, String.valueOf(pageSize));
        query.put(KEY_PAGE_INDEX, String.valueOf(pageIndex));
        query.put("Sort", "Distance");
        getAPI("search", query, fetchMoreOldContentResponse);
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
                    listingsList.add(new SearchResultListing(list.optJSONObject(i)));
                }

                for (int i = 0; i < size; i++) {
                    if (hide_unavailable_services) {
                        if (listingsList.get(i).Available)
                            displayList.add(listingsList.get(i));
                    } else
                        displayList.add(listingsList.get(i));
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

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.closeSearchModuleAndGoHome){
            goBack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                goBack();
                return true;
            case R.id.home:
                MyApplication.closeSearchModuleAndGoHome = true;
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
