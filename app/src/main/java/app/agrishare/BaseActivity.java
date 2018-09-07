package app.agrishare;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import app.c2.android.AsyncResponse;
import app.c2.android.RemoteImageManager;
import app.c2.android.Utils;
import okhttp3.MultipartBody;
import app.c2.android.MyTaskParams;
import app.c2.android.OkHttp;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.exoplayer2.ui.PlayerView;

import net.hockeyapp.android.CrashManager;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCrashes();
        //BuildMenu();

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

    public AsyncTask postAPI(String Endpoint, HashMap<String, String> Query, AsyncResponse delegate, File file, String file_field_keyname, String file_type) {

     /*   String params = "";
        for (Map.Entry<String, String> entry : Query.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try { params += key + "=" + URLEncoder.encode(value, "UTF-8") + "&"; }
            catch (UnsupportedEncodingException ex) {
                Log(ex.getMessage());
            }
        }   */

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        for (Map.Entry<String, String> entry : Query.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }

        MyTaskParams taskparams = new MyTaskParams(Endpoint, builder, file, file_field_keyname, file_type);

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
                Response response = OkHttp.postData(params[0].endpoint, params[0].builder, params[0].file, params[0].file_field_keyname, params[0].file_type, delegate);
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

    /* HOCKEY APP */

    private void checkForCrashes() {
        CrashManager.register(this, MyApplication.HockeyAppId);
    }

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
}
