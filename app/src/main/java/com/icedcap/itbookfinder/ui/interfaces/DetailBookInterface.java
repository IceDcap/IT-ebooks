package com.icedcap.itbookfinder.ui.interfaces;

/**
 * Author: doushuqi
 * Date: 16-4-19
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public interface DetailBookInterface<T> extends RefreshInterface<T> {
    void isFavorite(boolean is);
    void isDownloaded(boolean is);
    void forceExit();
}
