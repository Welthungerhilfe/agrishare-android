package app.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import app.agrishare.MyApplication;
import app.database.Categories;
import app.database.Transactions;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class Transaction implements Parcelable {

    public long Id = 0;
    public long BookingId = 0;
    public String BookingUser = "";
    public String Reference = "";
    public double Amount = 0;
    public long StatusId = 0;
    public String Status = "";
    public String DateCreated = "";

    public Transaction(JSONObject json) {
        if (json != null) {
            Id = json.optLong("Id");
            BookingId = json.optLong("BookingId");
            BookingUser = json.optString("BookingUser");
            Reference = json.optString("Reference");
            Amount = json.optDouble("Amount");
            StatusId = json.optLong("StatusId");
            Status = json.optString("Status");
            DateCreated = json.optString("DateCreated");


            RealmResults<Transactions> results = MyApplication.realm.where(Transactions.class)
                    .equalTo("Id", Id)
                    .findAll();

            if (results.size() == 0) {

                // All writes must be wrapped in a transaction to facilitate safe multi threading
                MyApplication.realm.beginTransaction();

                Transactions transaction = MyApplication.realm.createObject(Transactions.class);

                transaction.setId(Id);
                transaction.setBookingId(BookingId);
                transaction.setBookingUser(BookingUser);
                transaction.setReference(Reference);
                transaction.setAmount(Amount);
                transaction.setStatusId(StatusId);
                transaction.setStatus(Status);
                transaction.setDateCreated(DateCreated);

                // When the transaction is committed, all changes a synced to disk.
                MyApplication.realm.commitTransaction();


            } else {

                MyApplication.realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        Transactions transaction = bgRealm.where(Transactions.class).equalTo("Id", Id).findFirst();

                        transaction.setBookingId(BookingId);
                        transaction.setBookingUser(BookingUser);
                        transaction.setReference(Reference);
                        transaction.setAmount(Amount);
                        transaction.setStatusId(StatusId);
                        transaction.setStatus(Status);
                        transaction.setDateCreated(DateCreated);

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


    public Transaction(Transactions entry) {
        if (entry != null) {
            Id = entry.getId();
            BookingId = entry.getBookingId();
            BookingUser = entry.getBookingUser();
            Reference = entry.getReference();
            Amount = entry.getAmount();
            StatusId = entry.getStatusId();
            Status = entry.getStatus();
            DateCreated = entry.getDateCreated();
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
        dest.writeLong(BookingId);
        dest.writeString(BookingUser);
        dest.writeString(Reference);
        dest.writeDouble(Amount);
        dest.writeLong(StatusId);
        dest.writeString(Status);
        dest.writeString(DateCreated);
    }

    private Transaction(Parcel in){
        this.Id = in.readLong();
        this.BookingId = in.readLong();
        this.BookingUser = in.readString();
        this.Reference = in.readString();
        this.Amount = in.readDouble();
        this.StatusId = in.readLong();
        this.Status = in.readString();
        this.DateCreated = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

}
