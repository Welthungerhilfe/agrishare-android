package app.ratings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.agrishare.R;
import app.c2.android.Utils;
import app.dao.FAQ;
import app.dao.Rating;
import app.faqs.FAQsDetailActivity;

import static app.agrishare.Constants.KEY_FAQ;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class RatingsAdapter extends BaseAdapter {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<Rating> ratingsList = null;
    private ArrayList<Rating> arraylist;

    Activity activity;

    public RatingsAdapter(Context context, List<Rating> ratingsList, Activity activity) {
        this.context = context;
        this.ratingsList = ratingsList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Rating>();
        this.arraylist.addAll(ratingsList);
    }

    public class ViewHolder {
        TextView name, date, comments;
        ImageView rating_imageview;

    }

    @Override
    public int getCount() {
        return ratingsList.size();
    }

    @Override
    public Object getItem(int position) {
        return ratingsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();

            view = inflater.inflate(R.layout.row_rating, null);

            holder.name = view.findViewById(R.id.name);
            holder.date = view.findViewById(R.id.date);
            holder.comments = view.findViewById(R.id.comments);
            holder.rating_imageview = view.findViewById(R.id.rating_imageview);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(ratingsList.get(position).Title);
        holder.comments.setText(ratingsList.get(position).Comments);
        holder.date.setText(Utils.timeAgo(ratingsList.get(position).DateCreated));

        if (ratingsList.get(position).Stars == 0) {
            Picasso.get()
                    .load(R.drawable.ratings_zero_30)
                    .into(holder.rating_imageview);
        }
        else if (ratingsList.get(position).Stars == 1) {
            Picasso.get()
                    .load(R.drawable.ratings_one_30)
                    .into(holder.rating_imageview);
        }
        else if (ratingsList.get(position).Stars == 2) {
            Picasso.get()
                    .load(R.drawable.ratings_two_30)
                    .into(holder.rating_imageview);
        }
        else if (ratingsList.get(position).Stars == 3) {
            Picasso.get()
                    .load(R.drawable.ratings_three_30)
                    .into(holder.rating_imageview);
        }
        else if (ratingsList.get(position).Stars == 4) {
            Picasso.get()
                    .load(R.drawable.ratings_four_30)
                    .into(holder.rating_imageview);
        }
        else if (ratingsList.get(position).Stars == 5) {
            Picasso.get()
                    .load(R.drawable.ratings_five_30)
                    .into(holder.rating_imageview);
        }

        return view;
    }
}
