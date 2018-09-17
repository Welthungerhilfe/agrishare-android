package app.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import app.agrishare.R;
import app.dao.SplashPage;

/**
 * Created by ernestnyumbu on 17/9/2018.
 */

public class SplashAdapter extends PagerAdapter {

    private SplashActivity _activity;
    private ArrayList<SplashPage> pageList;
    private LayoutInflater inflater;

    public SplashAdapter(SplashActivity activity, ArrayList<SplashPage> pageList) {
        this._activity = activity;
        this.pageList = pageList;
    }

    @Override
    public int getCount() {
        return this.pageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final TextView title, subtitle, intro, action;

        inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.row_splash_page, container, false);

        subtitle = viewLayout.findViewById(R.id.subtitle);
        title = viewLayout.findViewById(R.id.title);
        intro = viewLayout.findViewById(R.id.intro);
        action = viewLayout.findViewById(R.id.action);

        subtitle.setText(pageList.get(position).subtitle);
        title.setText(pageList.get(position).title);
        intro.setText(pageList.get(position).intro);
        action.setText(pageList.get(position).action);

        ((ViewPager) container).addView(viewLayout);

        (viewLayout.findViewById(R.id.action)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (position == 0) {
                        _activity.gotoNext();
                    } else if (position == 1) {
                        _activity.gotoNext();
                    } else {
                        _activity.gotoPrevious();
                    }
                }
            }
        });

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}


