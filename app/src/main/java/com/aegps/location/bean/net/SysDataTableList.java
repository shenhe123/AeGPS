package com.aegps.location.bean.net;

import java.util.List;

/**
 * Created by shenhe on 2019/8/4.
 *
 * @description
 */
public class SysDataTableList {

    private List<SysDataTable> SysDataTable;

    public List<SysDataTable> getData() {
        return SysDataTable;
    }

    public void setData(List<SysDataTable> data) {
        this.SysDataTable = data;
    }

    public static class SysDataTable {
        private String HandleType; //0 
        private String HandleCode;//功能编号 
        private String UserCode;//车牌号 
        private String PlatCode;//07 
        private String DataBaseName;//账套名称 查询账套是为空 
        private String ClientIP;//空 
        private String FunctionID;//空 

        public SysDataTable(String handleType, String handleCode, String userCode, String platCode, String dataBaseName, String clientIP, String functionID) {
            HandleType = handleType;
            HandleCode = handleCode;
            UserCode = userCode;
            PlatCode = platCode;
            DataBaseName = dataBaseName;
            ClientIP = clientIP;
            FunctionID = functionID;
        }

        public String getHandleType() {
            return HandleType;
        }

        public void setHandleType(String handleType) {
            HandleType = handleType;
        }

        public String getHandleCode() {
            return HandleCode;
        }

        public void setHandleCode(String handleCode) {
            HandleCode = handleCode;
        }

        public String getUserCode() {
            return UserCode;
        }

        public void setUserCode(String userCode) {
            UserCode = userCode;
        }

        public String getPlatCode() {
            return PlatCode;
        }

        public void setPlatCode(String platCode) {
            PlatCode = platCode;
        }

        public String getDataBaseName() {
            return DataBaseName;
        }

        public void setDataBaseName(String dataBaseName) {
            DataBaseName = dataBaseName;
        }

        public String getClientIP() {
            return ClientIP;
        }

        public void setClientIP(String clientIP) {
            ClientIP = clientIP;
        }

        public String getFunctionID() {
            return FunctionID;
        }

        public void setFunctionID(String functionID) {
            FunctionID = functionID;
        }
    }


}
