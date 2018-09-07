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

public class User implements Parcelable {

    public long Id = 0;
    public String FirstName = "";
    public String LastName = "";
    public String EmailAddress = "";
    public String Telephone = "";
    public String DateOfBirth = "";
    public long GenderId = 0;
    public String Gender = "";
    public String AuthToken = "";
    public String NotificationPreferences = "";
    public long InterestId = 0;
    public String Interest = "";


    public User(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            FirstName = json.optString("FirstName");
            LastName = json.optString("LastName");
            EmailAddress = json.optString("EmailAddress");
            Telephone = json.optString("Telephone");
            DateOfBirth = json.optString("DateOfBirth");
            GenderId = json.optLong("GenderId");
            Gender = json.optString("Gender");
            AuthToken = json.optString("AuthToken");
            NotificationPreferences = json.optString("NotificationPreferences");
            InterestId = json.optLong("InterestId");
            Interest = json.optString("Interest");

            if (!AuthToken.isEmpty() && !AuthToken.equals("null")){

                RealmResults<Users> results = MyApplication.realm.where(Users.class)
                        .equalTo("AuthToken", AuthToken)
                        .findAll();

                if (results.size() == 0) {

                    // All writes must be wrapped in a transaction to facilitate safe multi threading
                    MyApplication.realm.beginTransaction();

                    Users user = MyApplication.realm.createObject(Users.class);

                    user.setId(Id);
                    user.setFirstName(FirstName);
                    user.setLastName(LastName);
                    user.setEmailAddress(EmailAddress);
                    user.setTelephone(Telephone);
                    user.setDateOfBirth(DateOfBirth);
                    user.setGenderId(GenderId);
                    user.setGender(Gender);
                    user.setAuthToken(AuthToken);
                    user.setNotificationPreferences(NotificationPreferences);
                    user.setInterestId(InterestId);
                    user.setInterest(Interest);

                    // When the transaction is committed, all changes a synced to disk.
                    MyApplication.realm.commitTransaction();


                } else {

                    MyApplication.realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            Users user = bgRealm.where(Users.class).equalTo("AuthToken", AuthToken).findFirst();

                            user.setId(Id);
                            user.setFirstName(FirstName);
                            user.setLastName(LastName);
                            user.setEmailAddress(EmailAddress);
                            user.setTelephone(Telephone);
                            user.setDateOfBirth(DateOfBirth);
                            user.setGenderId(GenderId);
                            user.setGender(Gender);
                            user.setAuthToken(AuthToken);
                            user.setNotificationPreferences(NotificationPreferences);
                            user.setInterestId(InterestId);
                            user.setInterest(Interest);

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

    public User(Users entry) {
        if (entry != null){
            Id = entry.getId();
            FirstName = entry.getFirstName();
            LastName = entry.getLastName();
            EmailAddress = entry.getEmailAddress();
            Telephone = entry.getTelephone();
            DateOfBirth = entry.getDateOfBirth();
            GenderId = entry.getGenderId();
            Gender = entry.getGender();
            AuthToken = entry.getAuthToken();
            NotificationPreferences = entry.getNotificationPreferences();
            InterestId = entry.getInterestId();
            Interest = entry.getInterest();
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
        dest.writeString(LastName);
        dest.writeString(EmailAddress);
        dest.writeString(Telephone);
        dest.writeString(DateOfBirth);
        dest.writeLong(GenderId);
        dest.writeString(Gender);
        dest.writeString(AuthToken);
        dest.writeString(NotificationPreferences);
        dest.writeLong(InterestId);
        dest.writeString(Interest);

    }

    private User(Parcel in){
        this.Id = in.readLong();
        this.FirstName = in.readString();
        this.LastName = in.readString();
        this.EmailAddress = in.readString();
        this.Telephone = in.readString();
        this.DateOfBirth = in.readString();
        this.GenderId = in.readLong();
        this.Gender = in.readString();
        this.AuthToken = in.readString();
        this.NotificationPreferences = in.readString();
        this.InterestId = in.readLong();
        this.Interest = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void deleteRecord(){
        RealmResults<Users> query_results = MyApplication.realm.where(Users.class)
                .equalTo("AuthToken", AuthToken)
                .findAll();
        MyApplication.realm.beginTransaction();
        query_results.deleteAllFromRealm();
        MyApplication.realm.commitTransaction();
    }


}
