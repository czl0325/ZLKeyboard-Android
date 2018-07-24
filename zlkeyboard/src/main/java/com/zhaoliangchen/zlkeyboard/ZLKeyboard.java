package com.zhaoliangchen.zlkeyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.lang.reflect.Method;

public class ZLKeyboard {
    private Context mContext;
    private EditText mEditText;
    private FrameLayout decordView;
    //private FrameLayout rootView;
    private ViewGroup myKeyboard;

    public ZLKeyboard(Context context) {
        super();
        this.mContext = context;
        init();
    }

    private void init() {
        decordView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
        decordView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!(view instanceof EditText)) {
                    mEditText.clearFocus();
                    decordView.setFocusable(true);
                    decordView.setFocusableInTouchMode(true);
                    return true;
                }
                return false;
            }
        });
//        rootView = new FrameLayout(mContext);
//        FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        rootView.setLayoutParams(layoutParams1);
//        rootView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dismiss();
//            }
//        });
//        rootView.setBackgroundColor(Color.TRANSPARENT);
        myKeyboard = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.zlkeyboard, decordView, false);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.gravity = Gravity.BOTTOM;
        myKeyboard.setLayoutParams(layoutParams2);

        Button btFinish = myKeyboard.findViewById(R.id.bt_finish);
        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void bindEditText(EditText editText) {
        mEditText = editText;
        disableShowInput();
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e("czl","键盘是否获取焦点---"+hasFocus);
                if (hasFocus) {
                    show();
                } else {
                    dismiss();
                }
            }
        });

        ZLKeyboardView zlKeyboardView = myKeyboard.findViewById(R.id.zlKeyboardView);
        zlKeyboardView.setEditText(mEditText);

        ViewGroup viewGroup = (ViewGroup) mEditText.getParent();
        if (viewGroup != null) {
            viewGroup.setFocusable(true);
            viewGroup.setFocusableInTouchMode(true);
        }
    }

    public void show() {
        //decordView.addView(rootView);
        //rootView.addView(myKeyboard);
        decordView.addView(myKeyboard);
        myKeyboard.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom));
    }

    private void dismiss() {
        mEditText.clearFocus();

        Animation out = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_bottom);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (myKeyboard != null) {
                    decordView.removeView(myKeyboard);
                }
//                if (rootView != null) {
//                    decordView.removeView(rootView);
//                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        myKeyboard.startAnimation(out);
    }

    public void disableShowInput() {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            mEditText.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(mEditText, false);
            } catch (Exception e) {//TODO: handle exception
            }
            try {
                method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(mEditText, false);
            } catch (Exception e) {//TODO: handle exception

            }
        }
    }
}
