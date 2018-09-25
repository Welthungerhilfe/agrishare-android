package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class Photo implements Parcelable {

    public String Filename = "";
    public String Thumb = "";
    public String Zoom = "";

    public Photo(JSONObject json) {
        if (json != null) {
            Filename = json.optString("Filename");
            Thumb = json.optString("Thumb");
            Zoom = json.optString("Zoom");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Storing the data to Parcel object
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Filename);
        dest.writeString(Thumb);
        dest.writeString(Zoom);
    }

    private Photo(Parcel in){
        this.Filename = in.readString();
        this.Thumb = in.readString();
        this.Zoom = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

}
