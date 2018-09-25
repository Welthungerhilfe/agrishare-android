package app.search;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import app.agrishare.R;
import app.dao.Photo;

/**
 * Created by ernestnyumbu on 13/9/2018.
 */

public class SliderAdapter extends PagerAdapter {

    private ArrayList<Photo> images;
    private LayoutInflater inflater;
    private Context context;
    private Activity activity;

    public SliderAdapter(Context context, ArrayList<Photo> images, Activity activity) {
        this.activity = activity;
        this.context = context;
        this.images=images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.image);
        // myImage.setImageResource(images.get(position));
       /* Picasso.with(context).load(images.get(position))
                .placeholder(R.drawable.default_image)
                .into(myImage); */
        //Picasso.with(context).load(R.drawable.default_image).into(myImage);
        view.addView(myImageLayout, 0);

      /*  myImageLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent _intent = new Intent(context, ImageFullScreenActivity.class);
                _intent.putExtra(MyApplication.KEY_PHOTOS, photos);
                _intent.putExtra(MyApplication.KEY_CURRENT_IMAGE, position);
                context.startActivity(_intent);
                activity.overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
            }
        }); */

        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}