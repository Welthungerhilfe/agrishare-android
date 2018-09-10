package app.account;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import app.agrishare.BaseActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;

public class PrivacyPolicyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        setNavBar("", R.drawable.grey_close);
        initViews();
    }

    private void initViews(){
        String html =
                "<html>" +
                        "<head>" + getString(R.string.css_content) + "</head>" +
                        "<body>" +
                        "Hello world... This is the privacy policy text here. Put whatever terms and conditions here. Put whatever terms and conditions here.Put whatever terms and conditions here.Put whatever terms and conditions here.Put whatever terms and conditions here.Put whatever terms and conditions here.Put whatever terms and conditions here." +
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
