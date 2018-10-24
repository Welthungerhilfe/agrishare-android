package app.seeking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.agrishare.MyApplication;
import app.agrishare.R;
import app.bookings.BookingsActivity;
import app.c2.android.AsyncResponse;
import app.c2.android.OkHttp;
import app.c2.android.Utils;
import app.dao.Booking;
import app.dao.Dashboard;
import app.dao.Notification;
import app.manage.BookingDetailActivity;
import app.manage.FilteredEquipmentListActivity;
import app.notifications.NotificationsActivity;
import app.searchwizard.SearchActivity;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_BOOKING;
import static app.agrishare.Constants.KEY_CATEGORY_ID;
import static app.agrishare.Constants.KEY_SEEKER;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class NotificationsAndBookingsAdapter extends RecyclerView.Adapter<NotificationsAndBookingsAdapter.MyViewHolder> {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<Dashboard> dashboardList = null;
    private ArrayList<Dashboard> arraylist;

    Activity activity;
    boolean isSeeking = false;

    double monthly_total;
    double all_time_total;

    public NotificationsAndBookingsAdapter(Context context, List<Dashboard> dashboardList, Activity activity, boolean isSeeking, double monthly_total, double all_time_total) {
        this.context = context;
        this.dashboardList = dashboardList;
        this.activity = activity;
        this.isSeeking = isSeeking;
        this.monthly_total = monthly_total;
        this.all_time_total = all_time_total;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Dashboard>();
        this.arraylist.addAll(dashboardList);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, date;
        ImageView photo, unread_dot;
        Button button;

        TextView details, price;
        Button payment_due, awaiting_confirmation, rate_this_service;

        TextView notification_count, month, all_time, page_header_textview;
        RelativeLayout tractors, lorries, processing, view_all_notifications_container, view_past_bookings_container;

        public MyViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
            date = view.findViewById(R.id.date);
            button = view.findViewById(R.id.button);
            photo = view.findViewById(R.id.photo);
            unread_dot = view.findViewById(R.id.unread_dot);

            details = view.findViewById(R.id.details);
            price = view.findViewById(R.id.price);
            payment_due = view.findViewById(R.id.payment_due);
            awaiting_confirmation = view.findViewById(R.id.awaiting_confirmation);
            rate_this_service = view.findViewById(R.id.rate_this_service);

            tractors = view.findViewById(R.id.tractors);
            lorries = view.findViewById(R.id.lorries);
            processing = view.findViewById(R.id.processing);

            view_all_notifications_container = view.findViewById(R.id.view_all_notifications_container);
            view_past_bookings_container = view.findViewById(R.id.view_past_bookings_container);

            notification_count = view.findViewById(R.id.notification_count);
            month = view.findViewById(R.id.month);
            all_time = view.findViewById(R.id.all_time);

            page_header_textview = view.findViewById(R.id.find_services);
        }
    }

    @Override
    public int getItemCount() {
        return dashboardList.size();
    }

    @Override
    public int getItemViewType(int position) {
      //  return super.getItemViewType(position);

        if (dashboardList.get(position).isPageHeader){
            return 0;
        }
        else if (dashboardList.get(position).isNotificationHeader){
            return 1;
        }
        else if (dashboardList.get(position).isBookingHeader){
            return 2;
        } else if (dashboardList.get(position).isSummaryHeader){
            return 3;
        }
        else {
            if (dashboardList.get(position).Booking == null)
                return 4;       //notification
            else
                return 5;       //booking
        }
    }

    @Override
    public NotificationsAndBookingsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 0)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dash_header, parent, false);
        else if (viewType == 1)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_header, parent, false);
        else if (viewType == 2)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookings_header, parent, false);
        else if (viewType == 3)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_manager_header, parent, false);
        else if (viewType == 4)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dashboard_listing, parent, false);
        else if (viewType == 5)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_manage_listing, parent, false);
        else
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_manage_listing, parent, false);
        return new NotificationsAndBookingsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NotificationsAndBookingsAdapter.MyViewHolder holder, final int position) {

        if (dashboardList.get(position).isPageHeader) {
            if (isSeeking)
                holder.page_header_textview.setText(context.getResources().getString(R.string.find_services));
            else
                holder.page_header_textview.setText(context.getResources().getString(R.string.my_equipment));
            holder.tractors.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSeeking)
                        showSearch(1);
                    else
                        showMyFilteredEquipment(1);
                }
            });

            holder.lorries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSeeking)
                        showSearch(2);
                    else
                        showMyFilteredEquipment(2);
                }
            });

            holder.processing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSeeking)
                        showSearch(3);
                    else
                        showMyFilteredEquipment(3);
                }
            });
        }
        else if (dashboardList.get(position).isNotificationHeader) {
            int unreadNotificationsCount = getNotificationCount();
            if (unreadNotificationsCount == 0)
                holder.notification_count.setVisibility(View.INVISIBLE);
            else if (unreadNotificationsCount <= 5)
                holder.notification_count.setText(String.valueOf(unreadNotificationsCount));
            else
                holder.notification_count.setText("5+");


            holder.view_all_notifications_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NotificationsActivity.class);
                    intent.putExtra(KEY_SEEKER, isSeeking);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                }
            });
        }
        else if (dashboardList.get(position).isBookingHeader){
            holder.view_past_bookings_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BookingsActivity.class);
                    intent.putExtra(KEY_SEEKER, isSeeking);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);

                }
            });
        }
        else if (dashboardList.get(position).isSummaryHeader){
            holder.month.setText("$" + String.format("%.2f", monthly_total));
            holder.all_time.setText("$" + String.format("%.2f", all_time_total));
        }
        else if (dashboardList.get(position).Notification != null) {

            String media_thumb = Utils.getFirstThumbPath(dashboardList.get(position).Notification.Booking.Listing.Photos);
            if (!media_thumb.isEmpty()) {
                Picasso.get()
                        .load(media_thumb)
                        .placeholder(R.drawable.default_image)
                        .into(holder.photo);
            } else {
                Picasso.get()
                        .load(R.drawable.default_image)
                        .into(holder.photo);
            }

            try {
                JSONObject serviceObject = new JSONObject(dashboardList.get(position).Notification.Booking.Service);
                String service = serviceObject.optJSONObject("Category").optString("Title") + " Services";
                holder.date.setText(Utils.convertDateToFriendlyStart(dashboardList.get(position).Notification.Booking.StartDate) + " - " + Utils.convertDateToFriendly(dashboardList.get(position).Notification.Booking.EndDate)
                        + " • " + service);
            } catch (JSONException ex) {
                Log.d("JSONException", ex.getMessage());
            }

            String time = Utils.timeAgo(dashboardList.get(position).Notification.DateCreated);
            holder.button.setText("");
            if (dashboardList.get(position).Notification.Seeking) {
                if (dashboardList.get(position).Notification.Booking.StatusId == 1) {
                    holder.button.setText(context.getResources().getString(R.string.pay_now));
                    holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.your_request_confirmed), time));
                } else if (dashboardList.get(position).Notification.Booking.StatusId == 3) {
                    holder.button.setText(context.getResources().getString(R.string.view));
                    try {
                        if (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dashboardList.get(position).Notification.Booking.EndDate).before(new Date())) {
                            holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.service_period_is_over_please_confirm_service_is_complete), time));
                        } else {
                            holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.service_is_currently_in_progress), time));
                        }
                    } catch (ParseException ex) {
                        Log.d("ParseException", "CompetitionsAdapter: " + ex.getMessage());
                    }
                } else if (dashboardList.get(position).Notification.Booking.StatusId == 4) {
                    holder.button.setText(context.getResources().getString(R.string.view));
                    holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.complete), time));
                } else if (dashboardList.get(position).Notification.Booking.StatusId == 6) {
                    holder.button.setText(context.getResources().getString(R.string.view));
                    holder.title.setText(getTitleHtmlText(dashboardList.get(position).Notification.Title, time));
                } else if (dashboardList.get(position).Notification.Booking.StatusId == 5) {
                    holder.button.setText(context.getResources().getString(R.string.view_reviews));
                    holder.title.setText(getTitleHtmlText(dashboardList.get(position).Notification.Title, time));
                } else {
                    // holder.button.setVisibility(View.INVISIBLE);
                    holder.button.setText(context.getResources().getString(R.string.view));
                    holder.title.setText(getTitleHtmlText(dashboardList.get(position).Notification.Title, time));
                }

            } else {
                if (dashboardList.get(position).Notification.Booking.StatusId == 0) {
                    holder.button.setText(context.getResources().getString(R.string.confirm));
                    holder.title.setText(getTitleHtmlText("Request waiting authorisation for " + Utils.convertDateToFriendlyStart(dashboardList.get(position).Notification.Booking.StartDate) + " - " + Utils.convertDateToFriendly(dashboardList.get(position).Notification.Booking.EndDate), time));
                } else if (dashboardList.get(position).Notification.Booking.StatusId == 1) {
                    holder.button.setText(context.getResources().getString(R.string.view));
                    holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.waiting_for_payment), time));
                } else if (dashboardList.get(position).Notification.Booking.StatusId == 2) {
                    holder.button.setText(getTitleHtmlText(context.getResources().getString(R.string.pay_now), time));
                } else if (dashboardList.get(position).Notification.Booking.StatusId == 3) {
                    holder.button.setText(context.getResources().getString(R.string.view));
                    try {
                        if (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dashboardList.get(position).Notification.Booking.EndDate).before(new Date())) {
                            holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.service_period_is_over), time));
                        } else {
                            holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.service_is_currently_in_progress), time));
                        }
                    } catch (ParseException ex) {
                        Log.d("ParseException", "CompetitionsAdapter: " + ex.getMessage());
                    }
                } else if (dashboardList.get(position).Notification.Booking.StatusId == 4) {
                    holder.button.setText(context.getResources().getString(R.string.review));
                    holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.complete), time));
                } else {
                    holder.button.setText(context.getResources().getString(R.string.view));
                    holder.title.setText(getTitleHtmlText(dashboardList.get(position).Notification.Title, time));
                }
            }

            if (dashboardList.get(position).Notification.StatusId == 0)
                holder.unread_dot.setVisibility(View.VISIBLE);
            else
                holder.unread_dot.setVisibility(View.INVISIBLE);
        }
        else if (dashboardList.get(position).Booking != null) {

            String media_thumb = Utils.getFirstThumbPath(dashboardList.get(position).Booking.Listing.Photos);
            if (!media_thumb.isEmpty()) {
                Picasso.get()
                        .load(media_thumb)
                        .placeholder(R.drawable.default_image)
                        .into(holder.photo);
            }
            else {
                Picasso.get()
                        .load(R.drawable.default_image)
                        .into(holder.photo);
            }

            if (dashboardList.get(position).Booking.StatusId == 1){
                holder.awaiting_confirmation.setVisibility(View.VISIBLE);
                holder.payment_due.setVisibility(View.GONE);
                holder.rate_this_service.setVisibility(View.GONE);
            }
            else if (dashboardList.get(position).Booking.StatusId == 2){
                holder.awaiting_confirmation.setVisibility(View.GONE);
                holder.payment_due.setVisibility(View.VISIBLE);
                holder.rate_this_service.setVisibility(View.GONE);
            }
            else if (dashboardList.get(position).Booking.StatusId == 4){
                holder.awaiting_confirmation.setVisibility(View.GONE);
                holder.payment_due.setVisibility(View.GONE);
                holder.rate_this_service.setVisibility(View.VISIBLE);
            }
            else {
                holder.awaiting_confirmation.setVisibility(View.GONE);
                holder.payment_due.setVisibility(View.GONE);
                holder.rate_this_service.setVisibility(View.GONE);
            }

            holder.date.setText(Utils.convertDateToFriendlyStart(dashboardList.get(position).Booking.StartDate) + " - " + Utils.convertDateToFriendly(dashboardList.get(position).Booking.EndDate));
            holder.title.setText(dashboardList.get(position).Booking.Listing.Title);
            holder.price.setText("$" + String.format("%.2f", dashboardList.get(position).Booking.Price));

            try {
                JSONObject serviceJSONObject = new JSONObject(dashboardList.get(position).Booking.Service);
                holder.details.setText(serviceJSONObject.optJSONObject("Category").optString("Title"));
            } catch (JSONException ex){
                if (MyApplication.DEBUG)
                    Log.d("JSONException", "" + ex.getMessage());
            }

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (dashboardList.get(position).Notification != null) {
                        Intent intent = new Intent(context, BookingDetailActivity.class);
                        intent.putExtra(KEY_BOOKING, dashboardList.get(position).Notification.Booking);
                        intent.putExtra(KEY_SEEKER, dashboardList.get(position).Notification.Seeking);
                        context.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);

                        if (dashboardList.get(position).Notification .StatusId == 0)
                            markAsRead(dashboardList.get(position).Notification .Id);
                    }
                    else if (dashboardList.get(position).Booking != null) {
                        Intent intent = new Intent(context, BookingDetailActivity.class);
                        intent.putExtra(KEY_BOOKING, dashboardList.get(position).Booking);
                        context.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                    }
                }
            }
        });
    }

    public String getGreyColor(){
        int orange = activity.getResources().getColor(R.color.grey_for_text);
        String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(orange), Color.green(orange), Color.blue(orange))));
        return htmlColor;
    }

    public String getBlackColor(){
        int orange = activity.getResources().getColor(android.R.color.black);
        String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(orange), Color.green(orange), Color.blue(orange))));
        return htmlColor;
    }

    public Spanned getTitleHtmlText(String message, String time){
        Spanned html_title = Html.fromHtml("<font color=\"" + getBlackColor() + "\">" + message + " </font>" + "<font color=\"" + getGreyColor() + "\">" + time + "</font>");
        return html_title;
    }

    private void showMyFilteredEquipment(long categoryId){
        Intent intent = new Intent(context, FilteredEquipmentListActivity.class);
        intent.putExtra(KEY_CATEGORY_ID, categoryId);
        context.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
    }

    private void showSearch(long categoryId){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(KEY_CATEGORY_ID, categoryId);
        context.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
    }

    public void markAsRead(long id)
    {
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("NotificationId", String.valueOf(id));
        getAPI("notifications/read", query, fetchResponse);

    }

    private int getNotificationCount(){
        int unreadNotificationsCount = 0;
        for (int i = 0; i < dashboardList.size(); i++){
            if (dashboardList.get(i).Notification != null){
                if (dashboardList.get(i).Notification.StatusId == 0){
                    unreadNotificationsCount = unreadNotificationsCount + 1;
                }
            }
        }
        return unreadNotificationsCount;
    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            if (MyApplication.DEBUG)
                Log.d("NOTIFICATION SUCCESS", "MARK AS READ: " + result.toString());

            boolean isSeeking = false;
            if (result.optJSONObject("Notification").optInt("GroupId") == 2){
                isSeeking = true;
            }

            Notification notification = new Notification(result.optJSONObject("Notification"), isSeeking);
            for (int i = 0; i < getItemCount(); i++){
                if (dashboardList.get(i).Notification != null) {
                    if (notification.Id == dashboardList.get(i).Notification.Id) {
                        dashboardList.get(i).Notification.StatusId = notification.StatusId;
                        dashboardList.get(i).Notification.Status = notification.Status;
                        notifyDataSetChanged();
                        break;
                    }
                }
            }
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskCancelled(Response response) {

        }

        @Override
        public void taskError(String errorMessage) {
            if (MyApplication.DEBUG)
                Log.d("NOTIFICATION SUCCESS", "MARK AS READ FAILED");
        }
    };

    public AsyncTask getAPI(String Endpoint, HashMap<String, String> Query, AsyncResponse delegate) {

        Endpoint = Endpoint + "?";
        for (Map.Entry<String, String> entry : Query.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try { Endpoint += key + "=" + URLEncoder.encode(value, "UTF-8") + "&"; }
            catch (UnsupportedEncodingException ex) {
                Log.d("GET API NOTIFI-ADAPTER", ex.getMessage());
            }
        }

        //the last "&" is causing news/headlines endpoint not to work. So remove it.
        String last_character = Endpoint.substring(Endpoint.length() - 1);
        if (last_character.equals("&")){
            Endpoint = Endpoint.substring(0, Endpoint.length() - 1);
        }

        //   MyTaskParams taskparams = new MyTaskParams(Endpoint, params);

        GetAPIRequest task = new GetAPIRequest(delegate);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Endpoint);
        return task;

    }

    private class GetAPIRequest extends AsyncTask<String, Object, Response>
    {
        public AsyncResponse delegate = null;

        public GetAPIRequest(AsyncResponse asyncResponse) {
            delegate = asyncResponse;
        }

        @Override
        protected Response doInBackground(String... params)
        {
            // return JSONUtils.GetJSON(urls[0]);
            try {
                Response response = OkHttp.getData(params[0]);
                return response;
            } catch (IOException ex){
                Log.d("IOException", ex.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Response response)
        {

            //  Log.d("ONPOST J", result.toString());
            if (isCancelled())
                return;

            if (delegate != null) {
                if (response != null){
                    if (MyApplication.DEBUG)
                        Log.d("RESPONSE CODE", "" + response.code());
                    try {

                        String response_string = response.body().string();
                        JSONObject jsonObject = new JSONObject(response_string);

                        if (jsonObject == null)
                            delegate.taskError("Invalid response");
                        else if (response.code() == 200)
                            delegate.taskSuccess(jsonObject);
                        else {
                            if (jsonObject.optString("Message").equals("Authentication required")){
                                //  logout();
                            }
                            delegate.taskError(jsonObject.optString("Message"));
                        }

                    } catch (JSONException ex){
                        Log.d("JSONException", ex.getMessage());
                        delegate.taskError(ex.getMessage());
                    } catch (IOException ex){
                        Log.d("IOException", ex.getMessage());
                        delegate.taskError(ex.getMessage());
                    }

                }
                else {
                    delegate.taskError("Something went wrong");
                }

            }
        }
    }

}
