package app.equipment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.agrishare.R;
import app.category.CategoryActivity;
import app.dao.Category;
import app.dao.EquipmentService;
import app.dao.Service;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class EquipmentServiceAdapter extends BaseAdapter {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<EquipmentService> serviceList = null;
    private ArrayList<EquipmentService> arraylist;

    AddEquipmentActivity activity;

    public EquipmentServiceAdapter(Context context, List<EquipmentService> serviceList, AddEquipmentActivity activity) {
        this.context = context;
        this.serviceList = serviceList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<EquipmentService>();
        this.arraylist.addAll(serviceList);
    }

    public class ViewHolder {
        TextView title;
        Switch toggle_switch;

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

            view = inflater.inflate(R.layout.row_equipment_service, null);
            holder.title = view.findViewById(R.id.title);
            holder.toggle_switch= view.findViewById(R.id.service_switch);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.title.setText(serviceList.get(position).title);

        if (serviceList.get(position).enabled)
            holder.toggle_switch.setChecked(true);
        else
            holder.toggle_switch.setChecked(false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    activity.openServiceDetailForm(serviceList.get(position));
                }
            }
        });

        return view;
    }
}
