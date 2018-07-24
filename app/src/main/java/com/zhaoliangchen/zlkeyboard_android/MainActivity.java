package com.zhaoliangchen.zlkeyboard_android;

import android.content.Context;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.zhaoliangchen.zlkeyboard.ZLKeyboard;

public class MainActivity extends AppCompatActivity {
    private EditText mEditText;
    private ZLKeyboard keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = findViewById(R.id.edittext);
        new ZLKeyboard(this).bindEditText(mEditText);
    }

    public void clickToKeyboard(View view) {

    }

}
