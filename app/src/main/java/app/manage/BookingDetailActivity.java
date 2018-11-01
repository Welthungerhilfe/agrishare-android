package app.manage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import app.agrishare.BaseActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.bookings.IncompleteServiceActivity;
import app.c2.android.AsyncResponse;
import app.c2.android.Utils;
import app.calendar.CalendarActivity;
import app.dao.Booking;
import app.dao.GroupMember;
import app.dao.Listing;
import app.dao.ListingDetailService;
import app.dao.Notification;
import app.dao.Photo;
import app.dao.SearchResultListing;
import app.dao.Transaction;
import app.database.Transactions;
import app.equipment.AddEquipmentActivity;
import app.equipment.EquipmentServiceAdapter;
import app.ratings.RatingsActivity;
import app.search.DetailActivity;
import app.search.SliderAdapter;
import app.services.ServicesDetailActivity;
import io.realm.RealmResults;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_BOOKING;
import static app.agrishare.Constants.KEY_BOOKING_ID;
import static app.agrishare.Constants.KEY_EDIT;
import static app.agrishare.Constants.KEY_FROM_NOTIFICATION;
import static app.agrishare.Constants.KEY_ID;
import static app.agrishare.Constants.KEY_LISTING;
import static app.agrishare.Constants.KEY_LISTING_ID;
import static app.agrishare.Constants.KEY_NOTIFICATION;
import static app.agrishare.Constants.KEY_NOTIFICATION_ID;
import static app.agrishare.Constants.KEY_PAGE_INDEX;
import static app.agrishare.Constants.KEY_PAGE_SIZE;
import static app.agrishare.Constants.KEY_REVIEW_NOTIFICATION;
import static app.agrishare.Constants.KEY_SEARCH_RESULT_LISTING;
import static app.agrishare.Constants.KEY_SEEKER;
import static app.agrishare.Constants.LAUNCH_EVENT;
import static app.agrishare.Constants.TRANSACTION_EVENT;

public class BookingDetailActivity extends BaseActivity {

    private static ViewPager mPager;
    private static int currentPage = 0;
    private ArrayList<Photo> imagesList = new ArrayList<>();
    private static Timer swipeTimer;
    int counter_offset = 0;

    int rating_stars = 1;
    Booking booking;
    long bookingId = 0;

    private ArrayList<GroupMember> membersList = new ArrayList<>();
    GroupMemberAdapter adapter;

    boolean autoOpenReviews = false;

    boolean isSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Detail", R.drawable.button_back);
        if (getIntent().hasExtra(KEY_FROM_NOTIFICATION) && getIntent().getBooleanExtra(KEY_FROM_NOTIFICATION, false)) {
            bookingId = getIntent().getLongExtra(KEY_BOOKING_ID, 0);
            if (getIntent().getBooleanExtra(KEY_REVIEW_NOTIFICATION, false)){
                autoOpenReviews = true;
            }
            fetchBookingDetails();
        }
        else if (getIntent().hasExtra(KEY_BOOKING)) {
            booking = getIntent().getParcelableExtra(KEY_BOOKING);
            isSeeking = booking.Seeking;
            bookingId = booking.Id;

            if (getIntent().hasExtra(KEY_REVIEW_NOTIFICATION) && getIntent().getBooleanExtra(KEY_REVIEW_NOTIFICATION, false)){
                autoOpenReviews = true;
            }

            fetchBookingDetails();
        }
    }

    private void initViews(){
        //for group payments
        double total_price_to_pay = booking.Price + booking.HireCost + booking.FuelCost + booking.TransportCost;
        long quantityunitId = 1;
        try {
            JSONObject jsonObject = new JSONObject(booking.Service);
            ListingDetailService listingDetailService = new ListingDetailService(jsonObject);
            quantityunitId = listingDetailService.QuantityUnitId;
        } catch (JSONException ex){
            Log("JSONException" + ex.getMessage());
        }
        listview = findViewById(R.id.group_member_list);
        adapter = new GroupMemberAdapter(BookingDetailActivity.this, membersList, BookingDetailActivity.this, total_price_to_pay, booking.Quantity, quantityunitId);
        listview.setAdapter(adapter);
        Utils.setListViewHeightBasedOnChildren(listview);

        try {
            JSONObject serviceObject = new JSONObject(booking.Service);
            setNavBar(serviceObject.optJSONObject("Category").optString("Title"), R.drawable.button_back);

            JSONArray photosArray = new JSONArray(booking.Listing.Photos);
            int size = photosArray.length();
            for (int i = 0; i < size; i++) {
                imagesList.add(new Photo(photosArray.getJSONObject(i)));
            }

            //using viewpager just so i can display dots
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(new SliderAdapter(BookingDetailActivity.this, imagesList, BookingDetailActivity.this));
            //disable touch/swipe
            mPager.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    return true;
                }
            });

            CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
            indicator.setViewPager(mPager);

            currentPage = 0;
            mPager.setCurrentItem(currentPage);

            Picasso.get()
                    .load(imagesList.get(0).Zoom)
                    .placeholder(R.drawable.default_image)
                    .into((ImageView) findViewById(R.id.photo));


            // Auto start of viewpager
            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == imagesList.size()) {
                        currentPage = 0;
                    }
                    Animation myFadeInAnimation = AnimationUtils.loadAnimation(BookingDetailActivity.this, R.anim.fadein);
                    ((ImageView) findViewById(R.id.photo)).setAnimation(myFadeInAnimation); //Set animation to your ImageView


                    Picasso.get()
                            .load(imagesList.get(currentPage).Zoom)
                            .placeholder(R.drawable.default_image)
                            .into((ImageView) findViewById(R.id.photo));

                    mPager.setCurrentItem(currentPage++);
                    counter_offset = 1;
                    //mPager.setCurrentItem(currentPage++, false);
                }
            };
            swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 4000, 4000);

            ((ImageView) findViewById(R.id.photo)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    {
                                 /*  Intent _intent = new Intent(ProductDetailActivity.this, ImageFullScreenActivity.class);
                                    _intent.putExtra(MyApplication.KEY_ITEM_RESOURCES, getIntent().getStringExtra(MyApplication.KEY_ITEM_RESOURCES));
                                    _intent.putExtra(MyApplication.KEY_CURRENT_IMAGE, currentPage - counter_offset);
                                    startActivity(_intent);
                                    overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold); */
                    }
                }
            });

        } catch (JSONException ex){
            Log("JSONException"+ ex.getMessage());
        }

        ((TextView) findViewById(R.id.title)).setText(booking.Listing.Title);
        ((TextView) findViewById(R.id.description)).setText(booking.Listing.Description);


        if (booking.Listing.Brand != null && !booking.Listing.Brand.isEmpty())
            addRow(getResources().getString(R.string.brand), booking.Listing.Brand);

        if (booking.Listing.HorsePower != 0)
            addRow(getResources().getString(R.string.horse_power), booking.Listing.HorsePower + "HP");

        if (booking.Listing.Year != 0)
            addRow(getResources().getString(R.string.year), String.valueOf(booking.Listing.Year));

        /*if (booking.Listing.Condition != null && !booking.Listing.Condition.isEmpty())
            addRow(getResources().getString(R.string.condition), booking.Listing.Condition);*/

        Log("SEEKING VALUES BOOL: " + isSeeking + " - " + booking.Seeking);
        if (booking.StatusId >= 3 && !booking.Seeking) {
            if (!booking.User.isEmpty()) {
                try {
                    JSONObject userObject = new JSONObject(booking.User);
                    addRow(isSeeking ? getResources().getString(R.string.offeror) : getResources().getString(R.string.seeker), userObject.optString("FirstName") + " " + userObject.optString("LastName"));
                    addRow(getResources().getString(R.string.telephone), userObject.optString("Telephone"));
                    addRow(getResources().getString(R.string.location), booking.Listing.Location);
                    if (booking.DestinationLocation != null && !booking.DestinationLocation.isEmpty() && !booking.DestinationLocation.equals("null"))
                        addRow(getResources().getString(R.string.destination_location), booking.DestinationLocation);
                } catch (JSONException ex) {
                    Log("JSONException" + ex.getMessage());
                }
            }
        }

        displayBookingDetails();
        showAppropriateActionFooter();
    }


    private void displayBookingDetails(){

        try {
            JSONObject jsonObject = new JSONObject(booking.Service);
            ListingDetailService listingDetailService = new ListingDetailService(jsonObject);

            //dates
            ((TextView) findViewById(R.id.dates)).setText(makeFriendlyDateString(booking.StartDate) + " - " + makeFriendlyDateString(booking.EndDate));

            //distance
            if (booking.Distance > 0) {
                (findViewById(R.id.distance_from_location_container)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.distance_from_location)).setText(String.format("%.2f", booking.Distance) + " kilometres");
                ((TextView) findViewById(R.id.distance_unit_charge)).setText("$" + String.format("%.2f", listingDetailService.PricePerDistanceUnit) + "/km");
                ((TextView) findViewById(R.id.distance_total)).setText("$" + String.format("%.2f", booking.TransportCost));
            }
            else {
                (findViewById(R.id.distance_from_location_container)).setVisibility(View.GONE);
            }

            if (booking.Listing.Category.Id == 2){
                (findViewById(R.id.quantity_divider)).setVisibility(View.GONE);
                (findViewById(R.id.quantity_container)).setVisibility(View.GONE);

                (findViewById(R.id.fuel_divider)).setVisibility(View.GONE);
                (findViewById(R.id.fuel_container)).setVisibility(View.GONE);
            }
            else {
                //field size
                if (listingDetailService.QuantityUnitId == 2)
                    ((TextView) findViewById(R.id.quantity_label)).setText(getResources().getString(R.string.bags));
                ((TextView) findViewById(R.id.quantity)).setText(String.format("%.2f", booking.Quantity) + listingDetailService.QuantityUnit);
                ((TextView) findViewById(R.id.quantity_unit_charge)).setText("$" + String.format("%.2f", listingDetailService.PricePerQuantityUnit) + "/" + Utils.getAbbreviatedQuantityUnit(listingDetailService.QuantityUnitId));
                ((TextView) findViewById(R.id.quantity_total)).setText("$" + String.format("%.2f", booking.HireCost));

                (findViewById(R.id.quantity_divider)).setVisibility(View.VISIBLE);
                (findViewById(R.id.quantity_container)).setVisibility(View.VISIBLE);


                //fuel
                if (booking.IncludeFuel) {
                    (findViewById(R.id.fuel_container)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.fuel)).setText(String.format("%.2f", booking.Quantity) + listingDetailService.QuantityUnit);
                    ((TextView) findViewById(R.id.fuel_unit_charge)).setText("$" + String.format("%.2f", listingDetailService.FuelPerQuantityUnit) + "/" + Utils.getAbbreviatedQuantityUnit(listingDetailService.QuantityUnitId));
                    ((TextView) findViewById(R.id.fuel_total)).setText("$" + String.format("%.2f", booking.FuelCost));
                }
                else {
                    (findViewById(R.id.fuel_container)).setVisibility(View.GONE);
                }

                (findViewById(R.id.fuel_divider)).setVisibility(View.VISIBLE);
                (findViewById(R.id.fuel_container)).setVisibility(View.VISIBLE);
            }



            //total
            ((TextView) findViewById(R.id.agrishare_commission)).setText("$" + String.format("%.2f", booking.AgriShareCommission));

            //total
            ((TextView) findViewById(R.id.request_total)).setText("$" + String.format("%.2f", booking.Price));

        } catch (JSONException ex){
            Log.d("JSONException", ex.getMessage());
        }
    }

    private void fetchBookingDetails(){
        showLoader("Fetching Details", "Please wait...");
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("BookingId", String.valueOf(bookingId));
        getAPI("bookings/detail", query, fetchResponse);
    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("BOOKING DETAIL SUCCESS", result.toString() + "");


            hideLoader();
            booking = new Booking(result.optJSONObject("Booking"), getIntent().getBooleanExtra(KEY_SEEKER, false));
            booking.Rated = result.optBoolean("Rated");
            isSeeking = booking.Seeking;

            initViews();

            if (autoOpenReviews) { // coz its coming from notification
                autoOpenReviews = false;
                Intent _intent = new Intent(BookingDetailActivity.this, RatingsActivity.class);
                _intent.putExtra(KEY_LISTING, booking.Listing);
                startActivity(_intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("BOOKING DETAIL:  " + errorMessage);
            showFeedbackWithButton(R.drawable.feedback_error, getResources().getString(R.string.error), getResources().getString(R.string.failed_to_fetch_listing_details));
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
                    fetchBookingDetails();
                }
            }
        });
    }

    private void showAppropriateActionFooter(){
        if (booking.Seeking){
            if (booking.StatusId == 0){
                (findViewById(R.id.waiting_for_feedback)).setVisibility(View.VISIBLE);
                (findViewById(R.id.waiting_for_payment)).setVisibility(View.GONE);
                (findViewById(R.id.confirm)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash)).setVisibility(View.GONE);
                (findViewById(R.id.complete)).setVisibility(View.GONE);
                (findViewById(R.id.in_progress)).setVisibility(View.GONE);
                (findViewById(R.id.please_leave_a_review)).setVisibility(View.GONE);
                (findViewById(R.id.all_done)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
            }
            else if (booking.StatusId == 1){
                boolean pollForEcocash = false;
                RealmResults<Transactions> results = MyApplication.realm.where(Transactions.class).findAll();
                int size = results.size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                         if (results.get(i).getBookingId() == booking.Id) {
                             if (results.get(i).getStatusId() == 14) {
                                 //pending
                                 pollForEcocash = true;
                             }
                         }
                    }
                }

                if (pollForEcocash) {
                    //there is an on going transaction so poll
                    pollForEcocash();
                }
                else {
                    (findViewById(R.id.waiting_for_feedback)).setVisibility(View.GONE);
                    (findViewById(R.id.waiting_for_payment)).setVisibility(View.GONE);
                    (findViewById(R.id.confirm)).setVisibility(View.GONE);
                    (findViewById(R.id.complete)).setVisibility(View.GONE);
                    (findViewById(R.id.in_progress)).setVisibility(View.GONE);
                    (findViewById(R.id.please_leave_a_review)).setVisibility(View.GONE);
                    (findViewById(R.id.all_done)).setVisibility(View.GONE);

                    if (booking.ForId == 0) {
                        ((EditText) findViewById(R.id.ecocash_number)).setText(MyApplication.currentUser.Telephone);
                        (findViewById(R.id.pay_with_ecocash)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.full_name_parent_container)).setVisibility(View.GONE);
                        (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.pay_with_ecocash_caption)).setText(getResources().getString(R.string.enter_your_ecocash_number));
                    } else if (booking.ForId == 1) {
                        (findViewById(R.id.pay_with_ecocash)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.full_name_parent_container)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.pay_with_ecocash_caption)).setText(getResources().getString(R.string.enter_the_ecocash_number_for_the_person));
                    } else if (booking.ForId == 2) {
                        (findViewById(R.id.pay_with_ecocash)).setVisibility(View.GONE);
                        (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.VISIBLE);

                        addNewGroupMember();

                    }
                }
            }
            else if (booking.StatusId == 3){
                (findViewById(R.id.waiting_for_feedback)).setVisibility(View.GONE);
                (findViewById(R.id.waiting_for_payment)).setVisibility(View.GONE);
                (findViewById(R.id.confirm)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash)).setVisibility(View.GONE);
                (findViewById(R.id.please_leave_a_review)).setVisibility(View.GONE);
                (findViewById(R.id.all_done)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
                (findViewById(R.id.complete)).setVisibility(View.VISIBLE);
                (findViewById(R.id.in_progress)).setVisibility(View.GONE);

                /*try {
                    if (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(booking.EndDate).after(new Date())) {
                        (findViewById(R.id.in_progress)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.complete)).setVisibility(View.GONE);
                    }
                    else {
                        (findViewById(R.id.in_progress)).setVisibility(View.GONE);
                        (findViewById(R.id.complete)).setVisibility(View.VISIBLE);
                    }
                } catch (ParseException ex){
                    Log.d("ParseException", "CompetitionsAdapter: " + ex.getMessage());
                }*/

            }
            else if (booking.StatusId == 4){
                (findViewById(R.id.waiting_for_feedback)).setVisibility(View.GONE);
                (findViewById(R.id.waiting_for_payment)).setVisibility(View.GONE);
                (findViewById(R.id.confirm)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash)).setVisibility(View.GONE);
                (findViewById(R.id.complete)).setVisibility(View.GONE);
                (findViewById(R.id.in_progress)).setVisibility(View.GONE);
                if (booking.Rated)
                    (findViewById(R.id.please_leave_a_review)).setVisibility(View.GONE);
                else
                    (findViewById(R.id.please_leave_a_review)).setVisibility(View.VISIBLE);
                (findViewById(R.id.all_done)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
            }
            else {
                (findViewById(R.id.waiting_for_feedback)).setVisibility(View.GONE);
                (findViewById(R.id.waiting_for_payment)).setVisibility(View.GONE);
                (findViewById(R.id.confirm)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash)).setVisibility(View.GONE);
                (findViewById(R.id.complete)).setVisibility(View.GONE);
                (findViewById(R.id.in_progress)).setVisibility(View.GONE);
                (findViewById(R.id.please_leave_a_review)).setVisibility(View.GONE);
                (findViewById(R.id.all_done)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
            }

        }
        else {
            if (booking.StatusId == 0){
                (findViewById(R.id.waiting_for_feedback)).setVisibility(View.GONE);
                (findViewById(R.id.waiting_for_payment)).setVisibility(View.GONE);
                (findViewById(R.id.confirm)).setVisibility(View.VISIBLE);
                (findViewById(R.id.pay_with_ecocash)).setVisibility(View.GONE);
                (findViewById(R.id.complete)).setVisibility(View.GONE);
                (findViewById(R.id.in_progress)).setVisibility(View.GONE);
                (findViewById(R.id.please_leave_a_review)).setVisibility(View.GONE);
                (findViewById(R.id.all_done)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
            }
            else if (booking.StatusId == 1){
                (findViewById(R.id.waiting_for_feedback)).setVisibility(View.GONE);
                (findViewById(R.id.waiting_for_payment)).setVisibility(View.VISIBLE);
                (findViewById(R.id.confirm)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash)).setVisibility(View.GONE);
                (findViewById(R.id.complete)).setVisibility(View.GONE);
                (findViewById(R.id.in_progress)).setVisibility(View.GONE);
                (findViewById(R.id.please_leave_a_review)).setVisibility(View.GONE);
                (findViewById(R.id.all_done)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
            }
            else if (booking.StatusId == 3){
                (findViewById(R.id.waiting_for_feedback)).setVisibility(View.GONE);
                (findViewById(R.id.waiting_for_payment)).setVisibility(View.GONE);
                (findViewById(R.id.confirm)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash)).setVisibility(View.GONE);
                (findViewById(R.id.complete)).setVisibility(View.GONE);
                (findViewById(R.id.in_progress)).setVisibility(View.VISIBLE);
                (findViewById(R.id.please_leave_a_review)).setVisibility(View.GONE);
                (findViewById(R.id.all_done)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
            }
            else if (booking.StatusId == 4){
                (findViewById(R.id.waiting_for_feedback)).setVisibility(View.GONE);
                (findViewById(R.id.waiting_for_payment)).setVisibility(View.GONE);
                (findViewById(R.id.confirm)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash)).setVisibility(View.GONE);
                (findViewById(R.id.complete)).setVisibility(View.GONE);
                (findViewById(R.id.in_progress)).setVisibility(View.GONE);
                (findViewById(R.id.please_leave_a_review)).setVisibility(View.GONE);
                (findViewById(R.id.all_done)).setVisibility(View.VISIBLE);
                (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
            }
            else {
                (findViewById(R.id.waiting_for_feedback)).setVisibility(View.GONE);
                (findViewById(R.id.waiting_for_payment)).setVisibility(View.GONE);
                (findViewById(R.id.confirm)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash)).setVisibility(View.GONE);
                (findViewById(R.id.complete)).setVisibility(View.GONE);
                (findViewById(R.id.in_progress)).setVisibility(View.GONE);
                (findViewById(R.id.please_leave_a_review)).setVisibility(View.GONE);
                (findViewById(R.id.all_done)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
            }
        }


        (findViewById(R.id.decline)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showFooterLoader("Declining", "Please wait...");
                HashMap<String, String> query = new HashMap<String, String>();
                query.put("BookingId", String.valueOf(booking.Id));
                getAPI("bookings/decline", query, fetchDeclineResponse);
            }
        });

        (findViewById(R.id.approve)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showFooterLoader("Approving", "Please wait...");
                HashMap<String, String> query = new HashMap<String, String>();
                query.put("BookingId", String.valueOf(booking.Id));
                getAPI("bookings/confirm", query, fetchApproveResponse);
            }
        });

        (findViewById(R.id.complete_button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showFooterLoader("Finishing", "Please wait...");
                HashMap<String, String> query = new HashMap<String, String>();
                query.put("BookingId", String.valueOf(booking.Id));
                getAPI("bookings/complete", query, fetchCompleteBookingResponse);
            }
        });

        (findViewById(R.id.not_complete_button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(BookingDetailActivity.this, IncompleteServiceActivity.class);
                intent.putExtra(KEY_BOOKING, booking);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }
        });

        (findViewById(R.id.star_container1)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                rating_stars = 1;
                showStars(0);
            }
        });

        (findViewById(R.id.star_container2)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                rating_stars = 2;
                showStars(1);
            }
        });

        (findViewById(R.id.star_container3)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                rating_stars = 3;
                showStars(2);
            }
        });

        (findViewById(R.id.star_container4)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                rating_stars = 4;
                showStars(3);
            }
        });

        (findViewById(R.id.star_container5)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                rating_stars = 5;
                showStars(4);
            }
        });

        ((EditText) findViewById(R.id.rating_message)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitRating();
                    return true;
                }
                return false;
            }
        });

        (findViewById(R.id.rating_submit)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                submitRating();
            }
        });

        (findViewById(R.id.add_group_member)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addNewGroupMember();
            }
        });

        (findViewById(R.id.group_pay_now)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                boolean readyToSubmit = true;
                String error_message = "";
                int size = membersList.size();
                if (size > 0){
                    for (int i = 0; i < size; i++){
                        if (membersList.get(i).name.isEmpty()){
                            readyToSubmit = false;
                            error_message = getResources().getString(R.string.name_is_required);
                            break;
                        }

                        if (membersList.get(i).quantity == 0){
                            readyToSubmit = false;
                            error_message = getResources().getString(R.string.quantity_is_required);
                            break;
                        }

                        if (membersList.get(i).ecocash_number.isEmpty()){
                            readyToSubmit = false;
                            error_message = getResources().getString(R.string.ecocash_is_required);
                            break;
                        }
                    }

                    if (readyToSubmit) {
                        try {
                            double total_quantity = 0;
                            JSONArray groupPaymentUsersArray = new JSONArray();
                            for (int j = 0; j < size; j++) {
                                JSONObject paymentUserObject = new JSONObject();
                                paymentUserObject.accumulate("Quantity", membersList.get(j).quantity);
                                paymentUserObject.accumulate("Telephone", membersList.get(j).ecocash_number);
                                paymentUserObject.accumulate("Name", membersList.get(j).name);
                                groupPaymentUsersArray.put(paymentUserObject);
                                total_quantity = total_quantity + membersList.get(j).quantity;
                            }

                            if (total_quantity != booking.Quantity) {
                                readyToSubmit = false;
                                error_message = getResources().getString(R.string.error_members_quantity_does_not_match_booking_total);
                            }

                            if (readyToSubmit)
                                pay(groupPaymentUsersArray);
                            else {
                                popToast(BookingDetailActivity.this, error_message);
                            }
                        } catch (JSONException ex) {
                            Log.d("JSONException", ex.getMessage());
                            popToast(BookingDetailActivity.this, ex.getMessage());
                        }
                    }
                    else {
                        popToast(BookingDetailActivity.this, error_message);
                    }
                }
                else {
                    popToast(BookingDetailActivity.this, getResources().getString(R.string.please_add_at_least_one_member));
                }


            }
        });

        (findViewById(R.id.pay_now)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                EditText fname_editText = findViewById(R.id.fname);
                EditText lname_editText = findViewById(R.id.lname);
                EditText phone_editText = findViewById(R.id.ecocash_number);
                fname_editText.setError(null);
                lname_editText.setError(null);
                phone_editText.setError(null);

                if (fname_editText.getText().toString().isEmpty() && booking.Id ==1){
                    fname_editText.setError(getString(R.string.error_field_required));
                    fname_editText.requestFocus();
                }
                else if (lname_editText.getText().toString().isEmpty() && booking.Id ==1){
                    lname_editText.setError(getString(R.string.error_field_required));
                    lname_editText.requestFocus();
                }
                else if (phone_editText.getText().toString().isEmpty()){
                    phone_editText.setError(getString(R.string.error_field_required));
                    phone_editText.requestFocus();
                }
                else {

                    try {
                        JSONObject paymentUserObject = new JSONObject();
                        paymentUserObject.accumulate("Quantity", booking.Quantity);
                        paymentUserObject.accumulate("Telephone", phone_editText.getText().toString());

                        if (booking.ForId == 0) {
                            paymentUserObject.accumulate( "Name", MyApplication.currentUser.FirstName + " " + MyApplication.currentUser.LastName);
                        } else if (booking.ForId == 1) {
                            paymentUserObject.accumulate( "Name", fname_editText.getText().toString() + " " + lname_editText.getText().toString());
                        }

                        JSONArray groupPaymentUsersArray = new JSONArray();
                        groupPaymentUsersArray.put(paymentUserObject);

                        pay(groupPaymentUsersArray);
                    } catch (JSONException ex){
                        Log.d("JSONException", ex.getMessage());
                        popToast(BookingDetailActivity.this, ex.getMessage());
                    }
                }

            }
        });
    }

    private void submitRating(){
        EditText message_editText = findViewById(R.id.rating_message);
        message_editText.setError(null);

        if (message_editText.getText().toString().isEmpty()){
            message_editText.setError(getString(R.string.error_field_required));
            message_editText.requestFocus();
        }
        else {
            closeKeypad();
            showFooterLoader("Submitting Review", "Please wait...");
            HashMap<String, Object> query = new HashMap<String, Object>();
            query.put("BookingId", booking.Id);
            query.put("Rating", rating_stars);
            query.put("Comments", message_editText.getText().toString());
            postAPI("ratings/add", query, fetchSubmitReviewResponse);
        }
    }

    private void pay(JSONArray groupPaymentUsersArray){
        showFooterLoader("Processing", "Please wait...");
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("BookingId", booking.Id);
        query.put("Users", groupPaymentUsersArray);
        postAPI("transactions/create", query, fetchPayResponse);
    }

    private void addNewGroupMember(){
        membersList.add(new GroupMember());
        adapter.notifyDataSetChanged();
        Utils.setListViewHeightBasedOnChildren(listview);
        setupGroupMemberFooterText();
    }

    public void removeGroupMember(int position){
        membersList.remove(position);
        adapter.notifyDataSetChanged();
        Utils.setListViewHeightBasedOnChildren(listview);
        setupGroupMemberFooterText();
    }

    private void setupGroupMemberFooterText(){
        try {
            JSONObject serviceObject = new JSONObject(booking.Service);
            String text = "";
            if (membersList.size() == 1)
                text = "You have added " +  membersList.size() + " member and the total quantity is " + booking.Quantity + serviceObject.optString("QuantityUnit");
            else
                text = "You have added " +  membersList.size() + " members and the total quantity is " + booking.Quantity + serviceObject.optString("QuantityUnit");
            ((TextView) findViewById(R.id.you_have_added)).setText(text);

        } catch (JSONException ex){
            Log("JSONException" +  ex.getMessage());
        }
    }


    private void showStars(int position){
        if (position == 0){
            ((ImageView) findViewById(R.id.star1)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star2)).setImageDrawable(getResources().getDrawable(R.drawable.grey_star_60));
            ((ImageView) findViewById(R.id.star3)).setImageDrawable(getResources().getDrawable(R.drawable.grey_star_60));
            ((ImageView) findViewById(R.id.star4)).setImageDrawable(getResources().getDrawable(R.drawable.grey_star_60));
            ((ImageView) findViewById(R.id.star5)).setImageDrawable(getResources().getDrawable(R.drawable.grey_star_60));
        }
        else if (position == 1){
            ((ImageView) findViewById(R.id.star1)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star2)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star3)).setImageDrawable(getResources().getDrawable(R.drawable.grey_star_60));
            ((ImageView) findViewById(R.id.star4)).setImageDrawable(getResources().getDrawable(R.drawable.grey_star_60));
            ((ImageView) findViewById(R.id.star5)).setImageDrawable(getResources().getDrawable(R.drawable.grey_star_60));
        }
        else if (position == 2){
            ((ImageView) findViewById(R.id.star1)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star2)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star3)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star4)).setImageDrawable(getResources().getDrawable(R.drawable.grey_star_60));
            ((ImageView) findViewById(R.id.star5)).setImageDrawable(getResources().getDrawable(R.drawable.grey_star_60));
        }
        else if (position == 3){
            ((ImageView) findViewById(R.id.star1)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star2)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star3)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star4)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star5)).setImageDrawable(getResources().getDrawable(R.drawable.grey_star_60));
        }
        else if (position == 4){
            ((ImageView) findViewById(R.id.star1)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star2)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star3)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star4)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
            ((ImageView) findViewById(R.id.star5)).setImageDrawable(getResources().getDrawable(R.drawable.green_star_60));
        }
    }

    AsyncResponse fetchSubmitReviewResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("SUBMIT REVIEW SUCCES", result.toString() + "");
            hideFooterLoader();
            showFooterFeedback(R.drawable.feedbacksuccess, getResources().getString(R.string.done), "Your review has been submitted.");
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("SUBMIT REVIEW ERROR:  " + errorMessage);
            hideFooterLoader();
            popToast(BookingDetailActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {}
    };

    AsyncResponse fetchPayResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("TRANSACTIONS SUCCESS", result.toString() + "");
            hideFooterLoader();
          //  pollForEcocash();         PLEASE PUT BACK POLLING FOR LIVE

            //PLEASE REMOVE THIS: ONLY HERE FOR TESTING....
            //REMOVE FROM HERE...........
            hideFooterLoader();
            (findViewById(R.id.waiting_for_feedback)).setVisibility(View.GONE);
            (findViewById(R.id.waiting_for_payment)).setVisibility(View.GONE);
            (findViewById(R.id.confirm)).setVisibility(View.GONE);
            (findViewById(R.id.pay_with_ecocash)).setVisibility(View.GONE);
            (findViewById(R.id.complete)).setVisibility(View.VISIBLE);
            (findViewById(R.id.please_leave_a_review)).setVisibility(View.GONE);
            (findViewById(R.id.all_done)).setVisibility(View.GONE);
            (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
            //.....UP TO HERE.....
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("TRANSACTIONS ERROR:  " + errorMessage);
            hideFooterLoader();
            popToast(BookingDetailActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {}
    };

    private void pollForEcocash(){
        showFooterLoader("Polling Ecocash", "Please wait...");
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("BookingId", booking.Id);
        postAPI("transactions/ecocash/poll", query, fetchEcocashPollResponse);
    }

    AsyncResponse fetchEcocashPollResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("POLL SUCCESS", result.toString() + "");

            hideFooterLoader();

            RealmResults<Transactions> transactions = MyApplication.realm.where(Transactions.class).findAll();
            MyApplication.realm.beginTransaction();
            transactions.deleteAllFromRealm();
            MyApplication.realm.commitTransaction();

            Transaction currentTransaction = null;

            JSONArray list = result.optJSONArray("Transactions");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++){
                    Transaction transaction = new Transaction(list.optJSONObject(i));
                    if (transaction.BookingId == booking.Id){
                        currentTransaction = transaction;
                    }
                }

                if (currentTransaction != null) {
                    if (currentTransaction.StatusId == 1) {
                        popToast(BookingDetailActivity.this, getResources().getString(R.string.the_transaction_was_cancelled));
                        showAppropriateActionFooter();
                    }
                    else if (currentTransaction.StatusId == 2) {
                        popToast(BookingDetailActivity.this, getResources().getString(R.string.the_transaction_was_created_but_not_paid));
                        showAppropriateActionFooter();
                    }
                    else if (currentTransaction.StatusId == 5) {
                        popToast(BookingDetailActivity.this, getResources().getString(R.string.the_transaction_failed));
                        showAppropriateActionFooter();
                    }
                    else if (currentTransaction.StatusId == 9) {
                        popToast(BookingDetailActivity.this, getResources().getString(R.string.the_transaction_was_refused));
                        showAppropriateActionFooter();
                    }
                    else if (currentTransaction.StatusId == 10) {
                        popToast(BookingDetailActivity.this, getResources().getString(R.string.the_transaction_was_denied));
                        showAppropriateActionFooter();
                    }
                    else if (currentTransaction.StatusId == 12) {
                        popToast(BookingDetailActivity.this, getResources().getString(R.string.the_transaction_was_deleted));
                        showAppropriateActionFooter();
                    }
                    else if (currentTransaction.StatusId == 4) {           //PAID
                        try {
                            JSONObject bookingUserObject = new JSONObject(currentTransaction.BookingUser);
                            booking.StatusId = bookingUserObject.optLong("StatusId");
                            booking.Status = bookingUserObject.optString("Status");
                        } catch (JSONException ex) {
                            Log("JSONException " + ex.getMessage());
                        }
                        showAppropriateActionFooter();
                    }
                    else if (currentTransaction.StatusId == 13) {           //pending
                        pollForEcocash();
                    }
                    else {
                        popToast(BookingDetailActivity.this, ".");
                        showAppropriateActionFooter();
                    }
                }
                else {
                    //can't find the transaction associated to the booking. shouldn't come to this though
                    showAppropriateActionFooter();
                }

            }
            else {
                showAppropriateActionFooter();
            }

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("POLL ERROR:  " + errorMessage);
            hideFooterLoader();
            popToast(BookingDetailActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {}
    };

    AsyncResponse fetchCompleteBookingResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("COMPLETE BOOKING SUCCES", result.toString() + "");
            hideFooterLoader();
            booking.StatusId = result.optJSONObject("Booking").optLong("StatusId");
            booking.Status = result.optJSONObject("Booking").optString("Status");
            showAppropriateActionFooter();
            MyApplication.refreshManageOfferingTab = true;
            //sendEventToServer(TRANSACTION_EVENT, booking.Service, "", "", 1, false);

            try {
                JSONObject serviceObject = new JSONObject(booking.Service);
                String service_title = serviceObject.optJSONObject("Category").optString("Title");
                //sendEventToServer(TRANSACTION_EVENT, service_title, "", "", 1, false);
            } catch (JSONException ex) {
                Log("JSONException: " + ex.getMessage());
            }
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("COMPLETE BOOKING ERROR:  " + errorMessage);
            hideFooterLoader();
            popToast(BookingDetailActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {}
    };

    AsyncResponse fetchDeclineResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("DECLINE BOOKING SUCCESS", result.toString() + "");
            hideFooterLoader();
            booking.StatusId = result.optJSONObject("Booking").optLong("StatusId");
            booking.Status = result.optJSONObject("Booking").optString("Status");
            showAppropriateActionFooter();
            MyApplication.refreshManageOfferingTab = true;
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("DECLINE BOOKING ERROR:  " + errorMessage);
            hideFooterLoader();
            popToast(BookingDetailActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {}
    };

    AsyncResponse fetchApproveResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("APPROVE BOOKING SUCCESS", result.toString() + "");
            hideFooterLoader();
            booking.StatusId = result.optJSONObject("Booking").optLong("StatusId");
            booking.Status = result.optJSONObject("Booking").optString("Status");
            showAppropriateActionFooter();
            MyApplication.refreshManageOfferingTab = true;
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("APPROVE BOOKING ERROR:  " + errorMessage);
            hideFooterLoader();
            popToast(BookingDetailActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {}
    };

    public static String makeFriendlyDateString(String raw_date)
    {
        Date date = Utils.formatStringAsDate(raw_date);
        return formatDateAsFriendlyDateDayString(date);
    }

    public static String formatDateAsFriendlyDateDayString(Date date) {
        return new SimpleDateFormat("EEEE dd, MMMM").format(date);
    }

    private void addRow(String label, String value){
        final View specsView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_spec, null, false);
        ((TextView) specsView.findViewById(R.id.label)).setText(label);
        ((TextView) specsView.findViewById(R.id.value)).setText(value);
        ((LinearLayout) findViewById(R.id.specs_container)).addView(specsView);
    }

    public void showFooterFeedback(int iconResourceId, String title, String message) {
        findViewById(R.id.feedback_for_payment_footer).setVisibility(View.VISIBLE);
        findViewById(R.id.feedback_activity_for_payment_footer).setVisibility(View.GONE);
        findViewById(R.id.feedback_progress_for_payment_footer).setVisibility(View.GONE);
        findViewById(R.id.feedback_icon_for_payment_footer).setVisibility(View.VISIBLE);
        ((ImageView)findViewById(R.id.feedback_icon_for_payment_footer)).setImageResource(iconResourceId);
        ((TextView)findViewById(R.id.feedback_title_for_payment_footer)).setText(title);
        ((TextView)findViewById(R.id.feedback_message_for_payment_footer)).setText(message);
        findViewById(R.id.feedback_retry_for_payment_footer).setVisibility(View.GONE);

        findViewById(R.id.footer_subcontainer).setVisibility(View.GONE);
    }

    public void showFooterFeedbackWithButton(int iconResourceId, String title, String message) {
        findViewById(R.id.feedback_for_payment_footer).setVisibility(View.VISIBLE);
        findViewById(R.id.feedback_activity_for_payment_footer).setVisibility(View.GONE);
        findViewById(R.id.feedback_progress_for_payment_footer).setVisibility(View.GONE);
        findViewById(R.id.feedback_icon_for_payment_footer).setVisibility(View.VISIBLE);
        ((ImageView)findViewById(R.id.feedback_icon_for_payment_footer)).setImageResource(iconResourceId);
        ((TextView)findViewById(R.id.feedback_title_for_payment_footer)).setText(title);
        ((TextView)findViewById(R.id.feedback_message_for_payment_footer)).setText(message);
        findViewById(R.id.feedback_retry_for_payment_footer).setVisibility(View.VISIBLE);

        findViewById(R.id.footer_subcontainer).setVisibility(View.GONE);
    }

    public void hideFooterFeedback() {
        findViewById(R.id.feedback_for_payment_footer).setVisibility(View.GONE);
        findViewById(R.id.footer_subcontainer).setVisibility(View.VISIBLE);
    }

    public void showFooterLoader() {
        showFooterLoader("", "");
    }

    public void showFooterLoader(String title, String message) {
        findViewById(R.id.feedback_for_payment_footer).setVisibility(View.VISIBLE);
        findViewById(R.id.feedback_activity_for_payment_footer).setVisibility(View.VISIBLE);
        findViewById(R.id.feedback_progress_for_payment_footer).setVisibility(View.GONE);
        findViewById(R.id.feedback_icon_for_payment_footer).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.feedback_title_for_payment_footer)).setText(title);
        ((TextView)findViewById(R.id.feedback_message_for_payment_footer)).setText(message);
        findViewById(R.id.feedback_retry_for_payment_footer).setVisibility(View.GONE);

        findViewById(R.id.footer_subcontainer).setVisibility(View.GONE);
    }

    public void showFooterLoader(String title, String message, int progress) {
        findViewById(R.id.feedback_for_payment_footer).setVisibility(View.VISIBLE);
        findViewById(R.id.feedback_activity_for_payment_footer).setVisibility(View.GONE);
        findViewById(R.id.feedback_progress_for_payment_footer).setVisibility(View.VISIBLE);
        ((DonutProgress) findViewById(R.id.feedback_progress_for_payment_footer)).setProgress(progress);
        findViewById(R.id.feedback_icon_for_payment_footer).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.feedback_title_for_payment_footer)).setText(title);
        ((TextView)findViewById(R.id.feedback_message_for_payment_footer)).setText(message);
        findViewById(R.id.feedback_retry_for_payment_footer).setVisibility(View.GONE);

        findViewById(R.id.footer_subcontainer).setVisibility(View.GONE);
    }

    public void hideFooterLoader() {
        findViewById(R.id.feedback_for_payment_footer).setVisibility(View.GONE);
        findViewById(R.id.footer_subcontainer).setVisibility(View.VISIBLE);
    }

    private void verifyIfUserWantsToCancelBooking(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BookingDetailActivity.this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.cancel_booking));
        alertDialogBuilder
                .setMessage(getResources().getString(R.string.are_you_sure_you_want_to_cancel_booking))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Query and update the result asynchronously in another thread
                        cancelBooking();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);
    }

    private void cancelBooking(){
        showLoader("Canceling Booking", "Please wait...");
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("BookingId", String.valueOf(booking.Id));
        getAPI("bookings/cancel", query, fetchCancelBookingResponse);
    }

    AsyncResponse fetchCancelBookingResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("BOOKING CANCEL SUCCESS", result.toString() + "");
            showFeedbackWithButton(R.drawable.feedbacksuccess, getResources().getString(R.string.done), getResources().getString(R.string.booking_has_been_canceled));
            MyApplication.refreshManageOfferingTab = true;
            setCloseButton();
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("BOOKING CANCEL:  " + errorMessage);
            hideLoader();
            popAlert(BookingDetailActivity.this, "Error", errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    public void setCloseButton(){
        ((Button) (findViewById(R.id.feedback_retry))).setText(getResources().getString(R.string.close));
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
    protected void onResume() {
        super.onResume();
        if (MyApplication.closeBookingDetailActivity) {
            MyApplication.closeBookingDetailActivity = false;
            goBack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_booking_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                goBack();
                return true;
            case R.id.cancel_booking:
                verifyIfUserWantsToCancelBooking();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(swipeTimer != null) {
            swipeTimer.cancel();
            swipeTimer = null;
        }
    }

}
