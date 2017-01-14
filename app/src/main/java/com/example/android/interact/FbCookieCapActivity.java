package com.example.android.interact;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO: Do in application instance
        android.webkit.CookieSyncManager.createInstance(getApplicationContext());
        android.webkit.CookieManager.getInstance().setAcceptCookie(true);
        WebkitCookieManagerProxy coreCookieManager = new WebkitCookieManagerProxy(null, java.net.CookiePolicy.ACCEPT_ALL);
        java.net.CookieHandler.setDefault(coreCookieManager);
                final String fbFriendUrl = getIntent().getStringExtra("URL");
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
                                        Toast.makeText(activity, "Friend request sent", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                            // not really needed, never loads desktop version of fb by default, though user might switch to it
                            String desktopJs = "javascript:";
                            desktopJs += "var aTags = document.getElementsByTagName(\"button\");";
                            desktopJs += "var searchText = \"Add Friend\";";
                            desktopJs += "for (var i = 0; i < aTags.length; i++) {";
                            desktopJs += "if (aTags[i].textContent == searchText) {";
                            desktopJs += "aTags[i].click(); } }";
                            wv.loadUrl(desktopJs);

                            String js = "javascript:";
                            js += "var h = document.getElementsByTagName('html')[0].innerHTML;";
                            js += "var elem = document.createElement('textarea');";
                            js += "elem.innerHTML = h;";
                            js += "h = elem.value;";
                            js += "var start = h.indexOf('/a/mobile/friends/profile_add_friend');";
                            js += "if(start != -1) {";
                            js += "h = h.substring(start);";
                            js += "end = h.indexOf(\"\\\"\");";
                            js += "fburl = 'https://m.facebook.com/' + h.substring(0, end);";
                            // js += "window.alert(fburl);";
                            js += "window.location = fburl;}";
                            wv.loadUrl(js);
                        }
                    }
                });

                wv.loadUrl(fbFriendUrl);
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
