package com.icedcap.itbookfinder.ui.interfaces;

/**
 * Author: doushuqi
 * Date: 16-4-5
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public interface LoadFavoritesInterface<T> extends BaseViewInterface<T> {
    void showLoading();

    void hideLoading();

    void showEmptyPage();
}
