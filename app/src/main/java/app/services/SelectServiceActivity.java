package app.services;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import app.agrishare.BaseActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.category.CategoryActivity;
import app.category.CategoryAdapter;
import app.dao.Category;
import app.dao.Service;
import app.database.Categories;
import io.realm.RealmResults;

import static app.agrishare.Constants.KEY_CATEGORY;
import static app.agrishare.Constants.KEY_ID;
import static app.agrishare.Constants.KEY_LISTING;
import static app.agrishare.Constants.KEY_SERVICE;

public class SelectServiceActivity extends BaseActivity {

    SelectServiceAdapter adapter;
    ArrayList<Service> serviceList;
    long category_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_service);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Services", R.drawable.button_back);
        category_id = getIntent().getLongExtra(KEY_ID, 0);
        Log("CATEGORY ID" + category_id);
        initViews();
    }

    private void initViews(){
        listview = (ListView) findViewById(R.id.list);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.refresher);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFromCache();
            }
        });
        loadFromCache();
    }

    private void loadFromCache(){
        adapter = null;
        serviceList = new ArrayList<>();

        RealmResults<Categories> results = MyApplication.realm.where(Categories.class)
                .equalTo("Id", category_id)
                .findAll();

        int size = results.size();
        if (size > 0) {
            try {
                JSONArray servicesArray = new JSONArray(results.get(0).getServices());
                int services_size = servicesArray.length();
                if (services_size > 0) {
                    for (int i = 0; i < services_size; i++) {
                        serviceList.add(new Service(servicesArray.optJSONObject(i)));
                    }

                    if (adapter == null) {
                        adapter = new SelectServiceAdapter(SelectServiceActivity.this, serviceList, SelectServiceActivity.this);
                        listview.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                        listview.setAdapter(adapter);
                    }
                }
                else {
                    showFeedback(R.drawable.empty, getResources().getString(R.string.empty), getResources().getString(R.string.no_services_found));
                }
            } catch (JSONException ex) {
                Log("JSONException: " + ex.getMessage());
            }
        }
        else {
            showFeedback(R.drawable.empty, getResources().getString(R.string.error), getResources().getString(R.string.invalid_category_id));
        }
    }

    public void returnResult(Service service) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(KEY_SERVICE, service);
        setResult(Activity.RESULT_OK, returnIntent);
        closeKeypad();
        goBack();
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
