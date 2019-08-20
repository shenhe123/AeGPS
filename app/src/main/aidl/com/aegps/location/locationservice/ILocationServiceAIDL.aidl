package com.aegps.location.locationservice;



interface ILocationServiceAIDL {

    /** 当其他服务已经绑定时调起 */
    void onFinishBind();
}
