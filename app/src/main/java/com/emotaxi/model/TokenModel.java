package com.emotaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TokenModel {

    @SerializedName("version")
    @Expose
    public Version version;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("error_code")
    @Expose
    public Integer errorCode;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {

        @SerializedName("token_id")
        @Expose
        public String tokenId;
        @SerializedName("charge_id")
        @Expose
        public String charge_id;
        @SerializedName("livemode")
        @Expose
        public Boolean livemode;

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


