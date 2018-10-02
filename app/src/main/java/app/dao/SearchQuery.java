package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class SearchQuery implements Parcelable {

    public long ForId = 0;
    public long CategoryId = 0;
    public Service Service;
    public double Latitude = 0;
    public double Longitude = 0;
    public String StartDate = "";
    public double Size = 0;
    public boolean IncludeFuel = false;
    public String Location = "";
    public boolean Mobile = false;

    public String NewlySelectedStartDate = "";

    public SearchQuery(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Storing the data to Parcel object
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ForId);
        dest.writeLong(CategoryId);
        dest.writeParcelable(Service, flags);
        dest.writeDouble(Latitude);
        dest.writeDouble(Longitude);
        dest.writeString(StartDate);
        dest.writeDouble(Size);
        dest.writeByte((byte) (IncludeFuel ? 1 : 0));
        dest.writeString(Location);
        dest.writeByte((byte) (Mobile ? 1 : 0));

    }

    private SearchQuery(Parcel in){
        this.ForId = in.readLong();
        this.CategoryId = in.readLong();
        this.Service = in.readParcelable(Service.class.getClassLoader());
        this.Latitude = in.readDouble();
        this.Longitude = in.readDouble();
        this.StartDate = in.readString();
        this.Size = in.readDouble();
        this.IncludeFuel = in.readByte() != 0;
        this.Location = in.readString();
        this.Mobile = in.readByte() != 0;
    }

    public static final Creator<SearchQuery> CREATOR = new Creator<SearchQuery>() {
        @Override
        public SearchQuery createFromParcel(Parcel source) {
            return new SearchQuery(source);
        }

        @Override
        public SearchQuery[] newArray(int size) {
            return new SearchQuery[size];
        }
    };

}
