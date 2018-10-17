package app.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.Utils;
import app.dao.Listing;
import app.dao.Notification;
import app.manage.BookingDetailActivity;
import app.manage.ManageEquipmentAdapter;
import app.search.DetailActivity;

import static app.agrishare.Constants.KEY_BOOKING;
import static app.agrishare.Constants.KEY_LISTING_ID;
import static app.agrishare.Constants.KEY_SEEKER;

/**
 * Created by ernestnyumbu on 17/10/2018.
 */

public class NotificationsAdapter extends BaseAdapter {

    String objectID;
    Context context;
    LayoutInflater inflater;

    private List<Notification> notificationList = null;
    private ArrayList<Notification> arraylist;

    Activity activity;

    public NotificationsAdapter(Context context, List<Notification> notificationList, Activity activity) {
        this.context = context;
        this.notificationList = notificationList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Notification>();
        this.arraylist.addAll(notificationList);
    }

    public class ViewHolder {
        TextView title, date;
        ImageView photo;
        Button button;
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();

            view = inflater.inflate(R.layout.row_dashboard_listing, null);
            holder.title = view.findViewById(R.id.title);
            holder.date = view.findViewById(R.id.date);
            holder.button = view.findViewById(R.id.button);
            holder.photo = view.findViewById(R.id.photo);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(context, BookingDetailActivity.class);
                    intent.putExtra(KEY_BOOKING, notificationList.get(position).Booking);
                    intent.putExtra(KEY_SEEKER, notificationList.get(position).Seeking);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                }
            }
        });

        return view;
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
}
