package app.database;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by ernestnyumbu on 15/10/2018.
 */

public class MyMigration  implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        // During a migration, a DynamicRealm is exposed. A DynamicRealm is an untyped variant of a normal Realm, but
        // with the same object creation and query capabilities.
        // A DynamicRealm uses Strings instead of Class references because the Classes might not even exist or have been
        // renamed.

        // Access the Realm schema in order to create, modify or delete classes and their fields.
        RealmSchema schema = realm.getSchema();

        Log.d("OLD VERSION", ""+ oldVersion);

        // Migrate to version 1: Add a new class AnalyticsCounters.
        if (oldVersion == 0) {
            schema.create("AnalyticsCounters")
                    .addField("Event", String.class)
                    .addField("Serviceid", long.class)
                    .addField("Date", String.class)
                    .addField("Hits", int.class);
            oldVersion++;
        }

        // Migrate to version 2: Add a new class SavedListing.
        if (oldVersion == 1) {
            schema.create("SavedListing")
                    .addField("Id", long.class)
                    .addField("UserId", long.class)
                    .addField("Category", String.class)
                    .addField("Title", String.class)
                    .addField("Description", String.class)
                    .addField("Location", String.class)
                    .addField("Latitude", double.class)
                    .addField("Longitude", double.class)
                    .addField("Brand", String.class)
                    .addField("HorsePower", int.class)
                    .addField("Year", int.class)
                    .addField("ConditionId", int.class)
                    .addField("Condition", String.class)
                    .addField("GroupServices", boolean.class)
                    .addField("Photos", String.class)
                    .addField("AverageRating", double.class)
                    .addField("RatingCount", int.class)
                    .addField("Services", String.class)
                    .addField("StatusId", int.class)
                    .addField("Status", String.class)
                    .addField("DateCreated", String.class)
                    .addField("AvailableWithoutFuel", boolean.class);
            oldVersion++;
        }


    }
}