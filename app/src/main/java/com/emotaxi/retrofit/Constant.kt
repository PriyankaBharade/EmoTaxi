package com.emotaxi.retrofit

class Constant {
    companion object {
        val CallbackFrom: String = "CallbackFrom"
        val Splash: String = "Splash"
        val PaymentDialog: String = "PaymentDialog"
        val CustomerId: String = "customer_id"
        var serverUrl: String =
            "https://taxiaylmer.ddns.fraxion.com:8063/Webservice_Repartition/Service_Repartition/v1/"
       /* var serverUrlNew: String =
            "http://taxiaylmer.ddns.fraxion.com:8061/Webservice_Repartition/Service_Repartition/v1/"*/
        var serverUrlorg1: String = "http://phssdhar.com/stripe/index.php/webservices_bazaar/"
        var serverUrl1: String = "http://phssdhar.com/stripe/stripe_test/index.php/webservices_bazaar/"
        var booking_id: String = "booking_id"
        var name: String = "name"
        var number: String = "number"
        var email: String = "email"
        var cardnumber: String = "cardnumber"
        var expiryYear: String = "year"
        var expiryMonth: String = "month"
        var cvc: String = "cvc"
        var postal: String = "postal"
        val token: String =  "token"
        val language: String =  "en"
        val USER_DATA: String =  "user_data"
        val Price: String =  "price"
        val PickUpLatitude: String =  "pick_up_latitude"
        val PickUpLongitude: String =  "pick_up_longitude"
        val ProfileData: String =  "ProfileData"
    }
}