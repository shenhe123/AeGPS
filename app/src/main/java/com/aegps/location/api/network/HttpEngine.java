package com.aegps.location.api.network;

import org.ksoap2.SoapEnvelope;

/**
 * Created by shenhe on 2017/3/6.
 */

public interface HttpEngine {
    SoapEnvelope doGet() throws Exception;

    SoapEnvelope doPost() throws Exception;
}
