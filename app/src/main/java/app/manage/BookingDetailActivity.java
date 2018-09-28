package app.manage;

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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import app.c2.android.AsyncResponse;
import app.c2.android.Utils;
import app.dao.Booking;
import app.dao.GroupMember;
import app.dao.Listing;
import app.dao.Notification;
import app.dao.Photo;
import app.dao.Transaction;
import app.database.Transactions;
import app.equipment.AddEquipmentActivity;
import app.equipment.EquipmentServiceAdapter;
import app.search.DetailActivity;
import app.search.SliderAdapter;
import app.services.ServicesDetailActivity;
import io.realm.RealmResults;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_BOOKING;
import static app.agrishare.Constants.KEY_EDIT;
import static app.agrishare.Constants.KEY_LISTING;
import static app.agrishare.Constants.KEY_NOTIFICATION;
import static app.agrishare.Constants.KEY_NOTIFICATION_ID;
import static app.agrishare.Constants.KEY_PAGE_INDEX;
import static app.agrishare.Constants.KEY_PAGE_SIZE;

public class BookingDetailActivity extends BaseActivity {

    private static ViewPager mPager;
    private static int currentPage = 0;
    private ArrayList<Photo> imagesList = new ArrayList<>();
    private static Timer swipeTimer;
    int counter_offset = 0;

    int rating_stars = 1;
    Booking booking;

    private ArrayList<GroupMember> membersList = new ArrayList<>();
    GroupMemberAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Detail", R.drawable.button_back);
        booking = getIntent().getParcelableExtra(KEY_BOOKING);
        initViews();
        showAppropriateActionFooter();
    }

    private void initViews(){
        //for group payments
        double total_price_to_pay = booking.Price + booking.HireCost + booking.FuelCost + booking.TransportCost;
        listview = findViewById(R.id.group_member_list);
        adapter = new GroupMemberAdapter(BookingDetailActivity.this, membersList, BookingDetailActivity.this, total_price_to_pay, booking.Quantity);
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

        if (booking.Listing.Condition != null && !booking.Listing.Condition.isEmpty())
            addRow(getResources().getString(R.string.condition), booking.Listing.Condition);

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
                        (findViewById(R.id.pay_with_ecocash)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.full_name_parent_container)).setVisibility(View.GONE);
                        (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.pay_with_ecocash_caption)).setText(getResources().getString(R.string.enter_your_ecocash_number));
                    } else if (booking.ForId == 1) {
                        (findViewById(R.id.pay_with_ecocash)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.full_name_parent_container)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.pay_with_ecocash_caption)).setText(getResources().getString(R.string.enter_the_ecocash_number_for_the_person));
                    } else if (booking.ForId == 3) {
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

                try {
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
                }

            }
            else if (booking.StatusId == 4){
                (findViewById(R.id.waiting_for_feedback)).setVisibility(View.GONE);
                (findViewById(R.id.waiting_for_payment)).setVisibility(View.GONE);
                (findViewById(R.id.confirm)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash)).setVisibility(View.GONE);
                (findViewById(R.id.complete)).setVisibility(View.GONE);
                (findViewById(R.id.in_progress)).setVisibility(View.GONE);
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
                (findViewById(R.id.complete)).setVisibility(View.VISIBLE);
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
                (findViewById(R.id.please_leave_a_review)).setVisibility(View.GONE);
                (findViewById(R.id.all_done)).setVisibility(View.GONE);
                (findViewById(R.id.pay_with_ecocash_for_group)).setVisibility(View.GONE);
            }
        }


        (findViewById(R.id.decline)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showLoader("Declining", "Please wait...");
                HashMap<String, String> query = new HashMap<String, String>();
                query.put("BookingId", String.valueOf(booking.Id));
                getAPI("bookings/decline", query, fetchDeclineResponse);
            }
        });

        (findViewById(R.id.approve)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showLoader("Approving", "Please wait...");
                HashMap<String, String> query = new HashMap<String, String>();
                query.put("BookingId", String.valueOf(booking.Id));
                getAPI("bookings/confirm", query, fetchApproveResponse);
            }
        });

        (findViewById(R.id.complete_button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showLoader("Finishing", "Please wait...");
                HashMap<String, String> query = new HashMap<String, String>();
                query.put("BookingId", String.valueOf(booking.Id));
                getAPI("bookings/complete", query, fetchCompleteBookingResponse);
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

        (findViewById(R.id.rating_submit)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                EditText message_editText = findViewById(R.id.fname);
                message_editText.setError(null);

                if (message_editText.getText().toString().isEmpty()){
                    message_editText.setError(getString(R.string.error_field_required));
                    message_editText.requestFocus();
                }
                else {
                    showLoader("Submitting Review", "Please wait...");
                    HashMap<String, Object> query = new HashMap<String, Object>();
                    query.put("ListingId", booking.Listing.Id);
                    query.put("Rating", rating_stars);
                    query.put("Comments", message_editText.getText().toString());
                    postAPI("ratings/add", query, fetchSubmitReviewResponse);
                }
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

                    if (readyToSubmit){
                        try {

                            JSONArray groupPaymentUsersArray = new JSONArray();
                            for(int j = 0; j < size; j++){
                                JSONObject paymentUserObject = new JSONObject();
                                paymentUserObject.accumulate("Quantity", membersList.get(j).quantity);
                                paymentUserObject.accumulate("Telephone", membersList.get(j).ecocash_number);
                                paymentUserObject.accumulate( "Name", membersList.get(j).name);
                                groupPaymentUsersArray.put(paymentUserObject);
                            }

                            pay(groupPaymentUsersArray);
                        } catch (JSONException ex){
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

    private void pay(JSONArray groupPaymentUsersArray){
        showLoader("Processing", "Please wait...");
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
            hideLoader();

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("SUBMIT REVIEW ERROR:  " + errorMessage);
            hideLoader();
            popToast(BookingDetailActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {}
    };

    AsyncResponse fetchPayResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("TRANSACTIONS SUCCESS", result.toString() + "");
            hideLoader();
            pollForEcocash();
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("TRANSACTIONS ERROR:  " + errorMessage);
            hideLoader();
            popToast(BookingDetailActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {}
    };

    private void pollForEcocash(){
        showLoader("Polling Ecocash", "Please wait...");
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("BookingId", booking.Id);
        postAPI("transactions/ecocash/poll", query, fetchEcocashPollResponse);
    }

    AsyncResponse fetchEcocashPollResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("POLL SUCCESS", result.toString() + "");

            hideLoader();

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
            hideLoader();
            popToast(BookingDetailActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {}
    };

    AsyncResponse fetchCompleteBookingResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("COMPLETE BOOKING SUCCES", result.toString() + "");
            hideLoader();
            booking.StatusId = result.optJSONObject("Booking").optLong("StatusId");
            booking.Status = result.optJSONObject("Booking").optString("Status");
            showAppropriateActionFooter();
            MyApplication.refreshManageOfferingTab = true;
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("COMPLETE BOOKING ERROR:  " + errorMessage);
            hideLoader();
            popToast(BookingDetailActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {}
    };

    AsyncResponse fetchDeclineResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("DECLINE BOOKING SUCCESS", result.toString() + "");
            hideLoader();
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
            hideLoader();
            popToast(BookingDetailActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {}
    };

    AsyncResponse fetchApproveResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log.d("APPROVE BOOKING SUCCESS", result.toString() + "");
            hideLoader();
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
            hideLoader();
            popToast(BookingDetailActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {}
    };

    private void addRow(String label, String value){
        final View specsView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_spec, null, false);
        ((TextView) specsView.findViewById(R.id.label)).setText(label);
        ((TextView) specsView.findViewById(R.id.value)).setText(value);
        ((LinearLayout) findViewById(R.id.specs_container)).addView(specsView);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(swipeTimer != null) {
            swipeTimer.cancel();
            swipeTimer = null;
        }
    }

}
