package com.icedcap.itbookfinder.model;

/**
 * Created by shuqi on 16-3-27.
 */
public interface JsonAttributes {
    String ERROR = "Error";
    String TIME = "Time";
    String TOTAL = "Total";
    String PAGE = "Page";
    String BOOKS = "Books";

    String BOOK_ID = "ID";
    String BOOK_TITLE = "Title";
    String BOOK_SUBTITLE = "SubTitle";
    String BOOK_DESCRIPTION = "Description";
    String BOOK_AUTHOR = "Author";
    String BOOK_IMAGE = "Image";
    String SEARCH_BOOK_ISBN = "isbn";
    String BOOK_ISBN = "ISBN";
    String BOOK_YEAR = "Year";
    String BOOK_PAGE = "Page";
    String BOOK_PUBLISHER = "Publisher";
    String BOOK_DOWNLOAD = "Download";

    String[] BOOK_DETAILS = new String[] {
            BOOK_TITLE,
            BOOK_SUBTITLE,
            BOOK_DESCRIPTION,
            BOOK_AUTHOR,
            BOOK_ISBN,
            BOOK_YEAR,
            BOOK_PAGE,
            BOOK_PUBLISHER,
            BOOK_DOWNLOAD,
    };

}
