package app.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import net.hockeyapp.android.metrics.model.Base;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CellNumberActivity extends BaseActivity {

    @BindView(R.id.submit)
    public Button submit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_number);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.page_bg_grey));
        setSupportActionBar(toolbar);
        setNavBar("", R.drawable.white_close);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    goToRegister();
                }
            }
        });
    }

    private void goToRegister(){
        Intent intent = new Intent(CellNumberActivity.this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
    }

    private void goToLogin(){
        Intent intent = new Intent(CellNumberActivity.this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
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
