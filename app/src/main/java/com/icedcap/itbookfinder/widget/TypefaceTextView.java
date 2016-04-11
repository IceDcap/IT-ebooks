package com.icedcap.itbookfinder.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by shuqi on 16-4-3.
 */
public class TypefaceTextView extends TextView {
    public TypefaceTextView(Context context) {
        super(context);
        init(context);
    }

    public TypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        setTypeface(Typeface.createFromAsset(context.getAssets(), "Lobster-Regular.ttf"));
    }
}
