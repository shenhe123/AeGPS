package com.aegps.location.bean;

import java.util.List;

/**
 * Created by shenhe on 2019/8/8.
 *
 * @description
 */
public class MobileVehicleResult {
    private List<ReturnTableBean> ReturnTable;

    public List<ReturnTableBean> getReturnTable() {
        return ReturnTable;
    }

    public void setReturnTable(List<ReturnTableBean> ReturnTable) {
        this.ReturnTable = ReturnTable;
    }

    public static class ReturnTableBean {
        /**
         * MobileID : 1234567890
         * VehicleCode : 冀RW3A29
         */

        private String MobileID;
        private String VehicleCode;

        public String getMobileID() {
            return MobileID;
        }

        public void setMobileID(String MobileID) {
            this.MobileID = MobileID;
        }

        public String getVehicleCode() {
            return VehicleCode;
        }

        public void setVehicleCode(String VehicleCode) {
            this.VehicleCode = VehicleCode;
        }
    }
}
