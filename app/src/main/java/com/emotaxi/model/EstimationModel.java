package com.emotaxi.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EstimationModel {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("prix")
    @Expose
    public Integer prix;
    @SerializedName("duration")
    @Expose
    public String duration;
    @SerializedName("durationvalue")
    @Expose
    public Integer durationvalue;
    @SerializedName("distance")
    @Expose
    public String distance;
    @SerializedName("distancevalue")
    @Expose
    public Integer distancevalue;
    @SerializedName("trip")
    @Expose
    public List<Trip> trip = null;

    public class Trip {

        @SerializedName("latitude")
        @Expose
        public Integer latitude;
        @SerializedName("longitude")
        @Expose
        public Integer longitude;
        @SerializedName("derniere_mise_a_jour")
        @Expose
        public String derniereMiseAJour;

    }
}

