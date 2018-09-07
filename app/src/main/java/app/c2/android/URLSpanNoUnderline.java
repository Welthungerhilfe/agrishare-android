package app.c2.android;

import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * Created by ernestnyumbu on 6/3/18.
 */

public class URLSpanNoUnderline extends URLSpan {
    public URLSpanNoUnderline(String url) {
        super(url);
    }
    @Override public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}
