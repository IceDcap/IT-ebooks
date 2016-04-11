package com.icedcap.itbookfinder.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by shuqi on 16-3-27.
 */
public final class SearchResult implements Parcelable {

    private String mErrorCode;
    private double mTime;
    private String mTotal;
    private long mPage;
    private List<Book> mBooks;

    public SearchResult(String errorCode, double time, String total, long page, List<Book> books) {
        mErrorCode = errorCode;
        mTime = time;
        mTotal = total;
        mPage = page;
        mBooks = books;
    }

    public String getErrorCode() {
        return mErrorCode;
    }

    public double getTime() {
        return mTime;
    }

    public String getTotal() {
        return mTotal;
    }

    public void setPage(long page) {
        mPage = page;
    }

    public long getPage() {
        return mPage;
    }

    public List<Book> getBooks() {
        return mBooks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mErrorCode);
        dest.writeDouble(this.mTime);
        dest.writeString(this.mTotal);
        dest.writeLong(this.mPage);
        dest.writeTypedList(mBooks);
    }

    protected SearchResult(Parcel in) {
        this.mErrorCode = in.readString();
        this.mTime = in.readDouble();
        this.mTotal = in.readString();
        this.mPage = in.readLong();
        this.mBooks = in.createTypedArrayList(Book.CREATOR);
    }

    public static final Creator<SearchResult> CREATOR = new Creator<SearchResult>() {
        @Override
        public SearchResult createFromParcel(Parcel source) {
            return new SearchResult(source);
        }

        @Override
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };
}
