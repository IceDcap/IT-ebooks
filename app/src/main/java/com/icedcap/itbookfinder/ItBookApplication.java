package com.icedcap.itbookfinder;

import android.app.Application;
import android.app.DownloadManager;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import com.icedcap.itbookfinder.broadcast.DownloadBroadcast;

/**
 * Created by shuqi on 16-4-2.
 */
public class ItBookApplication extends Application {
    public static String jsonFilePath;

    @Override
    public void onCreate() {
        super.onCreate();

        jsonFilePath = getFilesDir().getPath();
//        registerReceiver(new DownloadBroadcast(), new IntentFilter(
//                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
