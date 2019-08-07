package com.aegps.location.bean;

import java.util.List;

/**
 * Created by shenhe on 2019/8/7.
 *
 * @description
 */
public class ReturnTableResult{
    private List<ReturnTableBean> ReturnTable;

    public List<ReturnTableBean> getReturnTable() {
        return ReturnTable;
    }

    public void setReturnTable(List<ReturnTableBean> ReturnTable) {
        this.ReturnTable = ReturnTable;
    }

    public static class ReturnTableBean {
        /**
         * DataBasesName : ElectronTrading999DB
         * Number : 001
         * CountingRoomName : 经销商平台帐套
         * CoName :
         * DefaultBackupFolder :
         * KeyCode :
         * KeyValue :
         */

        private String DataBasesName;
        private String Number;
        private String CountingRoomName;
        private String CoName;
        private String DefaultBackupFolder;
        private String KeyCode;
        private String KeyValue;

        public String getDataBasesName() {
            return DataBasesName;
        }

        public void setDataBasesName(String DataBasesName) {
            this.DataBasesName = DataBasesName;
        }

        public String getNumber() {
            return Number;
        }

        public void setNumber(String Number) {
            this.Number = Number;
        }

        public String getCountingRoomName() {
            return CountingRoomName;
        }

        public void setCountingRoomName(String CountingRoomName) {
            this.CountingRoomName = CountingRoomName;
        }

        public String getCoName() {
            return CoName;
        }

        public void setCoName(String CoName) {
            this.CoName = CoName;
        }

        public String getDefaultBackupFolder() {
            return DefaultBackupFolder;
        }

        public void setDefaultBackupFolder(String DefaultBackupFolder) {
            this.DefaultBackupFolder = DefaultBackupFolder;
        }

        public String getKeyCode() {
            return KeyCode;
        }

        public void setKeyCode(String KeyCode) {
            this.KeyCode = KeyCode;
        }

        public String getKeyValue() {
            return KeyValue;
        }

        public void setKeyValue(String KeyValue) {
            this.KeyValue = KeyValue;
        }
    }
}
