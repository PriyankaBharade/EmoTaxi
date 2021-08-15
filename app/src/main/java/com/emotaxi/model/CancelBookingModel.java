package com.emotaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancelBookingModel {

    @SerializedName("version")
    @Expose
    private Version version;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("error_code")
    @Expose
    private Integer errorCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Object data;

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public class Version {

        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("versioncode")
        @Expose
        private Integer versioncode;
        @SerializedName("versionmessage")
        @Expose
        private String versionmessage;
        @SerializedName("current_version")
        @Expose
        private String currentVersion;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Integer getVersioncode() {
            return versioncode;
        }

        public void setVersioncode(Integer versioncode) {
            this.versioncode = versioncode;
        }

        public String getVersionmessage() {
            return versionmessage;
        }

        public void setVersionmessage(String versionmessage) {
            this.versionmessage = versionmessage;
        }

        public String getCurrentVersion() {
            return currentVersion;
        }

        public void setCurrentVersion(String currentVersion) {
            this.currentVersion = currentVersion;
        }

    }
}


