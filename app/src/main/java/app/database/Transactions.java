package app.database;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class Transactions extends RealmObject {

    // All fields are by default persisted.

    private long Id;
    private long BookingId;
    private String BookingUser;
    private String Reference;
    private double Amount;
    private long StatusId;
    private String Status;
    private String DateCreated;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public long getBookingId() {
        return BookingId;
    }

    public void setBookingId(long bookingId) {
        BookingId = bookingId;
    }

    public String getBookingUser() {
        return BookingUser;
    }

    public void setBookingUser(String bookingUser) {
        BookingUser = bookingUser;
    }

    public String getReference() {
        return Reference;
    }

    public void setReference(String reference) {
        Reference = reference;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public long getStatusId() {
        return StatusId;
    }

    public void setStatusId(long statusId) {
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

    // You can instruct Realm to ignore a field and not persist it.
    @Ignore
    private int tempReference;

}

