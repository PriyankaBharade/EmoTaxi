package com.emotaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateCustomer {

    @SerializedName("version")
    @Expose
    public Version version;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("errorcode")
    @Expose
    public Integer errorcode;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {
        @SerializedName("customer_id")
        @Expose
        public String customerId;
    }

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
}

