package app.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.agrishare.R;
import app.c2.android.Utils;
import app.dao.Booking;
import app.dao.Notification;
import app.dao.SearchResultListing;
import app.manage.BookingDetailActivity;
import app.search.DetailActivity;

import static app.agrishare.Constants.KEY_BOOKING;
import static app.agrishare.Constants.KEY_LISTING;

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
        ImageView photo;
        Button button;

        public MyViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
            date = view.findViewById(R.id.date);
            button = view.findViewById(R.id.button);
            photo = view.findViewById(R.id.photo);
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

        holder.date.setText(Utils.convertDateToFriendlyStart(notificationList.get(position).Booking.StartDate) + " - " + Utils.convertDateToFriendly(notificationList.get(position).Booking.EndDate));
        holder.title.setText(notificationList.get(position).Title);

        holder.button.setText("");
        if (notificationList.get(position).Seeking) {
            if (notificationList.get(position).Booking.StatusId == 1 || notificationList.get(position).Booking.StatusId == 6){
                holder.button.setText(context.getResources().getString(R.string.view));
            }
            else if (notificationList.get(position).Booking.StatusId == 5){
                holder.button.setText(context.getResources().getString(R.string.view_reviews));
            }
            else {
                holder.button.setVisibility(View.INVISIBLE);
            }

        }
        else {
            if (notificationList.get(position).Booking.StatusId == 2){
                holder.button.setText(context.getResources().getString(R.string.pay_now));
            }
            else if (notificationList.get(position).Booking.StatusId == 4){
                holder.button.setText(context.getResources().getString(R.string.review));
            }
            else {
                holder.button.setVisibility(View.INVISIBLE);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(context, BookingDetailActivity.class);
                    intent.putExtra(KEY_BOOKING, notificationList.get(position).Booking);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

}
