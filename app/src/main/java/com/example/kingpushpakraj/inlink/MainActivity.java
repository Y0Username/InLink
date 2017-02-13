package com.example.kingpushpakraj.inlink;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.Arrays;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.R.attr.bitmap;
import static java.lang.System.in;

public class MainActivity extends AppCompatActivity implements
        NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback, NavigationView.OnNavigationItemSelectedListener {

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
    private String name;
    private String phone;
    private ProgressBar pb;
    private View header;

    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Track App Installs and App Opens
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main_first);

        Intent nintent = getIntent();
        String msg =nintent.getStringExtra("myMessage");
        System.out.println(msg);

        if(msg != null)
        {
            String[] list = new String[3];
            if (!msg.isEmpty())
            list = msg.split("\n");
            phone = list[1];
            name = list[2];
            SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor rediter = sh.edit();
            rediter.putString("FBURL", msg);
            rediter.putString("thePhone", phone);
            rediter.putString("theName", name);
            rediter.apply();
        }

        else {
            SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(this);
            phone = sh.getString("thePhone", "thePhone");
            name = sh.getString("theName", "theName");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawer.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem menuName = menu.findItem(R.id.nav_camera);
        MenuItem menuPhone = menu.findItem(R.id.nav_gallery);
        menuName.setTitle(name);
        menuPhone.setTitle(phone);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);
        TextView tv = (TextView) header.findViewById(R.id.navo);
        tv.setText(name);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void nfcLink(View v)
    {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(MainActivity.this,
                    "nfcAdapter==null, no NFC adapter exists",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,
                    "NFC loaded",
                    Toast.LENGTH_LONG).show();
            nfcAdapter.setNdefPushMessageCallback(this, this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    public void contact(String s, String n) {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            int rawContactInsertIndex = ops.size();

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, s)
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, n)
                    .build());
            try {
                ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                int rawContactInsertIndex = ops.size();

                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, PreferenceManager.getDefaultSharedPreferences(this).getString("otherphone", "123123423"))
                        .build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, PreferenceManager.getDefaultSharedPreferences(this).getString("othername", "skehr"))
                        .build());
                try {
                    ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    ;
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);

        // See below
        if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Log.d("In beam", "Extracting message");
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            // only one message sent during the beam
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            // record 0 contains the MIME type, record 1 is the AAR, if present
            Log.d("In beamqew", new String(msg.getRecords()[0].getPayload()));
            String[] ab = new String(msg.getRecords()[0].getPayload()).split("\n");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor sh = sharedPreferences.edit();
            sh.putString("othername", ab[2]);
            sh.putString("otherphone", ab[1]);
            sh.apply();
            contact(ab[1], ab[2]);
            Intent intent1 = new Intent(MainActivity.this, FbCookieCapActivity.class);
            intent1.putExtra(FbCookieCapActivity.KEY_URL, ab[0]);
            intent1.putExtra(FbCookieCapActivity.KEY_JS, jString());
            startActivity(intent1);
        }
    }
    private void getTagInfo(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    }


    public void fbLogin(View v) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String rt = sharedPreferences.getString("FBURL", "msg");
        if(!rt.equals("msg")) {
            Log.v("wholey", rt);
            Intent intent = new Intent(this, QRActivity.class);
            intent.putExtra("code", rt);
            startActivity(intent);

        }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                Intent intent = new Intent(MainActivity.this, FbCookieCapActivity.class);
                String[] ab = contents.split("\n");
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor sh = sharedPreferences.edit();
                sh.putString("othername", ab[2]);
                sh.putString("otherphone", ab[1]);
                sh.apply();
                contact(ab[1], ab[2]);
                intent.putExtra(FbCookieCapActivity.KEY_URL, ab[0]);
                intent.putExtra(FbCookieCapActivity.KEY_JS, jString());
                startActivity(intent);
            }
        }

    }

    private String jString() {
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
        return js;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(this);
        String testMessage = sh.getString("FBURL", "https://www.facebook.com/");

        NdefMessage msg = new NdefMessage(
                new NdefRecord[]{NdefRecord.createMime(
                        getString(R.string.nfc_mime), testMessage.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                        */
                        //, NdefRecord.createApplicationRecord(getString(R.string.package_str))
                });
        return msg;
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


}
