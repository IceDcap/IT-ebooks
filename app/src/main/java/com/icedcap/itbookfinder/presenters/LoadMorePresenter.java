package com.icedcap.itbookfinder.presenters;

import android.os.AsyncTask;
import android.util.Log;

import com.icedcap.itbookfinder.help.BookJsonHelper;
import com.icedcap.itbookfinder.help.Utils;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.model.SearchResult;
import com.icedcap.itbookfinder.network.RequestManager;
import com.icedcap.itbookfinder.ui.interfaces.RefreshInterface;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: doushuqi
 * Date: 16-4-1
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class LoadMorePresenter extends NetBasePresenter<RefreshInterface<List<Book>>> {
    private static final String LOG_TAG = "LoadMorePresenter";
    private int mPendingPage;
    private List<Book> mBookList;

    public LoadMorePresenter(RefreshInterface v) {
        mView = v;
    }

    public void fetchMoreData(final String key, int total, List<Book> originList) {
        if (!canFetchNextPage(originList, total)) {
            mView.toast("No more data to load.");
            return;
        }
        mBookList = originList;
        mView.showLoading();
        new AsyncTask<Void, Void, List<Book>>() {
            @Override
            protected List<Book> doInBackground(Void... params) {
                try {
                    SearchResult result = getSearchResult(getJsonStr(key));
                    if (result != null) {
                        return result.getBooks();
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                }

                return null;
            }

            @Override
            protected void onPostExecute(List<Book> books) {
                fetchCompleted(books);
            }
        }.execute();

    }

    private void fetchCompleted(List<Book> result) {
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                mBookList.add(result.get(i));
            }
        }

        mView.fetchedData(mBookList);
        mView.hideLoading();
    }

    private String getJsonStr(String key) throws IOException {
        RequestManager requestManager = new RequestManager.Builder()
                .reqType(RequestManager.ReqType.SEARCH)
                .req(key + "/page/" + mPendingPage)
                .build();
        String json = requestManager.getResponse();
//        Log.e(LOG_TAG, json + "");
        return json;
    }

    private SearchResult getSearchResult(String json) throws JSONException {
        return BookJsonHelper.getSearchResult(json);
    }

    private boolean canFetchNextPage(List<Book> origin, int total) {
        mPendingPage = (int) Math.ceil(origin.size() / 10d) + 1;
        return mPendingPage <= total;
    }
}
