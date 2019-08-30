package com.aegps.location.bean.net;

import java.util.List;

/**
 * Created by shenhe on 2019/8/30.
 *
 * @description
 */
public class CheckUpdateResult {
    private List<ReturnTableBean> ReturnTable;

    public List<ReturnTableBean> getReturnTable() {
        return ReturnTable;
    }

    public void setReturnTable(List<ReturnTableBean> ReturnTable) {
        this.ReturnTable = ReturnTable;
    }

    public static class ReturnTableBean {
        /**
         * IsForcedUpdate : true
         * UpdateDescribe : 修复登录问题
         * DownloadLink : http://files.liuzhiyuan.cn/v10.apk
         * IsUpdate : true
         * ApkName : 北极欧-云物流
         * MD5Value :
         * IsMD5Check : true
         */

        private boolean IsForcedUpdate;
        private String UpdateDescribe;
        private String DownloadLink;
        private boolean IsUpdate;
        private String ApkName;
        private String MD5Value;
        private boolean IsMD5Check;

        public boolean isIsForcedUpdate() {
            return IsForcedUpdate;
        }

        public void setIsForcedUpdate(boolean IsForcedUpdate) {
            this.IsForcedUpdate = IsForcedUpdate;
        }

        public String getUpdateDescribe() {
            return UpdateDescribe;
        }

        public void setUpdateDescribe(String UpdateDescribe) {
            this.UpdateDescribe = UpdateDescribe;
        }

        public String getDownloadLink() {
            return DownloadLink;
        }

        public void setDownloadLink(String DownloadLink) {
            this.DownloadLink = DownloadLink;
        }

        public boolean isIsUpdate() {
            return IsUpdate;
        }

        public void setIsUpdate(boolean IsUpdate) {
            this.IsUpdate = IsUpdate;
        }

        public String getApkName() {
            return ApkName;
        }

        public void setApkName(String ApkName) {
            this.ApkName = ApkName;
        }

        public String getMD5Value() {
            return MD5Value;
        }

        public void setMD5Value(String MD5Value) {
            this.MD5Value = MD5Value;
        }

        public boolean isIsMD5Check() {
            return IsMD5Check;
        }

        public void setIsMD5Check(boolean IsMD5Check) {
            this.IsMD5Check = IsMD5Check;
        }
    }
}
