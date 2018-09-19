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
    public String Category = "";
    public String Title = "";
    public String Description = "";
    public String Location = "";
    public double Latitude = 0;
    public double Longitude = 0;
    public String Brand = "";
    public int HorsePower = 0;
    public int Year = 0;
    public int ConditionId = 0;
    public String Condition = "";
    public boolean GroupServices = false;
    public String Photos = "";
    public double AverageRating = 0;
    public int RatingCount = 0;
    public String Services = "";
    public int StatusId = 0;
    public String Status = "";
    public String DateCreated = "";

    public Listing(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            UserId = json.optLong("UserId");
            Category = json.optString("Category");
            Title = json.optString("Title");
            Description = json.optString("Description");
            Location = json.optString("Location");
            Latitude = json.optDouble("Latitude");
            Longitude = json.optDouble("Longitude");
            Brand = json.optString("Brand");
            HorsePower = json.optInt("HorsePower");
            Year = json.optInt("Year");
            ConditionId = json.optInt("ConditionId");
            Condition = json.optString("Condition");
            GroupServices = json.optBoolean("GroupServices");
            Photos = json.optString("Photos");
            AverageRating = json.optDouble("AverageRating");
            RatingCount = json.optInt("RatingCount");
            Services = json.optString("Services");
            StatusId = json.optInt("StatusId");
            Status = json.optString("Status");
            DateCreated = json.optString("DateCreated");
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
        dest.writeString(Category);
        dest.writeString(Title);
        dest.writeString(Description);
        dest.writeString(Location);
        dest.writeDouble(Latitude);
        dest.writeDouble(Longitude);
        dest.writeString(Brand);
        dest.writeInt(HorsePower);
        dest.writeInt(Year);
        dest.writeInt(ConditionId);
        dest.writeString(Condition);
        dest.writeByte((byte) (GroupServices ? 1 : 0));
        dest.writeString(Photos);
        dest.writeDouble(AverageRating);
        dest.writeInt(RatingCount);
        dest.writeString(Services);
        dest.writeInt(StatusId);
        dest.writeString(Status);
        dest.writeString(DateCreated);

    }

    private Listing(Parcel in){
        this.Id = in.readLong();
        this.UserId = in.readLong();
        this.Category = in.readString();
        this.Title = in.readString();
        this.Description = in.readString();
        this.Location = in.readString();
        this.Latitude = in.readDouble();
        this.Longitude = in.readDouble();
        this.Brand = in.readString();
        this.HorsePower = in.readInt();
        this.Year = in.readInt();
        this.ConditionId = in.readInt();
        this.Condition = in.readString();
        this.GroupServices = in.readByte() != 0;
        this.Photos = in.readString();
        this.AverageRating = in.readDouble();
        this.RatingCount = in.readInt();
        this.Services = in.readString();
        this.StatusId = in.readInt();
        this.Status = in.readString();
        this.DateCreated = in.readString();
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
