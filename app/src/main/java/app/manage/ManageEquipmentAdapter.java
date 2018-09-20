package app.manage;

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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import app.agrishare.MyApplication;
import app.agrishare.R;
import app.dao.FAQ;
import app.dao.Listing;
import app.faqs.FAQsDetailActivity;
import app.search.DetailActivity;

import static app.agrishare.Constants.KEY_FAQ;
import static app.agrishare.Constants.KEY_LISTING;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class ManageEquipmentAdapter extends BaseAdapter {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<Listing> listingList = null;
    private ArrayList<Listing> arraylist;

    Activity activity;

    public ManageEquipmentAdapter(Context context, List<Listing> listingList, Activity activity) {
        this.context = context;
        this.listingList = listingList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Listing>();
        this.arraylist.addAll(listingList);
    }

    public class ViewHolder {
        TextView title, availability, details;
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

            view = inflater.inflate(R.layout.row_equipment, null);
            holder.title = view.findViewById(R.id.title);
            holder.availability = view.findViewById(R.id.availability);
            holder.details = view.findViewById(R.id.details);
            holder.photo = view.findViewById(R.id.photo);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.title.setText(listingList.get(position).Title);
        //holder.availability.setText(listingList.get(position).Title);
        String services = "";
        try {
            JSONArray jsonArray = new JSONArray(listingList.get(position).Services);
            int size = jsonArray.length();
            if (size > 0){
                for (int i = 0; i < size; i++){
                    services = services + jsonArray.optJSONObject(i).optJSONObject("Subcategory").optString("Title");

                    if (i < size - 1){
                        services = services + ", ";
                    }
                }
                holder.details.setVisibility(View.VISIBLE);
                holder.details.setText(services);
            }
            else {
                holder.details.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException ex){
            if (MyApplication.DEBUG)
                Log.d("JSONException", "" + ex.getMessage());
        }
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
