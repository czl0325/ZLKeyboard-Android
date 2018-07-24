package com.zhaoliangchen.zlkeyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.util.List;

public class ZLKeyboardView extends KeyboardView {
    private Context mContext;
    private Keyboard mKeyBoard;

    private Keyboard digitalKeyboard;
    private Keyboard letterKeyboard;
    private Keyboard symbolKeyboard;

    private EditText mEditText;

    private boolean isUpper;

    public ZLKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public ZLKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        digitalKeyboard = new Keyboard(mContext, R.xml.zl_digit_keyboard);
        letterKeyboard = new Keyboard(mContext, R.xml.zl_letter_keyboard);
        symbolKeyboard = new Keyboard(mContext, R.xml.zl_symbol_keyboard);

        isUpper = false;

        setKeyboard(letterKeyboard);
        setPreviewEnabled(false);
        setOnKeyboardActionListener(listener);
    }

    public void setEditText(EditText editText) {
        this.mEditText = editText;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mKeyBoard = this.getKeyboard();

        List<Keyboard.Key> keys = null;
        if (mKeyBoard != null) {
            keys = mKeyBoard.getKeys();
        }

        if (keys != null) {
            int count = keys.size();
            for (int i = 0; i < count; i++) {
                if (keys.get(0).codes[0] == 47 && keys.size() == 12) {//数字键盘
                    drawKeyBackground(R.drawable.key_num_normal, canvas, keys.get(i));
                    drawText(canvas, keys.get(i), Color.WHITE);
                } else if (keys.get(0).label.toString().equals("q")) {
                    if (keys.get(i).label != null) {
                        if (keys.get(i).label.toString().equals("空格")) {
                            drawKeyBackground(R.drawable.key_space_normal, canvas, keys.get(i));
                            drawText(canvas, keys.get(i), Color.WHITE);
                        } else {
                            drawKeyBackground(R.drawable.key_normal, canvas, keys.get(i));
                            drawText(canvas, keys.get(i), Color.WHITE);
                        }
                    } else {
                        drawKeyBackground(R.drawable.key_mood_normal, canvas, keys.get(i));
                        if (keys.get(i).codes[0] == -1) {
                            if (isUpper) {
                                drawIcon(canvas, keys.get(i), R.drawable.key_icon_shift_highlighted);
                            } else {
                                drawIcon(canvas, keys.get(i), R.drawable.key_icon_shift_actived);
                            }
                        } else {
                            drawText(canvas, keys.get(i), Color.WHITE);
                        }
                    }
                } else {
                    if (keys.get(i).label != null) {
                        if (keys.get(i).label.toString().equals("空格")) {
                            drawKeyBackground(R.drawable.key_space_normal, canvas, keys.get(i));
                            drawText(canvas, keys.get(i), Color.WHITE);
                        } else {
                            drawKeyBackground(R.drawable.key_normal, canvas, keys.get(i));
                            drawText(canvas, keys.get(i), Color.WHITE);
                        }
                    } else {
                        drawKeyBackground(R.drawable.key_mood_normal, canvas, keys.get(i));
                        drawText(canvas, keys.get(i), Color.WHITE);
                    }
                }
            }
        }

    }

    private void drawKeyBackground(int drawableId, Canvas canvas, Keyboard.Key key) {
        Drawable keyBackground = mContext.getResources().getDrawable(drawableId);
        int[] drawableState = key.getCurrentDrawableState();
        keyBackground.setState(drawableState);
        keyBackground.setBounds(key.x + getPaddingTop(), key.y + getPaddingLeft(), key.x + key.width + getPaddingRight(), key.y + key.height + getPaddingBottom());
        keyBackground.draw(canvas);
    }


    private void drawText(Canvas canvas, Keyboard.Key key, int color) {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        paint.setColor(color);
        if (key.label != null) {
            String label = key.label.toString();

            Field field;

            if (label.length() > 1 && key.codes.length < 2) {
                int labelTextSize = 0;
                try {
                    field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                    field.setAccessible(true);
                    labelTextSize = (int) field.get(this);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                paint.setTextSize(labelTextSize);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                int keyTextSize = 0;
                try {
                    field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                    field.setAccessible(true);
                    keyTextSize = (int) field.get(this);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                paint.setTextSize(keyTextSize);
                paint.setTypeface(Typeface.DEFAULT);
            }

            paint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), bounds);
            canvas.drawText(key.label.toString(), key.x + key.width / 2 + getPaddingLeft(),
                    key.y + key.height / 2 + bounds.height() / 2 + getPaddingTop(), paint);
        } else if (key.icon != null) {
            key.icon.setBounds(key.x + (key.width - key.icon.getIntrinsicWidth() / 2) / 2 + getPaddingLeft(),
                    key.y + (key.height - key.icon.getIntrinsicHeight() / 2) / 2 + getPaddingTop(),
                    key.x + (key.width - key.icon.getIntrinsicWidth() / 2) / 2 + key.icon.getIntrinsicWidth() / 2 + getPaddingRight()
                    , key.y + (key.height - key.icon.getIntrinsicHeight() / 2) / 2 + key.icon.getIntrinsicHeight() / 2 + getPaddingBottom());

            key.icon.draw(canvas);
        }
    }

    private void drawIcon(Canvas canvas, Keyboard.Key key, int resId) {
        key.icon = getResources().getDrawable(resId);
        key.icon.setBounds(key.x + (key.width - key.icon.getIntrinsicWidth() / 2) / 2 + getPaddingLeft(),
                key.y + (key.height - key.icon.getIntrinsicHeight() / 2) / 2 + getPaddingTop(),
                key.x + (key.width - key.icon.getIntrinsicWidth() / 2) / 2 + key.icon.getIntrinsicWidth() / 2 + getPaddingRight()
                , key.y + (key.height - key.icon.getIntrinsicHeight() / 2) / 2 + key.icon.getIntrinsicHeight() / 2 + getPaddingBottom());
        key.icon.draw(canvas);
    }

    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void onPress(int keyCode) {

        }

        @Override
        public void onRelease(int keyCode) {
            if (keyCode == -1) {
                isUpper = !isUpper;
                changeUpper();
            }
        }

        @Override
        public void onKey(int keyCode, int[] keyCodes) {
            if (getKeyboard() == digitalKeyboard) {
                if (keyCode == 97) {
                    setKeyboard(letterKeyboard);
                } else if (keyCode == Keyboard.KEYCODE_DELETE) {
                    fallBack();
                } else if (keyCode == 35) {//符号
                    setKeyboard(symbolKeyboard);
                } else {
                    intputContent(keyCode);
                }
            } else if (getKeyboard() == letterKeyboard) {
                if (keyCode == 46) {//数字
                    setKeyboard(digitalKeyboard);
                } else if (keyCode == 35) {//符号
                    setKeyboard(symbolKeyboard);
                } else if (keyCode == Keyboard.KEYCODE_DELETE) {//回退
                    fallBack();
                } else if (keyCode == Keyboard.KEYCODE_SHIFT) {//切换大小写
                    //switchLettersCase();
                } else {
                    intputContent(keyCode);
                }
            } else {
                if (keyCode == 46) {//数字
                    setKeyboard(digitalKeyboard);
                } else if (keyCode == 97) {//字母
                    setKeyboard(letterKeyboard);
                } else if (keyCode == Keyboard.KEYCODE_DELETE) {//回退
                    fallBack();
                } else {
                    intputContent(keyCode);
                }
            }
        }

        @Override
        public void onText(CharSequence charSequence) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    /**
     * 回退
     */
    private void fallBack() {
        if (mEditText != null) {
            Editable editable = mEditText.getText();
            int start = mEditText.getSelectionStart();
            if (editable != null && editable.length() > 0) {
                if (start > 0) {
                    editable.delete(start - 1, start);
                }
            }
        }
    }

    /**
     * 输入内容
     */
    private void intputContent(int primaryCode) {
        if (mEditText != null) {
            Editable editable = mEditText.getText();
            int start = mEditText.getSelectionStart();
            editable.insert(start, Character.toString((char) primaryCode));
        }
    }

    /**
     * 键盘大小写切换
     */
    private void changeUpper() {
        List<Keyboard.Key> keylist = mKeyBoard.getKeys();
        if (isUpper) {
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                } else if (key.codes[0] == -1) {
                    key.icon = getResources().getDrawable(R.drawable.key_icon_shift_highlighted);
                }
            }
        } else {//小写切换大写
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                } else if (key.codes[0] == -1) {
                    key.icon = getResources().getDrawable(R.drawable.key_icon_shift_actived);
                }
            }
        }
    }

    private boolean isword(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }

}
