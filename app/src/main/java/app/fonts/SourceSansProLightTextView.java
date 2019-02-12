package app.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ernestnyumbu on 30/1/17.
 */
public class SourceSansProLightTextView extends TextView {

    public SourceSansProLightTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansProLight.ttf"));
     //   this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/TradeGothicLTCom.ttf"));
    }
}
