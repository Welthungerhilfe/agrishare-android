package app.c2.android;

import java.io.File;

import okhttp3.MultipartBody;

public class MyTaskParams {
    public String endpoint;
    public String json;

    public MyTaskParams(String endpoint, String json) {
        this.endpoint = endpoint;
        this.json = json;
    }
}