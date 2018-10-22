package app.offering;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.bookings.BookingsActivity;
import app.c2.android.AsyncResponse;
import app.dao.Booking;
import app.dao.Notification;
import app.dashboard.NotificationAdapter;
import app.equipment.AddEquipmentActivity;
import app.manage.FilteredEquipmentListActivity;
import app.manage.ManageAdapter;
import app.manage.ManageSeekingAdapter2;
import app.notifications.NotificationsActivity;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_CATEGORY_ID;
import static app.agrishare.Constants.KEY_PAGE_INDEX;
import static app.agrishare.Constants.KEY_PAGE_SIZE;
import static app.agrishare.Constants.KEY_SEEKER;
import static app.agrishare.Constants.OFFERING;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class OfferingFragment extends BaseFragment {

    int pageIndex = 0;
    int pageSize = 5;
    int unreadNotificationsCount = 0;

    NotificationAdapter notificationAdapter;
    ArrayList<Notification> notificationsList;

    ManageAdapter bookingAdapter;
    ArrayList<Booking> bookingsList;

    RecyclerView notificationsRecyclerView;
    RecyclerView bookingsRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_offering_parent, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {
        setToolbar();
        notificationsRecyclerView = rootView.findViewById(R.id.notifications_list_offering);
        bookingsRecyclerView = rootView.findViewById(R.id.bookings_list_offering);
        (rootView.findViewById(R.id.tractors)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyFilteredEquipment(1);
            }
        });

        (rootView.findViewById(R.id.lorries)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyFilteredEquipment(2);
            }
        });

        (rootView.findViewById(R.id.processing)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyFilteredEquipment(3);
            }
        });

        (rootView.findViewById(R.id.view_all_notifications_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationsActivity.class);
                intent.putExtra(KEY_SEEKER, false);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
            }
        });

        (rootView.findViewById(R.id.view_past_bookings_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BookingsActivity.class);
                intent.putExtra(KEY_SEEKER, false);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);

            }
        });

        (rootView.findViewById(R.id.add_listing)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEquipment(0);
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
        getAPI("notifications/offering", query, fetchNotificationsResponse);
    }

    AsyncResponse fetchNotificationsResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("NOTIFICATIONS OFFERING SUCCESS"+ result.toString());

            hideLoader();
            JSONArray list = result.optJSONArray("List");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    Notification notification = new Notification(list.optJSONObject(i), false);
                    notificationsList.add(notification);
                    if (notification.StatusId == 0){
                        unreadNotificationsCount = unreadNotificationsCount + 1;
                    }
                }

                if (notificationAdapter == null) {
                    if (getActivity() != null) {
                        /*notificationAdapter = new NotificationsAdapter(getActivity(), notificationsList, getActivity());
                        notificationsListView.setAdapter(notificationAdapter);
                        setListViewHeightBasedOnChildren2(notificationsListView);*/

                        int columns = 1;
                        notificationAdapter = new NotificationAdapter(getActivity(), notificationsList, getActivity());
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), columns);
                        notificationsRecyclerView.setHasFixedSize(true);
                        notificationsRecyclerView.setLayoutManager(gridLayoutManager);
                        notificationsRecyclerView.setAdapter(notificationAdapter);
                    }
                } else {
                    notificationAdapter.notifyDataSetChanged();
                   /* notificationsListView.setAdapter(notificationAdapter);
                    setListViewHeightBasedOnChildren2(notificationsListView);*/
                }
            }
            fetchBookings();

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("NOTIFICATIONS OFFERING ERROR:  " + errorMessage);
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
        getAPI("bookings/offering", query, fetchBookingsResponse);
    }

    AsyncResponse fetchBookingsResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("BOOKINGS OFFERING SUCCESS" + result.toString());

            hideLoader();
            JSONArray list = result.optJSONArray("Bookings");
            int size = list.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    bookingsList.add(new Booking(list.optJSONObject(i), false));
                }

                if (bookingAdapter == null) {
                    if (getActivity() != null) {
                        int columns = 1;
                        bookingAdapter = new ManageAdapter(getActivity(), bookingsList, getActivity());
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), columns);
                        bookingsRecyclerView.setHasFixedSize(true);
                        bookingsRecyclerView.setLayoutManager(gridLayoutManager);
                        bookingsRecyclerView.setAdapter(bookingAdapter);
                    }
                } else {
                    bookingAdapter.notifyDataSetChanged();
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
            Log("BOOKING OFFERING ERROR:  " + errorMessage);
            showFeedbackWithButton(R.drawable.feedback_error, getResources().getString(R.string.error), getResources().getString(R.string.please_make_sure_you_have_working_internet));
            setRefreshButton();
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    private void showMyFilteredEquipment(long categoryId){
        Intent intent = new Intent(getActivity(), FilteredEquipmentListActivity.class);
        intent.putExtra(KEY_CATEGORY_ID, categoryId);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
    }


    private void showAddEquipment(long categoryId){
        Intent intent = new Intent(getActivity(), AddEquipmentActivity.class);
        intent.putExtra(KEY_CATEGORY_ID, categoryId);
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
                    showAddEquipment(0);
                }
            }
        });
    }

    private void setToolbar(){
        if (rootView != null){
            Toolbar toolbar = rootView.findViewById(R.id.toolbar);
            toolbar.setTitle(getActivity().getString(R.string.offering));
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
        setToolbar();
      //  ((MainActivity) getActivity()).setActionBarTitle("Profile");

        if (MyApplication.tabsStackList.contains(OFFERING))
            MyApplication.tabsStackList.remove(OFFERING);
        MyApplication.tabsStackList.add(OFFERING);
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
