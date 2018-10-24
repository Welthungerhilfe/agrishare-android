package app.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import app.dao.FAQ;
import app.dao.Listing;
import app.dao.SearchResultListing;
import app.faqs.FAQsDetailActivity;

import static app.agrishare.Constants.KEY_FAQ;
import static app.agrishare.Constants.KEY_LISTING;
import static app.agrishare.Constants.KEY_LISTING_ID;
import static app.agrishare.Constants.KEY_SEARCH_RESULT_LISTING;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class SearchResultsAdapter extends BaseAdapter {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<SearchResultListing> listingList = null;
    private ArrayList<SearchResultListing> arraylist;

    Activity activity;

    public SearchResultsAdapter(Context context, List<SearchResultListing> listingList, Activity activity) {
        this.context = context;
        this.listingList = listingList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<SearchResultListing>();
        this.arraylist.addAll(listingList);
    }

    public class ViewHolder {
        TextView title, distance, details, price, view_availability;
        ImageView photo;
        Button availability;

    }

    @Override
    public int getCount() {
        return listingList.size();
    }

    @Override
    public Object getItem(int position) {
        return listingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();

            view = inflater.inflate(R.layout.row_search_result, null);
            holder.title = view.findViewById(R.id.title);
            holder.distance = view.findViewById(R.id.distance);
            holder.details = view.findViewById(R.id.details);
            holder.price = view.findViewById(R.id.price);
            holder.view_availability = view.findViewById(R.id.view_availability);
            holder.photo = view.findViewById(R.id.photo);
            holder.availability = view.findViewById(R.id.availability);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String media_thumb = Utils.getFirstThumbPath(listingList.get(position).Photos);
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

        holder.title.setText(listingList.get(position).Title);
        holder.distance.setText(String.format("%.2f", listingList.get(position).Distance) + "km away");
        holder.details.setText("Year: " + listingList.get(position).Year);
        holder.price.setText("$" + String.format("%.2f", listingList.get(position).Price));

        if (listingList.get(position).Available){
            holder.availability.setText(context.getResources().getString(R.string.available));
            holder.availability.setBackground(context.getResources().getDrawable(R.drawable.grey_border_bg));
            holder.availability.setTextColor(context.getResources().getColor(R.color.grey_for_text));
        }
        else {
            holder.availability.setText(context.getResources().getString(R.string.not_available));
            holder.availability.setBackground(context.getResources().getDrawable(R.drawable.blood_red_bg));
            holder.availability.setTextColor(context.getResources().getColor(android.R.color.white));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(KEY_LISTING_ID, listingList.get(position).ListingId);
                    intent.putExtra(KEY_SEARCH_RESULT_LISTING, listingList.get(position));
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                }
            }
        });

        return view;
    }
}
