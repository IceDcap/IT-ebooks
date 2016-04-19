package com.icedcap.itbookfinder.presenters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.icedcap.itbookfinder.help.BookJsonHelper;
import com.icedcap.itbookfinder.help.SharedPreferencesHelper;
import com.icedcap.itbookfinder.help.Utils;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.model.CacheBook;
import com.icedcap.itbookfinder.network.RequestManager;
import com.icedcap.itbookfinder.persistence.BookDatabaseHelper;
import com.icedcap.itbookfinder.ui.interfaces.DetailBookInterface;

/**
 * Author: doushuqi
 * Date: 16-4-19
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class LoadDetailPresenter extends NetBasePresenter<DetailBookInterface<Book>> {
    private static final String TAG = "LoadDetailPresenter";
    private Book mBook;
    private Context mContext;

    public LoadDetailPresenter(Context context, DetailBookInterface bookInterface, Book book) {
        mContext = context;
        mBook = book;
        mView = bookInterface;
    }

    public void fetchData() {
        if (mBook == null) {
            mView.toast("book is null!");
            mView.forceExit();
            return;
        }
        if (!getBookFromDatabase()) {
            new AsyncTask<Void, CharSequence, Book>() {
                @Override
                protected Book doInBackground(Void... params) {
                    if (!Utils.isNetworkConnected(mContext)){
                        return null;
                    }
                    Book book = null;
                    try {
                        String response = new RequestManager.Builder()
                                .req(String.valueOf(mBook.getId()))
                                .reqType(RequestManager.ReqType.BOOK)
                                .build()
                                .getResponse();
                        book = BookJsonHelper.getBookFromJson(response.replace("\\", "\\\\"), mBook);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                        publishProgress(e.toString());
                    }
                    return book;
                }

                @Override
                protected void onProgressUpdate(CharSequence... values) {
                    mView.toast(values[0]);
                    mView.forceExit();
                }

                @Override
                protected void onPostExecute(Book book) {
                    if (null == book) {
                        mView.toast("Please check network!");
                        mView.forceExit();
                        return;
                    }
                    if (SharedPreferencesHelper.getAutoCache(mContext)) {
                        BookDatabaseHelper.insert(mContext, book, false);
                    }
                    mView.fetchedData(book);
                }
            }.execute();
        }
    }

    private boolean getBookFromDatabase() {
        CacheBook cacheBook = BookDatabaseHelper.getCacheBook(mContext, mBook);
        if (cacheBook == null) {
            return false;
        } else {
            mBook = cacheBook.mBook;
            mView.isFavorite(cacheBook.isFavorite);
            mView.isDownloaded(cacheBook.isDownload);
            mView.fetchedData(mBook);
            return true;
        }
    }
}
