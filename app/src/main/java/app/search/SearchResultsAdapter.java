package app.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.agrishare.R;
import app.c2.android.Utils;
import app.dao.FAQ;
import app.dao.Listing;
import app.faqs.FAQsDetailActivity;

import static app.agrishare.Constants.KEY_FAQ;
import static app.agrishare.Constants.KEY_LISTING;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class SearchResultsAdapter extends BaseAdapter {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<Listing> listingList = null;
    private ArrayList<Listing> arraylist;

    Activity activity;

    public SearchResultsAdapter(Context context, List<Listing> listingList, Activity activity) {
        this.context = context;
        this.listingList = listingList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Listing>();
        this.arraylist.addAll(listingList);
    }

    public class ViewHolder {
        TextView title;
        ImageView photo;

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
            holder.photo = view.findViewById(R.id.photo);

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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(KEY_LISTING, listingList.get(position));
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                }
            }
        });

        return view;
    }
}
