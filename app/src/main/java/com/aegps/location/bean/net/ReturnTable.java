package com.aegps.location.bean.net;

import java.util.List;

/**
 * Created by ShenHe on 2019/8/13.
 */

public class ReturnTable {
    private List<ReturnTableBean> ReturnTable;

    public List<ReturnTableBean> getReturnTable() {
        return ReturnTable;
    }

    public void setReturnTable(List<ReturnTableBean> ReturnTable) {
        this.ReturnTable = ReturnTable;
    }

    public static class ReturnTableBean {
        /**
         * UserCode : 866217032117130
         * UserName : 冀AJ7G78
         * Password : 冀AJ7G78
         * StopFlag : false
         * IntervalDuration : 2
         */

        private String UserCode;
        private String UserName;
        private String Password;
        private boolean StopFlag;
        private int IntervalDuration;

        public String getUserCode() {
            return UserCode;
        }

        public void setUserCode(String UserCode) {
            this.UserCode = UserCode;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }

        public String getPassword() {
            return Password;
        }

        public void setPassword(String Password) {
            this.Password = Password;
        }

        public boolean isStopFlag() {
            return StopFlag;
        }

        public void setStopFlag(boolean StopFlag) {
            this.StopFlag = StopFlag;
        }

        public int getIntervalDuration() {
            return IntervalDuration;
        }

        public void setIntervalDuration(int IntervalDuration) {
            this.IntervalDuration = IntervalDuration;
        }
    }
}
