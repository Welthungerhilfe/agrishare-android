package app.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ernestnyumbu on 30/1/17.
 */
public class SourceSansProRegularTextView extends TextView {

    public SourceSansProRegularTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansProRegular.ttf"));
    }
}
