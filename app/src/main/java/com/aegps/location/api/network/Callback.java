package com.aegps.location.api.network;

/**
 * Created by shenhe on 2017/3/6.
 */

public interface Callback {
    void onResponse(boolean success, String data);

    void onFailure(Object o);
}
