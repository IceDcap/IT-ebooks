package com.icedcap.itbookfinder.presenters;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.icedcap.itbookfinder.ItBookApplication;
import com.icedcap.itbookfinder.help.BookJsonHelper;
import com.icedcap.itbookfinder.help.Utils;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.model.SearchResult;
import com.icedcap.itbookfinder.network.RequestManager;
import com.icedcap.itbookfinder.persistence.BookDatabaseHelper;
import com.icedcap.itbookfinder.persistence.Constants;
import com.icedcap.itbookfinder.ui.interfaces.SearchInterface;

import java.io.IOException;
import java.util.List;

/**
 * Created by shuqi on 16-4-2.
 */
public class SearchPresenter extends NetBasePresenter<SearchInterface<SearchResult>> {
    private static final String LOG_TAG = "SearchPresenter";
    private String mJsonStr;
    private String mKeyword;
    private Context mContext;


    public SearchPresenter(Context context, String keyword, SearchInterface view) {
        mKeyword = keyword;
        mContext = context;
        mView = view;
    }

    public void fetchSearchResult() {
        mView.showSwiperefreshlayout();
        new AsyncTask<Void, Void, SearchResult>() {
            @Override
            protected SearchResult doInBackground(Void... params) {
                SearchResult result = null;

                try {
                    if (isKeywordHadSaved()) {
                        if (Constants.DEBUG) {
                            Log.d(LOG_TAG, "fetch data from db");
                        }
                        result = BookJsonHelper.getSearchFromDb(mContext, mKeyword);
                    } else if (Utils.isWifiConnected(mContext)){
                        if (Constants.DEBUG) {
                            Log.d(LOG_TAG, "fetch data from network");
                        }
                        final String json = getJsonStr();
                        result = BookJsonHelper.getSearchResult(json);
                    } else {
                        mView.toast("please check network!");
                    }

                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                }

                return result;
            }

            @Override
            protected void onPostExecute(SearchResult result) {
                mView.fetchedData(result);
                mView.hideSwiperefreshlayout();
            }
        }.execute();
    }

    public void saveToLocal() {
        if (isKeywordHadSaved()) {
            mView.toast("This tab has already added!");
            return;
        }
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if (!TextUtils.isEmpty(mJsonStr)) {
                    return writeJsonToDb(mJsonStr);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    mView.toast("Success to save this tab!");
                } else {
                    mView.toast("Failed to save this tab!");
                }
            }
        }.execute();
    }

    private boolean writeJsonToDb(String json) {
        if (isKeywordHadSaved()) {
            return BookDatabaseHelper.updateKeyword(mContext, mKeyword, json) > -1;
        } else {
            return BookDatabaseHelper.insertKeyword(mContext, mKeyword, json) > -1;
        }
    }

    private String getJsonStr() throws IOException {
        RequestManager manager = new RequestManager.Builder()
                .reqType(RequestManager.ReqType.SEARCH)
                .req(mKeyword)
                .build();
        mJsonStr = manager.getResponse();
        return mJsonStr;
    }

    private boolean isKeywordHadSaved() {
        return BookDatabaseHelper.checkIfHadExists(mContext, mKeyword);
    }
}
