package app.search;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import app.agrishare.BaseActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.dao.Listing;
import app.services.ServicesDetailActivity;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_LISTING;

public class DetailActivity extends BaseActivity {

    private static ViewPager mPager;
    private static int currentPage = 0;
 //   private ArrayList<Product> imagesList = new ArrayList<>();
    private static Timer swipeTimer;
    int counter_offset = 0;

    Listing listing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Detail", R.drawable.button_back);
        listing = getIntent().getParcelableExtra(KEY_LISTING);
        initViews();
    }

    private void initViews(){
        //using viewpager just so i can display dots
       /* mPager = (ViewPager) findViewById(R.id.pager);
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

        ///  currentPage = getIntent().getIntExtra(MyApplication.KEY_IMAGE_POSITION_TO_DISPLAY, 0);
        currentPage = 0;
        mPager.setCurrentItem(currentPage);


        ((ImageView) findViewById(R.id.photo)).setImageBitmap(bitmap);
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == imagesList.size()) {
                    currentPage = 0;
                }
                Animation myFadeInAnimation = AnimationUtils.loadAnimation(ProductDetailActivity.this, R.anim.fadein);
                ((ImageView) findViewById(R.id.photo)).setAnimation(myFadeInAnimation); //Set animation to your ImageView

                if (imagesAreInCacheDir) {
                    File file = new File(cacheDir.getAbsolutePath() + "/" + imagesList.get(currentPage).thumb_name + imagesList.get(currentPage).thumb_type);
                    if (file.exists()){
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        ((ImageView) findViewById(R.id.photo)).setImageBitmap(bitmap);
                    }
                }
                else {
                    //  Picasso.with(ProductDetailActivity.this).load(imagesList.get(currentPage).thumbnail).into(((ImageView) findViewById(R.id.photo)));
                    Utils.displayImageButDontSearchCacheDir(ProductDetailActivity.this, ((ImageView) findViewById(R.id.photo)), imagesList.get(currentPage).thumbnail, imagesList.get(currentPage).thumb_name, imagesList.get(currentPage).thumb_type);
                }
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
                             *//*  Intent _intent = new Intent(ProductDetailActivity.this, ImageFullScreenActivity.class);
                                _intent.putExtra(MyApplication.KEY_ITEM_RESOURCES, getIntent().getStringExtra(MyApplication.KEY_ITEM_RESOURCES));
                                _intent.putExtra(MyApplication.KEY_CURRENT_IMAGE, currentPage - counter_offset);
                                startActivity(_intent);
                                overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold); *//*
                }
            }
        });
*/
        ((TextView) findViewById(R.id.title)).setText(listing.Title);
        ((TextView) findViewById(R.id.description)).setText(listing.Description);
        if (listing.UserId == MyApplication.currentUser.Id) {
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
        }

    }

    AsyncResponse fetchDeleteResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("SUCCESS LISTING DELETE: " + result.toString());
            MyApplication.refreshEquipmentTab = true;
            showFeedbackWithButton(R.drawable.feedbacksuccess, "Done", "Your listing has been successfully deleted.");
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

    public void setCloseButton(){
        ((Button) (findViewById(R.id.feedback_retry))).setText("CLOSE");
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
