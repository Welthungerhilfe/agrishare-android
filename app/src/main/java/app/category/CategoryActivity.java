package app.category;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.agrishare.BaseActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.dao.Category;
import app.dao.FAQ;
import app.database.Categories;
import app.faqs.FAQAdapter;
import app.faqs.FAQsActivity;
import io.realm.RealmResults;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_CATEGORY;

public class CategoryActivity extends BaseActivity {

    CategoryAdapter adapter;
    ArrayList<Category> categoriesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Select Type", R.drawable.white_close);
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
        loadFromCache();
    }

    private void loadFromCache(){
        adapter = null;
        categoriesList = new ArrayList<>();

        RealmResults<Categories> results = MyApplication.realm.where(Categories.class)
                .findAll();

        int size = results.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Category category = new Category(results.get(i));
                categoriesList.add(category);
                Log("MY CATEGORY SERVICES 2: " + category.Services);
            }

            if (adapter == null) {
                adapter = new CategoryAdapter(CategoryActivity.this, categoriesList, CategoryActivity.this);
                listview.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
                listview.setAdapter(adapter);
            }
        }
        else {
            showFeedbackWithButton(R.drawable.empty, "Empty", "No Categories are available at this moment. Please check back later.");
            setRefreshButton();
        }
    }

    public void refresh(){
        adapter = null;
        categoriesList = new ArrayList<>();

        showLoader("Fetching Categories", "Please wait...");
        HashMap<String, String> query = new HashMap<String, String>();
        getAPI("categories/list", query, fetchResponse);
    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("CATEGORIES SUCCESS", result.toString() + "");

            hideLoader();
            refreshComplete();
            JSONArray list = result.optJSONArray("List");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    categoriesList.add(new Category(list.optJSONObject(i), true));
                }

                if (adapter == null) {
                    adapter = new CategoryAdapter(CategoryActivity.this, categoriesList, CategoryActivity.this);
                    listview.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                    listview.setAdapter(adapter);
                }
            }
            else {
                showFeedbackWithButton(R.drawable.empty, "Empty", "No Categories are available at this moment. Please check back later.");
                setRefreshButton();
            }

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("CATEGORIES ERROR:  " + errorMessage);
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

    public void returnResult(Category category) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(KEY_CATEGORY, category);
        setResult(Activity.RESULT_OK, returnIntent);
        closeKeypad();
        goBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                close();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        close();
    }

}
