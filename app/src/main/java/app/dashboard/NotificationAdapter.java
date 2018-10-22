package app.dashboard;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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
import app.c2.android.AsyncResponse;
import app.c2.android.OkHttp;
import app.c2.android.Utils;
import app.dao.Booking;
import app.dao.Notification;
import app.dao.SearchResultListing;
import app.manage.BookingDetailActivity;
import app.notifications.NotificationsAdapter;
import app.search.DetailActivity;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_BOOKING;
import static app.agrishare.Constants.KEY_LISTING;
import static app.agrishare.Constants.KEY_SEEKER;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<Notification> notificationList = null;
    private ArrayList<Notification> arraylist;

    Activity activity;

    public NotificationAdapter(Context context, List<Notification> notificationList, Activity activity) {
        this.context = context;
        this.notificationList = notificationList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Notification>();
        this.arraylist.addAll(notificationList);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, date;
        ImageView photo, unread_dot;
        Button button;

        public MyViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
            date = view.findViewById(R.id.date);
            button = view.findViewById(R.id.button);
            photo = view.findViewById(R.id.photo);
            unread_dot = view.findViewById(R.id.unread_dot);
        }
    }

    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_dashboard_listing, parent, false);
        return new NotificationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.MyViewHolder holder, final int position) {

        String media_thumb = Utils.getFirstThumbPath(notificationList.get(position).Booking.Listing.Photos);
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

        try {
            JSONObject serviceObject = new JSONObject(notificationList.get(position).Booking.Service);
            String service = serviceObject.optJSONObject("Category").optString("Title") + " Services";
            holder.date.setText(Utils.convertDateToFriendlyStart(notificationList.get(position).Booking.StartDate) + " - " + Utils.convertDateToFriendly(notificationList.get(position).Booking.EndDate)
                    + " • " + service);
        } catch (JSONException ex) {
            Log.d("JSONException", ex.getMessage());
        }

        String time = Utils.timeAgo(notificationList.get(position).DateCreated);
        holder.button.setText("");
        if (notificationList.get(position).Seeking) {
            if (notificationList.get(position).Booking.StatusId == 1){
                holder.button.setText(context.getResources().getString(R.string.pay_now));
                holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.your_request_confirmed), time));
            }
            else if (notificationList.get(position).Booking.StatusId == 3){
                holder.button.setText(context.getResources().getString(R.string.view));
                try {
                    if (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(notificationList.get(position).Booking.EndDate).before(new Date())) {
                        holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.service_period_is_over_please_confirm_service_is_complete), time));
                    }
                    else {
                        holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.service_is_currently_in_progress), time));
                    }
                } catch (ParseException ex){
                    Log.d("ParseException", "CompetitionsAdapter: " + ex.getMessage());
                }
            }
            else if (notificationList.get(position).Booking.StatusId == 4){
                holder.button.setText(context.getResources().getString(R.string.view));
                holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.complete), time));
            }
            else if (notificationList.get(position).Booking.StatusId == 6){
                holder.button.setText(context.getResources().getString(R.string.view));
                holder.title.setText(getTitleHtmlText(notificationList.get(position).Title, time));
            }
            else if (notificationList.get(position).Booking.StatusId == 5){
                holder.button.setText(context.getResources().getString(R.string.view_reviews));
                holder.title.setText(getTitleHtmlText(notificationList.get(position).Title, time));
            }
            else {
                // holder.button.setVisibility(View.INVISIBLE);
                holder.button.setText(context.getResources().getString(R.string.view));
                holder.title.setText(getTitleHtmlText(notificationList.get(position).Title, time));
            }

        }
        else {
            if (notificationList.get(position).Booking.StatusId == 0){
                holder.button.setText(context.getResources().getString(R.string.confirm));
                holder.title.setText(getTitleHtmlText("Request waiting authorisation for " + Utils.convertDateToFriendlyStart(notificationList.get(position).Booking.StartDate) + " - " + Utils.convertDateToFriendly(notificationList.get(position).Booking.EndDate), time));
            }
            else if (notificationList.get(position).Booking.StatusId == 1){
                holder.button.setText(context.getResources().getString(R.string.view));
                holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.waiting_for_payment), time));
            }
            else if (notificationList.get(position).Booking.StatusId == 2){
                holder.button.setText(getTitleHtmlText(context.getResources().getString(R.string.pay_now), time));
            }
            else if (notificationList.get(position).Booking.StatusId == 3){
                holder.button.setText(context.getResources().getString(R.string.view));
                try {
                    if (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(notificationList.get(position).Booking.EndDate).before(new Date())) {
                        holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.service_period_is_over), time));
                    }
                    else {
                        holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.service_is_currently_in_progress), time));
                    }
                } catch (ParseException ex){
                    Log.d("ParseException", "CompetitionsAdapter: " + ex.getMessage());
                }
            }
            else if (notificationList.get(position).Booking.StatusId == 4){
                holder.button.setText(context.getResources().getString(R.string.review));
                holder.title.setText(getTitleHtmlText(context.getResources().getString(R.string.complete), time));
            }
            else {
                holder.button.setText(context.getResources().getString(R.string.view));
                holder.title.setText(getTitleHtmlText(notificationList.get(position).Title, time));
            }
        }

        if (notificationList.get(position).StatusId == 0)
            holder.unread_dot.setVisibility(View.VISIBLE);
        else
            holder.unread_dot.setVisibility(View.INVISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(context, BookingDetailActivity.class);
                    intent.putExtra(KEY_BOOKING, notificationList.get(position).Booking);
                    intent.putExtra(KEY_SEEKER, notificationList.get(position).Seeking);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);

                    if (notificationList.get(position).StatusId == 0)
                        markAsRead(notificationList.get(position).Id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
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

    public void markAsRead(long id)
    {
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("NotificationId", String.valueOf(id));
        getAPI("notifications/read", query, fetchResponse);

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
                if (notification.Id == notificationList.get(i).Id){
                    notificationList.get(i).StatusId = notification.StatusId;
                    notificationList.get(i).Status = notification.Status;
                    notifyDataSetChanged();
                    break;
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
