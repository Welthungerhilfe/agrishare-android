package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class BookingUser implements Parcelable {

    public long Id = 0;
    public long BookingId = 0;
    public String User = "";
    public String Name = "";
    public String Telephone = "";
    public double Ratio = 0;
    public long StatusId = 0;
    public String Status = "";
    public String DateCreated = "";
    public String LastModified = "";

    public BookingUser(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            BookingId = json.optLong("BookingId");
            User = json.optString("User");
            Name = json.optString("Name");
            Telephone = json.optString("Telephone");
            Ratio = json.optDouble("Ratio");
            StatusId = json.optLong("StatusId");
            Status = json.optString("Status");
            DateCreated = json.optString("DateCreated");
            LastModified = json.optString("LastModified");
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
        dest.writeLong(BookingId);
        dest.writeString(User);
        dest.writeString(Name);
        dest.writeString(Telephone);
        dest.writeDouble(Ratio);
        dest.writeLong(StatusId);
        dest.writeString(Status);
        dest.writeString(DateCreated);
        dest.writeString(LastModified);

    }

    private BookingUser(Parcel in){
        this.Id = in.readLong();
        this.BookingId = in.readLong();
        this.User = in.readString();
        this.Name = in.readString();
        this.Telephone = in.readString();
        this.Ratio = in.readDouble();
        this.StatusId = in.readLong();
        this.Status = in.readString();
        this.DateCreated = in.readString();
        this.LastModified = in.readString();
    }

    public static final Creator<BookingUser> CREATOR = new Creator<BookingUser>() {
        @Override
        public BookingUser createFromParcel(Parcel source) {
            return new BookingUser(source);
        }

        @Override
        public BookingUser[] newArray(int size) {
            return new BookingUser[size];
        }
    };

}
