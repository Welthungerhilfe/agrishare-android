package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class Listing implements Parcelable {

    public long Id = 0;
    public long UserId = 0;
    public long CategoryId = 0;
    public String Title = "";
    public String Location = "";
    public double Latitude = 0;
    public double Longitude = 0;
    public int HorsePower = 0;
    public int Year = 0;
    public String Mobile = "";
    public int ConditionId = 0;
    public int GroupServices = 0;
    public String Photos = "";
    public double AverageRating = 0;
    public int RatingCount = 0;
    public int StatusId = 0;
    public String DateCreated = "";
    public String LateModified = "";
    public int Deleted = 0;

    public Listing(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            UserId = json.optLong("UserId");
            CategoryId = json.optLong("CategoryId");
            Title = json.optString("Title");
            Location = json.optString("Location");
            Latitude = json.optDouble("Latitude");
            Longitude = json.optDouble("Longitude");
            HorsePower = json.optInt("HorsePower");
            Year = json.optInt("Year");
            Mobile = json.optString("Mobile");
            ConditionId = json.optInt("ConditionId");
            GroupServices = json.optInt("GroupServices");
            Photos = json.optString("Photos");
            AverageRating = json.optDouble("AverageRating");
            RatingCount = json.optInt("RatingCount");
            StatusId = json.optInt("StatusId");
            DateCreated = json.optString("DateCreated");
            LateModified = json.optString("LateModified");
            Deleted = json.optInt("Deleted");
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
        dest.writeLong(UserId);
        dest.writeLong(CategoryId);
        dest.writeString(Title);
        dest.writeString(Location);
        dest.writeDouble(Latitude);
        dest.writeDouble(Longitude);
        dest.writeInt(HorsePower);
        dest.writeInt(Year);
        dest.writeString(Mobile);
        dest.writeInt(ConditionId);
        dest.writeInt(GroupServices);
        dest.writeString(Photos);
        dest.writeDouble(AverageRating);
        dest.writeInt(RatingCount);
        dest.writeInt(StatusId);
        dest.writeString(DateCreated);
        dest.writeString(LateModified);
        dest.writeInt(Deleted);

    }

    private Listing(Parcel in){
        this.Id = in.readLong();
        this.UserId = in.readLong();
        this.CategoryId = in.readLong();
        this.Title = in.readString();
        this.Location = in.readString();
        this.Latitude = in.readDouble();
        this.Longitude = in.readDouble();
        this.HorsePower = in.readInt();
        this.Year = in.readInt();
        this.Mobile = in.readString();
        this.ConditionId = in.readInt();
        this.GroupServices = in.readInt();
        this.Photos = in.readString();
        this.AverageRating = in.readDouble();
        this.RatingCount = in.readInt();
        this.StatusId = in.readInt();
        this.DateCreated = in.readString();
        this.LateModified = in.readString();
        this.Deleted = in.readInt();
    }

    public static final Creator<Listing> CREATOR = new Creator<Listing>() {
        @Override
        public Listing createFromParcel(Parcel source) {
            return new Listing(source);
        }

        @Override
        public Listing[] newArray(int size) {
            return new Listing[size];
        }
    };

}
