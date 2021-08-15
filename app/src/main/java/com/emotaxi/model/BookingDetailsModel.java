package com.emotaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookingDetailsModel {

    @SerializedName("chauffeur_id")
    @Expose
    public Integer chauffeurId;
    @SerializedName("compagnie_id")
    @Expose
    public Integer compagnieId;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("vehicule")
    @Expose
    public String vehicule;
    @SerializedName("vehicule_geo")
    @Expose
    public VehiculeGeo vehiculeGeo;
    @SerializedName("vehicule_id")
    @Expose
    public Integer vehiculeId;

    public class VehiculeGeo {

        @SerializedName("derniere_mise_a_jour")
        @Expose
        public String derniereMiseAJour;
        @SerializedName("latitude")
        @Expose
        public Double latitude;
        @SerializedName("longitude")
        @Expose
        public Double longitude;

    }

}


