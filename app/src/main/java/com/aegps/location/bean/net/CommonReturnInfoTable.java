package com.aegps.location.bean.net;

import java.util.List;

/**
 * Created by ShenHe on 2019/8/7.
 */

public class CommonReturnInfoTable {

    private List<CommonReturnInfoTableBean> CommonReturnInfoTable;

    public List<CommonReturnInfoTableBean> getCommonReturnInfoTable() {
        return CommonReturnInfoTable;
    }

    public void setCommonReturnInfoTable(List<CommonReturnInfoTableBean> CommonReturnInfoTable) {
        this.CommonReturnInfoTable = CommonReturnInfoTable;
    }

    public static class CommonReturnInfoTableBean {
        /**
         * IsUserError : true
         * ErrorCode : 000
         * ReturnBillCode :
         * ExcpetionData : 帐套错误
         */

        private boolean IsUserError;
        private String ErrorCode;
        private String ReturnBillCode;
        private String ExcpetionData;

        public boolean isIsUserError() {
            return IsUserError;
        }

        public void setIsUserError(boolean IsUserError) {
            this.IsUserError = IsUserError;
        }

        public String getErrorCode() {
            return ErrorCode;
        }

        public void setErrorCode(String ErrorCode) {
            this.ErrorCode = ErrorCode;
        }

        public String getReturnBillCode() {
            return ReturnBillCode;
        }

        public void setReturnBillCode(String ReturnBillCode) {
            this.ReturnBillCode = ReturnBillCode;
        }

        public String getExcpetionData() {
            return ExcpetionData;
        }

        public void setExcpetionData(String ExcpetionData) {
            this.ExcpetionData = ExcpetionData;
        }
    }
}
