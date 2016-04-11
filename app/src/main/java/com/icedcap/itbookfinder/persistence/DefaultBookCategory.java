package com.icedcap.itbookfinder.persistence;

import android.support.annotation.RawRes;

import com.icedcap.itbookfinder.R;

/**
 * Created by shuqi on 16-4-4.
 */
public enum DefaultBookCategory {
    //TITLE, JSON,
    ANDROID("ANDROID", R.raw.android),
    IOS("IOS", R.raw.ios),
    HTTP("HTTP", R.raw.http);
//    JAVA("JAVA", R.raw.java),
//    SQL("SQL", R.raw.sql);


    @RawRes
    private int mJson;
    private CharSequence mName;

    DefaultBookCategory(CharSequence name, int json) {
        mName = name;
        mJson = json;
    }

    public void setJson(@RawRes int json) {
        mJson = json;
    }

    public void setName(CharSequence name) {
        mName = name;
    }

    public int getJson() {
        return mJson;
    }

    public CharSequence getName() {
        return mName;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
