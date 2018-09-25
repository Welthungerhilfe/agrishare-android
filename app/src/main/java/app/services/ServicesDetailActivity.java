package app.services;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import app.dao.FAQ;
import app.dao.Listing;
import app.dao.ListingDetailService;
import app.faqs.FAQAdapter;
import app.faqs.FAQsActivity;

import static app.agrishare.Constants.KEY_LISTING;

public class ServicesDetailActivity extends BaseActivity {

    ServicesDetailAdapter adapter;
    ArrayList<ListingDetailService> serviceList;
    View headerView;

    Listing listing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Services", R.drawable.button_back);
        listing = getIntent().getParcelableExtra(KEY_LISTING);
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
        serviceList = new ArrayList<>();

        try {
            JSONArray servicesArray = new JSONArray(listing.Services);
            int size = servicesArray.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    serviceList.add(new ListingDetailService(servicesArray.optJSONObject(i)));
                }

                if (adapter == null) {
                    adapter = new ServicesDetailAdapter(ServicesDetailActivity.this, serviceList, ServicesDetailActivity.this);
                    listview.setAdapter(adapter);
                    headerView = getLayoutInflater().inflate(R.layout.row_services_header, null);
                    JSONArray photosArray = new JSONArray(listing.Photos);
                    int photos_size = photosArray.length();
                    if (photos_size > 0) {
                        Picasso.get()
                                .load(photosArray.optJSONObject(0).optString("Zoom"))
                                .placeholder(R.drawable.default_image)
                                .into((ImageView) headerView.findViewById(R.id.photo));
                    }
                    ((TextView) headerView.findViewById(R.id.title)).setText(listing.Title);
                    listview.addHeaderView(headerView);
                } else {
                    adapter.notifyDataSetChanged();
                    listview.setAdapter(adapter);
                }
            } else {
                showFeedbackWithButton(R.drawable.empty, getResources().getString(R.string.empty), getResources().getString(R.string.no_services_available));
                setCloseButton();
            }
        } catch (JSONException ex){
            Log("JSONException: "+  ex.getMessage());
        }
    }

    public void setCloseButton(){
        ((Button) findViewById(R.id.feedback_retry)).setText(getResources().getString(R.string.close));
        findViewById(R.id.feedback_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    goBack();
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
