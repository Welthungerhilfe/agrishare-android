package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class Rating implements Parcelable {

    public long Id = 0;
    public long ListingId = 0;
    public String User = "";
    public String Title = "";
    public String Comments = "";
    public int Stars = 0;
    public String DateCreated = "";

    public Rating(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            ListingId = json.optLong("ListingId");
            JSONObject userObject = json.optJSONObject("User");
            User = userObject.toString();
            Title = json.optString("Title");
            Comments = json.optString("Comments");
            Stars = json.optInt("Stars");
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
        dest.writeString(User);
        dest.writeString(Title);
        dest.writeString(Comments);
        dest.writeInt(Stars);
        dest.writeString(DateCreated);
    }

    private Rating(Parcel in){
        this.Id = in.readLong();
        this.ListingId = in.readLong();
        this.User = in.readString();
        this.Title = in.readString();
        this.Comments = in.readString();
        this.Stars = in.readInt();
        this.DateCreated = in.readString();
    }

    public static final Creator<Rating> CREATOR = new Creator<Rating>() {
        @Override
        public Rating createFromParcel(Parcel source) {
            return new Rating(source);
        }

        @Override
        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };

}
