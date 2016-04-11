package com.icedcap.itbookfinder.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import com.icedcap.itbookfinder.R;

import java.lang.reflect.Constructor;

/**
 * Created by shuqi on 16-3-27.
 */
public class Book implements Parcelable {

    private long mId;
    private String mBookTitle;
    private String mBookSubtitle;
    private String mDescription;
    private String mAuthor;
    private String mIsbn;
    private String mYear;
    private String mPage;
    private String mPublisher;
    private String mIconUrl;
    private String mDownloadUrl;

    private SparseArray<String> mBookStates;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mBookTitle);
        dest.writeString(this.mBookSubtitle);
        dest.writeString(this.mDescription);
        dest.writeString(this.mAuthor);
        dest.writeString(this.mIsbn);
        dest.writeString(this.mYear);
        dest.writeString(this.mPage);
        dest.writeString(this.mPublisher);
        dest.writeString(this.mIconUrl);
        dest.writeString(this.mDownloadUrl);
    }

    public Book(long id, String bookTitle, String bookSubtitle,
                String description, String iconUrl, String isbn) {
        mId = id;
        mBookTitle = bookTitle;
        mBookSubtitle = bookSubtitle;
        mDescription = description;
        mIconUrl = iconUrl;
        mIsbn = isbn;
    }

    public Book(long id, String bookTitle, String bookSubtitle, String description,
                String author, String isbn, String year, String page,
                String publisher, String iconUrl, String downloadUrl) {
        mId = id;
        mBookTitle = bookTitle;
        mBookSubtitle = bookSubtitle;
        mDescription = description;
        mAuthor = author;
        mIsbn = isbn;
        mYear = year;
        mPage = page;
        mPublisher = publisher;
        mIconUrl = iconUrl;
        mDownloadUrl = downloadUrl;
    }

    protected Book(Parcel in) {
        this.mId = in.readLong();
        this.mBookTitle = in.readString();
        this.mBookSubtitle = in.readString();
        this.mDescription = in.readString();
        this.mAuthor = in.readString();
        this.mIsbn = in.readString();
        this.mYear = in.readString();
        this.mPage = in.readString();
        this.mPublisher = in.readString();
        this.mIconUrl = in.readString();
        this.mDownloadUrl = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public Book update(long id, String bookTitle, String bookSubtitle, String description,
                       String author, String isbn, String year, String page,
                       String publisher, String iconUrl, String downloadUrl) {
        mId = id;
        mBookTitle = bookTitle;
        mBookSubtitle = bookSubtitle;
        mDescription = description;
        mAuthor = author;
        mIsbn = isbn;
        mYear = year;
        mPage = page;
        mPublisher = publisher;
        mIconUrl = iconUrl;
        mDownloadUrl = downloadUrl;
        if (null == mBookStates) {
            mBookStates = new SparseArray<>();
        }
        mBookStates.append(R.string.book_title, mBookTitle);
        mBookStates.append(R.string.book_subtitle, mBookSubtitle);
        mBookStates.append(R.string.book_description, mDescription);
        mBookStates.append(R.string.book_author, mAuthor);
        mBookStates.append(R.string.book_isbn, mIsbn);
        mBookStates.append(R.string.book_year, mYear);
        mBookStates.append(R.string.book_page, mPage);
        mBookStates.append(R.string.book_publisher, mPublisher);
        mBookStates.append(R.string.book_download_url, mDownloadUrl);

        return this;
    }


    public long getId() {
        return mId;
    }

    public String getBookTitle() {
        return mBookTitle;
    }

    public String getBookSubtitle() {
        return mBookSubtitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getIsbn() {
        return mIsbn;
    }

    public String getYear() {
        return mYear;
    }

    public String getPage() {
        return mPage;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public String getIconUrl() {
        return mIconUrl;
    }

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public static Creator<Book> getCREATOR() {
        return CREATOR;
    }

    public SparseArray<String> getBookStates() {
        return mBookStates;
    }

    @Override
    public String toString() {
        return "id = " + this.mId +
                "\ntitle = " + this.mBookTitle +
                "\nsubtitle = " + this.mBookSubtitle +
                "\ndescription = " + this.mDescription +
                "\nauthor = " + this.mAuthor +
                "\nisbn = " + this.mIsbn +
                "\nyear = " + this.mYear +
                "\npage = " + this.mPage +
                "\npublisher = " + this.mPublisher +
                "\nimage = " + this.mIconUrl +
                "\ndownload = " + this.mDownloadUrl;
    }

}
