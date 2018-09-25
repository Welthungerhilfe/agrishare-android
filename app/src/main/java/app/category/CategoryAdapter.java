package app.category;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.agrishare.R;
import app.dao.Category;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class CategoryAdapter extends BaseAdapter {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<Category> categoryList = null;
    private ArrayList<Category> arraylist;

    CategoryActivity activity;

    public CategoryAdapter(Context context, List<Category> categoryList, CategoryActivity activity) {
        this.context = context;
        this.categoryList = categoryList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Category>();
        this.arraylist.addAll(categoryList);
    }

    public class ViewHolder {
        TextView title;

    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
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

        holder.title.setText(categoryList.get(position).Title);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    activity.returnResult(categoryList.get(position));
                }
            }
        });

        return view;
    }
}
