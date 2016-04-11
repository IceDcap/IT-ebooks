package com.icedcap.itbookfinder.help;

import android.content.Context;
import android.support.annotation.RawRes;
import android.util.Log;

import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.model.JsonAttributes;
import com.icedcap.itbookfinder.model.SearchResult;
import com.icedcap.itbookfinder.persistence.BookDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shuqi on 16-3-27.
 */
public class BookJsonHelper {
    private static final String LOG_TAG = "BookJsonHelper";

    public static SearchResult getSearchFromResource(Context context, @RawRes int id) throws IOException, JSONException {
        return getSearchResult(readBooksFromResource(context, id));
    }

    public static SearchResult getSearchFromDb(Context context, String key) throws IOException, JSONException {
        return getSearchResult(readBooksFromDb(context, key));
    }

    private static String readBooksFromResource(Context context, @RawRes int id) throws IOException {
        StringBuilder booksJson = new StringBuilder();
        InputStream rawBooks = context.getResources().openRawResource(id);
        BufferedReader reader = new BufferedReader(new InputStreamReader(rawBooks));
        String line;

        while ((line = reader.readLine()) != null) {
            booksJson.append(line);
        }
        return booksJson.toString();
    }

    private static String readBooksFromDb(Context context, String key) {
        return BookDatabaseHelper.getJsonStrFromDb(context, key);

    }

    public static Book getBookFromJson(String json, Book book) throws JSONException {
        if (null == json || json.length() == 0) {
            return null;
        }
        JSONObject details = new JSONObject(json);

        final String code = details.getString(JsonAttributes.ERROR);
        if (!code.equals("0")) {
            return null;
        }
        final double time = details.getDouble(JsonAttributes.TIME);

        final long id = details.getLong(JsonAttributes.BOOK_ID);
        final String title = details.getString(JsonAttributes.BOOK_TITLE);
        final String subtitle = details.getString(JsonAttributes.BOOK_SUBTITLE);
        final String description = details.getString(JsonAttributes.BOOK_DESCRIPTION);
        final String author = details.getString(JsonAttributes.BOOK_AUTHOR);
        final String isbn = details.getString(JsonAttributes.BOOK_ISBN);
        final String year = details.getString(JsonAttributes.BOOK_YEAR);
        final String page = details.getString(JsonAttributes.BOOK_PAGE);
        final String publisher = details.getString(JsonAttributes.BOOK_PUBLISHER);
        final String image = details.getString(JsonAttributes.BOOK_IMAGE);
        final String download = details.getString(JsonAttributes.BOOK_DOWNLOAD);

        return book.update(id, title, subtitle, description, author, isbn, year, page, publisher, image, download);
    }

    public static SearchResult getSearchResult(String json) throws JSONException {
        JSONObject searchResult = new JSONObject(json);

        String code = searchResult.getString(JsonAttributes.ERROR);
        if (!code.equals("0")) {
            return null;
        }

        double time = searchResult.getDouble(JsonAttributes.TIME);
        String total = searchResult.getString(JsonAttributes.TOTAL);
        long page = searchResult.getLong(JsonAttributes.PAGE);
        JSONArray jsonBook = searchResult.getJSONArray(JsonAttributes.BOOKS);
        List<Book> books = new ArrayList<>(jsonBook.length());
        for (int i = 0; i < jsonBook.length(); i++) {
            JSONObject bookObj = jsonBook.getJSONObject(i);
            long bookId = bookObj.getLong(JsonAttributes.BOOK_ID);
            String bookTitle = bookObj.getString(JsonAttributes.BOOK_TITLE);
            String bookSubTitle = "";
            try {
                bookSubTitle = bookObj.getString(JsonAttributes.BOOK_SUBTITLE);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.toString());
            }

            String bookDescription = bookObj.getString(JsonAttributes.BOOK_DESCRIPTION);
            String bookImage = bookObj.getString(JsonAttributes.BOOK_IMAGE);
            String bookIsbn = bookObj.getString(JsonAttributes.SEARCH_BOOK_ISBN);
            Book book = new Book(bookId, bookTitle, bookSubTitle, bookDescription, bookImage, bookIsbn);
            books.add(book);
        }

        return new SearchResult(code, time, total, page, books);
    }


}
