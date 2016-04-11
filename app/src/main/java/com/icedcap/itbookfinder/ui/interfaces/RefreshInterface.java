package com.icedcap.itbookfinder.ui.interfaces;

/**
 * Author: doushuqi
 * Date: 16-4-1
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public interface RefreshInterface<T> extends BaseViewInterface<T> {
    void showLoading();
    void hideLoading();
}
