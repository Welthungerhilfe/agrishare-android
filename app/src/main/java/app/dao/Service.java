package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class Service implements Parcelable {

    public boolean enabled = false;
    public String hours_required_per_hectare = "";
    public String hire_cost = "";
    public String fuel_cost = "";
    public String minimum_field_size = "";
    public String distance_charge = "";
    public String maximum_distance = "";

    public Service() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Storing the data to Parcel object
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeString(hours_required_per_hectare);
        dest.writeString(hire_cost);
        dest.writeString(fuel_cost);
        dest.writeString(minimum_field_size);
        dest.writeString(distance_charge);
        dest.writeString(maximum_distance);

    }

    private Service(Parcel in){
        this.enabled = in.readByte() != 0;
        this.hours_required_per_hectare = in.readString();
        this.hire_cost = in.readString();
        this.fuel_cost = in.readString();
        this.minimum_field_size = in.readString();
        this.distance_charge = in.readString();
        this.maximum_distance = in.readString();
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel source) {
            return new Service(source);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

}
