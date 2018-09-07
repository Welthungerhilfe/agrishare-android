package app.c2.android;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by bradleysearle on 5/6/15.
 */
public interface CustomAsyncResponse {
    void taskSuccess(JSONObject result, RelativeLayout like, RelativeLayout loader, int position, TextView likesCount);
    void taskProgress(int progress);
    void taskError(String errorMessage, RelativeLayout like, RelativeLayout loader, RelativeLayout like_text_layout);
}