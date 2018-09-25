package app.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.dao.FAQ;
import app.dao.Listing;
import app.faqs.FAQAdapter;
import app.faqs.FAQsActivity;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_SEARCH_QUERY;

public class SearchResultsActivity extends BaseActivity {

    SearchResultsAdapter adapter;
    ArrayList<Listing> listingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Search Results", R.drawable.button_back);
        initViews();
    }

    private void initViews(){
        listview = (ListView) findViewById(R.id.list);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.refresher);
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
        listingsList = new ArrayList<>();

        showLoader("Searching", "Please wait...");
       // HashMap<String, String> query = new HashMap<String, String>();
        HashMap<String, String> query = (HashMap<String, String>) getIntent().getSerializableExtra(KEY_SEARCH_QUERY);
        getAPI("search", query, fetchResponse);
        Intent intent = new Intent();

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
                    listingsList.add(new Listing(list.optJSONObject(i)));
                }

                if (adapter == null) {
                    adapter = new SearchResultsAdapter(SearchResultsActivity.this, listingsList, SearchResultsActivity.this);
                    listview.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                    listview.setAdapter(adapter);
                }
            }
            else {
                showFeedbackWithButton(R.drawable.empty, getResources().getString(R.string.empty), getResources().getString(R.string.no_results_found));
                setRefreshButton();
            }

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("SEARCH RESULTS ERROR:  " + errorMessage);
            showFeedbackWithButton(R.drawable.error, getResources().getString(R.string.error), getResources().getString(R.string.please_make_sure_you_have_working_internet) + " : " + errorMessage);
            setRefreshButton();
            refreshComplete();
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
