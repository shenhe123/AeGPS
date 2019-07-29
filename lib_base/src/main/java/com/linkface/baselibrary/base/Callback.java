package com.linkface.baselibrary.base;

/**
 * Created by zjy on QE.
 */

public interface Callback<T> {
    void success(T t);

    void fail(String msg);
}
