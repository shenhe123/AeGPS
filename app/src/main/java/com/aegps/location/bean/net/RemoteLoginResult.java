package com.aegps.location.bean.net;

import java.util.List;

/**
 * Created by shenhe on 2019/8/14.
 *
 * @description
 */
public class RemoteLoginResult {

    private List<ReturnTableBean> ReturnTable;

    public List<ReturnTableBean> getReturnTable() {
        return ReturnTable;
    }

    public void setReturnTable(List<ReturnTableBean> ReturnTable) {
        this.ReturnTable = ReturnTable;
    }

    public static class ReturnTableBean {
        /**
         * CustomerCode : C.010001
         * CustomerName : 北京盛大维新科技发展有限公司
         */

        private String CustomerCode;
        private String CustomerName;

        public String getCustomerCode() {
            return CustomerCode;
        }

        public void setCustomerCode(String CustomerCode) {
            this.CustomerCode = CustomerCode;
        }

        public String getCustomerName() {
            return CustomerName;
        }

        public void setCustomerName(String CustomerName) {
            this.CustomerName = CustomerName;
        }
    }
}
