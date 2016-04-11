package com.icedcap.itbookfinder.broadcast;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.icedcap.itbookfinder.persistence.BookDatabaseHelper;
import com.icedcap.itbookfinder.persistence.Constants;

/**
 * Author: doushuqi
 * Date: 16-4-7
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class DownloadBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //check if the broadcast message is for our Enqueued download
        long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

        for (String key : Constants.DOWNLOAD_IDS.keySet()) {
            if (referenceId == Constants.DOWNLOAD_IDS.get(key)) {
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(referenceId);
                final Cursor cursor = downloadManager.query(query);
                saveToDatabase(context, cursor);
                Constants.DOWNLOAD_IDS.remove(key);
                return;
            }
        }

    }


    private void saveToDatabase(Context c, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            long id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
            String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
            String localFilename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
            String totalSize = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
            String status = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            BookDatabaseHelper.insertDownload(c, id, localFilename, uri, title, totalSize);
        }
    }
}
