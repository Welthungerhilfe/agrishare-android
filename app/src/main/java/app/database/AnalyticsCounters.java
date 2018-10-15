package app.database;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class AnalyticsCounters extends RealmObject {

    // All fields are by default persisted.

    private String Event;
    private String Category;
    private String Subcategory;
    private String Date;
    private int Hits;

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getSubcategory() {
        return Subcategory;
    }

    public void setSubcategory(String subcategory) {
        Subcategory = subcategory;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getHits() {
        return Hits;
    }

    public void setHits(int hits) {
        Hits = hits;
    }

    // You can instruct Realm to ignore a field and not persist it.
    @Ignore
    private int tempReference;

}

