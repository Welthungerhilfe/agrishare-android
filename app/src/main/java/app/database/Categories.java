package app.database;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class Categories extends RealmObject {

    // All fields are by default persisted.

    private long Id;
    private String Title;
    private String Services;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getServices() {
        return Services;
    }

    public void setServices(String services) {
        Services = services;
    }

    // You can instruct Realm to ignore a field and not persist it.
    @Ignore
    private int tempReference;

}

