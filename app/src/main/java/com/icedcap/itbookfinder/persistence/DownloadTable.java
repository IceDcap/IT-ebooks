package com.icedcap.itbookfinder.persistence;

/**
 * Author: doushuqi
 * Date: 16-4-7
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public interface DownloadTable {
    String NAME = "download";

    String COLUMN_DOWNLOAD_ID = "downloadId";
    String COLUMN_TITLE = "title";
    String COLUMN_URI = "uri";
    String COLUMN_FILENAME = "localFilename";
    String COLUMN_TOTAL_SIZE = "totalSize";

    String[] PROJECTION = new String[]{COLUMN_DOWNLOAD_ID, COLUMN_TITLE, COLUMN_URI, COLUMN_FILENAME, COLUMN_TOTAL_SIZE};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_DOWNLOAD_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_TITLE + " TEXT, "
            + COLUMN_URI + " TEXT, "
            + COLUMN_FILENAME + " TEXT, "
            + COLUMN_TOTAL_SIZE + " TEXT);";
}
