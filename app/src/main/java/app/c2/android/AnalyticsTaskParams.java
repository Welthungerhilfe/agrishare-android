package app.c2.android;

public class AnalyticsTaskParams {
    public String endpoint;
    public String event;
    public String category;
    public String subcategory;
    public String date;
    public int hits;
    public boolean recordExistsInLocalDB = false;

    public AnalyticsTaskParams(String endpoint, String event, String category, String subcategory, String date, int hits, boolean recordExistsInLocalDB) {
        this.endpoint = endpoint;
        this.event = event;
        this.category = category;
        this.subcategory = subcategory;
        this.date = date;
        this.hits = hits;
        this.recordExistsInLocalDB = recordExistsInLocalDB;
    }
}