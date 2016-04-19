package com.icedcap.itbookfinder.help;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v4.content.SharedPreferencesCompat;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.icedcap.itbookfinder.R;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.ui.BookDetailActivity;

import java.io.File;

/**
 * Created by shuqi on 16-4-2.
 */
public class Utils {
    private static final String SHARE_ELEMENT_NAME = "book_icon";

    public static boolean isFileExists(String path, String name) {
        final File file = new File(path, name);
        return file.exists();
    }

    public static String getAppExternalFolder(Context context) {
        if (existSDCard()) {
            return context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        }

        return null;
    }

    private static boolean existSDCard() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getDataPath(Context c) {

        return Environment.getDataDirectory().getAbsolutePath();
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return wifiNetworkInfo.isConnected();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    public static Typeface getTypefaceFromAsset(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "Lobster-Regular.ttf");
    }

    public static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int pxTodp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static Point getDisplayPoint(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        final Point outSize = new Point();
        display.getSize(outSize);
        return outSize;
    }


    public static void setTextViewCharSequence(Activity context, @IdRes int id, String text) {
        text = text.replace("\\n", "\n");
        text = text.replace("\\u00", "...");
        ((TextView) context.findViewById(id)).setText(text);
    }


    public static void openFolder(Context c, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri, "resource/*");
        if (intent.resolveActivityInfo(c.getPackageManager(), 0) != null) {
            c.startActivity(intent);
        } else {
            // if you reach this place, it means there is no any file
            // explorer app installed on your device
        }
//        c.startActivity(Intent.createChooser(intent, "Open folder"));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void startBookDetailWithTransition(Activity activity, View icon, Book book) {
        View toolBar = activity.findViewById(R.id.main_appbar);
        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity,
                new Pair<>(icon, SHARE_ELEMENT_NAME)
//                , new Pair<>(toolBar, "mainToolbar")
        );
        ActivityOptions sceneTransitionAnimation = ActivityOptions.makeSceneTransitionAnimation(activity, pairs);

        activity.startActivity(BookDetailActivity.getStartIntent(activity.getBaseContext(), book), sceneTransitionAnimation.toBundle());
    }

    public static Bitmap drawableToBitamp(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

}
