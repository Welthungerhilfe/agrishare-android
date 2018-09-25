package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class Booking implements Parcelable {

    public long Id = 0;
    public long ForId = 0;
    public String For = "";
    public long UserId = 0;
    public Listing Listing;
    public String Service = "";
    public String Location = "";
    public double Latitude = 0;
    public double Longitude = 0;
    public double Quantity = 0;
    public double Distance = 0;
    public boolean IncludeFuel = false;
    public String StartDate = "";
    public String EndDate = "";
    public double Price = 0;
    public double HireCost = 0;
    public double FuelCost = 0;
    public double TransportCost = 0;
    public long StatusId = 0;
    public String Status = "";
    public String DateCreated = "";

    public Booking(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            ForId = json.optLong("ForId");
            For = json.optString("For");
            UserId = json.optLong("UserId");
            JSONObject listingObject = json.optJSONObject("Listing");
            Listing = new Listing(listingObject);
            Service = json.optString("Service");
            Location = json.optString("Location");
            Latitude = json.optDouble("Latitude");
            Longitude = json.optDouble("Longitude");
            Quantity = json.optDouble("Quantity");
            Distance = json.optDouble("Distance");
            IncludeFuel = json.optBoolean("IncludeFuel");
            StartDate = json.optString("StartDate");
            EndDate = json.optString("EndDate");
            Price = json.optDouble("Price");
            HireCost = json.optDouble("HireCost");
            FuelCost = json.optDouble("FuelCost");
            TransportCost = json.optDouble("TransportCost");
            StatusId = json.optLong("StatusId");
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
        dest.writeLong(ForId);
        dest.writeString(For);
        dest.writeLong(UserId);
        dest.writeParcelable(Listing, flags);
        dest.writeString(Service);
        dest.writeString(Location);
        dest.writeDouble(Latitude);
        dest.writeDouble(Longitude);
        dest.writeDouble(Quantity);
        dest.writeDouble(Distance);
        dest.writeByte((byte) (IncludeFuel ? 1 : 0));
        dest.writeString(StartDate);
        dest.writeString(EndDate);
        dest.writeDouble(Price);
        dest.writeDouble(HireCost);
        dest.writeDouble(FuelCost);
        dest.writeDouble(TransportCost);
        dest.writeLong(StatusId);
        dest.writeString(Status);
        dest.writeString(DateCreated);
    }

    private Booking(Parcel in){
        this.Id = in.readLong();
        this.ForId = in.readLong();
        this.For = in.readString();
        this.UserId = in.readLong();
        this.Listing = in.readParcelable(Listing.class.getClassLoader());
        this.Service = in.readString();
        this.Location = in.readString();
        this.Latitude = in.readDouble();
        this.Longitude = in.readDouble();
        this.Quantity = in.readDouble();
        this.Distance = in.readDouble();
        this.IncludeFuel = in.readByte() != 0;
        this.StartDate = in.readString();
        this.EndDate = in.readString();
        this.Price = in.readDouble();
        this.HireCost = in.readDouble();
        this.FuelCost = in.readDouble();
        this.TransportCost = in.readDouble();
        this.StatusId = in.readLong();
        this.Status = in.readString();
        this.DateCreated = in.readString();
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel source) {
            return new Booking(source);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

}
