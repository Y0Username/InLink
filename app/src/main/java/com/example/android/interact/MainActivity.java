package com.example.android.interact;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    private String name;
    private String phone;
    private ProgressBar pb;
    private CheckBox tempBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Track App Installs and App Opens
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(this);
        phone = sh.getString("phone", "6199292596");
        name = sh.getString("name", "prg");

        setContentView(R.layout.activity_main);
        //fbLogin here.
        qrCode = (ImageView) findViewById(R.id.qr_code);

        tempBox = (CheckBox) findViewById(R.id.temp_box);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String str = sharedPreferences.getString("FBURL", "https://www.facebook.com/");
        if (str != null && !str.equals("https://www.facebook.com/")) {
            Log.v("hryuygw:", str);
            new QRTask().execute(str);
        }
    }

    public void checkButton(View v) {
        boolean check = ((CheckBox) v).isChecked();
        if(check) {

        }
        else {
            
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
            updateLinkOnServer(ab[1], ab[2], ab[3]);
            Intent intent1 = new Intent(MainActivity.this, FbCookieCapActivity.class);
            intent1.putExtra(FbCookieCapActivity.KEY_URL, ab[0]);
            intent1.putExtra(FbCookieCapActivity.KEY_JS, fbFriendJS());
            startActivity(intent1);
        }
    }

    protected void updateLinkOnServer(final String fromPhone, final String fromName, final String fromUid) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String str = sharedPreferences.getString("FBURL", "https://www.facebook.com/");
        final String name = sharedPreferences.getString("phone", "");
        final String phone = sharedPreferences.getString("name", "");
        final String fbuid = sharedPreferences.getString("fbuid", "");
        if (name.equals("") || phone.equals("") || fbuid.equals("")) {
            Log.e("Pending friend loader", "Couldn't load user name, phone and/or fb uid. Fuck.");
            return;
        }

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Updating server");
        progress.setMessage("Please wait...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        final JSONObject obj = new JSONObject();
        try {
            obj.put("password", "this_is_lame_security1234!");
            obj.put("to_fuid", fbuid);
            obj.put("to_phone", phone);
            obj.put("to_name", name);
            obj.put("from_fuid", fromUid);
            // obj.put("to_fuid", fbuid);
            final Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    progress.dismiss();
                }
            };
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.inlink_server) + "/put_new_connection", obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Server link update", "Received 200");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                        }
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Server link update", "Failed to update server");
                }
            });
            RequestQueue q = Volley.newRequestQueue(MainActivity.this);
            q.add(jsObjRequest);

        }
        catch (JSONException e) {
            progress.dismiss();
            Toast.makeText(this, "Failed to create json object", Toast.LENGTH_LONG).show();
            return;
        }
            /*
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(getString(R.string.inlink_server) + "/put_new_connection");
                        URLConnection con = url.openConnection();
                        OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
                        wr.write(obj.toString());
                    } catch(MalformedURLException e) {
                    }catch(IOException e) {
                    } finally {
                        progress.dismiss();
                    }
                }
            });*/
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
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor sht = sharedPreferences.edit();
                String rt = "https://www.facebook.com/" + userId + "\n" + phone + "\n" + name + "\n" + userId + "\n" + "false" /*is temp contact*/;
                sht.putString("fbuid", userId);
                sht.putString("FBURL", rt);
                sht.apply();
//                Log.v("hey", rt);
                new QRTask().execute(rt);
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
        if (accessTokenTracker != null)
            accessTokenTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
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
                updateLinkOnServer(ab[1], ab[2], ab[3]);
                intent.putExtra(FbCookieCapActivity.KEY_URL, ab[0]);
                intent.putExtra(FbCookieCapActivity.KEY_JS, fbFriendJS());
                startActivity(intent);
            }
        }
    }

    private static final String fbFriendJS() {
        String js = "javascript:";
        // not really needed, never loads desktop version of fb by default, though user might switch to it
        /*js += "try {";
        js += "var aTags = document.getElementsByTagName(\"button\");";
        js += "var searchText = \"Add Friend\";";
        js += "for (var i = 0; i < aTags.length; i++) {";
        js += "if (aTags[i].textContent == searchText) {";
        js += "aTags[i].click(); } }";
        js += "} catch(err) {}";*/

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
                PendingFriendLoader l = new PendingFriendLoader(MainActivity.this);
                l.load(0);
                Toast.makeText(getApplicationContext(),
                        eventString,
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    class QRTask extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = (ProgressBar) findViewById(R.id.spinner);
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                bitmap = encodeAsBitmap(params[0]);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                qrCode.setImageBitmap(bitmap);
                pb.setVisibility(View.INVISIBLE);
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
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white);

                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, 1000, 0, 0, w, h);
            return bitmap;
        }
    }

    protected void createTemporaryNumberAndSave(final String durationInDays, final String to_uid, final String to_num, final String to_name) {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        JSONObject obj = new JSONObject();
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            final String phone = sharedPreferences.getString("name", "");
            final String fbuid = sharedPreferences.getString("fbuid", "");
            if (phone.equals("") || fbuid.equals("")) {
                Log.e("Pending friend loader", "Couldn't load user name, phone or fb uid. Fuck.");
                return;
            }

            obj.put("password", "this_is_lame_security1234!");
            obj.put("from_fuid", fbuid);
            obj.put("to_fuid", to_uid);
            obj.put("real_number_1", phone);
            obj.put("real_number_2", to_num);
            obj.put("duration", durationInDays);
        } catch (JSONException e) {
            progress.dismiss();
            Toast.makeText(this, "Failed to create json object", Toast.LENGTH_LONG).show();
            return;
        }

        final Handler handler = new Handler();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.inlink_server) + "/create_inlink_number", obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final String tempPhone = response.getString("temp_number");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            contact(tempPhone, to_name);
                            // phone, name, uid
                            updateLinkOnServer(to_num, to_name, "");
                            progress.dismiss();
                        }
                    });
                } catch(JSONException e) {
                    Log.e("Get temp number", "JSONException");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Get temp number", "An error occurred");
            }
        });
        progress.dismiss();
    }

    private class PendingFriendLoader {
        Activity activity;
        PendingFriendLoader(final Activity activity) {
            this.activity = activity;
        }

        void load(final int attempt) {
            final ProgressDialog progress = new ProgressDialog(this.activity);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            String friendId = null;
            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    progress.dismiss();
                }
            };

            Handler handler = new Handler();
            Runnable r=new Runnable() {
                public void run() {
                    JSONObject obj = new JSONObject();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    final String fbuid = sharedPreferences.getString("fbuid", "");
                    //String name = sharedPreferences.getString("phone", "");
                    // String phone = sharedPreferences.getString("name", "");
                    if (fbuid.equals("")) {
                        Log.e("Pending friend loader", "Couldn't load user fb uid. Fuck.");
                        return;
                    }
                    try {
                        obj.put("password", "this_is_lame_security1234!");
                        obj.put("from_fuid", fbuid);
                        // obj.put("to_fuid", fbuid);
                    } catch (JSONException e) {
                        progress.dismiss();
                        Toast.makeText(activity, "Failed to create json object", Toast.LENGTH_LONG).show();
                        return;
                    }

                    /*Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            URL url = new URL("http://localhost:8080/api" + "?" + query);
                            URLConnection connection = url.openConnection();
                        }
                    });*/
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.inlink_server) + "/get_unclaimed_friends?from_fuid="+fbuid, obj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.dismiss();
                            try {
                                if(response.has("to_fuid")) {
                                    final String fbUrl = "https://m.facebook.com/" + response.getString("to_fuid");
                                    Intent intent = new Intent(PendingFriendLoader.this.activity, FbCookieCapActivity.class);
                                    intent.putExtra(FbCookieCapActivity.KEY_URL, fbUrl);
                                    intent.putExtra(FbCookieCapActivity.KEY_JS, MainActivity.fbFriendJS());
                                    startActivity(intent);
                                }
                                contact(response.getString("to_phone"), response.getString("to_name"));
                            } catch(JSONException e) {
                            } finally {
                                progress.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //if(attempt < 3)
                            //    load(attempt+1);
                            // else {
                                progress.dismiss();
                            //    Toast.makeText(PendingFriendLoader.this.activity, "Failed to get conatct", Toast.LENGTH_LONG);
                            //}
                        }
                    }) {
                        @Override
                        public Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> m = new HashMap<String, String>();
                            m.put("password", "this_is_lame_security1234!");
                            m.put("from_fuid", fbuid);
                            return m;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(PendingFriendLoader.this.activity);
                    queue.add(jsObjRequest);
                }
            };
            handler.postDelayed(r, 7777);
        }
    }
}
