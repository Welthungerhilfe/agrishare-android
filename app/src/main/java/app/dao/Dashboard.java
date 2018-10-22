package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import app.agrishare.MyApplication;
import app.database.Categories;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ernestnyumbu on 22/10/2018.
 */

public class Dashboard implements Parcelable {

 //   public long Id = 0;
    public Notification Notification;
    public Booking Booking;
    public boolean isPageHeader = false;
    public boolean isNotificationHeader = false;
    public boolean isBookingHeader = false;

    public Dashboard(JSONObject json, boolean isNotification, boolean isSeeking) {
        if (json != null) {
            if (isNotification){
                Notification = new Notification(json, isSeeking);
            }
            else {
                Booking = new Booking(json, isSeeking);
            }
        }
    }

    public Dashboard(boolean isPageHeader, boolean isNotificationHeader, boolean isBookingHeader) {
        this.isPageHeader = isPageHeader;
        this.isNotificationHeader = isNotificationHeader;
        this.isBookingHeader = isBookingHeader;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Storing the data to Parcel object
    @Override
    public void writeToParcel(Parcel dest, int flags) {
       // dest.writeLong(Id);
        dest.writeParcelable(Notification, flags);
        dest.writeParcelable(Booking, flags);
        dest.writeByte((byte) (isPageHeader ? 1 : 0));
        dest.writeByte((byte) (isNotificationHeader ? 1 : 0));
        dest.writeByte((byte) (isBookingHeader ? 1 : 0));
    }

    private Dashboard(Parcel in){
        this.Notification = in.readParcelable(Service.class.getClassLoader());
        this.Booking = in.readParcelable(Service.class.getClassLoader());
        this.isPageHeader = in.readByte() != 0;
        this.isNotificationHeader = in.readByte() != 0;
        this.isBookingHeader = in.readByte() != 0;
    }

    public static final Creator<Dashboard> CREATOR = new Creator<Dashboard>() {
        @Override
        public Dashboard createFromParcel(Parcel source) {
            return new Dashboard(source);
        }

        @Override
        public Dashboard[] newArray(int size) {
            return new Dashboard[size];
        }
    };

}
