package app.faqs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.agrishare.R;
import app.dao.FAQ;

import static app.agrishare.Constants.KEY_FAQ;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class FAQAdapter  extends BaseAdapter {

    String objectID;
    // Declare Variables
    Context context;
    LayoutInflater inflater;

    private List<FAQ> faqList = null;
    private ArrayList<FAQ> arraylist;

    Activity activity;

    public FAQAdapter(Context context, List<FAQ> faqList, Activity activity) {
        this.context = context;
        this.faqList = faqList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<FAQ>();
        this.arraylist.addAll(faqList);
    }

    public class ViewHolder {
        TextView title;

    }

    @Override
    public int getCount() {
        return faqList.size();
    }

    @Override
    public Object getItem(int position) {
        return faqList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();

            view = inflater.inflate(R.layout.row_faq, null);
            holder.title = view.findViewById(R.id.title);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.title.setText(faqList.get(position).Question);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(context, FAQsDetailActivity.class);
                    intent.putExtra(KEY_FAQ, faqList.get(position));
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                }
            }
        });

        return view;
    }
}
