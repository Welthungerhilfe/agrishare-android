package app.c2.android;

import org.json.JSONObject;

import okhttp3.Response;

public interface AnalyticsAsyncResponse {
    void taskSuccess(JSONObject result, String event, long serviceId, String date, int hits, boolean recordExistsInLocalDB);
    void taskProgress(int progress);
    void taskError(String errorMessage, String event, long serviceId, String date, int hits, boolean recordExistsInLocalDB);
    void taskCancelled(Response response);
}