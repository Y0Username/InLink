package com.example.kingpushpakraj.inlink;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by kingpushpakraj on 14-01-2017.
 */

public class EditTextActivity extends AppCompatActivity {
    private EditText editText;
    private EditText nameText;
    private Intent intent;
    private SharedPreferences sh;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private String userId;
    private String name;
    private String phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.my_text);
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();



        //editText = (EditText) findViewById(R.id.prg_text);
        //nameText = (EditText) findViewById(R.id.grp_text);
        //sh = PreferenceManager.getDefaultSharedPreferences(this);
        //intent = new Intent(this, MainActivity.class);
//        if(!sh.getString("name","prgf").equals("prgf") && !sh.getString("phone", "668989").equals("668989")) {
          //  startActivity(intent);
  //      }
    }

    public void faceLog(View v)
    {
        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_buttonn);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FACEBOOK MANAGER", "SUCCESS");
                userId = loginResult.getAccessToken().getUserId();
                Profile profile = Profile.getCurrentProfile();
                if(profile != null) {
                    name = profile.getName();
                }
                editText = (EditText) findViewById(R.id.edit_text);
                editText.setVisibility(View.VISIBLE);
                Button b1 = (Button) findViewById(R.id.login);
                Button b2 = (Button) findViewById(R.id.submit);
                b1.setVisibility(View.INVISIBLE);
                b2.setVisibility(View.VISIBLE);
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

    }

    public void faceAfter(View v) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EditTextActivity.this);
        SharedPreferences.Editor sht = sharedPreferences.edit();
        phone = editText.getText().toString();
        if(phone.isEmpty())
            Toast.makeText(this, "Please enter your phone number!", Toast.LENGTH_SHORT).show();
        else {
            System.out.println(name);
            System.out.println(phone);
            String rt = "https://www.facebook.com/" + userId + "\n" + phone + "\n" + name;
            sht.putString("FBURL", rt);
            sht.apply();
            Intent newIntent = new Intent(this, MainActivity.class);
            newIntent.putExtra("myMessage", rt);
            newIntent.putExtra("id", userId);
            startActivity(newIntent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accessTokenTracker != null)
            accessTokenTracker.stopTracking();
    }

}

