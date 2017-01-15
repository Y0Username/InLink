package com.example.android.interact;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_text);
        editText = (EditText) findViewById(R.id.prg_text);
    }

    public void theButton(View v) {
        String num = editText.getText().toString();
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor shty = sh.edit();
        shty.putString("phone", num);
        shty.apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
