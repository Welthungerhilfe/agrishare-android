package app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import app.agrishare.MyApplication;
import app.database.Users;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class MiniUser implements Parcelable {

    public long Id = 0;
    public String FirstName = "";
    public long StatusId = 0;


    public MiniUser(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            FirstName = json.optString("FirstName");
            StatusId = json.optLong("StatusId");
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
        dest.writeString(FirstName);
        dest.writeLong(StatusId);

    }

    private MiniUser(Parcel in){
        this.Id = in.readLong();
        this.FirstName = in.readString();
        this.StatusId = in.readLong();
    }

    public static final Creator<MiniUser> CREATOR = new Creator<MiniUser>() {
        @Override
        public MiniUser createFromParcel(Parcel source) {
            return new MiniUser(source);
        }

        @Override
        public MiniUser[] newArray(int size) {
            return new MiniUser[size];
        }
    };

}
