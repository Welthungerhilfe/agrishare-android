package app.seeking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import app.about.AboutActivity;
import app.account.EditProfileActivity;
import app.account.ForgotPinActivity;
import app.account.NotificationPreferencesActivity;
import app.account.PrivacyPolicyActivity;
import app.account.SplashActivity;
import app.agrishare.BaseFragment;
import app.agrishare.MainActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.bookings.BookingsActivity;
import app.c2.android.AsyncResponse;
import app.c2.android.Utils;
import app.contact.ContactUsActivity;
import app.dao.Booking;
import app.dao.Listing;
import app.dao.Notification;
import app.dashboard.NotificationAdapter;
import app.equipment.AddEquipmentActivity;
import app.faqs.FAQsActivity;
import app.manage.BookingDetailActivity;
import app.manage.ManageEquipmentAdapter;
import app.manage.ManageSeekingAdapter;
import app.notifications.NotificationsActivity;
import app.notifications.NotificationsAdapter;
import app.search.SearchResultsActivity;
import app.searchwizard.SearchActivity;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_BOOKING;
import static app.agrishare.Constants.KEY_CATEGORY_ID;
import static app.agrishare.Constants.KEY_PAGE_INDEX;
import static app.agrishare.Constants.KEY_PAGE_SIZE;
import static app.agrishare.Constants.KEY_SEEKER;
import static app.agrishare.Constants.PREFS_CURRENT_LANGUAGE;
import static app.agrishare.Constants.PREFS_CURRENT_LANGUAGE_LOCALE_NAME;
import static app.agrishare.Constants.PREFS_IS_DEVICE_REGISTERED_ON_OUR_SERVER;
import static app.agrishare.Constants.PREFS_TOKEN;
import static app.agrishare.Constants.PROFILE;
import static app.agrishare.Constants.SEEKING;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class SeekingFragment extends BaseFragment {

    ListView notificationsListView, bookingsListView;

    int pageIndex = 0;
    int pageSize = 5;
    int unreadNotificationsCount = 0;

    NotificationsAdapter notificationAdapter;
    ArrayList<Notification> notificationsList;

    ManageSeekingAdapter bookingAdapter;
    ArrayList<Booking> bookingsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_seeking_parent, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {
        setToolbar();
        notificationsListView = rootView.findViewById(R.id.notifications_list);
        bookingsListView = rootView.findViewById(R.id.bookings_list);
        (rootView.findViewById(R.id.tractors)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long categoryId = 1;
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra(KEY_CATEGORY_ID, categoryId);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }
        });

        (rootView.findViewById(R.id.lorries)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long categoryId = 2;
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra(KEY_CATEGORY_ID, categoryId);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }
        });

        (rootView.findViewById(R.id.processing)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long categoryId = 3;
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra(KEY_CATEGORY_ID, categoryId);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }
        });

        (rootView.findViewById(R.id.view_all_notifications_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationsActivity.class);
                intent.putExtra(KEY_SEEKER, true);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }
        });

        (rootView.findViewById(R.id.view_past_bookings_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BookingsActivity.class);
                intent.putExtra(KEY_SEEKER, true);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);

            }
        });
        fetchNotifications();
    }

    public void fetchNotifications(){
        unreadNotificationsCount = 0;
        notificationAdapter = null;
        showLoader("Fetching notifications", "Please wait...");
        notificationsList = new ArrayList<>();

        HashMap<String, String> query = new HashMap<String, String>();
        query.put(KEY_PAGE_SIZE, pageSize + "");
        query.put(KEY_PAGE_INDEX, pageIndex + "");
        getAPI("notifications/seeking", query, fetchNotificationsResponse);
    }

    AsyncResponse fetchNotificationsResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("NOTIFICATIONS SEEKING SUCCESS"+ result.toString());

            hideLoader();
            JSONArray list = result.optJSONArray("List");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    Notification notification = new Notification(list.optJSONObject(i), true);
                    notificationsList.add(notification);
                    if (notification.StatusId == 0){
                        unreadNotificationsCount = unreadNotificationsCount + 1;
                    }
                }

                if (notificationAdapter == null) {
                    if (getActivity() != null) {
                        notificationAdapter = new NotificationsAdapter(getActivity(), notificationsList, getActivity());
                        notificationsListView.setAdapter(notificationAdapter);
                        Utils.setListViewHeightBasedOnChildren2(notificationsListView);
                    }
                } else {
                    notificationAdapter.notifyDataSetChanged();
                    notificationsListView.setAdapter(notificationAdapter);
                    Utils.setListViewHeightBasedOnChildren2(notificationsListView);
                }
            }
            fetchBookings();
           /* else {
                if (getActivity() != null) {
                    showFeedbackWithButton(R.drawable.feedback_empty, getResources().getString(R.string.empty), getResources().getString(R.string.you_have_not_added_any_equipment));
                    setAddButton();
                }
            }*/

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("NOTIFICATIONS SEEKING ERROR:  " + errorMessage);
            showFeedbackWithButton(R.drawable.feedback_error, getResources().getString(R.string.error), getResources().getString(R.string.please_make_sure_you_have_working_internet));
            setRefreshButton();
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    public void fetchBookings(){
        bookingAdapter = null;
        showLoader("Fetching bookings", "Please wait...");
        bookingsList = new ArrayList<>();

        HashMap<String, String> query = new HashMap<String, String>();
        query.put(KEY_PAGE_SIZE, pageSize + "");
        query.put(KEY_PAGE_INDEX, pageIndex + "");
        getAPI("bookings/seeking", query, fetchBookingsResponse);
    }

    AsyncResponse fetchBookingsResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("BOOKING SEEKING SUCCESS" + result.toString());

            hideLoader();
            JSONArray list = result.optJSONArray("Bookings");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    bookingsList.add(new Booking(list.optJSONObject(i), true));
                }

                if (bookingAdapter == null) {
                    if (getActivity() != null) {
                        bookingAdapter = new ManageSeekingAdapter(getActivity(), bookingsList, getActivity());
                        bookingsListView.setAdapter(bookingAdapter);
                        Utils.setListViewHeightBasedOnChildren(bookingsListView);
                    }
                } else {
                    bookingAdapter.notifyDataSetChanged();
                    bookingsListView.setAdapter(bookingAdapter);
                    Utils.setListViewHeightBasedOnChildren(bookingsListView);
                }
            }

            if (notificationsList.size() + bookingsList.size() == 0) {
                (rootView.findViewById(R.id.empty_container)).setVisibility(View.VISIBLE);
                (rootView.findViewById(R.id.details_container)).setVisibility(View.GONE);
            }
            else {
                (rootView.findViewById(R.id.empty_container)).setVisibility(View.GONE);
                (rootView.findViewById(R.id.details_container)).setVisibility(View.VISIBLE);

                if (unreadNotificationsCount == 0)
                    (rootView.findViewById(R.id.notification_count)).setVisibility(View.INVISIBLE);
                else if (unreadNotificationsCount <= 5)
                    ((TextView) rootView.findViewById(R.id.notification_count)).setText(String.valueOf(unreadNotificationsCount));
                else
                    ((TextView) rootView.findViewById(R.id.notification_count)).setText("5+");


                ((TextView) rootView.findViewById(R.id.month)).setText("$" + String.format("%.2f", result.optJSONObject("Summary").optDouble("Month")));
                ((TextView) rootView.findViewById(R.id.all_time)).setText("$" + String.format("%.2f", result.optJSONObject("Summary").optDouble("Total")));

            }

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("BOOKING SEEKING ERROR:  " + errorMessage);
            showFeedbackWithButton(R.drawable.feedback_error, getResources().getString(R.string.error), getResources().getString(R.string.please_make_sure_you_have_working_internet));
            setRefreshButton();
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };


    private void showAddEquipment(){
        Intent intent = new Intent(getActivity(), AddEquipmentActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
    }

    public void setRefreshButton(){
        ((Button) rootView.findViewById(R.id.feedback_retry)).setText(getResources().getString(R.string.retry));
        rootView.findViewById(R.id.feedback_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    fetchNotifications();
                }
            }
        });
    }

    public void setAddButton(){
        ((Button) rootView.findViewById(R.id.feedback_retry)).setText(getResources().getString(R.string.add_listing));
        rootView.findViewById(R.id.feedback_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showAddEquipment();
                }
            }
        });
    }

    private void setToolbar(){
        if (rootView != null){
            Toolbar toolbar = rootView.findViewById(R.id.toolbar);
            toolbar.setTitle(getActivity().getString(R.string.seeking));
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }
    //    setToolbar();
      //  ((MainActivity) getActivity()).setActionBarTitle("Profile");

        if (MyApplication.tabsStackList.contains(SEEKING))
            MyApplication.tabsStackList.remove(SEEKING);
        MyApplication.tabsStackList.add(SEEKING);
    }

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }
}
