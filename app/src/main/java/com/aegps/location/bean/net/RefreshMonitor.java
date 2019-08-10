package com.aegps.location.bean.net;

import java.util.List;

/**
 * Created by shenhe on 2019/8/8.
 *
 * @description
 */
public class RefreshMonitor {

    private List<MonitorHeaderTableBean> MonitorHeaderTable;
    private List<MonitorEntryTableBean> MonitorEntryTable;

    public List<MonitorHeaderTableBean> getMonitorHeaderTable() {
        return MonitorHeaderTable;
    }

    public void setMonitorHeaderTable(List<MonitorHeaderTableBean> MonitorHeaderTable) {
        this.MonitorHeaderTable = MonitorHeaderTable;
    }

    public List<MonitorEntryTableBean> getMonitorEntryTable() {
        return MonitorEntryTable;
    }

    public void setMonitorEntryTable(List<MonitorEntryTableBean> MonitorEntryTable) {
        this.MonitorEntryTable = MonitorEntryTable;
    }

    public static class MonitorHeaderTableBean {
        /**
         * TrafficMainID : 18
         * TrafficCode : PE-190806-002
         * VehicleCode : 冀RW3A29
         * ShippingModeName : 零担
         * BeginningTime : 2019-08-08 08:48
         * DrivingDuration : 2天13时
         * MileageMeasure : 50571.87
         */

        private int TrafficMainID;
        private String TrafficCode;
        private String VehicleCode;
        private String ShippingModeName;
        private String BeginningTime;
        private String DrivingDuration;
        private double MileageMeasure;

        public int getTrafficMainID() {
            return TrafficMainID;
        }

        public void setTrafficMainID(int TrafficMainID) {
            this.TrafficMainID = TrafficMainID;
        }

        public String getTrafficCode() {
            return TrafficCode;
        }

        public void setTrafficCode(String TrafficCode) {
            this.TrafficCode = TrafficCode;
        }

        public String getVehicleCode() {
            return VehicleCode;
        }

        public void setVehicleCode(String VehicleCode) {
            this.VehicleCode = VehicleCode;
        }

        public String getShippingModeName() {
            return ShippingModeName;
        }

        public void setShippingModeName(String ShippingModeName) {
            this.ShippingModeName = ShippingModeName;
        }

        public String getBeginningTime() {
            return BeginningTime;
        }

        public void setBeginningTime(String BeginningTime) {
            this.BeginningTime = BeginningTime;
        }

        public String getDrivingDuration() {
            return DrivingDuration;
        }

        public void setDrivingDuration(String DrivingDuration) {
            this.DrivingDuration = DrivingDuration;
        }

        public double getMileageMeasure() {
            return MileageMeasure;
        }

        public void setMileageMeasure(double MileageMeasure) {
            this.MileageMeasure = MileageMeasure;
        }
    }

    public static class MonitorEntryTableBean {
        /**
         * TrafficMainID : 18
         * ExpressMainID : 160
         * ExpressCode : PA-190424-001
         * BaccName : 沈阳范生钦
         * DeliveryAddress : 辽宁省锦州市太和区钢花里2-13号 18641635678
         * DeliveryCity : 沈阳
         * ContactPerson :
         * TelephoneCode :
         * MobileTeleCode :
         * RemarkSub :
         * EndingTime :
         * MileageMeasure : 0
         */

        private int TrafficMainID;
        private int ExpressMainID;
        private String ExpressCode;
        private String BaccName;
        private String DeliveryAddress;
        private String DeliveryCity;
        private String ContactPerson;
        private String TelephoneCode;
        private String MobileTeleCode;
        private String RemarkSub;
        private String EndingTime;
        private int MileageMeasure;

        public int getTrafficMainID() {
            return TrafficMainID;
        }

        public void setTrafficMainID(int TrafficMainID) {
            this.TrafficMainID = TrafficMainID;
        }

        public int getExpressMainID() {
            return ExpressMainID;
        }

        public void setExpressMainID(int ExpressMainID) {
            this.ExpressMainID = ExpressMainID;
        }

        public String getExpressCode() {
            return ExpressCode;
        }

        public void setExpressCode(String ExpressCode) {
            this.ExpressCode = ExpressCode;
        }

        public String getBaccName() {
            return BaccName;
        }

        public void setBaccName(String BaccName) {
            this.BaccName = BaccName;
        }

        public String getDeliveryAddress() {
            return DeliveryAddress;
        }

        public void setDeliveryAddress(String DeliveryAddress) {
            this.DeliveryAddress = DeliveryAddress;
        }

        public String getDeliveryCity() {
            return DeliveryCity;
        }

        public void setDeliveryCity(String DeliveryCity) {
            this.DeliveryCity = DeliveryCity;
        }

        public String getContactPerson() {
            return ContactPerson;
        }

        public void setContactPerson(String ContactPerson) {
            this.ContactPerson = ContactPerson;
        }

        public String getTelephoneCode() {
            return TelephoneCode;
        }

        public void setTelephoneCode(String TelephoneCode) {
            this.TelephoneCode = TelephoneCode;
        }

        public String getMobileTeleCode() {
            return MobileTeleCode;
        }

        public void setMobileTeleCode(String MobileTeleCode) {
            this.MobileTeleCode = MobileTeleCode;
        }

        public String getRemarkSub() {
            return RemarkSub;
        }

        public void setRemarkSub(String RemarkSub) {
            this.RemarkSub = RemarkSub;
        }

        public String getEndingTime() {
            return EndingTime;
        }

        public void setEndingTime(String EndingTime) {
            this.EndingTime = EndingTime;
        }

        public int getMileageMeasure() {
            return MileageMeasure;
        }

        public void setMileageMeasure(int MileageMeasure) {
            this.MileageMeasure = MileageMeasure;
        }
    }
}
