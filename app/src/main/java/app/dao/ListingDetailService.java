package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class ListingDetailService implements Parcelable {

    public long Id = 0;
    public long ListingId = 0;
    public Service Subcategory;
    public boolean Mobile = false;
    public long TotalVolume = 0;
    public long QuantityUnitId = 0;
    public String QuantityUnit = "";
    public long TimeUnitId = 0;
    public String TimeUnit = "";
    public long DistanceUnitId = 0;
    public String DistanceUnit = "";
    public double MinimumQuantity = 0;
    public double MaximumDistance = 0;
    public double PricePerQuantityUnit = 0;
    public double FuelPerQuantityUnit = 0;
    public double TimePerQuantityUnit = 0;
    public double PricePerDistanceUnit = 0;
    public String DateCreated = "";


    public ListingDetailService(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            ListingId = json.optLong("Id");
            JSONObject subcategoryJSONObject = json.optJSONObject("Subcategory");
            Subcategory = new Service(subcategoryJSONObject);
            Mobile = json.optBoolean("Mobile");
            TotalVolume = json.optLong("TotalVolume");
            QuantityUnitId = json.optLong("QuantityUnitId");
            QuantityUnit = json.optString("QuantityUnit");
            TimeUnitId = json.optLong("TimeUnitId");
            TimeUnit = json.optString("TimeUnit");
            DistanceUnitId = json.optLong("DistanceUnitId");
            DistanceUnit = json.optString("DistanceUnit");
            MinimumQuantity = json.optDouble("MinimumQuantity");
            MaximumDistance = json.optDouble("MaximumDistance");
            PricePerQuantityUnit = json.optDouble("PricePerQuantityUnit");
            FuelPerQuantityUnit = json.optDouble("FuelPerQuantityUnit");
            TimePerQuantityUnit = json.optDouble("TimePerQuantityUnit");
            PricePerDistanceUnit = json.optDouble("PricePerDistanceUnit");
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
        dest.writeLong(ListingId);
        dest.writeParcelable(Subcategory, flags);
        dest.writeByte((byte) (Mobile ? 1 : 0));
        dest.writeLong(TotalVolume);
        dest.writeLong(QuantityUnitId);
        dest.writeString(QuantityUnit);
        dest.writeLong(TimeUnitId);
        dest.writeString(TimeUnit);
        dest.writeLong(DistanceUnitId);
        dest.writeString(DistanceUnit);
        dest.writeDouble(MinimumQuantity);
        dest.writeDouble(MaximumDistance);
        dest.writeDouble(PricePerQuantityUnit);
        dest.writeDouble(FuelPerQuantityUnit);
        dest.writeDouble(TimePerQuantityUnit);
        dest.writeDouble(PricePerDistanceUnit);
        dest.writeString(DateCreated);

    }

    private ListingDetailService(Parcel in){
        this.Id = in.readLong();
        this.ListingId = in.readLong();
        this.Subcategory = in.readParcelable(Service.class.getClassLoader());
        this.Mobile = in.readByte() != 0;
        this.TotalVolume = in.readLong();
        this.QuantityUnitId = in.readLong();
        this.QuantityUnit = in.readString();
        this.TimeUnitId = in.readLong();
        this.TimeUnit = in.readString();
        this.DistanceUnitId = in.readLong();
        this.DistanceUnit = in.readString();
        this.MinimumQuantity = in.readDouble();
        this.MaximumDistance = in.readDouble();
        this.PricePerQuantityUnit = in.readDouble();
        this.FuelPerQuantityUnit = in.readDouble();
        this.TimePerQuantityUnit = in.readDouble();
        this.PricePerDistanceUnit = in.readDouble();
        this.DateCreated = in.readString();
    }

    public static final Creator<ListingDetailService> CREATOR = new Creator<ListingDetailService>() {
        @Override
        public ListingDetailService createFromParcel(Parcel source) {
            return new ListingDetailService(source);
        }

        @Override
        public ListingDetailService[] newArray(int size) {
            return new ListingDetailService[size];
        }
    };

}
