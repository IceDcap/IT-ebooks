package com.icedcap.itbookfinder.adapter;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Browser;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.icedcap.itbookfinder.R;
import com.icedcap.itbookfinder.help.Utils;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.network.FileDownloadManager;
import com.icedcap.itbookfinder.persistence.BookDatabaseHelper;
import com.icedcap.itbookfinder.persistence.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: doushuqi
 * Date: 16-3-29
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class DetailBookAdapter extends RecyclerView.Adapter<DetailBookAdapter.DetailBookViewHolder> {
    private SparseArray<String> mData;
    private static int[] sItems;
    private Context mContext;
    private Book mBook;


    static {
        sItems = new int[]{
                R.string.book_title,
                R.string.book_subtitle,
                R.string.book_description,
                R.string.book_author,
                R.string.book_isbn,
                R.string.book_year,
                R.string.book_page,
                R.string.book_publisher,
                R.string.book_download_url,
        };
    }

    public DetailBookAdapter(SparseArray<String> data, Book book) {
        mData = data;
        mBook = book;
    }

    @Override
    public DetailBookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new DetailBookViewHolder(LayoutInflater
                .from(mContext).inflate(R.layout.item_detail_book, parent, false));
    }

    @Override
    public void onBindViewHolder(final DetailBookViewHolder holder, int position) {
        holder.mTitle.setText(sItems[position]);
        if (mData != null) {
            final String content = mData.get(sItems[position], "");
            holder.mContent.setText(content);
            if (sItems[position] == R.string.book_download_url && !TextUtils.isEmpty(content)) {
                holder.mCardView.setClickable(true);
                holder.mCardView.setBackgroundResource(R.drawable.bg_ripple);
                holder.mCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String fake = "http://s.it-ebooks-api.info/3/php__mysql_the_missing_manual.jpg";
                        download(content);
                    }
                });


            }
        }
        holder.itemView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                if (v.getTop() >= 0 && v.getTop() < ((View) v.getParent()).getBottom()) {
                    holder.itemView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_item_enter));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return sItems.length;
    }


    private synchronized void download(String url) {
        // TODO: 16-4-7 不重复下载
        if (Constants.DOWNLOAD_IDS.containsKey(url)) {
            Log.e("ddd", "downloading.....");
            return;
        }
        long downloadId = BookDatabaseHelper.getDownloadId(mContext, mBook);

        final String filename = mData.get(sItems[0]) + ".pdf";
        final FileDownloadManager manager = FileDownloadManager.getManager(mContext);
        if (manager.isDownloaded(mContext, downloadId)
                && Utils.isFileExists(Utils.getAppExternalFolder(mContext), filename)
                ) {
            Toast.makeText(mContext, "This file has already exists!", Toast.LENGTH_SHORT).show();
            // TODO: 16-4-7 direct open file

            return;
        }

        long id = manager.downloadFile(mContext, url, filename);
        manager.setFlagToDatabase(mContext, mBook.getId(), id);
    }

    class DetailBookViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mTitle;
        private TextView mContent;

        public DetailBookViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
            mTitle = (TextView) itemView.findViewById(R.id.book_state_item);
            mContent = (TextView) itemView.findViewById(R.id.book_state_content);
        }
    }
}
