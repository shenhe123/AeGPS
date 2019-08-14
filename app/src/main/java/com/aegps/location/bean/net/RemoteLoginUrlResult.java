package com.aegps.location.bean.net;

import java.util.List;

/**
 * Created by shenhe on 2019/8/14.
 *
 * @description
 */
public class RemoteLoginUrlResult {
    private List<ReturnTableBean> ReturnTable;

    public List<ReturnTableBean> getReturnTable() {
        return ReturnTable;
    }

    public void setReturnTable(List<ReturnTableBean> ReturnTable) {
        this.ReturnTable = ReturnTable;
    }

    public static class ReturnTableBean {
        /**
         * RequestURL : http://Cloud.bjosoft.com:8800/TradingService.svc
         */

        private String RequestURL;

        public String getRequestURL() {
            return RequestURL;
        }

        public void setRequestURL(String RequestURL) {
            this.RequestURL = RequestURL;
        }
    }
}
