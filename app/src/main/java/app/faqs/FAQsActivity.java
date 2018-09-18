package app.faqs;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.dao.FAQ;
import okhttp3.Response;

public class FAQsActivity extends BaseActivity {

    FAQAdapter adapter;
    ArrayList<FAQ> faqsList;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("FAQs", R.drawable.button_back);
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
        if(headerView != null)
            listview.removeHeaderView(headerView);
        adapter = null;
        faqsList = new ArrayList<>();

        showLoader("Fetching FAQs", "Please wait...");
        HashMap<String, String> query = new HashMap<String, String>();
        getAPI("faqs/list", query, fetchResponse);
    }


    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("FAQs SUCCESS", result.toString() + "");

            hideLoader();
            refreshComplete();
            JSONArray list = result.optJSONArray("List");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    faqsList.add(new FAQ(list.optJSONObject(i)));
                }

                if (adapter == null) {
                    adapter = new FAQAdapter(FAQsActivity.this, faqsList, FAQsActivity.this);
                    listview.setAdapter(adapter);
                    headerView = (View) getLayoutInflater().inflate(R.layout.row_faqs_header, null);
                    listview.addHeaderView(headerView);
                } else {
                    adapter.notifyDataSetChanged();
                    listview.setAdapter(adapter);
                }
            }
            else {
                showFeedbackWithButton(R.drawable.empty, "Empty", "No FAQs are available at this moment. Please check back later.");
                setRefreshButton();
            }

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR FAQs:  " + errorMessage);
            showFeedbackWithButton(R.drawable.error, "Error", "Please make sure you have a working internet connection.");
            setRefreshButton();
            refreshComplete();

        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    public void setRefreshButton(){
        ((TextView) findViewById(R.id.feedback_retry)).setText("RETRY");
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
