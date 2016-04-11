package com.icedcap.itbookfinder.network;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.icedcap.itbookfinder.broadcast.DownloadBroadcast;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.persistence.BookDatabaseHelper;
import com.icedcap.itbookfinder.persistence.Constants;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Author: doushuqi
 * Date: 16-4-7
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class FileDownloadManager {
    private Context mContext;
    private long downloadReference;
    private DownloadManager mDownloadManager;

    private static FileDownloadManager sManager;

    private FileDownloadManager(Context context) {
        mContext = context;
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static FileDownloadManager getManager(Context context) {
        if (null == sManager) {
            sManager = new FileDownloadManager(context);
        }
        return sManager;
    }


    public long downloadFile(Context context, String url, String fileName) {
        DownloadManager.Request r = new DownloadManager.Request(Uri.parse(url));

        r.addRequestHeader("referer", "http://it-ebooks.info/");

        // This put the download in the same Download dir the browser uses
        // r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        r.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName);

        // When downloading music and videos they will be listed in the player
        // (Seems to be available since Honeycomb only)
        r.allowScanningByMediaScanner();

        // Notify user when download is completed
        // (Seems to be available since Honeycomb only)
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Start download
        downloadReference = mDownloadManager.enqueue(r);
        Constants.DOWNLOAD_IDS.put(url, downloadReference);
        return downloadReference;
    }

    public void setFlagToDatabase(Context c, long bookId, long downloadId) {
        BookDatabaseHelper.update(c, bookId, downloadId);
    }

    public boolean isDownloaded(Context c, long downloadId) {
        return null != BookDatabaseHelper.getDownload(c, downloadId);
    }

    public boolean isDownloading(Context context, String url, String filename) {

        return false;

    }

    public boolean downloaded(Context context, long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = mDownloadManager.query(query);
        if (null == cursor || cursor.getCount()==0){
            return false;
        }
        cursor.moveToFirst();


        return true;
    }


}
