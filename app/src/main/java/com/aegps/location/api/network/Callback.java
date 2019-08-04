package com.aegps.location.api.network;

import org.ksoap2.SoapEnvelope;

/**
 * Created by shenhe on 2017/3/6.
 */

public interface Callback {
    void onResponse(SoapEnvelope envelope);

    void onFailure(Object o);
}
