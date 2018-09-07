package app.c2.android;

import java.io.File;

import okhttp3.MultipartBody;

public class NotificationsParams {
    public String endpoint;
    public MultipartBody.Builder builder;
    public File file;
    public String file_field_keyname;
    public String file_type;
    public long notificationId;

    public NotificationsParams(String endpoint, MultipartBody.Builder builder, File file, String file_field_keyname, String file_type, long notificationId) {
        this.endpoint = endpoint;
        this.builder = builder;
        this.file = file;
        this.file_field_keyname = file_field_keyname;
        this.file_type = file_type;
        this.notificationId = notificationId;
    }
}