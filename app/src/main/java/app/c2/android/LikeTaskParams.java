package app.c2.android;

import java.io.File;

import okhttp3.MultipartBody;

public class LikeTaskParams {
    public String endpoint;
    public long postId;

    public LikeTaskParams(String endpoint, long postId) {
        this.endpoint = endpoint;
        this.postId = postId;
    }
}