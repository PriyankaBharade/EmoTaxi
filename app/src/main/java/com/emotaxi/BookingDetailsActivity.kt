package com.emotaxi

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.emotaxi.adapter.CardListAdapter
import com.emotaxi.adapter.CardSelectionAdapter
import com.emotaxi.drawPath.DownloadTask
import com.emotaxi.model.BookingDetailsModel
import com.emotaxi.model.CardListModel
import com.emotaxi.model.RefundModel
import com.emotaxi.retrofit.*
import com.emotaxi.utils.AnimationUtils
import com.emotaxi.utils.SessionManager
import com.emotaxi.widget.CustomDialogProgress
import com.emotaxi.widget.DataManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_booking_details.*
import kotlinx.android.synthetic.main.fragment_payment_dialog.*
import kotlinx.android.synthetic.main.item_card_layout.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingDetailsActivity : AppCompatActivity(), OnMapReadyCallback,
    SetonGoogleDistanceListener, CardSelectionAdapter.SetOnCloseListener {
    private var mMap: GoogleMap? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var customDialogProgress: CustomDialogProgress? = null
    private val locationRequestCode = 1000
    var sourceAddress: String? = null
    var price: String = ""

    // var email: String = ""
    var cardListAdapter: CardSelectionAdapter? = null
    var cardArrayList = ArrayList<CardListModel.Cardinfo>()

    //  var destinationAddress: String? = null
    var destinationmarker: Marker? = null
    var checkmarker: Boolean = false
    var rv_card: RecyclerView? = null
    var tip_amount: String? = null
    val mainHandler = Handler(Looper.getMainLooper())
    var cardinfo: CardListModel.Cardinfo? = null

    var runnable: Runnable? = object : Runnable {
        override fun run() {
            mainHandler.postDelayed(this, 10000)
            getCourseTimer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_details)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)
        price = intent.getStringExtra("price").toString()
        // email = intent.getStringExtra("email").toString()
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        btn_reset.text = "Cancel Booking"
        btn_reset!!.setOnClickListener {
            if (btn_reset.text.toString() == "Cancel Booking") {
               cancelBookingDialog()
            } else {
                BookingTip()
            }
        }
        image_back.setOnClickListener {
            finish()
        }
        //getCourse()
    }

    private fun bookingcancel() {
        customDialogProgress = CustomDialogProgress(this@BookingDetailsActivity)
        customDialogProgress?.show()
        Toast.makeText(
            this@BookingDetailsActivity, "Price $price"
            , Toast.LENGTH_SHORT
        ).show()
        var price_return: Double? = null
        price_return = if (price.contains(",")) {
            price.replace("$", "").replace(",", ".").toDouble() - 5
        } else {
            price.replace("$", "").toDouble() - 5
        }

        val hashmapheader = HashMap<String, String>()
        hashmapheader["version"] = "1"
        hashmapheader["device_type"] = "android"
        val hashmap = HashMap<String, String>()
        hashmap["charge_id"] = SessionManager.readString(this, Constant.token, "").toString()
        hashmap["email"] = SessionManager.readString(this, Constant.email, "").toString()
        //  var priceindouble = price_return!!.toDouble()
        // var newPrice = Math.round(priceindouble).toString()
        hashmap["amount"] = price_return.toString()
        /* if (price!!.contains(",")) {
             hashmap["amount"] = price_return.toString().replace("$", "").replace(",", ".")!!
         } else {
             hashmap["amount"] = price_return.toString().replace("$", "")
         }*/
        WebServiceClient?.client1.create(BackEndApi::class.java).refund(hashmapheader, hashmap)
            .enqueue(object : Callback<RefundModel> {
                override fun onFailure(call: Call<RefundModel>, t: Throwable) {
                    displayAlert(this@BookingDetailsActivity, t.message!!, t.message!!, false)
                    // Toast.makeText(this@BookingDetailsActivity, "Error Refund" + t.message, Toast.LENGTH_SHORT).show()
                    cancelBookingFraxionAPICall()
                }

                override fun onResponse(
                    call: Call<RefundModel>,
                    response: Response<RefundModel>
                ) {
                    displayAlert(
                        this@BookingDetailsActivity,
                        response.code().toString()!!,
                        response.body().toString()!!,
                        false
                    )
                    if (response.code() == 200) {
                        if (response.body()!!.status == 1) {
                            cancelBookingFraxionAPICall()
                        } else {
                            var alertDialog = AlertDialog.Builder(this@BookingDetailsActivity)
                            alertDialog.setMessage(response.body()!!.message)
                            alertDialog.setNeutralButton(
                                "Ok",
                                DialogInterface.OnClickListener { dialog, which ->
                                    dialog.dismiss()
                                })
                            alertDialog.show()
                        }
                    }
                }
            })
    }

    private fun displayAlert(
        activity: Activity,
        title: String,
        message: String,
        restartDemo: Boolean = false
    ) {
        //  runOnUiThread {
        val builder = AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
        builder.setPositiveButton("Ok", null)
        builder.create().show()
        // }
    }

    fun cancelBookingFraxionAPICall() {
        var url: String =
            "https://taxiaylmer.ddns.fraxion.com:8063/Webservice_Repartition/Service_Repartition/v1/course/?id=" + SessionManager.readString(
                applicationContext,
                Constant.booking_id,
                ""
            )
      //  Log.e("Cancel Booking request", url)
        // + "&message=" + "cancel booking" + "&desactive_callback" + true
        WebServiceClient?.client.create(BackEndApi::class.java)
            .coursedelete(
                "application/json",
                "Basic RU1POmk0RnVsamRuZ01XU3VtTFM=",
                url
            ).enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    customDialogProgress?.dismiss()
                    if (response!!.code() == 200) {
                        Toast.makeText(
                            this@BookingDetailsActivity,
                            "Your booking cancelled",
                            Toast.LENGTH_SHORT
                        ).show()
                        SessionManager.writeString(
                            applicationContext,
                            Constant.booking_id,
                            ""
                        )
                        startActivity(
                            Intent(
                                this@BookingDetailsActivity,
                                MainActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        Toast.makeText(
                            this@BookingDetailsActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    customDialogProgress?.dismiss()
                    Toast.makeText(this@BookingDetailsActivity, t.message, Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }


    fun cancelBookingDialog() {
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_cancel)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var tv_yes: TextView = dialog.findViewById(R.id.tv_yes)
        var tv_cancel: TextView = dialog.findViewById(R.id.tv_cancel)
        tv_cancel.setOnClickListener {
            dialog.dismiss()
        }

        tv_yes.setOnClickListener {
            bookingcancel()
            //  BookingTip()
            // cancelBooking()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun BookingTip() {
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_booking_tip)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var rd_group: RadioGroup = dialog.findViewById(R.id.rd_group)
        rv_card = dialog.findViewById(R.id.rv_card)
        var btn_yes: Button = dialog.findViewById(R.id.btn_yes)
        var btn_no: Button = dialog.findViewById(R.id.btn_no)
        rd_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rs5 -> {
                    tip_amount = "5"
                }
                R.id.rs10 -> {
                    tip_amount = "10"
                }
                R.id.rs15 -> {
                    tip_amount = "15"
                }
            }
        }
        btn_no.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java));
            finish()
        }
        btn_yes.setOnClickListener {
            if (tip_amount != null && cardinfo != null) {
                checkout("", "", "1", cardinfo?.id.toString())
            }else{
                Toast.makeText(this,"Please select the Tip Amount and Card",Toast.LENGTH_SHORT).show()
            }
        }
        getCardOnStripe()
        dialog.show()
    }


    fun checkout(token: String, bookingId: String, type: String, card_id: String) {
        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress.show()
        var hashmap = HashMap<String, String>()
        hashmap["booking_id"] = bookingId
        hashmap["stripe_token"] = token!!
        hashmap["type"] = type
        hashmap["card_id"] = card_id
        hashmap["customer_id"] = SessionManager.readString(this, Constant.CustomerId, "").toString()
        hashmap["total_amount"] = tip_amount.toString().replace("$", "")
        hashmap["user_id"] = ""
        hashmap["email"] = ""
        Log.e("TAG", hashmap.toString())
        var hashmapheader = HashMap<String, String>()
        hashmapheader["version"] = "1"
        hashmapheader["device_type"] = "android"
        WebServiceClient?.client1.create(BackEndApi::class.java)
            .checkout(
                hashmapheader,
                hashmap
            ).enqueue(object : Callback<JsonObject> {
                //CheckoutModel
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    customDialogProgress?.dismiss()
                    //  alert(response!!.body().toString())
                    val gson: JsonObject =
                        JsonParser().parse(response.body().toString()).getAsJsonObject()
                    val jsonObject = JSONObject(gson.toString())
                    if (response.code() == 200) {
                        if (jsonObject.optInt("status") == 1) {
                            if (jsonObject.optJSONObject("data") != null
                                && jsonObject.optJSONObject("data")
                                    .optJSONObject("response") != null
                                && jsonObject.optJSONObject("data").optJSONObject("response")
                                    .optString("id") != null
                            ) {
                                startActivity(
                                    Intent(
                                        this@BookingDetailsActivity,
                                        MainActivity::class.java
                                    )
                                )
                                finish()
                            }
                        } else {
                            alert(jsonObject.optString("message"))
                        }
                    } else {
                        alert(response!!.message())

                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("TAG value", t.message!!)
                    alert(t.message.toString())
                    Toast.makeText(
                        this@BookingDetailsActivity,
                        "Checkout API Error" + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    customDialogProgress?.dismiss()
                }
            })
    }


    fun getCardOnStripe() {
        var hashmap = HashMap<String, String>()
        hashmap["user_id"] = DataManager.dataManager.getSignUpModel(this).data[0].userId
        hashmap["customer_id"] = SessionManager.readString(this, Constant.CustomerId, "").toString()
        var hashmapheader = HashMap<String, String>()
        hashmapheader["version"] = "1"
        hashmapheader["device_type"] = "android"
        WebServiceClient.client1.create(BackEndApi::class.java)
            .getcard(hashmapheader, hashmap).enqueue(object : Callback<CardListModel> {
                override fun onFailure(call: Call<CardListModel>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<CardListModel>,
                    response: Response<CardListModel>
                ) {
                    if (response.body()?.data != null) {
                        if (response.body()?.data!![0].cardinfo != null &&
                            response.body()?.data!![0].cardinfo.size > 0
                        ) {
                            //  var cardArrayList = ArrayList<CardListModel.Cardinfo>()
                            cardArrayList?.addAll(response.body()?.data!![0].cardinfo)
                            cardListAdapter = CardSelectionAdapter(
                                this@BookingDetailsActivity,
                                response.body()?.data!![0].cardinfo as ArrayList<CardListModel.Cardinfo>,
                                this@BookingDetailsActivity
                            )
                            rv_card!!.adapter = cardListAdapter
                        }
                    } else {
                        Toast.makeText(
                            this@BookingDetailsActivity, response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
    }

    fun getCourse() {
        mainHandler.post(runnable!!)
        customDialogProgress = CustomDialogProgress(this)
        customDialogProgress?.show()
        var url: String =
            "https://taxiaylmer.ddns.fraxion.com:8063/Webservice_Repartition/Service_Repartition/v1/course/" +
                    SessionManager.readString(
                        applicationContext,
                        Constant.booking_id,
                        ""
                    )!!
        /* var url: String =
             "https://taxiaylmer.ddns.fraxion.com:8063/Webservice_Repartition/Service_Repartition/v1/course/" + "19817"*/
        WebServiceClient?.client.create(BackEndApi::class.java)
            .course(
                "application/json",
                "Basic RU1POmk0RnVsamRuZ01XU3VtTFM=",
                url
            ).enqueue(object : Callback<BookingDetailsModel> {
                override fun onResponse(
                    call: Call<BookingDetailsModel>,
                    response: Response<BookingDetailsModel>
                ) {
                    customDialogProgress?.dismiss()
                    if (response!!.code() == 200) {
                        tv_booking_id.text =
                            getString(R.string.booking_id) + ":- " + response.body()!!.id
                        tv_vehicle_id.text =
                            getString(R.string.vehicule) + ":- " + response.body()!!.vehiculeId
                        tv_booking_status.text = response.body()!!.status
                        addSourcemarkerInfo()
                        if (response!!.body()!!.vehiculeGeo != null) {
                            addDestinationmarkerInfoWindow(
                                response!!.body()!!.vehiculeGeo!!.latitude!!.toDouble(),
                                response!!.body()!!.vehiculeGeo!!.longitude!!.toDouble()
                            )
                            val url = getURL(
                                LatLng(latitude, longitude),
                                LatLng(
                                    response!!.body()!!.vehiculeGeo!!.latitude!!.toDouble(),
                                    response!!.body()!!.vehiculeGeo!!.longitude!!.toDouble()
                                )
                            )
                            drawPath(url)
                            val builder: LatLngBounds.Builder = LatLngBounds.Builder()
                            builder.include(LatLng(latitude, longitude))
                                .include(
                                    LatLng(
                                        response!!.body()!!.vehiculeGeo!!.latitude!!.toDouble(),
                                        response!!.body()!!.vehiculeGeo!!.longitude!!.toDouble()
                                    )
                                )
                            val cameraUpdate: CameraUpdate =
                                CameraUpdateFactory.newLatLngBounds(builder.build(), 0)
                            mMap!!.moveCamera(cameraUpdate)
                        }
                    } else {
                        Toast.makeText(this@BookingDetailsActivity, "Fail ", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<BookingDetailsModel>, t: Throwable) {
                    customDialogProgress?.dismiss()
                    Toast.makeText(this@BookingDetailsActivity, t.message, Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }

    fun getCourseTimer() {
        var url: String =
            "https://taxiaylmer.ddns.fraxion.com:8063/Webservice_Repartition/Service_Repartition/v1/course/" + SessionManager.readString(
                applicationContext,
                Constant.booking_id,
                ""
            )!!
        /* var url: String =
             "https://taxiaylmer.ddns.fraxion.com:8063/Webservice_Repartition/Service_Repartition/v1/course/" + "19817"*/
        WebServiceClient?.client.create(BackEndApi::class.java)
            .course(
                "application/json",
                "Basic RU1POmk0RnVsamRuZ01XU3VtTFM=",
                url
            ).enqueue(object : Callback<BookingDetailsModel> {
                override fun onResponse(
                    call: Call<BookingDetailsModel>,
                    response: Response<BookingDetailsModel>
                ) {
                    if (response!!.code() == 200) {
                        tv_booking_id.text =
                            getString(R.string.booking_id) + ":- " + response.body()!!.id
                        tv_vehicle_id.text =
                            getString(R.string.vehicule) + ":- " + response.body()!!.vehiculeId
                        tv_booking_status.text = response.body()!!.status
                        if (response.body()!!.status == "A_Repartir"
                            || response.body()!!.status == "Vehicule_Confirme"
                        ) {
                            btn_reset.text = "Cancel Booking"
                        } else {
                            btn_reset.text = getString(R.string.reset)
                        }
                        if (checkmarker) {
                            if (response!!.body()!!.vehiculeGeo != null) {
                                addDestinationmarkerInfoWindow(
                                    response!!.body()!!.vehiculeGeo!!.latitude!!.toDouble(),
                                    response!!.body()!!.vehiculeGeo!!.longitude!!.toDouble()
                                )
                            }
                        } else {
                           // addSourcemarkerInfo()
                            if (response!!.body()!!.vehiculeGeo != null) {
                                addDestinationmarkerInfoWindow(
                                    response!!.body()!!.vehiculeGeo!!.latitude!!.toDouble(),
                                    response!!.body()!!.vehiculeGeo!!.longitude!!.toDouble()
                                )
                                val url = getURL(
                                    LatLng(latitude, longitude),
                                    LatLng(
                                        response!!.body()!!.vehiculeGeo!!.latitude!!.toDouble(),
                                        response!!.body()!!.vehiculeGeo!!.longitude!!.toDouble()
                                    )
                                )
                                drawPath(url)
                                val builder: LatLngBounds.Builder = LatLngBounds.Builder()
                                builder.include(LatLng(latitude, longitude))
                                    .include(
                                        LatLng(
                                            response!!.body()!!.vehiculeGeo!!.latitude!!.toDouble(),
                                            response!!.body()!!.vehiculeGeo!!.longitude!!.toDouble()
                                        )
                                    )
                                val cameraUpdate: CameraUpdate =
                                    CameraUpdateFactory.newLatLngBounds(builder.build(), 50)
                                mMap!!.moveCamera(cameraUpdate)
                            }
                        }
                    } else {
                        Toast.makeText(this@BookingDetailsActivity, "Fail ", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<BookingDetailsModel>, t: Throwable) {
                    Toast.makeText(this@BookingDetailsActivity, t.message, Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(runnable!!)
    }

    override fun onRestart() {
        super.onRestart()
        mainHandler.post(runnable!!)
    }

    private fun drawPath(url: String) {
        checkmarker = true
        val downloadTask = DownloadTask(mMap, this)
        downloadTask.execute(url)
    }


    private fun addSourcemarkerInfo() {
        var valueMap1 = MarkerOptions()
            .position(
                LatLng(
                    latitude, longitude
                )
            )
            .title(sourceAddress)
            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue))
        val adapter = CustomInfoWindowAdapter(this@BookingDetailsActivity)
        //  mMap!!.setInfoWindowAdapter(adapter)
        mMap!!.addMarker(valueMap1)//.showInfoWindow()
    }

    private fun addDestinationmarkerInfoWindow(toDouble: Double, toDouble1: Double) {
        if (destinationmarker == null) {
            val height = 160
            val width = 80
            val bitmapdraw: BitmapDrawable =
                resources.getDrawable(R.mipmap.car_new) as BitmapDrawable
            val b: Bitmap = bitmapdraw.bitmap
            val smallMarker: Bitmap = Bitmap.createScaledBitmap(b, width, height, false)
            destinationmarker = mMap!!.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            toDouble,
                            toDouble1
                        )
                    )
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            )
            val adapter1 = CustomInfoWindowAdapter(this@BookingDetailsActivity)
            mMap!!.setInfoWindowAdapter(adapter1)
        } else {
            updateCarLocation(LatLng(toDouble, toDouble1))
        }

    }

    private fun getURL(from: LatLng, to: LatLng): String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/directions/json?$params\$&key" + "=AIzaSyAA11xopQ1pUTPpZdRnRnQD8mEWvWlSQSI"
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap!!.isMyLocationEnabled = true
        getCourse()
        // checkPermission()

    }

    override fun onGoogleResponseListener(distance: String, time: String) {
        if (distance != null && distance != "" && time != null && time != "") {
            tv_distance!!.text = getString(R.string.distance) + " " + distance
            tv_eta!!.text = getString(R.string.eta) + " " + time
        }
    }

    fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                locationRequestCode
            )
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    // longitude = location.longitude
                    //latitude = location.latitude
                    /*  mMap?.animateCamera(
                          CameraUpdateFactory.newLatLngZoom(
                              LatLng(
                                  latitude,
                                  longitude
                              ), 16f
                          )
                      )*/
                    getCourse()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                        if (location != null) {
                            //longitude = location.longitude
                            //latitude = location.latitude
                            //   getAddress(latitude, longitude)
                            // getCourse()
                        }
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun updateCarLocation(latLng: LatLng) {
        val valueAnimator = AnimationUtils.carAnimator()
        valueAnimator.addUpdateListener { va ->
            destinationmarker?.position = latLng
            /* val rotation = MapUtils.getRotation(previousLatLng!!, nextLocation)
             if (!rotation.isNaN()) {
                 destinationmarker?.rotation = rotation
             }*/
            destinationmarker?.setAnchor(0.5f, 0.5f)
            //  animateCamera(nextLocation)
        }
        valueAnimator.start()
    }

    override fun onCloseListener(cardinfo: CardListModel.Cardinfo, clickOnView: Boolean , position : Int) {
        this.cardinfo = cardinfo
    }


    fun alert(msg: String) {
        var alert = AlertDialog.Builder(this)
        alert.setMessage(msg)
        alert.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        alert.show()
    }


}