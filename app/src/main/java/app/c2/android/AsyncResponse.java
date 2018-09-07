package app.c2.android;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Response;

/**
 * Created by bradleysearle on 5/6/15.
 */
public interface AsyncResponse {
    void taskSuccess(JSONObject result);
    void taskProgress(int progress);
    void taskError(String errorMessage);
    void taskCancelled(Response response);
}