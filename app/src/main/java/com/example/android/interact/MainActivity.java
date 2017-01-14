package com.example.android.interact;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import com.facebook.FacebookSdk;

import android.nfc.NfcAdapter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.app.PendingIntent;
import android.nfc.Tag;
import android.os.Parcelable;

import java.util.Arrays;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements
        CreateNdefMessageCallback, OnNdefPushCompleteCallback {

    private ImageView qrCode;
    private String qrString;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private String userId;
    private String usr;
    private ZXingScannerView sView;

    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;

    private TextView textInfo;
    private EditText textOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Track App Installs and App Opens
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        //fbLogin here.
        qrCode = (ImageView) findViewById(R.id.qr_code);

        textInfo = (TextView)findViewById(R.id.info);
        textOut = (EditText)findViewById(R.id.textout);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null){
            Toast.makeText(MainActivity.this,
                    "nfcAdapter==null, no NFC adapter exists",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this,
                    "Set Callback(s)",
                    Toast.LENGTH_LONG).show();
            nfcAdapter.setNdefPushMessageCallback(this, this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)){
            Parcelable[] parcelables =
                    intent.getParcelableArrayExtra(
                            NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage inNdefMessage = (NdefMessage)parcelables[0];
            NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
            NdefRecord NdefRecord_0 = inNdefRecords[0];
            String inMsg = new String(NdefRecord_0.getPayload());
            textInfo.setText(inMsg);
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        setIntent(intent);
    }

    private void getTagInfo(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);


    }


    public void fbLogin(View v) {
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FACEBOOK MANAGER", "SUCCESS");
                userId = loginResult.getAccessToken().getUserId();
                new QRTask().execute("https://www.facebook.com/" + userId);
            }

            @Override
            public void onCancel() {
                Log.d("FACEBOOK MANAGER", "CANCEL");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FACEBOOK MANAGER", "ERROR");
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                accessToken = currentAccessToken;
            }
        };

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_status"));

    }

    public void handleQR(View v) {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(accessTokenTracker != null)
            accessTokenTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                Intent intent = new Intent(MainActivity.this, FbCookieCapActivity.class);

                String js = "javascript:";
                // not really needed, never loads desktop version of fb by default, though user might switch to it
                js += "try {";
                js += "var aTags = document.getElementsByTagName(\"button\");";
                js += "var searchText = \"Add Friend\";";
                js += "for (var i = 0; i < aTags.length; i++) {";
                js += "if (aTags[i].textContent == searchText) {";
                js += "aTags[i].click(); } }";
                js += "} catch(err) {}";

                // mobile specific
                js += "try {";
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
                js += "} catch(err) {}";

                intent.putExtra(FbCookieCapActivity.KEY_URL, contents);
                intent.putExtra(FbCookieCapActivity.KEY_JS, js);
                startActivity(intent);
            }
        }

    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        String stringOut = textOut.getText().toString();
        byte[] bytesOut = stringOut.getBytes();

        NdefRecord ndefRecordOut = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                "text/plain".getBytes(),
                new byte[] {},
                bytesOut);

        NdefMessage ndefMessageout = new NdefMessage(ndefRecordOut);
        return ndefMessageout;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {

        final String eventString = "onNdefPushComplete\n" + event.toString();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        eventString,
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    class QRTask extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try{
                bitmap = encodeAsBitmap(params[0]);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap !=null)
                qrCode.setImageBitmap(bitmap);
        }
    }

    private Bitmap encodeAsBitmap(String qr) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(qr, BarcodeFormat.QR_CODE, 1000, 1000, null);
        } catch (IllegalArgumentException e) {
            return null;
        }

        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w*h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.black):getResources().getColor(R.color.white);

            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels,0,1000,0,0,w,h);
        return bitmap;
    }
}
