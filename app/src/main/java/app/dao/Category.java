package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class Category implements Parcelable {

    public long Id = 0;
    public String Title = "";
    public String Services;

    public Category(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            Title = json.optString("Title");
            Services = json.optString("Services");
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
        dest.writeString(Title);
        dest.writeString(Services);
    }

    private Category(Parcel in){
        this.Id = in.readLong();
        this.Title = in.readString();
        this.Services = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

}
