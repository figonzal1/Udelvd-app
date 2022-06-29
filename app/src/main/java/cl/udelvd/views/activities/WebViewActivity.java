package cl.udelvd.views.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import cl.udelvd.R;
import cl.udelvd.utils.Utils;

public class WebViewActivity extends AppCompatActivity {

    private String linkGrafico;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Utils.configToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TABLEAU));

        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            linkGrafico = bundle.getString(getString(R.string.INTENT_LINK_GRAFICO));
        }

        WebView webView = findViewById(R.id.webview);
        webView.setVerticalScrollBarEnabled(true);
        webView.requestFocus();
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(linkGrafico);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest url) {
                view.loadUrl(url.getUrl().toString());
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
