package app.about;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("About AgriShare", R.drawable.button_back);
        initViews();
    }

    private void initViews(){
        (findViewById(R.id.seeking)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    (findViewById(R.id.seeking_layout)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.offering_layout)).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.seeking)).setTextColor(getResources().getColor(R.color.colorPrimary));
                    ((TextView) findViewById(R.id.offering)).setTextColor(getResources().getColor(R.color.mid_grey));
                }
            }
        });

        (findViewById(R.id.offering)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    (findViewById(R.id.seeking_layout)).setVisibility(View.GONE);
                    (findViewById(R.id.offering_layout)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.seeking)).setTextColor(getResources().getColor(R.color.mid_grey));
                    ((TextView) findViewById(R.id.offering)).setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

}
