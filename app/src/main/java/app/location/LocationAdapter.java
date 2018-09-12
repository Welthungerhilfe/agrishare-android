package app.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.agrishare.R;
import app.dao.FAQ;
import app.dao.Location;
import app.faqs.FAQsDetailActivity;

import static app.agrishare.Constants.KEY_FAQ;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class LocationAdapter extends BaseAdapter {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<Location> locationList = null;
    private ArrayList<Location> arraylist;

    SelectLocationActivity activity;

    public LocationAdapter(Context context, List<Location> locationList, SelectLocationActivity activity) {
        this.context = context;
        this.locationList = locationList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Location>();
        this.arraylist.addAll(locationList);
    }

    public class ViewHolder {
        TextView title;

    }

    @Override
    public int getCount() {
        return locationList.size();
    }

    @Override
    public Object getItem(int position) {
        return locationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();

            view = inflater.inflate(R.layout.row_location, null);
            holder.title = view.findViewById(R.id.title);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.title.setText(locationList.get(position).Title);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    activity.returnResult(locationList.get(position).Id, locationList.get(position).Title);
                }
            }
        });

        return view;
    }
}
