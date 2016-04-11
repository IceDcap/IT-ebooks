package com.icedcap.itbookfinder.presenters;

import android.content.Context;

import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.persistence.BookDatabaseHelper;
import com.icedcap.itbookfinder.ui.interfaces.LoadFavoritesInterface;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author: doushuqi
 * Date: 16-4-5
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class LoadFavoritePresenter<T extends LoadFavoritesInterface> {
    private T mView;
    private Context mContext;

    public LoadFavoritePresenter(Context context, T view) {
        mView = view;
        mContext = context;
    }

    public void fetchFavorites() {
        mView.showLoading();
        final List<Book> books = BookDatabaseHelper.getFavorites(mContext);
        mView.hideLoading();

        if (null == books || books.size() == 0) {
            mView.showEmptyPage();
        } else {
            Collections.sort(books, new BookComparator());
            mView.fetchedData(books);
        }
    }

    static class BookComparator implements Comparator<Book> {

        @Override
        public int compare(Book lhs, Book rhs) {
            return lhs.getBookTitle().compareToIgnoreCase(rhs.getBookTitle());
        }
    }


}
