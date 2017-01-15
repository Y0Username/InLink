package com.example.android.interact;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by kingpushpakraj on 14-01-2017.
 */

public class EditTextActivity extends AppCompatActivity {
    private EditText editText;
    private EditText nameText;
    private Intent intent;
    private SharedPreferences sh;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_text);
        editText = (EditText) findViewById(R.id.prg_text);
        nameText = (EditText) findViewById(R.id.grp_text);
        sh = PreferenceManager.getDefaultSharedPreferences(this);
        intent = new Intent(this, MainActivity.class);
       if(!sh.getString("name","prgf").equals("prgf") && !sh.getString("phone", "668989").equals("668989")) {
            startActivity(intent);
        }
    }

    public void theButton(View v) {
        String num = editText.getText().toString();
        String phon = nameText.getText().toString();
        SharedPreferences.Editor shty = sh.edit();
        shty.putString("phone", num);
        shty.putString("name", phon);
        shty.apply();
        startActivity(intent);
    }
}
