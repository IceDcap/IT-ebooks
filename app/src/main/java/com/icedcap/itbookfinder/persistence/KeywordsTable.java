package com.icedcap.itbookfinder.persistence;

/**
 * Created by shuqi on 16-4-2.
 */
public interface KeywordsTable {

    String NAME = "keywords";

    String COLUMN_KEYWORD = "keyword";
    String COLUMN_JSON = "value";

    String[] PROJECTION = new String[]{COLUMN_KEYWORD, COLUMN_JSON};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_KEYWORD + " TEXT PRIMARY KEY, "
            + COLUMN_JSON + " TEXT NOT NULL);";
}
