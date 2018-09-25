package app.manage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.Utils;
import app.dao.Booking;
import app.dao.Listing;
import app.search.DetailActivity;

import static app.agrishare.Constants.KEY_LISTING;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class ManageSeekingAdapter extends BaseAdapter {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<Booking> bookingList = null;
    private ArrayList<Booking> arraylist;

    Activity activity;

    public ManageSeekingAdapter(Context context, List<Booking> bookingList, Activity activity) {
        this.context = context;
        this.bookingList = bookingList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Booking>();
        this.arraylist.addAll(bookingList);
    }

    public class ViewHolder {
        TextView title, date, details, price;
        ImageView photo;
        Button payment_due, awaiting_confirmation;

    }

    @Override
    public int getCount() {
        return bookingList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();

            view = inflater.inflate(R.layout.row_manage_listing, null);
            holder.title = view.findViewById(R.id.title);
            holder.date = view.findViewById(R.id.date);
            holder.details = view.findViewById(R.id.details);
            holder.price = view.findViewById(R.id.price);
            holder.photo = view.findViewById(R.id.photo);
            holder.payment_due = view.findViewById(R.id.payment_due);
            holder.awaiting_confirmation = view.findViewById(R.id.awaiting_confirmation);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

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

        holder.title.setText(bookingList.get(position).Listing.Title);
        try {
            JSONObject serviceJSONObject = new JSONObject(bookingList.get(position).Service);
            holder.details.setText(serviceJSONObject.optJSONObject("Category").optString("Title"));
        } catch (JSONException ex){
            if (MyApplication.DEBUG)
                Log.d("JSONException", "" + ex.getMessage());
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(KEY_LISTING, bookingList.get(position).Listing);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                }
            }
        });

        return view;
    }
}