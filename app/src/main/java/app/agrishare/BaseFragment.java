package app.agrishare;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import app.c2.android.AsyncResponse;
import app.c2.android.ConnectionDetector;
import app.c2.android.MyTaskParams;
import app.c2.android.OkHttp;
import okhttp3.MultipartBody;

import com.github.lzyzsd.circleprogress.DonutProgress;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by ernestnyumbu on 7/03/16.
 */
public class BaseFragment extends Fragment {

    //views
    public SwipeRefreshLayout swipeContainer;
    public View rootView;
    public TextView no_listings_found;
    public ListView listview;
    public RecyclerView recyclerView;
    TextView caption;
    EditText search;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    public Activity mActivity;

    public String mtag = "";

    public static BaseFragment newInstance() {
        BaseFragment fragment = new BaseFragment();
        return fragment;
    }

    public BaseFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    /* API */

    public AsyncTask postAPI(String Endpoint, HashMap<String, String> Query, AsyncResponse delegate) {

        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : Query.entrySet()) {
                jsonObject.accumulate(entry.getKey(), entry.getValue());
            }
        } catch (JSONException ex){
            Log("JSONException: " + ex.getMessage());
        }

        MyTaskParams taskparams = new MyTaskParams(Endpoint, jsonObject.toString());

        PostAPIRequest task = new PostAPIRequest(delegate);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, taskparams);
        return task;

    }

    private class PostAPIRequest extends AsyncTask<MyTaskParams, Object, Response>
    {
        public AsyncResponse delegate = null;

        public PostAPIRequest(AsyncResponse asyncResponse) {
            delegate = asyncResponse;
        }

        @Override
        protected Response doInBackground(MyTaskParams... params)
        {
            // return JSONUtils.GetJSON(urls[0]);
            try {
                Response response = OkHttp.postJSONData(params[0].endpoint, params[0].json, delegate);
                return response;
            } catch (IOException ex){
                Log.d("IOException", ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onCancelled(Response response) {
            super.onCancelled(response);
            if (delegate != null) {
                delegate.taskCancelled(response);
            }
        }

        protected void onPostExecute(Response response)
        {

            //  Log.d("ONPOST J", result.toString());
            if (isCancelled())
                return;

            if (delegate != null) {
                if (response != null){

                    try {

                        String response_string = response.body().string(); Log("RESPONSE STRING" + response_string);
                        JSONObject jsonObject = new JSONObject(response_string);

                        if (jsonObject == null)
                            delegate.taskError("Invalid response");
                        else if (response.code() == 200)
                            delegate.taskSuccess(jsonObject);
                        else
                            delegate.taskError(jsonObject.optString("Message"));

                    } catch (JSONException ex){
                        Log.d("JSONException", ex.getMessage());
                        delegate.taskError(ex.getMessage());
                    } catch (IOException ex){
                        Log.d("IOException", ex.getMessage());
                        delegate.taskError(ex.getMessage());
                    }

                }
                else {
                    delegate.taskError("Something went wrong");
                }

            }
        }
    }

    public AsyncTask getAPI(String Endpoint, HashMap<String, String> Query, AsyncResponse delegate) {

        Endpoint = Endpoint + "?";
        for (Map.Entry<String, String> entry : Query.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try { Endpoint += key + "=" + URLEncoder.encode(value, "UTF-8") + "&"; }
            catch (UnsupportedEncodingException ex) {
                Log(ex.getMessage());
            }
        }

        //the last "&" is causing news/headlines endpoint not to work. So remove it.
        String last_character = Endpoint.substring(Endpoint.length() - 1);
        if (last_character.equals("&")){
            Endpoint = Endpoint.substring(0, Endpoint.length() - 1);
        }
        //   MyTaskParams taskparams = new MyTaskParams(Endpoint, params);

        GetAPIRequest task = new GetAPIRequest(delegate);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Endpoint);
        return task;
    }

    private class GetAPIRequest extends AsyncTask<String, Object, Response>
    {
        public AsyncResponse delegate = null;

        public GetAPIRequest(AsyncResponse asyncResponse) {
            delegate = asyncResponse;
        }

        @Override
        protected Response doInBackground(String... params)
        {
            // return JSONUtils.GetJSON(urls[0]);
            try {
                Response response = OkHttp.getData(params[0]);
                return response;
            } catch (IOException ex){
                Log.d("IOException", ex.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Response response)
        {

            //  Log.d("ONPOST J", result.toString());
            if (isCancelled())
                return;

            if (delegate != null) {
                if (response != null){

                    try {

                        String response_string = response.body().string();
                        JSONObject jsonObject = new JSONObject(response_string);

                        if (jsonObject == null)
                            delegate.taskError("Invalid response");
                        else if (response.code() == 200)
                            delegate.taskSuccess(jsonObject);
                        else {
                            if (jsonObject.optString("Message").equals("Authentication required")){
                                logout();
                            }
                            delegate.taskError(jsonObject.optString("Message"));
                        }

                    } catch (JSONException ex){
                        Log.d("JSONException", ex.getMessage());
                        delegate.taskError(ex.getMessage());
                    } catch (IOException ex){
                        Log.d("IOException", ex.getMessage());
                        delegate.taskError(ex.getMessage());
                    }

                }
                else {
                    delegate.taskError("Something went wrong");
                }

            }
        }
    }

    public void logout(){
        if (MyApplication.notificationManager != null)
            MyApplication.notificationManager.cancelAll();
     /*   MyApplication.token = "";
        MyApplication.userID = 0;
        MyApplication.firstName = "";
        MyApplication.lastName = "";
        MyApplication.userTitle = "";
        MyApplication.loggedIn = false;
        MyApplication.last_bookings_update = MyApplication.default_date;
        MyApplication.isDeviceRegisteredOnOurServer = false;
        MyApplication.email = "";
        SharedPreferences.Editor editor = MyApplication.prefs.edit();
        editor.putString(MyApplication.PREFS_TOKEN, MyApplication.token);
        editor.putInt(MyApplication.PREFS_USER_ID, MyApplication.userID);
        editor.putString(MyApplication.PREFS_FIRST_NAME, MyApplication.firstName);
        editor.putString(MyApplication.PREFS_LAST_NAME, MyApplication.lastName);
        editor.putString(MyApplication.PREFS_USER_TITLE, MyApplication.userTitle);
        editor.putBoolean(MyApplication.PREFS_LOGGED_IN, MyApplication.loggedIn);
        editor.putString(MyApplication.PREFS_LAST_BOOKINGS_UPDATE, MyApplication.last_update);
        editor.putString(MyApplication.PREFS_EMAIL, MyApplication.email);
        editor.putString(MyApplication.PREFS_NOTIFICATIONS_REG_ID, "");
        editor.putBoolean(MyApplication.PREFS_IS_DEVICE_REGISTERED_ON_OUR_SERVER, MyApplication.isDeviceRegisteredOnOurServer);
        editor.commit();        */

      /*  RealmResults<Bookings> results = MyApplication.realm.where(Bookings.class).findAll();
        MyApplication.realm.beginTransaction();
        results.deleteAllFromRealm();
        MyApplication.realm.commitTransaction();    */

    }


    public void Log(String Message) {
        if (MyApplication.DEBUG)
            Log.d(MyApplication.DEBUG_TAG, Message);
    }

    public void startLoading(){
      //  ((RelativeLayout) rootView.findViewById(R.id.loading))
      //          .setVisibility(View.VISIBLE);
    }

    public void stopLoading(){
      //  ((RelativeLayout) rootView.findViewById(R.id.loading))
      //          .setVisibility(View.GONE);
    }

    public void noListingsFound(String message){
        no_listings_found.setVisibility(View.VISIBLE);
        no_listings_found.setText(message);
    }

    public void hideNoListingsFound(){
        no_listings_found.setVisibility(View.GONE);
    }

    public void popAlert(Context context, String Title, String Message){
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);        //allows back button to cancel dialog box
    }

    public boolean isInternetPresent(Context context){
        // creating connection detector class instance
        cd = new ConnectionDetector(context);

        // get Internet status
        return isInternetPresent = cd.isConnectingToInternet();
    }

    public void refreshComplete() {
        swipeContainer.setRefreshing(false);
    }

    public void popToast(Context context, String Message) {
        Log(Message);
        Toast slice = Toast.makeText(context, Message, Toast.LENGTH_SHORT);
        slice.show();
    }

    public void sendHit(String screen_name) {
      //  MyApplication.tracker.setScreenName(screen_name);
     //   MyApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void showFeedback(int iconResourceId, String title, String message) {
        rootView.findViewById(R.id.feedback).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_activity).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_icon).setVisibility(View.VISIBLE);
        ((ImageView)rootView.findViewById(R.id.feedback_icon)).setImageResource(iconResourceId);
        ((TextView)rootView.findViewById(R.id.feedback_title)).setText(title);
        ((TextView)rootView.findViewById(R.id.feedback_message)).setText(message);
        rootView.findViewById(R.id.feedback_retry).setVisibility(View.GONE);
    }


    public void showFeedbackWithButton(int iconResourceId, String title, String message) {
        rootView.findViewById(R.id.feedback).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_activity).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_icon).setVisibility(View.VISIBLE);
        ((ImageView)rootView.findViewById(R.id.feedback_icon)).setImageResource(iconResourceId);
        ((TextView)rootView.findViewById(R.id.feedback_title)).setText(title);
        ((TextView)rootView.findViewById(R.id.feedback_message)).setText(message);
        rootView.findViewById(R.id.feedback_retry).setVisibility(View.VISIBLE);
    }

    public void hideFeedback() {
        rootView.findViewById(R.id.feedback).setVisibility(View.GONE);
    }

    public void showLoader() {
        showLoader("", "");
    }

    public void showLoader(String title, String message) {
        rootView.findViewById(R.id.feedback).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_activity).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_icon).setVisibility(View.GONE);
        ((TextView)rootView.findViewById(R.id.feedback_title)).setText(title);
        ((TextView)rootView.findViewById(R.id.feedback_message)).setText(message);
        rootView.findViewById(R.id.feedback_retry).setVisibility(View.GONE);
    }

    public void showLoader(String title, String message, int progress) {
        rootView.findViewById(R.id.feedback).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_activity).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.VISIBLE);
        ((DonutProgress) rootView.findViewById(R.id.feedback_progress)).setProgress(progress);
        rootView.findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_icon).setVisibility(View.GONE);
        ((TextView)rootView.findViewById(R.id.feedback_title)).setText(title);
        ((TextView)rootView.findViewById(R.id.feedback_message)).setText(message);
        rootView.findViewById(R.id.feedback_retry).setVisibility(View.GONE);
    }

    public void hideLoader() {
        rootView.findViewById(R.id.feedback).setVisibility(View.GONE);
    }

    public void closeKeypad(){
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            View focusedView = getActivity().getCurrentFocus();
            if (focusedView != null) {
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            }
        }
    }

}
