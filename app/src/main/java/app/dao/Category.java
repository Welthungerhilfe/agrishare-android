package app.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import app.agrishare.MyApplication;
import app.database.Categories;
import app.database.Users;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class Category implements Parcelable {

    public long Id = 0;
    public String Title = "";
    public String Services = "";

    public Category(JSONObject json, boolean persist) {
        if (json != null) {
            Id = json.optLong("Id");
            Title = json.optString("Title");
            if (json.has("Services"))
                Services = json.optJSONArray("Services").toString();

            if (persist) {
                RealmResults<Categories> results = MyApplication.realm.where(Categories.class)
                        .equalTo("Id", Id)
                        .findAll();

                if (results.size() == 0) {

                    // All writes must be wrapped in a transaction to facilitate safe multi threading
                    MyApplication.realm.beginTransaction();

                    Categories category = MyApplication.realm.createObject(Categories.class);

                    category.setId(Id);
                    category.setTitle(Title);
                    category.setServices(Services);

                    // When the transaction is committed, all changes a synced to disk.
                    MyApplication.realm.commitTransaction();


                } else {

                    MyApplication.realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            Categories category = bgRealm.where(Categories.class).equalTo("Id", Id).findFirst();

                            category.setTitle(Title);
                            category.setServices(Services);

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
    }

    public Category(Categories entry) {
        if (entry != null) {
            Id = entry.getId();
            Title = entry.getTitle();
            Services = entry.getServices();
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
