package app.manage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.Utils;
import app.dao.GroupMember;
import app.dao.Listing;
import app.search.DetailActivity;

import static app.agrishare.Constants.KEY_LISTING;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class GroupMemberAdapter extends BaseAdapter {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<GroupMember> memberList = null;
    private ArrayList<GroupMember> arraylist;

    BookingDetailActivity activity;

    double total_price = 0;
    double total_quantity = 0;

    public GroupMemberAdapter(Context context, List<GroupMember> memberList, BookingDetailActivity activity, double total_price, double total_quantity) {
        this.context = context;
        this.memberList = memberList;
        this.activity = activity;
        this.total_price = total_price;
        this.total_quantity = total_quantity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<GroupMember>();
        this.arraylist.addAll(memberList);
    }

    public class ViewHolder {
        TextView amount;
        EditText name, quantity, ecocash_number;
        ImageView cancel;

    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();

            view = inflater.inflate(R.layout.row_group_member, null);
            holder.amount = view.findViewById(R.id.group_member_amount);
            holder.name = view.findViewById(R.id.group_member_name);
            holder.quantity = view.findViewById(R.id.group_member_quantity);
            holder.ecocash_number = view.findViewById(R.id.group_member_number);
            holder.cancel = view.findViewById(R.id.group_member_cancel);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                memberList.get(position).name = holder.name.getText().toString();
            }
        });

        holder.quantity.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!holder.quantity.getText().toString().isEmpty()) {
                    memberList.get(position).quantity = Double.parseDouble(holder.quantity.getText().toString());
                    double amount = (memberList.get(position).quantity / total_quantity) * total_price;
                    holder.amount.setText("$" + String.format("%.2f", amount));
                }
                else {
                    holder.amount.setText("$0.00");
                }
            }
        });

        holder.ecocash_number.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                memberList.get(position).ecocash_number = holder.ecocash_number.getText().toString();
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    activity.removeGroupMember(position);
                }
            }
        });

        return view;
    }
}
