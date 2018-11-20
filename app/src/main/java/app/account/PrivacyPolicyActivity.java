package app.account;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

import app.agrishare.BaseActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;

public class PrivacyPolicyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (MyApplication.token.isEmpty()) {
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
            setNavBar("", R.drawable.grey_close);
        }
        else {
            setNavBar("Terms & Privacy Policy", R.drawable.button_back);
        }
        initViews();
    }

    private void initViews(){
        String html =
                "<html>" +
                        "<head>" + getString(R.string.css_content) + "</head>" +
                        "<body>" +
                        readPolicyFile("privacy_policy") +
                        "</body>" +
                        "</html>";

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.argb(1, 0, 0, 0));
        webView.setFocusable(false);
        webView.setFocusableInTouchMode(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                hideLoader();
            }
        });
        webView.loadDataWithBaseURL(MyApplication.BaseUrl, html, "text/html", "utf-8", null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (MyApplication.token.isEmpty())
                    close();
                else
                    goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (MyApplication.token.isEmpty())
            close();
        else
            goBack();
    }

    public String readPolicyFile(String name) {
        String tContents = "";

        try {
            InputStream stream = this.getAssets().open(name + ".txt");

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);

        } catch (IOException e) {
            // Handle exceptions here
            Log.d("IOException", "Couldnt read privacy_policy.txt file: " + e.getMessage());
        }

        return tContents;

    }

}
