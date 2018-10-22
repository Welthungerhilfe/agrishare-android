package app.manage;

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
import app.c2.android.AsyncResponse;
import app.c2.android.OkHttp;
import app.c2.android.Utils;
import app.dao.Booking;
import app.dao.Notification;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_BOOKING;
import static app.agrishare.Constants.KEY_SEEKER;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.MyViewHolder> {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<Booking> bookingList = null;
    private ArrayList<Booking> arraylist;

    Activity activity;

    public ManageAdapter(Context context, List<Booking> bookingList, Activity activity) {
        this.context = context;
        this.bookingList = bookingList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Booking>();
        this.arraylist.addAll(bookingList);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, details, price;
        ImageView photo;
        Button payment_due, awaiting_confirmation, rate_this_service;

        public MyViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
            date = view.findViewById(R.id.date);
            details = view.findViewById(R.id.details);
            price = view.findViewById(R.id.price);
            photo = view.findViewById(R.id.photo);
            payment_due = view.findViewById(R.id.payment_due);
            awaiting_confirmation = view.findViewById(R.id.awaiting_confirmation);
            rate_this_service = view.findViewById(R.id.rate_this_service);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ManageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_manage_listing, parent, false);
        return new ManageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ManageAdapter.MyViewHolder holder, final int position) {

        String media_thumb = Utils.getFirstThumbPath(bookingList.get(position).Listing.Photos);
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

        if (bookingList.get(position).StatusId == 1){
            holder.awaiting_confirmation.setVisibility(View.VISIBLE);
            holder.payment_due.setVisibility(View.GONE);
            holder.rate_this_service.setVisibility(View.GONE);
        }
        else if (bookingList.get(position).StatusId == 2){
            holder.awaiting_confirmation.setVisibility(View.GONE);
            holder.payment_due.setVisibility(View.VISIBLE);
            holder.rate_this_service.setVisibility(View.GONE);
        }
        else if (bookingList.get(position).StatusId == 4){
            holder.awaiting_confirmation.setVisibility(View.GONE);
            holder.payment_due.setVisibility(View.GONE);
            holder.rate_this_service.setVisibility(View.VISIBLE);
        }
        else {
            holder.awaiting_confirmation.setVisibility(View.GONE);
            holder.payment_due.setVisibility(View.GONE);
            holder.rate_this_service.setVisibility(View.GONE);
        }

        holder.date.setText(Utils.convertDateToFriendlyStart(bookingList.get(position).StartDate) + " - " + Utils.convertDateToFriendly(bookingList.get(position).EndDate));
        holder.title.setText(bookingList.get(position).Listing.Title);
        holder.price.setText("$" + String.format("%.2f", bookingList.get(position).Price));

        try {
            JSONObject serviceJSONObject = new JSONObject(bookingList.get(position).Service);
            holder.details.setText(serviceJSONObject.optJSONObject("Category").optString("Title"));
        } catch (JSONException ex){
            if (MyApplication.DEBUG)
                Log.d("JSONException", "" + ex.getMessage());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(context, BookingDetailActivity.class);
                    intent.putExtra(KEY_BOOKING, bookingList.get(position));
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

}
