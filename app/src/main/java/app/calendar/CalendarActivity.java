package app.calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.c2.android.Utils;
import app.dao.Booking;
import app.dao.Listing;
import app.dao.MyCalendar;
import app.equipment.ViewListingBookingsActivity;
import app.manage.BookingDetailActivity;
import app.manage.ManageSeekingAdapter;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_DAYS;
import static app.agrishare.Constants.KEY_EQUIPMENT_SERVICE;
import static app.agrishare.Constants.KEY_LISTING;
import static app.agrishare.Constants.KEY_PAGE_INDEX;
import static app.agrishare.Constants.KEY_PAGE_SIZE;
import static app.agrishare.Constants.KEY_SEARCH_RESULT_LISTING;
import static app.agrishare.Constants.KEY_START_DATE;

public class CalendarActivity extends BaseActivity {

    CalendarAdapter adapter;
    ImageView left_ImageView, right_ImageView;

    Listing listing;
    ArrayList<MyCalendar> daysList = new ArrayList<>();

    java.util.Calendar calendar;
    String start_date= "1999-01-01T12:59:59";
    String end_date= "1999-01-01T12:59:59";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Bookings", R.drawable.button_back);
        listing = getIntent().getParcelableExtra(KEY_LISTING);
        initViews();
    //    initializeDateTimes();
    }

    public void initViews(){
        listview = findViewById(R.id.list);
        left_ImageView =findViewById(R.id.left);
        right_ImageView =  findViewById(R.id.right);

        calendar = java.util.Calendar.getInstance();
        fetchSlots();

        left_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    calendar.add(Calendar.MONTH, -1);
                    Calendar calendarNow = java.util.Calendar.getInstance();
                    if (calendarNow.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && calendarNow.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
                        calendar.set(Calendar.DATE, calendarNow.get(Calendar.DATE));
                    }
                    else {
                        calendar.set(Calendar.DATE, 1);
                    }
                    fetchSlots();
                }
            }
        });

        right_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    calendar.add(Calendar.MONTH, 1);
                    Calendar calendarNow = java.util.Calendar.getInstance();
                    if (calendarNow.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && calendarNow.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
                        calendar.set(Calendar.DATE, calendarNow.get(Calendar.DATE));
                    }
                    else {
                        calendar.set(Calendar.DATE, 1);
                    }
                    fetchSlots();
                }
            }
        });

    }

    public void fetchSlots(){
        java.util.Calendar today_calendar = java.util.Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        String stringMonthToday = dateFormat.format(today_calendar.getTime());

        SimpleDateFormat df = new SimpleDateFormat("MM");
        String stringMonth = df.format(calendar.getTime());

        if (stringMonth.equals(stringMonthToday)) {
            disableLeftButton();
        }
        else {
            enableArrowButtons();
        }
        Log("CALENDAR BEFORE: " + calendar.get(Calendar.DATE) + " - " + calendar.get(Calendar.MONTH) + calendar.get(Calendar.YEAR));

        //Copy current calendar object first...
        Calendar nextMonthCalendar = java.util.Calendar.getInstance();
        nextMonthCalendar.set(Calendar.DATE, calendar.get(Calendar.DATE));
        nextMonthCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        nextMonthCalendar.set(Calendar.YEAR,  calendar.get(Calendar.YEAR));


        //...then set to first day of next month at time 00:00
        nextMonthCalendar.set(Calendar.HOUR, 0);
        nextMonthCalendar.set(Calendar.MINUTE, 0);
        nextMonthCalendar.set(Calendar.SECOND, 0);
        nextMonthCalendar.set(Calendar.DATE, 1);
        if (calendar.get(Calendar.MONTH) == 11){
            nextMonthCalendar.set(Calendar.MONTH, 0);
            nextMonthCalendar.add(Calendar.YEAR, 1);
        }
        else {
            nextMonthCalendar.add(Calendar.MONTH, 1);
        }
        Log("CALENDAR AFTER: " + calendar.get(Calendar.DATE) + " - " + calendar.get(Calendar.MONTH) + calendar.get(Calendar.YEAR));

        ((TextView) findViewById(R.id.month)).setText(Utils.getCalendarDateAsMonthString(CalendarActivity.this, calendar));
        showLoader("Fetching slots", "Please wait...");
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("ListingId", String.valueOf(listing.Id));
        query.put("StartDate", Utils.getCalendarAsStringDateTime(calendar));
        query.put("EndDate", Utils.getCalendarAsStringDateTime(nextMonthCalendar));
       // query.put("EndDate", Utils.getLastDateOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)));
        getAPI("listings/availability", query, fetchResponse);
    }

    public void disableLeftButton(){
        left_ImageView.setEnabled(false);
    }

    public void disableRightButton(){
        right_ImageView.setEnabled(false);
    }

    public void enableArrowButtons(){
        left_ImageView.setEnabled(true);
        right_ImageView.setEnabled(true);
    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("LISTING AVAILABILITY SUCCESS"+ result.toString() + "");
            daysList.clear();

            hideLoader();
            JSONArray list = result.optJSONArray("Calendar");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    daysList.add(new MyCalendar(list.optJSONObject(i)));
                }

                if (adapter == null) {
                    adapter = new CalendarAdapter(CalendarActivity.this, daysList, CalendarActivity.this);
                    listview.setAdapter(adapter);
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
            Log("LISTING AVAILABILITY ERROR:  " + errorMessage);
            showFeedbackWithButton(R.drawable.feedback_error, getResources().getString(R.string.error), getResources().getString(R.string.please_make_sure_you_have_working_internet));
            setRefreshButton();
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
                    fetchSlots();
                }
            }
        });
    }

    public void returnResult(String start_date) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(KEY_START_DATE, start_date);
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
