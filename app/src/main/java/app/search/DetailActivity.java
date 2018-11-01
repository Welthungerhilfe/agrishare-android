package app.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import app.c2.android.AsyncResponse;
import app.c2.android.Utils;
import app.calendar.CalendarActivity;
import app.calendar.CalendarAdapter;
import app.dao.EquipmentService;
import app.dao.Listing;
import app.dao.ListingDetailService;
import app.dao.MyCalendar;
import app.dao.Photo;
import app.dao.SearchResultListing;
import app.equipment.AddEquipmentActivity;
import app.equipment.ViewListingBookingsActivity;
import app.ratings.RatingsActivity;
import app.services.ServicesDetailActivity;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_CATEGORY;
import static app.agrishare.Constants.KEY_EDIT;
import static app.agrishare.Constants.KEY_EQUIPMENT_SERVICE;
import static app.agrishare.Constants.KEY_LISTING;
import static app.agrishare.Constants.KEY_LISTING_ID;
import static app.agrishare.Constants.KEY_SEARCH_RESULT_LISTING;
import static app.agrishare.Constants.KEY_START_DATE;

public class DetailActivity extends BaseActivity {

    private static ViewPager mPager;
    private static int currentPage = 0;
    private ArrayList<Photo> imagesList = new ArrayList<>();
    private static Timer swipeTimer;
    int counter_offset = 0;
    int upcoming_bookings_count = 0;

    Listing listing;
    String dates_caption_prefix = ""; //for send request
    ProgressDialog progressDialog;

    long service_id_required = 0;
    SearchResultListing searchResultListing;

    int CALENDAR_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Detail", R.drawable.button_back);
        searchResultListing = getIntent().getParcelableExtra(KEY_SEARCH_RESULT_LISTING);
        fetchListingDetails();
    }

    private void initViews(){
        try {
            JSONArray photosArray = new JSONArray(listing.Photos);
            int size = photosArray.length();
            for (int i = 0; i < size; i++) {
                imagesList.add(new Photo(photosArray.getJSONObject(i)));
            }

            //using viewpager just so i can display dots
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(new SliderAdapter(DetailActivity.this, imagesList, DetailActivity.this));
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
                    Animation myFadeInAnimation = AnimationUtils.loadAnimation(DetailActivity.this, R.anim.fadein);
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

        ((TextView) findViewById(R.id.title)).setText(listing.Title);
        ((TextView) findViewById(R.id.description)).setText(listing.Description);
        (findViewById(R.id.read_reviews)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent _intent = new Intent(DetailActivity.this, RatingsActivity.class);
                    _intent.putExtra(KEY_LISTING, listing);
                    startActivity(_intent);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                }
            }
        });

        
        if (listing.AverageRating == 0) {
            Picasso.get()
                    .load(R.drawable.ratings_zero_30)
                    .into(((ImageView) findViewById(R.id.rating_imageview)));
        }
        else if (listing.AverageRating == 1) {
            Picasso.get()
                    .load(R.drawable.ratings_one_30)
                    .into(((ImageView) findViewById(R.id.rating_imageview)));
        }
        else if (listing.AverageRating == 2) {
            Picasso.get()
                    .load(R.drawable.ratings_two_30)
                    .into(((ImageView) findViewById(R.id.rating_imageview)));
        }
        else if (listing.AverageRating == 3) {
            Picasso.get()
                    .load(R.drawable.ratings_three_30)
                    .into(((ImageView) findViewById(R.id.rating_imageview)));
        }
        else if (listing.AverageRating == 4) {
            Picasso.get()
                    .load(R.drawable.ratings_four_30)
                    .into(((ImageView) findViewById(R.id.rating_imageview)));
        }
        else if (listing.AverageRating == 5) {
            Picasso.get()
                    .load(R.drawable.ratings_five_30)
                    .into(((ImageView) findViewById(R.id.rating_imageview)));
        }


        if (listing.Brand != null && !listing.Brand.isEmpty())
            addRow(getResources().getString(R.string.brand), listing.Brand);

        if (listing.HorsePower != 0)
            addRow(getResources().getString(R.string.horse_power), listing.HorsePower + "HP");

        if (listing.Year != 0)
            addRow(getResources().getString(R.string.year), String.valueOf(listing.Year));

       /* if (listing.Condition != null && !listing.Condition.isEmpty())
            addRow(getResources().getString(R.string.condition), listing.Condition);*/

        if (listing.UserId == MyApplication.currentUser.Id) {
            ((TextView) findViewById(R.id.date)).setText("Created on " + Utils.makeFriendlyDateString(listing.DateCreated));

            (findViewById(R.id.send_request)).setVisibility(View.GONE);
            (findViewById(R.id.tasks_container)).setVisibility(View.VISIBLE);
            (findViewById(R.id.view_services_container)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    {
                        Intent _intent = new Intent(DetailActivity.this, ServicesDetailActivity.class);
                        _intent.putExtra(KEY_LISTING, listing);
                        startActivity(_intent);
                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                    }
                }
            });

            (findViewById(R.id.view_bookings_container)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    {
                        Intent _intent = new Intent(DetailActivity.this, ViewListingBookingsActivity.class);
                        _intent.putExtra(KEY_LISTING, listing);
                        startActivity(_intent);
                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                    }
                }
            });

            (findViewById(R.id.edit_container)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    {
                        Intent _intent = new Intent(DetailActivity.this, AddEquipmentActivity.class);
                        _intent.putExtra(KEY_LISTING, listing);
                        _intent.putExtra(KEY_EDIT, true);
                        startActivity(_intent);
                        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
                    }
                }
            });

            if (listing.StatusId == 1){
                ((TextView) findViewById(R.id.hide)).setText(getResources().getString(R.string.hide));
            }
            else {
                ((TextView) findViewById(R.id.hide)).setText(getResources().getString(R.string.unhide));
            }
            (findViewById(R.id.hide_container)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    {
                        if (listing.StatusId == 1){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailActivity.this);
                            alertDialogBuilder.setTitle("Hide");
                            alertDialogBuilder
                                    .setMessage("Are you sure you want to hide this listing?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            hideListing();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            alertDialog.setCancelable(true);
                        }
                        else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailActivity.this);
                            alertDialogBuilder.setTitle("Unhide");
                            alertDialogBuilder
                                    .setMessage("Are you sure you want to unhide this listing?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            unHideListing();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            alertDialog.setCancelable(true);
                        }

                    }
                }
            });

            (findViewById(R.id.delete_container)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailActivity.this);
                        alertDialogBuilder.setTitle("Logout");
                        alertDialogBuilder
                                .setMessage("Are you sure you want to delete this listing?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        showLoader("Deleting", "Please wait...");
                                        HashMap<String, String> query = new HashMap<String, String>();
                                        query.put("ListingId", String.valueOf(listing.Id));
                                        getAPI("listings/delete", query, fetchDeleteResponse);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        alertDialog.setCancelable(true);

                    }
                }
            });
        }
        else {
            (findViewById(R.id.tasks_container)).setVisibility(View.GONE);
            (findViewById(R.id.send_request)).setVisibility(View.VISIBLE);
            setupRequestFigures();
        }

    }

    private void setupRequestFigures(){

        try {
            JSONArray servicesArray = new JSONArray(listing.Services);
            int size = servicesArray.length();
            for (int i = 0; i < size; i++){
                if (servicesArray.optJSONObject(i).optJSONObject("Category").optLong("Id") == MyApplication.searchQuery.Service.Id){
                    service_id_required = servicesArray.optJSONObject(i).optLong("Id");
                    ListingDetailService listingDetailService = new ListingDetailService(servicesArray.optJSONObject(i));

                    disableSendRequestButton();

                    String current_start_date = MyApplication.searchQuery.StartDate;

                    if (!MyApplication.searchQuery.NewlySelectedStartDate.isEmpty())
                        current_start_date = MyApplication.searchQuery.NewlySelectedStartDate;

                    //upcoming bookings
                    if (upcoming_bookings_count == 0 ){
                        (findViewById(R.id.upcoming_bookings_container)).setVisibility(View.GONE);
                    }
                    else {
                        (findViewById(R.id.upcoming_bookings_container)).setVisibility(View.VISIBLE);
                        String text = "You have " + upcoming_bookings_count + " upcoming bookings.";
                        if (upcoming_bookings_count == 1)
                            text = "You have " + upcoming_bookings_count + " upcoming booking.";
                        ((TextView) findViewById(R.id.upcoming_bookings)).setText(text);
                    }

                    //dates
                    double total_time_required = 0;
                    if (MyApplication.searchQuery.CategoryId == 3){
                        total_time_required =  MyApplication.searchQuery.Size / listingDetailService.TimePerQuantityUnit;
                    }
                    else if (MyApplication.searchQuery.CategoryId == 2){
                        //for lorries
                        total_time_required = searchResultListing.Distance / 100;       //IN Hours
                        Log("DISTANCE: "+ searchResultListing.Distance + " - HOURS NEEDED: " + total_time_required);
                    }
                    else {
                        total_time_required = listingDetailService.TimePerQuantityUnit * MyApplication.searchQuery.Size;
                    }
                    double total_time_required_in_days = total_time_required / 24;
                    String time_string_prefix = String.format("%.1f", total_time_required) + "hours";
                    if (total_time_required > 24){
                        time_string_prefix = String.format("%.1f", total_time_required_in_days) + " days";

                    }
                    if (MyApplication.searchQuery.CategoryId == 2){
                        //for lorries
                        if (total_time_required_in_days == 0) {
                            dates_caption_prefix = String.format("%.1f", searchResultListing.Distance) + "Km";
                        } else {
                            dates_caption_prefix = String.format("%.1f", searchResultListing.Distance) + "Km will take " + time_string_prefix + ".";
                        }
                    }
                    else {
                        if (total_time_required_in_days == 0) {
                            dates_caption_prefix = MyApplication.searchQuery.Size + Utils.getAbbreviatedQuantityUnit(listingDetailService.QuantityUnitId);
                        } else {
                            dates_caption_prefix = MyApplication.searchQuery.Size + Utils.getAbbreviatedQuantityUnit(listingDetailService.QuantityUnitId) + " will take " + time_string_prefix + ".";
                        }
                    }
                    ((TextView) findViewById(R.id.dates_caption)).setText(dates_caption_prefix);

                    Log("CURRENT START DATE: " + current_start_date);
                    String friendly_start_date = makeFriendlyDateString(current_start_date);
                    if (total_time_required < 18) {
                        ((TextView) findViewById(R.id.dates)).setText(friendly_start_date);
                        String end_date = Utils.addDaysToDate(current_start_date, 1);
                        checkIfSelectedDaysAreAvailable(current_start_date, end_date);
                    }
                    else {
                        int total_days_required = (int) Math.ceil(total_time_required / 24); //round up
                        ((TextView) findViewById(R.id.dates)).setText(friendly_start_date + " - " + makeFriendlyDateString(Utils.addDaysToDate(current_start_date, total_days_required)));

                        String end_date = Utils.addDaysToDate(current_start_date, total_days_required + 1); //add an extra day so the endpoint includes the last day in the results list.
                        checkIfSelectedDaysAreAvailable(current_start_date, end_date);
                    }


                    //distance
                    SearchResultListing searchResultListing = getIntent().getParcelableExtra(KEY_SEARCH_RESULT_LISTING);
                    double total_distance_charge = searchResultListing.Distance * listingDetailService.PricePerDistanceUnit;
                    ((TextView) findViewById(R.id.distance_from_location)).setText(String.format("%.2f", searchResultListing.Distance) + " kilometres");
                    ((TextView) findViewById(R.id.distance_unit_charge)).setText("$" + String.format("%.2f", listingDetailService.PricePerDistanceUnit) + "/km");
                    ((TextView) findViewById(R.id.distance_total)).setText("$" + String.format("%.2f", total_distance_charge));

                    //field size
                    double total_quantity_charge = 0;
                    if (MyApplication.searchQuery.CategoryId == 2){
                        (findViewById(R.id.quantity_divider)).setVisibility(View.GONE);
                        (findViewById(R.id.quantity_container)).setVisibility(View.GONE);
                    }
                    else {
                        if (listingDetailService.QuantityUnitId == 2)
                            ((TextView) findViewById(R.id.quantity_label)).setText(getResources().getString(R.string.bags));
                        total_quantity_charge = MyApplication.searchQuery.Size * listingDetailService.PricePerQuantityUnit;
                        ((TextView) findViewById(R.id.quantity)).setText(String.format("%.2f", MyApplication.searchQuery.Size) + listingDetailService.QuantityUnit);
                        ((TextView) findViewById(R.id.quantity_unit_charge)).setText("$" + String.format("%.2f", listingDetailService.PricePerQuantityUnit) + "/" + Utils.getAbbreviatedQuantityUnit(listingDetailService.QuantityUnitId));
                        ((TextView) findViewById(R.id.quantity_total)).setText("$" + String.format("%.2f", total_quantity_charge));
                    }

                    //fuel
                    double total_fuel_charge = 0;
                    if (MyApplication.searchQuery.IncludeFuel) {
                        (findViewById(R.id.fuel_container)).setVisibility(View.VISIBLE);
                        total_fuel_charge = MyApplication.searchQuery.Size * listingDetailService.FuelPerQuantityUnit;
                        ((TextView) findViewById(R.id.fuel)).setText(String.format("%.2f", MyApplication.searchQuery.Size) + listingDetailService.QuantityUnit);
                        ((TextView) findViewById(R.id.fuel_unit_charge)).setText("$" + String.format("%.2f", listingDetailService.FuelPerQuantityUnit) + "/" + Utils.getAbbreviatedQuantityUnit(listingDetailService.QuantityUnitId));
                        ((TextView) findViewById(R.id.fuel_total)).setText("$" + String.format("%.2f", total_fuel_charge));
                    }
                    else {
                        (findViewById(R.id.fuel_container)).setVisibility(View.GONE);
                    }

                    //total
                    double total_charge = total_distance_charge + total_quantity_charge + total_fuel_charge;
                    ((TextView) findViewById(R.id.request_total)).setText("$" + String.format("%.2f", total_charge));

                    (findViewById(R.id.view_availability)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            {
                                Intent intent = new Intent(DetailActivity.this, CalendarActivity.class);
                                intent.putExtra(KEY_LISTING, listing);
                                startActivityForResult(intent, CALENDAR_REQUEST_CODE);
                                overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                            }
                        }
                    });

                    (findViewById(R.id.send_request_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            {

                                //send request
                                showSendRequestProgress();
                                HashMap<String, Object> query = new HashMap<String, Object>();
                                query.put("ServiceId", service_id_required);
                                query.put("ForId", MyApplication.searchQuery.ForId);
                                query.put("Location", MyApplication.searchQuery.Location);
                                query.put("Latitude", MyApplication.searchQuery.Latitude);
                                query.put("Longitude", MyApplication.searchQuery.Longitude);
                                query.put("Quantity", MyApplication.searchQuery.Size);
                                query.put("IncludeFuel", MyApplication.searchQuery.IncludeFuel);
                                if (MyApplication.searchQuery.NewlySelectedStartDate.isEmpty())
                                    query.put("StartDate", MyApplication.searchQuery.StartDate.replace("T00:00:00", ""));
                                else
                                    query.put("StartDate", MyApplication.searchQuery.NewlySelectedStartDate.replace("T00:00:00", ""));

                                query.put("Destination", MyApplication.searchQuery.DestinationLocation);
                                query.put("DestinationLatitude", MyApplication.searchQuery.DestinationLatitude);
                                query.put("DestinationLongitude", MyApplication.searchQuery.DestinationLongitude);
                                query.put("AdditionalInformation", MyApplication.searchQuery.AdditionalInformation);

                                postAPI("bookings/add", query, fetchSendRequestResponse);
                            }
                        }
                    });

                    break;
                }
            }
        } catch (JSONException ex){
            Log.d("JSONException", ex.getMessage());
        }
    }

    private void disableSendRequestButton(){
        (findViewById(R.id.send_request_button)).setEnabled(false);
        (findViewById(R.id.send_request_button)).setBackgroundColor(getResources().getColor(R.color.dark_grey_for_text));
    }

    private void enableSendRequestButton(){
        (findViewById(R.id.send_request_button)).setEnabled(true);
        (findViewById(R.id.send_request_button)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    private void checkIfSelectedDaysAreAvailable(String start_date, String end_date){
        (findViewById(R.id.checking_availability_progressbar)).setVisibility(View.VISIBLE);
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("ListingId", String.valueOf(listing.Id));
        query.put("StartDate", start_date);
        query.put("EndDate", end_date);
        getAPI("listings/availability", query, fetchAvailabilityResponse);
    }


    AsyncResponse fetchAvailabilityResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("CHECKING AVAILABILITY SUCCESS"+ result.toString() + "");

            (findViewById(R.id.checking_availability_progressbar)).setVisibility(View.GONE);
            JSONArray list = result.optJSONArray("Calendar");
            int size = list.length();
            if (size > 0) {
                boolean selectedDatesAvailable = true;
                for (int i = 0; i < size; i++) {
                    MyCalendar myCalendar = new MyCalendar(list.optJSONObject(i));
                    if (!myCalendar.Available){
                        selectedDatesAvailable = false;
                        break;
                    }
                }

                if (selectedDatesAvailable){
                    (findViewById(R.id.dates_confirmed_imageview)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.triangle)).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.dates_caption)).setText(dates_caption_prefix);
                    enableSendRequestButton();
                }
                else {
                    (findViewById(R.id.dates_confirmed_imageview)).setVisibility(View.GONE);
                    (findViewById(R.id.triangle)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.dates_caption)).setText(dates_caption_prefix + " " + getResources().getString(R.string.selected_dates_are_not_available));
                    disableSendRequestButton();
                }

            }

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("CHECKING AVAILABILITY ERROR:  " + errorMessage);
            (findViewById(R.id.checking_availability_progressbar)).setVisibility(View.GONE);
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    AsyncResponse fetchSendRequestResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("SUCCESS SEND REQUEST: " + result.toString());
            (findViewById(R.id.request_sent_feedback)).setVisibility(View.VISIBLE);
         //   (findViewById(R.id.send_request_button)).setVisibility(View.GONE);
         //   (findViewById(R.id.send_request_progress_bar)).setVisibility(View.GONE);
            (findViewById(R.id.send_request_details_container)).setVisibility(View.GONE);
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR SEND REQUEST: " + errorMessage);
            popToast(DetailActivity.this, "Failed to send request: " + errorMessage);
            hideSendRequestProgress();
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    private void showSendRequestProgress(){
        (findViewById(R.id.send_request_progress_bar)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.send_request_button)).setText("");
        ((Button) findViewById(R.id.send_request_button)).setEnabled(false);
    }

    private void hideSendRequestProgress(){
        (findViewById(R.id.send_request_progress_bar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.send_request_button)).setText(getResources().getString(R.string.send_request));
        ((Button) findViewById(R.id.send_request_button)).setEnabled(true);
    }

    private void fetchListingDetails(){
        //doing this to get listing services.

        showLoader("Fetching Details", "Please wait...");
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("ListingId", String.valueOf(getIntent().getLongExtra(KEY_LISTING_ID, 0)));
        getAPI("listings/detail", query, fetchResponse);
    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("LISTING DETAIL SUCCESS", result.toString() + "");

            hideLoader();
            listing = new Listing(result.optJSONObject("Listing"));
            upcoming_bookings_count = result.optInt("UpcomingBookings");
            initViews();
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR LISTING DETAIL:  " + errorMessage);
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
                    fetchListingDetails();
                }
            }
        });
    }

    private void addRow(String label, String value){
        final View specsView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_spec, null, false);
        ((TextView) specsView.findViewById(R.id.label)).setText(label);
        ((TextView) specsView.findViewById(R.id.value)).setText(value);
        ((LinearLayout) findViewById(R.id.specs_container)).addView(specsView);
    }

    AsyncResponse fetchDeleteResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("SUCCESS LISTING DELETE: " + result.toString());
            MyApplication.refreshEquipmentTab = true;
            showFeedbackWithButton(R.drawable.feedbacksuccess, getResources().getString(R.string.done), getResources().getString(R.string.your_listing_has_been_deleted));
            setCloseButton();
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR LISTING DELETE: " + errorMessage);
            popToast(DetailActivity.this, "Failed to delete: " + errorMessage);
            hideLoader();
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    private void hideListing(){
        progressDialog = new ProgressDialog(DetailActivity.this);
        progressDialog.setMessage("Please wait..."); // Setting Message
        progressDialog.setTitle("Hiding"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("ListingId", String.valueOf(listing.Id));
        getAPI("listings/hide", query, fetchHideResponse);
    }

    AsyncResponse fetchHideResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("SUCCESS LISTING HIDE: " + result.toString());
            MyApplication.refreshEquipmentTab = true;
            ((TextView) findViewById(R.id.hide)).setText(getResources().getString(R.string.unhide));
            listing.StatusId = 2;
            progressDialog.dismiss();
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR LISTING HIDE: " + errorMessage);
            popToast(DetailActivity.this, "Failed to hide: " + errorMessage);
            progressDialog.dismiss();
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    private void unHideListing(){
        progressDialog = new ProgressDialog(DetailActivity.this);
        progressDialog.setMessage("Please wait..."); // Setting Message
        progressDialog.setTitle("Hiding"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("ListingId", String.valueOf(listing.Id));
        getAPI("listings/show", query, fetchUnHideResponse);
    }

    AsyncResponse fetchUnHideResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("SUCCESS LISTING HIDE: " + result.toString());
            MyApplication.refreshEquipmentTab = true;
            ((TextView) findViewById(R.id.hide)).setText(getResources().getString(R.string.hide));
            listing.StatusId = 1;
            progressDialog.dismiss();
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ERROR LISTING HIDE: " + errorMessage);
            popToast(DetailActivity.this, "Failed to hide: " + errorMessage);
            progressDialog.dismiss();
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    public static String makeFriendlyDateString(String raw_date)
    {
        Date date = Utils.formatStringAsDate(raw_date);
        return formatDateAsFriendlyDateDayString(date);
    }

    public static String formatDateAsFriendlyDateDayString(Date date) {
        return new SimpleDateFormat("EEEE dd, MMMM").format(date);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log("ON ACTIVITY RESULT: " + requestCode);
        if (requestCode == CALENDAR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                MyApplication.searchQuery.NewlySelectedStartDate = data.getStringExtra(KEY_START_DATE);
                setupRequestFigures();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void closeScreen(){
        if (MyApplication.searchQuery != null)
            MyApplication.searchQuery.NewlySelectedStartDate = "";
        goBack();
    }

    public void setCloseButton(){
        ((Button) (findViewById(R.id.feedback_retry))).setText("CLOSE");
        findViewById(R.id.feedback_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    closeScreen();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.closeEquipmentDetailActivity){
            MyApplication.closeEquipmentDetailActivity = false;
            closeScreen();
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
                closeScreen();
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
        closeScreen();
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
