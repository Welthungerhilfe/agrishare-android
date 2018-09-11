package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ernestnyumbu on 11/9/2018.
 */

public class FAQ implements Parcelable {

    public long Id = 0;
    public String Title = "";
    public String Question = "";
    public String Answer = "";
    public long SortOrder = 0;
    public String DateCreated = "";
    public String LastModified = "";

    public FAQ(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            Title = json.optString("FirstName");
            Question = json.optString("Question");
            Answer = json.optString("Answer");
            SortOrder = json.optLong("SortOrder");
            DateCreated = json.optString("DateCreated");
            LastModified = json.optString("LastModified");
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
        dest.writeString(Question);
        dest.writeString(Answer);
        dest.writeLong(SortOrder);
        dest.writeString(DateCreated);
        dest.writeString(LastModified);

    }

    private FAQ(Parcel in){
        this.Id = in.readLong();
        this.Title = in.readString();
        this.Question = in.readString();
        this.Answer = in.readString();
        this.SortOrder = in.readLong();
        this.DateCreated = in.readString();
        this.LastModified = in.readString();
    }

    public static final Creator<FAQ> CREATOR = new Creator<FAQ>() {
        @Override
        public FAQ createFromParcel(Parcel source) {
            return new FAQ(source);
        }

        @Override
        public FAQ[] newArray(int size) {
            return new FAQ[size];
        }
    };

}
