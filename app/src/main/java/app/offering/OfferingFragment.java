package app.offering;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import app.dao.Dashboard;
import app.dao.Notification;
import app.dashboard.NotificationAdapter;
import app.equipment.AddEquipmentActivity;
import app.faqs.FAQsActivity;
import app.manage.FilteredEquipmentListActivity;
import app.manage.ManageAdapter;
import app.manage.ManageSeekingAdapter2;
import app.notifications.NotificationsActivity;
import app.seeking.NotificationsAndBookingsAdapter;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_CATEGORY_ID;
import static app.agrishare.Constants.KEY_PAGE_INDEX;
import static app.agrishare.Constants.KEY_PAGE_SIZE;
import static app.agrishare.Constants.KEY_SEEKER;
import static app.agrishare.Constants.OFFERING;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class OfferingFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener  {


    boolean hasHitFetchAtAndGotResponseLeastOnce = false;

    int pageIndex = 0;
    int pageSize = 5;

    NotificationsAndBookingsAdapter adapter;
    ArrayList<Dashboard> dashboardList = new ArrayList<>();

    JSONArray notificationslist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_offering_parent, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {
        setToolbar();
        recyclerView = rootView.findViewById(R.id.list);
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.refresher);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNotifications();
            }
        });
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

        (rootView.findViewById(R.id.add_listing)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEquipment(0);
            }
        });
        fetchNotifications();
    }

    public void fetchNotifications(){
       // adapter = null;
        if (dashboardList.size() == 0)
            showLoader("Fetching notifications", "Please wait...");
        else
            swipeContainer.setRefreshing(true);

        HashMap<String, String> query = new HashMap<String, String>();
        query.put(KEY_PAGE_SIZE, pageSize + "");
        query.put(KEY_PAGE_INDEX, pageIndex + "");
        getAPI("notifications/offering", query, fetchNotificationsResponse);
    }

    AsyncResponse fetchNotificationsResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("NOTIFICATIONS OFFERING SUCCESS"+ result.toString());

            notificationslist = result.optJSONArray("List");

            fetchBookings();
            hasHitFetchAtAndGotResponseLeastOnce = true;

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("NOTIFICATIONS OFFERING ERROR:  " + errorMessage);
            if (getActivity() != null) {
                showFeedbackWithButton(R.drawable.feedback_error, getResources().getString(R.string.error), getResources().getString(R.string.please_make_sure_you_have_working_internet));
                setRefreshButton();
            }
            hasHitFetchAtAndGotResponseLeastOnce = true;
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    public void fetchBookings(){
        if (!swipeContainer.isRefreshing())
            showLoader("Fetching bookings", "Please wait...");

        HashMap<String, String> query = new HashMap<String, String>();
        query.put(KEY_PAGE_SIZE, pageSize + "");
        query.put(KEY_PAGE_INDEX, pageIndex + "");
        getAPI("bookings/offering", query, fetchBookingsResponse);
    }

    AsyncResponse fetchBookingsResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("BOOKINGS OFFERING SUCCESS" + result.toString());

            dashboardList.clear();

            int notifications_size = notificationslist.length();
            if (notifications_size > 0) {
                Dashboard notificationDash = new Dashboard(false, true, false, false);
                if (!dashboardList.contains(notificationDash))
                    dashboardList.add(notificationDash);
                for (int i = 0; i < notifications_size; i++) {
                    Dashboard dashboard_notification = new Dashboard(notificationslist.optJSONObject(i), true, false);
                    if (!dashboardList.contains(dashboard_notification))
                        dashboardList.add(dashboard_notification);
                }
            }


            double monthly_total = 0;
            double all_time_total = 0;
            hideLoader();
            refreshComplete();
            JSONArray list = result.optJSONArray("Bookings");
            int size = list.length();
            if (size > 0) {
                monthly_total = result.optJSONObject("Summary").optDouble("Month");
                all_time_total = result.optJSONObject("Summary").optDouble("Total");

                Dashboard dashboard_bookings_header = new Dashboard(false, false, true, false);
                Dashboard dashboard_bookings_summary_header = new Dashboard(false, false, false, true);
                if (!dashboardList.contains(dashboard_bookings_header))
                dashboardList.add(dashboard_bookings_header);
                if (!dashboardList.contains(dashboard_bookings_summary_header))
                    dashboardList.add(dashboard_bookings_summary_header);
                for (int i = 0; i < size; i++) {
                    Dashboard dashboard_booking = new Dashboard(list.optJSONObject(i), false, false);
                    if (!dashboardList.contains(dashboard_booking))
                        dashboardList.add(dashboard_booking);
                }
            }

            if (dashboardList.size() == 0) {
                (rootView.findViewById(R.id.scrollView)).setVisibility(View.VISIBLE);
                (rootView.findViewById(R.id.empty_container)).setVisibility(View.VISIBLE);
                swipeContainer.setVisibility(View.GONE);
            }
            else {
                (rootView.findViewById(R.id.scrollView)).setVisibility(View.GONE);
                (rootView.findViewById(R.id.empty_container)).setVisibility(View.GONE);
                swipeContainer.setVisibility(View.VISIBLE);

                Dashboard headerDash = new Dashboard(true, false, false, false);
                if (!dashboardList.contains(headerDash))
                    dashboardList.add(0, headerDash);

                if (adapter == null) {
                    if (getActivity() != null) {
                        int columns = 1;
                        adapter = new NotificationsAndBookingsAdapter(getActivity(), dashboardList, getActivity(), false, monthly_total, all_time_total);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), columns);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    adapter.notifyDataSetChanged();
                }

            }

        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("BOOKING OFFERING ERROR:  " + errorMessage);
            if (getActivity() != null) {
                showFeedbackWithButton(R.drawable.feedback_error, getResources().getString(R.string.error), getResources().getString(R.string.please_make_sure_you_have_working_internet));
                setRefreshButton();
                refreshComplete();
            }
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
            toolbar.inflateMenu(R.menu.menu_add);
            toolbar.setOnMenuItemClickListener(this);
           // ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.add:
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), AddEquipmentActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
                }
                return true;
        }
        return false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }
      //  ((MainActivity) getActivity()).setActionBarTitle("Profile");


        if (hasHitFetchAtAndGotResponseLeastOnce)
            fetchNotifications();

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
