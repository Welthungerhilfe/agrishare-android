package app.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import app.agrishare.MyApplication;
import app.database.Categories;
import app.database.SavedListing;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class Listing implements Parcelable {

    public long Id = 0;
    public long UserId = 0;
    public Category Category;
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
    public boolean AvailableWithoutFuel = false;

    public String CategoryJsonString = "";

    public Listing(){ } //used when saving a listing

    public Listing(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            UserId = json.optLong("UserId");
            JSONObject subcategoryJSONObject = json.optJSONObject("Category");
            Category = new Category(subcategoryJSONObject, false);
            CategoryJsonString = subcategoryJSONObject.toString();
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
            Services = json.optJSONArray("Services").toString();
            StatusId = json.optInt("StatusId");
            Status = json.optString("Status");
            DateCreated = json.optString("DateCreated");
            AvailableWithoutFuel = json.optBoolean("AvailableWithoutFuel");
        }
    }

    public Listing(SavedListing entry) {
        //for saved Listing
        if (entry != null) {
            Id = entry.getId();
            Title = entry.getTitle();
            Services = entry.getServices();

            Id = entry.getId();
            UserId = entry.getUserId();
            try {
                JSONObject subcategoryJSONObject = new JSONObject(entry.getCategory());
                Category = new Category(subcategoryJSONObject, false);
                CategoryJsonString = subcategoryJSONObject.toString();
            } catch (JSONException ex){
                if (MyApplication.DEBUG){
                    Log.d("Listing object", "JSONException: " + ex.getMessage());
                }
            }
            Title = entry.getTitle();
            Description = entry.getDescription();
            Location = entry.getLocation();
            Latitude = entry.getLatitude();
            Longitude = entry.getLongitude();
            Brand = entry.getBrand();
            HorsePower = entry.getHorsePower();
            Year = entry.getYear();
            ConditionId = entry.getConditionId();
            Condition = entry.getCondition();
            GroupServices = entry.isGroupServices();
            Photos = entry.getPhotos();
            AverageRating = entry.getAverageRating();
            RatingCount = entry.getRatingCount();
            Services = entry.getServices();
            StatusId = entry.getStatusId();
            Status = entry.getStatus();
            DateCreated = entry.getDateCreated();
            AvailableWithoutFuel = entry.isAvailableWithoutFuel();
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
        dest.writeParcelable(Category, flags);
        dest.writeString(CategoryJsonString);
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
        dest.writeByte((byte) (AvailableWithoutFuel ? 1 : 0));

    }

    private Listing(Parcel in){
        this.Id = in.readLong();
        this.UserId = in.readLong();
        this.Category = in.readParcelable(Service.class.getClassLoader());
        this.CategoryJsonString = in.readString();
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
        this.AvailableWithoutFuel = in.readByte() != 0;
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

    @Override
    public boolean equals(Object obj) {
        // return super.equals(obj);
        boolean sameSame = false;

        if (obj != null && obj instanceof Listing)
        {
            sameSame = this.Id == ((Listing) obj).Id;
        }

        return sameSame;
    }

    public void save(){
        RealmResults<SavedListing> results = MyApplication.realm.where(SavedListing.class)
                .equalTo("Id", Id)
                .findAll();
        if (results.size() == 0) {

            // All writes must be wrapped in a transaction to facilitate safe multi threading
            MyApplication.realm.beginTransaction();

            SavedListing listing = MyApplication.realm.createObject(SavedListing.class);

            listing.setId(Id);
            listing.setUserId(UserId);
            listing.setCategory(CategoryJsonString);
            listing.setTitle(Title);
            listing.setDescription(Description);
            listing.setLocation(Location);
            listing.setLatitude(Latitude);
            listing.setLongitude(Longitude);
            listing.setBrand(Brand);
            listing.setHorsePower(HorsePower);
            listing.setYear(Year);
            listing.setConditionId(ConditionId);
            listing.setCondition(Condition);
            listing.setGroupServices(GroupServices);
            listing.setPhotos(Photos);
            listing.setAverageRating(AverageRating);
            listing.setRatingCount(RatingCount);
            listing.setServices(Services);
            listing.setStatusId(StatusId);
            listing.setStatus(Status);
            listing.setDateCreated(DateCreated);
            listing.setAvailableWithoutFuel(AvailableWithoutFuel);


            // When the transaction is committed, all changes a synced to disk.
            MyApplication.realm.commitTransaction();


        } else {

            MyApplication.realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    SavedListing listing = bgRealm.where(SavedListing.class).equalTo("Id", Id).findFirst();

                    listing.setUserId(UserId);
                    listing.setCategory(CategoryJsonString);
                    listing.setTitle(Title);
                    listing.setDescription(Description);
                    listing.setLocation(Location);
                    listing.setLatitude(Latitude);
                    listing.setLongitude(Longitude);
                    listing.setBrand(Brand);
                    listing.setHorsePower(HorsePower);
                    listing.setYear(Year);
                    listing.setConditionId(ConditionId);
                    listing.setCondition(Condition);
                    listing.setGroupServices(GroupServices);
                    listing.setPhotos(Photos);
                    listing.setAverageRating(AverageRating);
                    listing.setRatingCount(RatingCount);
                    listing.setServices(Services);
                    listing.setStatusId(StatusId);
                    listing.setStatus(Status);
                    listing.setDateCreated(DateCreated);
                    listing.setAvailableWithoutFuel(AvailableWithoutFuel);

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    // Original queries and Realm objects are automatically updated.


                }
            });

        }
    }
}
