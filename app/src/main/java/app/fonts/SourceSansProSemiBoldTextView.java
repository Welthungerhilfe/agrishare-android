package app.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ernestnyumbu on 30/1/17.
 */
public class SourceSansProSemiBoldTextView extends TextView {

    public SourceSansProSemiBoldTextView(Context context, AttributeSet attrs){
        super(context, attrs);
     //   this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansProSemiBold.ttf"));
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/TradeGothicLTComBold.ttf"));
    }
}
