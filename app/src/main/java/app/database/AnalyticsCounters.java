package app.database;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class AnalyticsCounters extends RealmObject {

    // All fields are by default persisted.

    private String Event;
    private long Serviceid;
    private String Date;
    private int Hits;

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public long getServiceid() {
        return Serviceid;
    }

    public void setServiceid(long serviceid) {
        Serviceid = serviceid;
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

