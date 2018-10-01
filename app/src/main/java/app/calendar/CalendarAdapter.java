package app.calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.agrishare.R;
import app.c2.android.Utils;
import app.dao.MyCalendar;

/**
 * Created by ernestnyumbu on 28/9/2018.
 */

public class CalendarAdapter extends BaseAdapter {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;


    private List<MyCalendar> daysList = null;
    private ArrayList<MyCalendar> arraylist;
    CalendarActivity activity;
    Boolean edit = false;

    public CalendarAdapter(Context context, List<MyCalendar> daysList, CalendarActivity activity) {
        this.context = context;
        this.activity = activity;
        this.daysList = daysList;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<MyCalendar>();
        this.arraylist.addAll(daysList);
        this.edit = edit;
    }

    public class ViewHolder {
        TextView date;
        RelativeLayout parent_container;
        RelativeLayout container;
    }

    @Override
    public int getCount() {
        return daysList.size();
    }

    @Override
    public Object getItem(int position) {
        return daysList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();

            view = inflater.inflate(R.layout.row_date, null);
            holder.date = (TextView) view.findViewById(R.id.date);
            holder.parent_container = (RelativeLayout) view.findViewById(R.id.parent_container);
            holder.container = (RelativeLayout) view.findViewById(R.id.container);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        int left = Utils.convertDPtoPx(16, context);
        int right = left;
        int top = Utils.convertDPtoPx(4, context);
        int bottom = top;
        if(position == 0){
            holder.parent_container.setPadding(left, Utils.convertDPtoPx(16, context), right, bottom);
        }
        else if(position == getCount() - 1){
            holder.parent_container.setPadding(left, top, right, Utils.convertDPtoPx(32, context));
        }
        else {
            holder.parent_container.setPadding(left, top, right, bottom);
        }


        if (daysList.get(position).Available) {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.date.setTextColor(context.getResources().getColor(android.R.color.white));
        }
        else {
            holder.container.setBackground(context.getResources().getDrawable(R.drawable.grey_border_bg));
            holder.date.setTextColor(context.getResources().getColor(R.color.another_grey_for_text));
        }

        holder.date.setText(Utils.makeFriendlyDayDateString(daysList.get(position).Date));

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (daysList.get(position).Available) {
                    activity.returnResult(daysList.get(position).Date);
                }
                else {
                    Toast.makeText(context, activity.getResources().getString(R.string.this_day_is_already_taken), Toast.LENGTH_LONG).show();
                }

            }
        });


        return view;
    }




}
