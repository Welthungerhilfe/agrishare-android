package app.location;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import app.agrishare.BaseActivity;
import app.agrishare.R;

public class SelectLocationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Select Location", R.drawable.white_close);
        initViews();
    }

    private void initViews(){

    }

    public void returnResult(long location_id, String location_title) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("location_id", String.valueOf(location_id));
        returnIntent.putExtra("location_title", location_title);
        setResult(Activity.RESULT_OK, returnIntent);
        closeKeypad();
        goBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                close();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        close();
    }

}
