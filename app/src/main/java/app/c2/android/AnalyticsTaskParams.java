package app.c2.android;

public class AnalyticsTaskParams {
    public String endpoint;
    public String event;
    public long serviceId;
    public String date;
    public int hits;
    public boolean recordExistsInLocalDB = false;

    public AnalyticsTaskParams(String endpoint, String event, long serviceId, String date, int hits, boolean recordExistsInLocalDB) {
        this.endpoint = endpoint;
        this.event = event;
        this.serviceId = serviceId;
        this.date = date;
        this.hits = hits;
        this.recordExistsInLocalDB = recordExistsInLocalDB;
    }
}