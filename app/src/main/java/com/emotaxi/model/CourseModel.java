package com.emotaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CourseModel {

    @SerializedName("dateheure_demande")
    @Expose
    public String dateheureDemande;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("nb_personne_minimum")
    @Expose
    public Integer nbPersonneMinimum;
    @SerializedName("location")
    @Expose
    public Location location;
    @SerializedName("destination_location")
    @Expose
    public DestinationLocation destinationLocation;
    @SerializedName("nom_client")
    @Expose
    public String nomClient;
    @SerializedName("photo_client")
    @Expose
    public String photoClient;
    @SerializedName("remarque")
    @Expose
    public String remarque;
    @SerializedName("telephone")
    @Expose
    public String telephone;
    @SerializedName("type_vehicule_requis")
    @Expose
    public Integer typeVehiculeRequis;
    @SerializedName("liste_restriction")
    @Expose
    public List<Integer> listeRestriction = null;
    @SerializedName("callback_reference_id")
    @Expose
    public String callbackReferenceId;

    public class Adresse {

        @SerializedName("civique")
        @Expose
        public String civique;
        @SerializedName("adresse")
        @Expose
        public String adresse;
        @SerializedName("ville")
        @Expose
        public String ville;
        @SerializedName("region")
        @Expose
        public String region;
        @SerializedName("appartement")
        @Expose
        public String appartement;
        @SerializedName("formatted_adresse")
        @Expose
        public String formattedAdresse;

    }


    public class Adresse_ {

        @SerializedName("civique")
        @Expose
        public String civique;
        @SerializedName("adresse")
        @Expose
        public String adresse;
        @SerializedName("ville")
        @Expose
        public String ville;
        @SerializedName("region")
        @Expose
        public String region;
        @SerializedName("appartement")
        @Expose
        public String appartement;
        @SerializedName("formatted_adresse")
        @Expose
        public String formattedAdresse;

    }

    public class DestinationLocation {

        @SerializedName("geo")
        @Expose
        public Geo_ geo;
        @SerializedName("adresse")
        @Expose
        public Adresse_ adresse;

    }


    public class Geo {

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


    public class Geo_ {

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


    public class Location {

        @SerializedName("geo")
        @Expose
        public Geo geo;
        @SerializedName("adresse")
        @Expose
        public Adresse adresse;

    }


}



