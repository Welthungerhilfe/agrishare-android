package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class MyCalendar implements Parcelable {

    public String Date = "";
    public boolean Available = false;

    public MyCalendar(JSONObject json) {
        if (json != null) {
            Date = json.optString("Date");
            Available = json.optBoolean("Available");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Storing the data to Parcel object
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Date);
        dest.writeByte((byte) (Available ? 1 : 0));
    }

    private MyCalendar(Parcel in){
        this.Date = in.readString();
        this.Available = in.readByte() != 0;
    }

    public static final Creator<MyCalendar> CREATOR = new Creator<MyCalendar>() {
        @Override
        public MyCalendar createFromParcel(Parcel source) {
            return new MyCalendar(source);
        }

        @Override
        public MyCalendar[] newArray(int size) {
            return new MyCalendar[size];
        }
    };

}
