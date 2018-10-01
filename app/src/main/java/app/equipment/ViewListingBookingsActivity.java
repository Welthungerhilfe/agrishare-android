package app.equipment;

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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.calendar.CalendarActivity;
import app.dao.Booking;
import app.dao.Listing;
import app.manage.BookingDetailActivity;
import app.manage.ManageSeekingAdapter;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_BOOKING;
import static app.agrishare.Constants.KEY_LISTING;
import static app.agrishare.Constants.KEY_PAGE_INDEX;
import static app.agrishare.Constants.KEY_PAGE_SIZE;

public class ViewListingBookingsActivity extends BaseActivity {

    ManageSeekingAdapter adapter;
    ArrayList<Booking> listingsList;
    View headerView;

    int pageSize = 10;
    int pageIndex = 0;

    Listing listing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_listing_bookings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Bookings", R.drawable.button_back);
        listing = getIntent().getParcelableExtra(KEY_LISTING);
        initViews();


        Intent intent = new Intent(ViewListingBookingsActivity.this, CalendarActivity.class);
        intent.putExtra(KEY_LISTING, listing);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
    }

    private void initViews(){
        listview = findViewById(R.id.list);
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
        if(headerView != null)
            listview.removeHeaderView(headerView);
        adapter = null;
        listingsList = new ArrayList<>();

        showLoader("Fetching Bookings", "Please wait...");
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("ListingId", String.valueOf(listing.Id));
        query.put(KEY_PAGE_SIZE, pageSize + "");
        query.put(KEY_PAGE_INDEX, pageIndex + "");
        getAPI("bookings/listing", query, fetchResponse);
    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("LISTING BOOKINGS SUCCESS"+ result.toString() + "");

            hideLoader();
            refreshComplete();
            JSONArray list = result.optJSONArray("Bookings");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    listingsList.add(new Booking(list.optJSONObject(i), false));
                }

                if (adapter == null) {
                    adapter = new ManageSeekingAdapter(ViewListingBookingsActivity.this, listingsList, ViewListingBookingsActivity.this);
                    listview.setAdapter(adapter);
                    headerView = (View) getLayoutInflater().inflate(R.layout.row_view_calendar, null);
                    headerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            {
                                Intent intent = new Intent(ViewListingBookingsActivity.this, CalendarActivity.class);
                                intent.putExtra(KEY_LISTING, listing);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                            }
                        }
                    });
                    listview.addHeaderView(headerView);
                } else {
                    adapter.notifyDataSetChanged();
                    listview.setAdapter(adapter);
                }
            }
            else {
                showFeedbackWithButton(R.drawable.feedback_empty, getResources().getString(R.string.empty), getResources().getString(R.string.there_are_no_bookings_available));
                setRefreshButton();
            }

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("LISTING BOOKINGS ERROR:  " + errorMessage);
            showFeedbackWithButton(R.drawable.feedback_error, getResources().getString(R.string.error), getResources().getString(R.string.please_make_sure_you_have_working_internet));
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
