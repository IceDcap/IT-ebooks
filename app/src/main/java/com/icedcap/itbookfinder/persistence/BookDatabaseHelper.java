package com.icedcap.itbookfinder.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.tech.NfcA;
import android.util.Log;

import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.model.CacheBook;
import com.icedcap.itbookfinder.model.Download;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: doushuqi
 * Date: 16-3-31
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class BookDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "BookDatabaseHelper";
    private static final String DB_NAME = "book.db";
    private static final int DB_VERSION = 1;
    private static BookDatabaseHelper sHelper;

    private BookDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static BookDatabaseHelper getInstance(Context context) {
        if (null == sHelper) {
            sHelper = new BookDatabaseHelper(context);
        }
        return sHelper;
    }

    public static Download getDownload(Context c, long id) {
        SQLiteDatabase database = getReadableDatabase(c);
        Cursor cursor = database.query(DownloadTable.NAME, null, DownloadTable.COLUMN_DOWNLOAD_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Download download = null;
        if (null != cursor && cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                final String filename = cursor.getString(cursor.getColumnIndex(DownloadTable.COLUMN_FILENAME));
                final String uri = cursor.getString(cursor.getColumnIndex(DownloadTable.COLUMN_URI));
                final String title = cursor.getString(cursor.getColumnIndex(DownloadTable.COLUMN_TITLE));
                final String totalSize = cursor.getString(cursor.getColumnIndex(DownloadTable.COLUMN_TOTAL_SIZE));
                download = new Download(id, uri, filename, title, totalSize);
            } finally {
                cursor.close();
            }

        }
        return download;
    }

    public static long insertDownload(Context c, long id, String filename, String uri, String title, String totalSize) {
        SQLiteDatabase database = getWritableDatabase(c);
        ContentValues cv = new ContentValues();
        cv.put(DownloadTable.COLUMN_DOWNLOAD_ID, id);
        cv.put(DownloadTable.COLUMN_FILENAME, filename);
        cv.put(DownloadTable.COLUMN_URI, uri);
        cv.put(DownloadTable.COLUMN_TITLE, title);
        cv.put(DownloadTable.COLUMN_TOTAL_SIZE, totalSize);
        return database.insert(DownloadTable.NAME, null, cv);
    }


    ///////////////////////////////////////////////////////////////////////////////

    public static long insertKeyword(Context context, String key, String value) {
        final ContentValues cv = new ContentValues();
        cv.put(KeywordsTable.COLUMN_KEYWORD, key.toLowerCase());
        cv.put(KeywordsTable.COLUMN_JSON, value);
        return getWritableDatabase(context).insert(KeywordsTable.NAME, null, cv);
    }

    public static long updateKeyword(Context context, String key, String value) {
        final ContentValues cv = new ContentValues();
        cv.put(KeywordsTable.COLUMN_KEYWORD, key.toLowerCase());
        cv.put(KeywordsTable.COLUMN_JSON, value);
        return getWritableDatabase(context).update(KeywordsTable.NAME, cv,
                KeywordsTable.COLUMN_KEYWORD + "=?", new String[]{key});
    }

    public static boolean checkIfHadExists(Context context, String key) {
        final Cursor cursor = getValue(context, key);
        return isCursorNotNull(cursor);
    }

    public static String getJsonStrFromDb(Context c, String key) {
        final Cursor cursor = getValue(c, key);
        if (isCursorNotNull(cursor)) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(KeywordsTable.COLUMN_JSON));
        }
        return null;
    }


    private static Cursor getValue(Context context, String key) {
        SQLiteDatabase db = getReadableDatabase(context);
        return db.query(KeywordsTable.NAME, null, KeywordsTable.COLUMN_KEYWORD + "=?",
                new String[]{key.toLowerCase()}, null, null, null);

    }

    private static boolean isCursorNotNull(Cursor c) {
        return c != null && c.getCount() > 0;
    }

    ///////////////////////////////////////////////////////////////////////////////////////


    public static long getDownloadId(Context c, Book book) {
        SQLiteDatabase database = getReadableDatabase(c);

        Cursor cursor = database.query(BookTable.NAME, new String[]{BookTable.COLUMN_DOWNLOAD_ID}, BookTable.COLUMN_BOOK_ID + "=?",
                new String[]{String.valueOf(book.getId())}, null, null, null);

        long result = -1;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getLong(cursor.getColumnIndex(BookTable.COLUMN_DOWNLOAD_ID));
            cursor.close();
        }
        return result;
    }

    public static void favorite(Context context, Book book, boolean isFavorite) {
        if (getBook(context, book.getId()) == null) {
            insert(context, book, isFavorite);
        } else {
            update(context, book, isFavorite);
        }
    }

    public static long insert(Context context, Book book, boolean isFavorite) {
        final SQLiteDatabase db = getWritableDatabase(context);
        final ContentValues cv = new ContentValues();
        cv.put(BookTable.COLUMN_BOOK_ID, book.getId());
        cv.put(BookTable.COLUMN_TITLE, book.getBookTitle());
        cv.put(BookTable.COLUMN_SUBTITLE, book.getBookSubtitle());
        cv.put(BookTable.COLUMN_DESCRIPTION, book.getDescription());
        cv.put(BookTable.COLUMN_AUTHOR, book.getAuthor());
        cv.put(BookTable.COLUMN_ISBN, book.getIsbn());
        cv.put(BookTable.COLUMN_YEAR, book.getYear());
        cv.put(BookTable.COLUMN_PAGE, book.getPage());
        cv.put(BookTable.COLUMN_PUBLISHER, book.getPublisher());
        cv.put(BookTable.COLUMN_IMAGE, book.getIconUrl());
        cv.put(BookTable.COLUMN_DOWNLOAD, book.getDownloadUrl());
        cv.put(BookTable.COLUMN_ISFAVORITE, isFavorite ? 1 : 0);
        return db.insert(BookTable.NAME, null, cv);
    }


    public static long update(Context context, Book book, boolean isFavorite) {
        final SQLiteDatabase db = getWritableDatabase(context);
        final ContentValues cv = new ContentValues();
        cv.put(BookTable.COLUMN_ISFAVORITE, isFavorite ? 1 : 0);
        return db.update(BookTable.NAME, cv, BookTable.COLUMN_BOOK_ID + "=?", new String[]{String.valueOf(book.getId())});
    }

    public static long update(Context context, long bookId, long downloadId) {
        final SQLiteDatabase db = getWritableDatabase(context);
        final ContentValues cv = new ContentValues();
        cv.put(BookTable.COLUMN_DOWNLOAD_ID, downloadId);
        return db.update(BookTable.NAME, cv, BookTable.COLUMN_BOOK_ID + "=?", new String[]{String.valueOf(bookId)});
    }

    public static CacheBook getCacheBook(Context c, Book book) {
        final SQLiteDatabase database = getReadableDatabase(c);
        Cursor cursor = database.query(BookTable.NAME, BookTable.PROJECTION, BookTable.COLUMN_BOOK_ID + "=?", new String[]{String.valueOf(book.getId())}, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        final long id = Long.decode(cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_BOOK_ID)));
        final String title = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_TITLE));
        final String subtitle = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_SUBTITLE));
        final String description = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_DESCRIPTION));
        final String author = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_AUTHOR));
        final String isbn = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_ISBN));
        final String year = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_YEAR));
        final String page = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_PAGE));
        final String publisher = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_PUBLISHER));
        final String iconUrl = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_IMAGE));
        final String downloadUrl = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_DOWNLOAD));
        Book b = book.update(id, title, subtitle, description, author, isbn, year, page, publisher, iconUrl, downloadUrl);
        final boolean isFavorite = cursor.getInt(cursor.getColumnIndex(BookTable.COLUMN_ISFAVORITE)) == 1;
        final boolean isDownload = cursor.getInt(cursor.getColumnIndex(BookTable.COLUMN_ISDOWNLOAD)) == 1;
        final CacheBook cacheBook = new CacheBook();
        cacheBook.mBook = b;
        cacheBook.isFavorite = isFavorite;
        cacheBook.isDownload = isDownload;
        return cacheBook;
    }

    public static List<Book> getCaches(Context context) {
        final SQLiteDatabase db = getReadableDatabase(context);
        Cursor cursor = db.query(BookTable.NAME, BookTable.BOOK_PROJECTION, null, null, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        List<Book> books = new ArrayList<>();
        cursor.moveToFirst();
        do {
            final long bookId = Long.decode(cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_BOOK_ID)));
            final String title = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_TITLE));
            final String subtitle = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_SUBTITLE));
            final String description = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_DESCRIPTION));
            final String author = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_AUTHOR));
            final String isbn = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_ISBN));
            final String year = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_YEAR));
            final String page = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_PAGE));
            final String publisher = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_PUBLISHER));
            final String iconUrl = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_IMAGE));
            final String downloadUrl = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_DOWNLOAD));
            final Book book = new Book(bookId, title, subtitle, description, author, isbn, year, page, publisher, iconUrl, downloadUrl);
            books.add(book);
        } while (cursor.moveToNext());
        return books;
    }

    public static List<Book> getFavorites(Context context) {
        final SQLiteDatabase db = getReadableDatabase(context);
        Cursor cursor = db.query(BookTable.NAME, BookTable.BOOK_PROJECTION, BookTable.COLUMN_ISFAVORITE + "=?", new String[]{"1"}, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        List<Book> books = new ArrayList<>();
        cursor.moveToFirst();
        do {
            final long bookId = Long.decode(cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_BOOK_ID)));
            final String title = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_TITLE));
            final String subtitle = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_SUBTITLE));
            final String description = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_DESCRIPTION));
            final String author = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_AUTHOR));
            final String isbn = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_ISBN));
            final String year = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_YEAR));
            final String page = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_PAGE));
            final String publisher = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_PUBLISHER));
            final String iconUrl = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_IMAGE));
            final String downloadUrl = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_DOWNLOAD));
            final Book book = new Book(bookId, title, subtitle, description, author, isbn, year, page, publisher, iconUrl, downloadUrl);
            books.add(book);
        } while (cursor.moveToNext());
        return books;
    }

    public static void clearAllFavorites(Context context) {
        final SQLiteDatabase database = getWritableDatabase(context);
        ContentValues cv = new ContentValues();
        cv.put(BookTable.COLUMN_ISFAVORITE, 0);
        database.update(BookTable.NAME, cv, BookTable.COLUMN_ISFAVORITE + "=?", new String[]{"1"});
    }

    private static Book getBook(Context context, long bookId) {
        final SQLiteDatabase database = getReadableDatabase(context);
        Cursor cursor = database.query(BookTable.NAME, BookTable.PROJECTION, BookTable.COLUMN_BOOK_ID + "=?", new String[]{String.valueOf(bookId)}, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        final String title = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_TITLE));
        final String subtitle = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_SUBTITLE));
        final String description = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_DESCRIPTION));
        final String author = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_AUTHOR));
        final String isbn = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_ISBN));
        final String year = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_YEAR));
        final String page = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_PAGE));
        final String publisher = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_PUBLISHER));
        final String iconUrl = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_IMAGE));
        final String downloadUrl = cursor.getString(cursor.getColumnIndex(BookTable.COLUMN_DOWNLOAD));
        return new Book(bookId, title, subtitle, description, author, isbn, year, page, publisher, iconUrl, downloadUrl);
    }


    private static SQLiteDatabase getWritableDatabase(Context context) {
        return getInstance(context).getWritableDatabase();
    }

    private static SQLiteDatabase getReadableDatabase(Context context) {
        return getInstance(context).getReadableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BookTable.CREATE);
        db.execSQL(KeywordsTable.CREATE);
        db.execSQL(DownloadTable.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
