package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class EquipmentService implements Parcelable {

    public long service_id = 0;
    public long parent_category_id = 0;
    public String title = "";

    public boolean enabled = false;

    //Tractors
    public String hours_required_per_hectare = "";
    public String hire_cost = "";
    public String fuel_cost = "";
    public String minimum_field_size = "";
    public String distance_charge = "";
    public String maximum_distance = "";

    //Lorry
    public boolean mobile = false;

    //Processors
    public String total_volume_in_tonne = "";


    public EquipmentService(Service service, long parent_category_id) {
        service_id = service.Id;
        this.parent_category_id = parent_category_id;
        title = service.Title;
    }

    public EquipmentService(Service service, long parent_category_id, ListingDetailService listingDetailService) {
        service_id = service.Id;
        this.parent_category_id = parent_category_id;
        title = service.Title;

        enabled = true;
        hours_required_per_hectare = String.valueOf(listingDetailService.TimePerQuantityUnit);
        hire_cost = String.valueOf(listingDetailService.PricePerQuantityUnit);
        fuel_cost = String.valueOf(listingDetailService.FuelPerQuantityUnit);
        minimum_field_size = String.valueOf(listingDetailService.MinimumQuantity);
        distance_charge = String.valueOf(listingDetailService.PricePerDistanceUnit);
        maximum_distance = String.valueOf(listingDetailService.MaximumDistance);
        mobile = listingDetailService.Mobile;
        total_volume_in_tonne = String.valueOf(listingDetailService.TotalVolume);


    }

    public EquipmentService(boolean dummy_item) {
        if (dummy_item) {
            title = "Please select a service";
        }
    }

    public void update(EquipmentService equipmentService) {

        title = equipmentService.title;
        enabled = equipmentService.enabled;
        hours_required_per_hectare = equipmentService.hours_required_per_hectare;
        hire_cost = equipmentService.hire_cost;
        fuel_cost = equipmentService.fuel_cost;
        minimum_field_size = equipmentService.minimum_field_size;
        distance_charge = equipmentService.distance_charge;
        maximum_distance = equipmentService.maximum_distance;

        mobile = equipmentService.mobile;

        total_volume_in_tonne = equipmentService.total_volume_in_tonne;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Storing the data to Parcel object
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(service_id);
        dest.writeLong(parent_category_id);
        dest.writeString(title);
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeString(hours_required_per_hectare);
        dest.writeString(hire_cost);
        dest.writeString(fuel_cost);
        dest.writeString(minimum_field_size);
        dest.writeString(distance_charge);
        dest.writeString(maximum_distance);
        dest.writeByte((byte) (mobile ? 1 : 0));
        dest.writeString(total_volume_in_tonne);

    }

    private EquipmentService(Parcel in){
        this.service_id = in.readLong();
        this.parent_category_id = in.readLong();
        this.title = in.readString();
        this.enabled = in.readByte() != 0;
        this.hours_required_per_hectare = in.readString();
        this.hire_cost = in.readString();
        this.fuel_cost = in.readString();
        this.minimum_field_size = in.readString();
        this.distance_charge = in.readString();
        this.maximum_distance = in.readString();
        this.mobile = in.readByte() != 0;
        this.total_volume_in_tonne = in.readString();
    }

    public static final Creator<EquipmentService> CREATOR = new Creator<EquipmentService>() {
        @Override
        public EquipmentService createFromParcel(Parcel source) {
            return new EquipmentService(source);
        }

        @Override
        public EquipmentService[] newArray(int size) {
            return new EquipmentService[size];
        }
    };

    @Override
    public String toString() {
        return title;
    }

}
