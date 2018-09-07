package app.database;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class Users extends RealmObject {

    // All fields are by default persisted.

    private long Id;
    private String FirstName;
    private String LastName;
    private String EmailAddress;
    private String Telephone;
    private String DateOfBirth;
    private long GenderId;
    private String Gender;
    private String AuthToken;
    private String NotificationPreferences;
    private long InterestId;
    private String Interest;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public long getGenderId() {
        return GenderId;
    }

    public void setGenderId(long genderId) {
        GenderId = genderId;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getAuthToken() {
        return AuthToken;
    }

    public void setAuthToken(String authToken) {
        AuthToken = authToken;
    }

    public String getNotificationPreferences() {
        return NotificationPreferences;
    }

    public void setNotificationPreferences(String notificationPreferences) {
        NotificationPreferences = notificationPreferences;
    }

    public long getInterestId() {
        return InterestId;
    }

    public void setInterestId(long interestId) {
        InterestId = interestId;
    }

    public String getInterest() {
        return Interest;
    }

    public void setInterest(String interest) {
        Interest = interest;
    }

    // You can instruct Realm to ignore a field and not persist it.
    @Ignore
    private int tempReference;

}

