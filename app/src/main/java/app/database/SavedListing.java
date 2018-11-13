package app.database;

import app.dao.Category;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by ernestnyumbu on 13/11/2018.
 */

public class SavedListing extends RealmObject {

    // All fields are by default persisted.

    private long Id;
    private long UserId;
    private String Category;
    private String Title;
    private String Description;
    private String Location;
    private double Latitude;
    private double Longitude;
    private String Brand;
    private int HorsePower;
    private int Year;
    private int ConditionId;
    private String Condition;
    private boolean GroupServices = false;
    private String Photos;
    private double AverageRating;
    private int RatingCount;
    private String Services;
    private int StatusId;
    private String Status;
    private String DateCreated;
    private boolean AvailableWithoutFuel;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public int getHorsePower() {
        return HorsePower;
    }

    public void setHorsePower(int horsePower) {
        HorsePower = horsePower;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public int getConditionId() {
        return ConditionId;
    }

    public void setConditionId(int conditionId) {
        ConditionId = conditionId;
    }

    public String getCondition() {
        return Condition;
    }

    public void setCondition(String condition) {
        Condition = condition;
    }

    public boolean isGroupServices() {
        return GroupServices;
    }

    public void setGroupServices(boolean groupServices) {
        GroupServices = groupServices;
    }

    public String getPhotos() {
        return Photos;
    }

    public void setPhotos(String photos) {
        Photos = photos;
    }

    public double getAverageRating() {
        return AverageRating;
    }

    public void setAverageRating(double averageRating) {
        AverageRating = averageRating;
    }

    public int getRatingCount() {
        return RatingCount;
    }

    public void setRatingCount(int ratingCount) {
        RatingCount = ratingCount;
    }

    public String getServices() {
        return Services;
    }

    public void setServices(String services) {
        Services = services;
    }

    public int getStatusId() {
        return StatusId;
    }

    public void setStatusId(int statusId) {
        StatusId = statusId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }

    public boolean isAvailableWithoutFuel() {
        return AvailableWithoutFuel;
    }

    public void setAvailableWithoutFuel(boolean availableWithoutFuel) {
        AvailableWithoutFuel = availableWithoutFuel;
    }

    // You can instruct Realm to ignore a field and not persist it.
    @Ignore
    private int tempReference;
}
