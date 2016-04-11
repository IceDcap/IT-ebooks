package com.icedcap.itbookfinder.presenters;

import com.icedcap.itbookfinder.ui.interfaces.RefreshInterface;

/**
 * Author: doushuqi
 * Date: 16-4-1
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public abstract class NetBasePresenter<T extends RefreshInterface<?>> {
    protected T mView;
}
