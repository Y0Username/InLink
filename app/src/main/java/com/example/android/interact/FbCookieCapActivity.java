package com.example.android.interact;

import android.app.Activity;
import android.graphics.Picture;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

        // TODO: Do in application instance
        android.webkit.CookieSyncManager.createInstance(getApplicationContext());
        android.webkit.CookieManager.getInstance().setAcceptCookie(true);
        WebkitCookieManagerProxy coreCookieManager = new WebkitCookieManagerProxy(null, java.net.CookiePolicy.ACCEPT_ALL);
        java.net.CookieHandler.setDefault(coreCookieManager);

        setContentView(R.layout.activity_fb_cookie_cap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button buttonLogin = (Button) findViewById(R.id.button_login_custom);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Opening webview", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                final String fbLoginUrl = "https://www.facebook.com/login/";

                final Activity activity = FbCookieCapActivity.this;
                WebView wv = new WebView(activity);
                wv.getSettings().setJavaScriptEnabled(true);
                wv.setWebViewClient(new WebViewClient() {
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        Toast.makeText(activity, "Old one", Toast.LENGTH_SHORT).show();
                    }

                    // er, which one to use? Would it owrk on older droid versions?
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        Toast.makeText(activity, "New one", Toast.LENGTH_SHORT).show();
                    }
                });

                wv.loadUrl(fbLoginUrl);
                setContentView(wv);
            }
        });

        Button buttonTest = (Button) findViewById(R.id.button_test_custom);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Opening test webview", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                final String fbFriendUrl = "https://www.facebook.com/anmol.ahuja";
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

                /*
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection urlConnection = null;
                        try {
                            URL url = new URL(fbFriendUrl);
                            urlConnection = (HttpURLConnection) url.openConnection();
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            int bytesRead = 0;
                            byte[] contents = new byte[1024];
                            String strFileContents = "";
                            while((bytesRead = in.read(contents)) != -1) {
                                strFileContents += new String(contents, 0, bytesRead);
                            }
                            Log.d("Contents", strFileContents);
                        } catch(MalformedURLException e) {
                            Toast.makeText(activity, "Malformed URL", Toast.LENGTH_SHORT).show();
                        } catch(IOException e) {
                            Toast.makeText(activity, "IO error", Toast.LENGTH_SHORT).show();
                        } finally {
                            if(urlConnection != null)
                                urlConnection.disconnect();
                        }
                    }
                });
                t.start();*/
            }
        });

    }


}
