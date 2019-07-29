package com.linkface.baselibrary.base;

/**
 * Created by ShenHe on 2019/4/10.
 */

public interface BaseView<T> {
    void onSuccess(T data);
    void onFailure(String message);
}
