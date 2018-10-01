package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class Notification implements Parcelable {

    public long Id = 0;
    public String User = "";
    public Booking Booking;
    public String Title = "";
    public int TypeId = 0;
    public String Type = "";
    public int StatusId = 0;
    public String Status = "";
    public int GroupId = 0;
    public String Group = "";
    public String DateCreated = "";

    public boolean Seeking = true;

    public Notification(JSONObject json, boolean seeking) {
        if (json != null) {
            Id = json.optLong("Id");
            User = json.optString("User");
            JSONObject bookingObject = json.optJSONObject("Booking");
            Booking = new Booking(bookingObject, seeking);
            Title = json.optString("Title");
            TypeId = json.optInt("TypeId");
            Type = json.optString("Type");
            StatusId = json.optInt("StatusId");
            Status = json.optString("Status");
            GroupId = json.optInt("GroupId");
            Group = json.optString("Group");
            DateCreated = json.optString("DateCreated");
            this.Seeking = seeking;
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
        dest.writeString(User);
        dest.writeParcelable(Booking, flags);
        dest.writeString(Title);
        dest.writeInt(TypeId);
        dest.writeString(Type);
        dest.writeInt(StatusId);
        dest.writeString(Status);
        dest.writeInt(GroupId);
        dest.writeString(Group);
        dest.writeString(DateCreated);
        dest.writeByte((byte) (Seeking ? 1 : 0));
    }

    private Notification(Parcel in){
        this.Id = in.readLong();
        this.User = in.readString();
        this.Booking = in.readParcelable(Booking.class.getClassLoader());
        this.Title = in.readString();
        this.TypeId = in.readInt();
        this.Type = in.readString();
        this.TypeId = in.readInt();
        this.Type = in.readString();
        this.StatusId = in.readInt();
        this.Status = in.readString();
        this.GroupId = in.readInt();
        this.Group = in.readString();
        this.DateCreated = in.readString();
        this.Seeking = in.readByte() != 0;
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel source) {
            return new Notification(source);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

}
