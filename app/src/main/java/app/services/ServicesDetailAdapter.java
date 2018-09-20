package app.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.agrishare.R;
import app.dao.FAQ;
import app.dao.ListingDetailService;
import app.faqs.FAQsDetailActivity;

import static app.agrishare.Constants.KEY_FAQ;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class ServicesDetailAdapter extends BaseAdapter {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<ListingDetailService> serviceList = null;
    private ArrayList<ListingDetailService> arraylist;

    Activity activity;

    public ServicesDetailAdapter(Context context, List<ListingDetailService> serviceList, Activity activity) {
        this.context = context;
        this.serviceList = serviceList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<ListingDetailService>();
        this.arraylist.addAll(serviceList);
    }

    public class ViewHolder {
        TextView title;
        LinearLayout specs_container;

    }

    @Override
    public int getCount() {
        return serviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return serviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();

            view = inflater.inflate(R.layout.row_service_detail, null);
            holder.title = view.findViewById(R.id.title);
            holder.specs_container = view.findViewById(R.id.specs_container);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.title.setText(serviceList.get(position).Subcategory.Title);

        holder.specs_container.removeAllViews();

        if (serviceList.get(position).TimePerQuantityUnit > 0) {
            addRow(holder.specs_container, serviceList.get(position).TimeUnit, serviceList.get(position).TimePerQuantityUnit + " " + getAbbreviatedTimeUnit(serviceList.get(position).TimeUnitId));
        }

        if (serviceList.get(position).PricePerQuantityUnit > 0) {
            addRow(holder.specs_container, context.getResources().getString(R.string.hire_cost), "$" + serviceList.get(position).PricePerQuantityUnit + "/" + getAbbreviatedQuantityUnit(serviceList.get(position).QuantityUnitId));
        }

        if (serviceList.get(position).FuelPerQuantityUnit > 0) {
            addRow(holder.specs_container, context.getResources().getString(R.string.fuel_cost), "$" + serviceList.get(position).FuelPerQuantityUnit + "/" + getAbbreviatedQuantityUnit(serviceList.get(position).QuantityUnitId));
        }

        if (serviceList.get(position).MinimumQuantity > 0) {
            String label = context.getResources().getString(R.string.minimum_field_size);
            if (serviceList.get(position).QuantityUnitId == 2) {
                label = context.getResources().getString(R.string.bags);
            }
            addRow(holder.specs_container, label, serviceList.get(position).MinimumQuantity + getAbbreviatedQuantityUnit(serviceList.get(position).QuantityUnitId));
        }

        if (serviceList.get(position).PricePerDistanceUnit > 0) {
            addRow(holder.specs_container, context.getResources().getString(R.string.distance_charge), "$" + serviceList.get(position).PricePerDistanceUnit + "/" + getAbbreviatedDistanceUnit(serviceList.get(position).DistanceUnitId));
        }

        if (serviceList.get(position).MaximumDistance > 0) {
            addRow(holder.specs_container, context.getResources().getString(R.string.maximum_distance), serviceList.get(position).MaximumDistance + getAbbreviatedDistanceUnit(serviceList.get(position).DistanceUnitId));
        }

        return view;
    }

    private void addRow(LinearLayout container, String label, String value){
        final View specsView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_spec, null, false);
        ((TextView) specsView.findViewById(R.id.label)).setText(label);
        ((TextView) specsView.findViewById(R.id.value)).setText(value);
        container.addView(specsView);
    }

    private String getAbbreviatedTimeUnit(long unit_id){
        if (unit_id == 1){
            return "ha/hr";
        }
        else if (unit_id == 2){
            return "hrs/100km";
        }
        else if (unit_id == 3){
            return "bags/hr";
        }

        return "";
    }

    private String getAbbreviatedQuantityUnit(long unit_id){
        if (unit_id == 1){
            return "ha";
        }
        else if (unit_id == 2){
            return "bags";
        }

        return "";
    }

    private String getAbbreviatedDistanceUnit(long unit_id){
        if (unit_id == 1){
            return "km";
        }

        return "";
    }

}
