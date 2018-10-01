package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class SearchResultListing implements Parcelable {

    public long ServiceId = 0;
    public long ListingId = 0;
    public String Title = "";
    public int Year = 0;
    public String Condition = "";
    public double AverageRating = 0;
    public String Photos = "";
    public double Distance = 0;
    public boolean Available = false;
    public double Price = 0;

    public SearchResultListing(JSONObject json) {
        if (json != null) {
            ServiceId = json.optLong("ServiceId");
            ListingId = json.optLong("ListingId");
            Title = json.optString("Title");
            Year = json.optInt("Year");
            Condition = json.optString("Condition");
            AverageRating = json.optDouble("AverageRating");
            Photos = json.optString("Photos");
            Distance = json.optDouble("Distance");
            Available = json.optBoolean("Available");
            Price = json.optDouble("Price");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Storing the data to Parcel object
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ServiceId);
        dest.writeLong(ListingId);
        dest.writeString(Title);
        dest.writeInt(Year);
        dest.writeString(Condition);
        dest.writeDouble(AverageRating);
        dest.writeString(Photos);
        dest.writeDouble(Distance);
        dest.writeByte((byte) (Available ? 1 : 0));
        dest.writeDouble(Price);
    }

    private SearchResultListing(Parcel in){
        this.ServiceId = in.readLong();
        this.ListingId = in.readLong();
        this.Title = in.readString();
        this.Year = in.readInt();
        this.Condition = in.readString();
        this.AverageRating = in.readDouble();
        this.Photos = in.readString();
        this.Distance = in.readDouble();
        this.Available = in.readByte() != 0;
        this.Price = in.readDouble();
    }

    public static final Creator<SearchResultListing> CREATOR = new Creator<SearchResultListing>() {
        @Override
        public SearchResultListing createFromParcel(Parcel source) {
            return new SearchResultListing(source);
        }

        @Override
        public SearchResultListing[] newArray(int size) {
            return new SearchResultListing[size];
        }
    };

}
