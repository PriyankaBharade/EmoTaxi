package com.emotaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class SignUpModel {

    @SerializedName("version")
    @Expose
    public Version version;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("errorcode")
    @Expose
    public Integer errorcode;
    @SerializedName("data")
    @Expose
    public List<Datum> data = null;
    @SerializedName("message")
    @Expose
    public String message;

    public class Version {

        @SerializedName("status")
        @Expose
        public Integer status;
        @SerializedName("versioncode")
        @Expose
        public Integer versioncode;
        @SerializedName("versionmessage")
        @Expose
        public String versionmessage;
        @SerializedName("current_version")
        @Expose
        public String currentVersion;

    }

    public class Datum {

        @SerializedName("user_id")
        @Expose
        public String userId;
        @SerializedName("customer_id")
        @Expose
        public String customerId;
        @SerializedName("username")
        @Expose
        public String username;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("mobile_no")
        @Expose
        public String mobileNo;
        @SerializedName("otp_verify")
        @Expose
        public Integer otpVerify;

    }


}

