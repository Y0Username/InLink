package com.example.kingpushpakraj.inlink;

import android.app.Activity;
import android.graphics.Picture;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.app.Activity;
import android.graphics.Picture;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FbCookieCapActivity extends AppCompatActivity {

    static final String KEY_URL = "extras_key_url";
    static final String KEY_JS = "extras_key_js";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getActionBar().setDisplayHomeAsUpEnabled(true);
        final String webUrl = getIntent().getStringExtra(FbCookieCapActivity.KEY_URL);
        final String execJs = getIntent().getStringExtra(FbCookieCapActivity.KEY_JS);

        // TODO: Do in application instance
        android.webkit.CookieSyncManager.createInstance(getApplicationContext());
        android.webkit.CookieManager.getInstance().setAcceptCookie(true);
        //WebkitCookieManagerProxy coreCookieManager = new WebkitCookieManagerProxy(null, java.net.CookiePolicy.ACCEPT_ALL);
        //java.net.CookieHandler.setDefault(coreCookieManager);
        final Activity activity = FbCookieCapActivity.this;

        final WebView wv = new WebView(activity);
        wv.setWebViewClient(new WebViewClient());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebChromeClient(new WebChromeClient());
        wv.setPictureListener(new WebView.PictureListener() {

            @Override
            public void onNewPicture(final WebView view, final Picture picture) {
                if (view.getProgress() == 100 && view.getContentHeight() > 0) {
                    view.setPictureListener(new WebView.PictureListener() {
                        @Override
                        public void onNewPicture(final WebView view, final Picture picture) {
                            if (view.getProgress() == 100 && view.getContentHeight() > 0) {
                                view.setPictureListener(null);
                                // Toast.makeText(activity, "Friend request sent", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

                    wv.loadUrl(execJs);
                }
            }
        });

        wv.loadUrl(webUrl);
        setContentView(wv);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
