package app.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ernestnyumbu on 30/1/17.
 */
public class SourceSansProBoldTextView extends TextView {

    public SourceSansProBoldTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        //this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansProBold.ttf"));
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/TradeGothicLTComBold.ttf"));
    }
}
