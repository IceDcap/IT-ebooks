package com.icedcap.itbookfinder.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: doushuqi
 * Date: 16-4-7
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class Download implements Parcelable {

    private Long mDownloadId;
    private String mUri;
    private String mLocalFilename;
    private String mTitle;
    private String mTotalSize;

    public Download(Long downloadId, String uri, String localFilename, String title, String totalSize) {
        mDownloadId = downloadId;
        mUri = uri;
        mLocalFilename = localFilename;
        mTitle = title;
        mTotalSize = totalSize;
    }

    public Long getDownloadId() {
        return mDownloadId;
    }

    public String getUri() {
        return mUri;
    }

    public String getLocalFilename() {
        return mLocalFilename;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTotalSize() {
        return mTotalSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mDownloadId);
        dest.writeString(this.mUri);
        dest.writeString(this.mLocalFilename);
        dest.writeString(this.mTitle);
        dest.writeString(this.mTotalSize);
    }

    public Download() {
    }

    protected Download(Parcel in) {
        this.mDownloadId = (Long) in.readValue(Long.class.getClassLoader());
        this.mUri = in.readString();
        this.mLocalFilename = in.readString();
        this.mTitle = in.readString();
        this.mTotalSize = in.readString();
    }

    public static final Creator<Download> CREATOR = new Creator<Download>() {
        @Override
        public Download createFromParcel(Parcel source) {
            return new Download(source);
        }

        @Override
        public Download[] newArray(int size) {
            return new Download[size];
        }
    };
}
