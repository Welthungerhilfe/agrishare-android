package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class Location implements Parcelable {

    public long Id = 0;
    public String Title = "";
    public double Latitude = 0;
    public double Longitude = 0;

    public Location(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            Title = json.optString("Title");
            Latitude = json.optDouble("Latitude");
            Longitude = json.optDouble("Longitude");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Storing the data to Parcel object
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(Id);
        dest.writeString(Title);
        dest.writeDouble(Latitude);
        dest.writeDouble(Longitude);

    }

    private Location(Parcel in){
        this.Id = in.readLong();
        this.Title = in.readString();
        this.Latitude = in.readLong();
        this.Longitude = in.readLong();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

}
