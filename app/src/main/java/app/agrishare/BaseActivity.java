package app.agrishare;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import app.account.CellNumberActivity;
import app.account.LoginActivity;
import app.account.RegisterActivity;
import app.c2.android.AnalyticsAsyncResponse;
import app.c2.android.AnalyticsTaskParams;
import app.c2.android.AsyncResponse;
import app.c2.android.RemoteImageManager;
import app.c2.android.Utils;
import app.dao.MiniUser;
import app.database.AnalyticsCounters;
import app.database.Categories;
import app.equipment.AddEquipmentActivity;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MultipartBody;
import app.c2.android.MyTaskParams;
import app.c2.android.OkHttp;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.exoplayer2.ui.PlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Response;

import static app.agrishare.Constants.KEY_TELEPHONE;
import static app.agrishare.Constants.KEY_USER;
import static app.agrishare.Constants.PREFS;

public class BaseActivity extends AppCompatActivity {

    public PlayerView playerView;

    public SharedPreferences sharedPreferences;
    public String screen_name = "";
    public Context context;
    public ProgressDialog progress_dialogue;
    public ArrayList<AsyncTask> tasks = new ArrayList<AsyncTask>();

    //Views
    public Button titlebar_left_button;
    public TextView no_listings_found, search;
    public ListView listview;
    public RecyclerView recyclerView;
    public SwipeRefreshLayout swipeContainer;

    //SHARE
    private static final int MY_PERMISSIONS_REQUEST = 1;
    String media_type = "";
    ImageView photoView;
    String caption = "";
    String username = "";
    String media_thumb = "";
    long postId = 0;
    Context share_context;
    Boolean shareOtherUsersPost = false;

    protected void onCreate(Bundle savedInstanceState, String screenName, int layoutResourceId) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PREFS, 0);
        setContentView(layoutResourceId);
        if (savedInstanceState != null) {
          //  menu_visible = savedInstanceState.getBoolean(STATE_MENU_POSITION);
        }
        context = this;
        screen_name = screenName;
       // if (!screenName.isEmpty())
         //   sendHit();


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       // outState.putBoolean(STATE_MENU_POSITION, menu_visible);
    }

    @Override
    protected void onDestroy() {

        for(int i = 0; i < tasks.size(); i++) {
            AsyncTask task = tasks.get(i);
            if (task!= null && task.getStatus() != AsyncTask.Status.FINISHED) {
                task.cancel(true);
            }
        }
        RemoteImageManager.getInstance().clear();
        super.onDestroy();
    }/*
    @Override
    public void onBackPressed() {       //all
        Utils.hideKeyboard((Activity) context);
        if (menu_visible)
            toggleMenuState(true);
        else
            super.onBackPressed();
    }
    */


    /* API */

    public AsyncTask postAPI(String Endpoint, HashMap<String, Object> Query, AsyncResponse delegate) {

        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry : Query.entrySet()) {
                jsonObject.accumulate(entry.getKey(), entry.getValue());
                Log(entry.getKey() + " : " + entry.getValue());
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
                    if (MyApplication.DEBUG)
                        Log.d("RESPONSE CODE", "" + response.code());
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

    public AsyncTask getAnalyticsAPI(String Endpoint, HashMap<String, String> Query, AnalyticsAsyncResponse delegate, String event, long serviceId, String date, int hits, boolean recordExistsInLocalDB) {

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

        AnalyticsTaskParams taskparams = new AnalyticsTaskParams(Endpoint, event, serviceId, date, hits, recordExistsInLocalDB);

        GetAnalyticsAPIRequest task = new GetAnalyticsAPIRequest(delegate);
      //  task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Endpoint);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, taskparams);
        return task;

    }

    private class GetAnalyticsAPIRequest extends AsyncTask<AnalyticsTaskParams, Object, Response>
    {
        String event = "";
        long serviceId = 0;
        String date = "";
        int hits = 0;
        boolean recordExistsInLocalDB = false;

        public AnalyticsAsyncResponse delegate = null;

        public GetAnalyticsAPIRequest(AnalyticsAsyncResponse asyncResponse) {
            delegate = asyncResponse;
        }

        @Override
        protected Response doInBackground(AnalyticsTaskParams... params)
        {
            // return JSONUtils.GetJSON(urls[0]);
            try {
                event = params[0].event;
                serviceId = params[0].serviceId;
                date = params[0].date;
                hits = params[0].hits;
                recordExistsInLocalDB = params[0].recordExistsInLocalDB;

                Response response = OkHttp.getData(params[0].endpoint);
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
                    if (MyApplication.DEBUG)
                        Log.d("RESPONSE CODE", "" + response.code());
                    try {

                        String response_string = response.body().string();
                        JSONObject jsonObject = new JSONObject(response_string);

                        if (jsonObject == null)
                            delegate.taskError("Invalid response", event, serviceId, date, hits, recordExistsInLocalDB);
                        else if (response.code() == 200)
                            delegate.taskSuccess(jsonObject, event, serviceId, date, hits, recordExistsInLocalDB);
                        else {
                            if (jsonObject.optString("Message").equals("Authentication required")){
                                logout();
                            }
                            delegate.taskError(jsonObject.optString("Message"), event, serviceId, date, hits, recordExistsInLocalDB);
                        }

                    } catch (JSONException ex){
                        Log.d("JSONException", ex.getMessage());
                        delegate.taskError(ex.getMessage(), event, serviceId, date, hits, recordExistsInLocalDB);
                    } catch (IOException ex){
                        Log.d("IOException", ex.getMessage());
                        delegate.taskError(ex.getMessage(), event, serviceId, date, hits, recordExistsInLocalDB);
                    }

                }
                else {
                    delegate.taskError("Something went wrong", event, serviceId, date, hits, recordExistsInLocalDB);
                }

            }
        }
    }

    public class GetMapLocationDetailsRequest extends AsyncTask<String, Object, Response>
    {
        public AsyncResponse delegate = null;

        public GetMapLocationDetailsRequest(AsyncResponse asyncResponse) {
            delegate = asyncResponse;
        }

        @Override
        protected Response doInBackground(String... params)
        {
            try {
                Log("PARAMS STRING LOCATION: " + params[0]);
                Response response = OkHttp.getLocationData(params[0]);
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
        editor.commit();    */



    }


    /* GOOGLE ANALYTICS */

 /*   public void sendHit() {
        MyApplication.tracker.setScreenName(screen_name);
        MyApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void sendHit(String screen_name) {
        MyApplication.tracker.setScreenName(screen_name);
        MyApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void sendEvent(String category, String action, String label) {
        MyApplication.tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }       */

    /* LOGGING */

    public void Log(String Message) {
        if (MyApplication.DEBUG)
            Log.d(MyApplication.DEBUG_TAG, Message);
    }

    public void popToast(Context context, String Message) {
        Log(Message);
        Toast slice = Toast.makeText(context, Message, Toast.LENGTH_SHORT);
        slice.show();
    }

    /* PROGRESS DIALOGUE */

  /*  public static ProgressDialog MyProgressDialog(Context context, String Message) {
        ProgressDialog dlg = new ProgressDialog(new ContextThemeWrapper(context, R.style.AppDialog));
        dlg.setMessage(Message);
        dlg.setCancelable(false);
        dlg.show();
        return dlg;
    } */
public String saveImage(String url) throws IOException  //download image and store in cache
{
    String filename = Utils.cleanFileName2(url);
    URL imageURL = new URL(url);
    URLConnection connection = imageURL.openConnection();
   InputStream inputStream = new BufferedInputStream(imageURL.openStream(), 10240);
    File cacheDir = getCacheFolder(BaseActivity.this);
    File cacheFile = new File(cacheDir, filename);
    FileOutputStream outputStream = new FileOutputStream(cacheFile);
    	byte buffer[] = new byte[1024];
    	int dataSize;
    	int loadedSize = 0;
   	    while ((dataSize = inputStream.read(buffer)) != -1) {
    	        loadedSize += dataSize;
    	      //  publishProgress(loadedSize);
    	        outputStream.write(buffer, 0, dataSize);
    	    }

    	    outputStream.close();
    return cacheDir.toString()+"/"+filename;
}

    public File getCacheFolder(Context context) {
        	    File cacheDir = null;
        	        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            	            cacheDir = new File(Environment.getExternalStorageDirectory(), "dreambigcache");
            	            if(!cacheDir.isDirectory()) {
                	                cacheDir.mkdirs();
                	            }
            	        }

        	        if(!cacheDir.isDirectory()) {
            	            cacheDir = context.getCacheDir(); //get system cache folder
            	        }

        	    return cacheDir;
        	}




    public String DownloadImage(String url) throws IOException
    {
        String filename = Utils.cleanFileName2(url);
        URL imageURL = new URL(url);
        URLConnection connection = imageURL.openConnection();
        InputStream inputStream = new BufferedInputStream(imageURL.openStream(), 10240);
        File cacheDir = getCacheFolder(BaseActivity.this);
        File cacheFile = new File(cacheDir, filename);
        FileOutputStream outputStream = new FileOutputStream(cacheFile);
        byte buffer[] = new byte[1024];
        int dataSize;
        int loadedSize = 0;
        while ((dataSize = inputStream.read(buffer)) != -1) {
            loadedSize += dataSize;
            outputStream.write(buffer, 0, dataSize);
        }
        outputStream.close();
        return cacheDir.toString()+"/"+filename;
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

    public void noListingsFound(String message){
        no_listings_found.setVisibility(View.VISIBLE);
        no_listings_found.setText(message);
    }

    public void hideNoListingsFound(){
        no_listings_found.setVisibility(View.GONE);
    }

    public void closeTransition(){
        //overridePendingTransition(R.anim.bottom_to_top, R.anim.top_to_bottom);
        //overridePendingTransition(R.anim.hold, R.anim.top_to_bottom);
    }

    public void backTransition(){
        overridePendingTransition(R.anim.abc_slide_out_bottom, R.anim.hold);
    }


    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void showFeedback(int iconResourceId, String title, String message) {
        findViewById(R.id.feedback).setVisibility(View.VISIBLE);
        findViewById(R.id.feedback_activity).setVisibility(View.GONE);
        findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        findViewById(R.id.feedback_icon).setVisibility(View.VISIBLE);
        ((ImageView)findViewById(R.id.feedback_icon)).setImageResource(iconResourceId);
        ((TextView)findViewById(R.id.feedback_title)).setText(title);
        ((TextView)findViewById(R.id.feedback_message)).setText(message);
        findViewById(R.id.feedback_retry).setVisibility(View.GONE);
    }

    public void showFeedbackWithButton(int iconResourceId, String title, String message) {
        findViewById(R.id.feedback).setVisibility(View.VISIBLE);
        findViewById(R.id.feedback_activity).setVisibility(View.GONE);
        findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        findViewById(R.id.feedback_icon).setVisibility(View.VISIBLE);
        ((ImageView)findViewById(R.id.feedback_icon)).setImageResource(iconResourceId);
        ((TextView)findViewById(R.id.feedback_title)).setText(title);
        ((TextView)findViewById(R.id.feedback_message)).setText(message);
        findViewById(R.id.feedback_retry).setVisibility(View.VISIBLE);
    }

    public void hideFeedback() {
        findViewById(R.id.feedback).setVisibility(View.GONE);
    }

    public void showLoader() {
        showLoader("", "");
    }

    public void showLoader(String title, String message) {
        findViewById(R.id.feedback).setVisibility(View.VISIBLE);
        findViewById(R.id.feedback_activity).setVisibility(View.VISIBLE);
        findViewById(R.id.feedback_progress).setVisibility(View.GONE);
        findViewById(R.id.feedback_icon).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.feedback_title)).setText(title);
        ((TextView)findViewById(R.id.feedback_message)).setText(message);
        findViewById(R.id.feedback_retry).setVisibility(View.GONE);
    }

    public void showLoader(String title, String message, int progress) {
        findViewById(R.id.feedback).setVisibility(View.VISIBLE);
        findViewById(R.id.feedback_activity).setVisibility(View.GONE);
        findViewById(R.id.feedback_progress).setVisibility(View.VISIBLE);
        ((DonutProgress) findViewById(R.id.feedback_progress)).setProgress(progress);
        findViewById(R.id.feedback_icon).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.feedback_title)).setText(title);
        ((TextView)findViewById(R.id.feedback_message)).setText(message);
        findViewById(R.id.feedback_retry).setVisibility(View.GONE);
    }

    public void hideLoader() {
        findViewById(R.id.feedback).setVisibility(View.GONE);
    }

    public void refreshComplete() {
        swipeContainer.setRefreshing(false);
    }

    public void goBack(){
        finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_out_to_right);
    }

    public void close(){
        finish();
        overridePendingTransition(R.anim.hold, R.anim.abc_slide_out_bottom);
    }

    public void closeKeypad(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

   public void setNavBar(String title, int iconResourceId) {
       getSupportActionBar().setDisplayShowTitleEnabled(true);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getSupportActionBar().setTitle(title);

       if (iconResourceId != 0)
           getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(iconResourceId));
   }

    public void disableSubmitButton(Button submit_button){
        submit_button.setEnabled(false);
        submit_button.setBackgroundColor(getResources().getColor(R.color.grey_for_text));
    }

    public void enableSubmitButton(Button submit_button){
        submit_button.setEnabled(true);
        submit_button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    public void sendEventToServer(String event, long serviceId, String date, int hits, boolean recordExistsInLocalDB){
        String formattedDateToday = "";
        if (date != null && !date.isEmpty()){
            formattedDateToday = date;
        }
        else {
            Date c = Calendar.getInstance().getTime();
            Log("RECORD EVENT AT Current time => " + c);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            formattedDateToday = df.format(c);
        }

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("Event", event);
        query.put("ServiceId", String.valueOf(serviceId));
        query.put("Date", formattedDateToday);
        query.put("Hits", String.valueOf(hits));
        getAnalyticsAPI("counter/add", query, fetchAnalyticsResponse, event, serviceId, formattedDateToday, hits, recordExistsInLocalDB);
    }

    AnalyticsAsyncResponse fetchAnalyticsResponse = new AnalyticsAsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result, String event, long serviceId, String date, int hits, boolean recordExistsInLocalDB) {
            Log("COUNTER UPDATE SUCCESS: "+ result.toString());
            if (recordExistsInLocalDB) {
                deleteAnalyticsEvent(event, serviceId, date);
            }

            final Handler handler = new Handler();
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            //It seems the refresh was being called before the above DB changes
                            // were committed.
                            uploadFirstItemInAnalyticsTable();
                        }
                    });
                }
            }, 1000);
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage, String event, long serviceId, String date, int hits, boolean recordExistsInLocalDB) {
            Log("ERROR COUNTER UPDATE : " + errorMessage);
            if (!recordExistsInLocalDB) {
                recordAnalyticsEvent(event, serviceId, date);
            }
        }

        @Override
        public void taskCancelled(Response response) {
        }
    };

    public void uploadFirstItemInAnalyticsTable(){
        RealmResults<AnalyticsCounters> results = MyApplication.realm.where(AnalyticsCounters.class)
                .findAll();

        if (results.size() > 0) {
            sendEventToServer(results.get(0).getEvent(), results.get(0).getServiceid(),  results.get(0).getDate(), results.get(0).getHits(), true);
        }
    }

    public void recordAnalyticsEvent(final String event, final long serviceId, final String dateToday){

        RealmResults<AnalyticsCounters> results = MyApplication.realm.where(AnalyticsCounters.class)
                .equalTo("Event", event)
                .equalTo("Serviceid", serviceId)
                .equalTo("Date", dateToday)
                .findAll();

        if (results.size() == 0) {

            // All writes must be wrapped in a transaction to facilitate safe multi threading
            MyApplication.realm.beginTransaction();

            AnalyticsCounters analyticsCounter = MyApplication.realm.createObject(AnalyticsCounters.class);

            analyticsCounter.setEvent(event);
            analyticsCounter.setServiceid(serviceId);
            analyticsCounter.setDate(dateToday);
            analyticsCounter.setHits(1);

            // When the transaction is committed, all changes a synced to disk.
            MyApplication.realm.commitTransaction();

        } else {

            MyApplication.realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    AnalyticsCounters analyticsCounter = bgRealm.where(AnalyticsCounters.class)
                            .equalTo("Event", event)
                            .equalTo("Serviceid", serviceId)
                            .equalTo("Date", dateToday)
                            .findFirst();

                    analyticsCounter.setHits(analyticsCounter.getHits() + 1);

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    // Original queries and Realm objects are automatically updated.

                }
            });

        }
    }

    public void deleteAnalyticsEvent(final String event, final long serviceId, final String dateToday){
        RealmResults<AnalyticsCounters> results = MyApplication.realm.where(AnalyticsCounters.class)
                .equalTo("Event", event)
                .equalTo("Serviceid", serviceId)
                .equalTo("Date", dateToday)
                .findAll();

        if (results.size() > 0) {

            MyApplication.realm.beginTransaction();
            results.deleteAllFromRealm();
            MyApplication.realm.commitTransaction();

        }
    }

    public void showToolTip(String toolName, String toolTipText, Context context){
        final Dialog d = new Dialog(context);
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        d.setCancelable(true);
        d.setContentView(R.layout.dialog_tool_tip);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView textView = (TextView) d.findViewById(R.id.tooltip_tv);
        TextView textView2 = (TextView) d.findViewById(R.id.tooltip_tv2);
        d.findViewById(R.id.close_tooltip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        textView.setText(toolName);
        textView2.setText(toolTipText);
        d.show();
    }
}
