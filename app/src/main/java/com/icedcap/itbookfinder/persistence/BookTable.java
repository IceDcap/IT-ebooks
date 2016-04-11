package com.icedcap.itbookfinder.persistence;

import android.provider.BaseColumns;

/**
 * Author: doushuqi
 * Date: 16-3-31
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public interface BookTable {

    String NAME = "book";

//    String COLUMN_ID = BaseColumns._ID;
    String COLUMN_BOOK_ID = "id";
    String COLUMN_TITLE = "title";
    String COLUMN_SUBTITLE = "subtitle";
    String COLUMN_DESCRIPTION = "description";
    String COLUMN_AUTHOR = "author";
    String COLUMN_ISBN = "isbn";
    String COLUMN_YEAR = "year";
    String COLUMN_PAGE = "page";
    String COLUMN_PUBLISHER = "publisher";
    String COLUMN_IMAGE = "image";
    String COLUMN_DOWNLOAD = "download";
    String COLUMN_ISFAVORITE = "isFavorite";
    String COLUMN_ISDOWNLOAD = "isDownload";
    String COLUMN_DOWNLOAD_ID = "downloadId";
    String COLUMN_PATH = "path";

    String[] PROJECTION = new String[]{COLUMN_BOOK_ID,
            COLUMN_TITLE, COLUMN_SUBTITLE, COLUMN_DESCRIPTION,
            COLUMN_AUTHOR, COLUMN_ISBN, COLUMN_YEAR, COLUMN_PAGE,
            COLUMN_PUBLISHER, COLUMN_IMAGE, COLUMN_DOWNLOAD,
            COLUMN_ISFAVORITE, COLUMN_ISDOWNLOAD, COLUMN_DOWNLOAD_ID, COLUMN_PATH};

    String[] BOOK_PROJECTION = new String[]{COLUMN_BOOK_ID,
            COLUMN_TITLE, COLUMN_SUBTITLE, COLUMN_DESCRIPTION,
            COLUMN_AUTHOR, COLUMN_ISBN, COLUMN_YEAR, COLUMN_PAGE,
            COLUMN_PUBLISHER, COLUMN_IMAGE, COLUMN_DOWNLOAD};

    String CREATE = "CREATE TABLE " + NAME + " ("
//            + COLUMN_ID + " TEXT PRIMARY KEY, "
            + COLUMN_BOOK_ID + " TEXT PRIMARY KEY, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_SUBTITLE + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
            + COLUMN_AUTHOR + " TEXT NOT NULL, "
            + COLUMN_ISBN + " TEXT NOT NULL, "
            + COLUMN_YEAR + " TEXT NOT NULL, "
            + COLUMN_PAGE + " TEXT NOT NULL, "
            + COLUMN_PUBLISHER + " TEXT NOT NULL, "
            + COLUMN_IMAGE + " TEXT NOT NULL, "
            + COLUMN_DOWNLOAD + " TEXT NOT NULL, "
            + COLUMN_ISFAVORITE + " INTEGER DEFAULT 0, "
            + COLUMN_ISDOWNLOAD + " INTEGER DEFAULT 0, "
            + COLUMN_DOWNLOAD_ID + " INTEGER, "
            + COLUMN_PATH + " TEXT);";
}
