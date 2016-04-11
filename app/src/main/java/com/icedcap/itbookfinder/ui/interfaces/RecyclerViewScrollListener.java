package com.icedcap.itbookfinder.ui.interfaces;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by shuqi on 16-4-2.
 */
public abstract class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private boolean mLoadMoreIsAtBottom;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE && mLoadMoreIsAtBottom) {
            if (onScrolledToBottom()) {
                mLoadMoreIsAtBottom = false;
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager lm = (LinearLayoutManager) layoutManager;
            int lastVisibleItemPosition = lm.findLastCompletelyVisibleItemPosition();
            mLoadMoreIsAtBottom = lastVisibleItemPosition != RecyclerView.NO_POSITION &&
                    lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
    }

    protected abstract boolean onScrolledToBottom();
}
