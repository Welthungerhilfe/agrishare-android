package app.services;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.agrishare.R;
import app.category.CategoryActivity;
import app.dao.Category;
import app.dao.Service;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class SelectServiceAdapter extends BaseAdapter {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<Service> serviceList = null;
    private ArrayList<Service> arraylist;

    SelectServiceActivity activity;

    public SelectServiceAdapter(Context context, List<Service> serviceList, SelectServiceActivity activity) {
        this.context = context;
        this.serviceList = serviceList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Service>();
        this.arraylist.addAll(serviceList);
    }

    public class ViewHolder {
        TextView title;

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

            view = inflater.inflate(R.layout.row_category, null);
            holder.title = view.findViewById(R.id.title);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.title.setText(serviceList.get(position).Title);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    activity.returnResult(serviceList.get(position));
                }
            }
        });

        return view;
    }
}
