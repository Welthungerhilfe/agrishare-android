package app.c2.android;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import app.agrishare.MyApplication;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ernestnyumbu on 27/6/17.
 */
public class OkHttp {
  //  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  public static final MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

    OkHttpClient client = new OkHttpClient();

    Response post(String url, String params) {
        RequestBody body = RequestBody.create(mediaType, params);
        Request request = new Request.Builder()
                .url(url)
              //  .addHeader("Authorization", MyApplication.token)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .post(body)
                .build();
        try  {
            Response response = client.newCall(request).execute();
            return response;
        } catch (Exception ex){
            return null;
        }
    }

    Response post(String url, MultipartBody.Builder builder, File file, String file_field_keyname, String file_type, final AsyncResponse delegate) {
        Log.d("POST API: ", url);
        Response response = null;
        try {
            MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");

            if (file != null) {
                builder.addFormDataPart(file_field_keyname, file.getName(), RequestBody.create(MediaType.parse(file_type), file));
            }


            RequestBody formBody = builder.build();
            // Decorate the request body to keep track of the upload progress
            CountingRequestBody countingBody = new CountingRequestBody(formBody,
                    new CountingRequestBody.Listener() {

                        @Override
                        public void onRequestProgress(long bytesWritten, long contentLength) {
                            float percentage = 100f * bytesWritten / contentLength;
                            // TODO: Do something useful with the values
                            if (MyApplication.DEBUG)
                                Log.d("PROGRESS", percentage + " - " + contentLength);
                            if (delegate != null)
                                delegate.taskProgress(Math.round(percentage));
                        }
                    });
            Request request = new Request.Builder()
                    .addHeader("Content-Type", "multipart/form-data")
                 //   .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                     .addHeader("Authorization", MyApplication.token)
                  //  .addHeader("cache-control", "no-cache")
                 //   .addHeader("postman-token", "60c31e7b-d1e4-6aac-395a-0d66ce8b2aec")
                    .url(url).post(countingBody).build();

            OkHttpClient.Builder _builder = new OkHttpClient.Builder();
            _builder.connectTimeout(960, TimeUnit.SECONDS);
            _builder.readTimeout(960, TimeUnit.SECONDS);
            _builder.writeTimeout(960, TimeUnit.SECONDS);
            OkHttpClient client = _builder.build();

            response = client.newCall(request).execute();
        } catch (IOException ex){
            Log.d("API IOException", ex.getMessage() + "");
        //    return "{\"error\":\"IOexception\"}";
        }

        return response;
    }

    Response postJSON(String url, String json, final AsyncResponse delegate) {
        if (MyApplication.DEBUG)
            Log.d("POST API: ", url);
        Response response = null;
        try {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody formBody = RequestBody.create(mediaType, json);
            // Decorate the request body to keep track of the upload progress
            CountingRequestBody countingBody = new CountingRequestBody(formBody,
                    new CountingRequestBody.Listener() {

                        @Override
                        public void onRequestProgress(long bytesWritten, long contentLength) {
                            float percentage = 100f * bytesWritten / contentLength;
                            // TODO: Do something useful with the values
                            if (MyApplication.DEBUG)
                                Log.d("PROGRESS", percentage + " - " + contentLength);
                            if (delegate != null)
                                delegate.taskProgress(Math.round(percentage));
                        }
                    });
            Request request = new Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", MyApplication.token)
                    .url(url).post(countingBody).build();

            OkHttpClient.Builder _builder = new OkHttpClient.Builder();
            _builder.connectTimeout(960, TimeUnit.SECONDS);
            _builder.readTimeout(960, TimeUnit.SECONDS);
            _builder.writeTimeout(960, TimeUnit.SECONDS);
            OkHttpClient client = _builder.build();

            response = client.newCall(request).execute();
        } catch (IOException ex){
            Log.d("API IOException", ex.getMessage() + "");
            //    return "{\"error\":\"IOexception\"}";
        }

        return response;
    }

    String postWithToken(String url, String params) {
        RequestBody body = RequestBody.create(mediaType, params);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", MyApplication.token)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .post(body)
                .build();
        try  {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception ex){
            return null;
        }
    }

    Response get(String url) throws IOException {

        Log.d("API: ", url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", MyApplication.token)
                .get()
                .build();
        try  {
            Response response = client.newCall(request).execute();
            return response;
        } catch (Exception ex){
            return null;
        }
    }

    String postWithBasicAuth(String url, String username, String password) throws IOException {
        String credential = Credentials.basic(username, password);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", credential)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


    public static String postDataWithToken(String endpoint, String params) throws IOException {
        OkHttp http = new OkHttp();
        String response = http.postWithToken(MyApplication.ApiUrl + endpoint, params);
        System.out.println(response);
        return response;
    }

    public static Response getData(String endpoint) throws IOException {
        OkHttp http = new OkHttp();
        Response response = http.get(MyApplication.ApiUrl + endpoint);
       // System.out.println(response);
        return response;
    }

    public static String POSTBasicAuthmain(String endpoint, String username, String password) throws IOException {
        OkHttp http = new OkHttp();
        String response = http.postWithBasicAuth(MyApplication.ApiUrl + endpoint, username, password);
        System.out.println(response);
        return response;
    }

    public static Response postData(String endpoint, String params) throws IOException {
        OkHttp http = new OkHttp();
        Response response = http.post(MyApplication.ApiUrl + endpoint, params);
      //  System.out.println(response);
        return response;
    }

    public static Response postData(String endpoint, MultipartBody.Builder builder, File file, String file_field_keyname, String file_type, AsyncResponse delegate) throws IOException {
        OkHttp http = new OkHttp();
        Response response = http.post(MyApplication.ApiUrl + endpoint, builder, file, file_field_keyname, file_type, delegate);
        //  System.out.println(response);
        return response;
    }

    public static Response postJSONData(String endpoint, String json, AsyncResponse delegate) throws IOException {
        OkHttp http = new OkHttp();
        Response response = http.postJSON(MyApplication.ApiUrl + endpoint, json, delegate);
        //  System.out.println(response);
        return response;
    }
}
