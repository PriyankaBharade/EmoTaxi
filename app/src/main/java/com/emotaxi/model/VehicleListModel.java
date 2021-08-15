package com.emotaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class VehicleListModel {

    @SerializedName("Vehicles")
    @Expose
    public List<Vehicle> vehicles = null;

    public class Vehicle {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("kms")
        @Expose
        public String kms;
        @SerializedName("latitude")
        @Expose
        public Double latitude;
        @SerializedName("longitude")
        @Expose
        public Double longitude;
        @SerializedName("numero_vehicule")
        @Expose
        public String numeroVehicule;

    }


}
