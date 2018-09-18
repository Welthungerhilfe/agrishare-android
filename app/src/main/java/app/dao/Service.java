package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class Service implements Parcelable {

    public long Id = 0;
    public String Title = "";

    public Service(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            Title = json.optString("Title");
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

    }

    private Service(Parcel in){
        this.Id = in.readLong();
        this.Title = in.readString();
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel source) {
            return new Service(source);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

}
