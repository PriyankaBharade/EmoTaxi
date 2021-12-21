package com.emotaxi

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.emotaxi.drawPath.DownloadTask
import com.emotaxi.retrofit.Constant
import com.emotaxi.retrofit.SetonGoogleDistanceListener
import com.emotaxi.widget.AddPaymentBottomSheetDialog
import com.emotaxi.widget.CustomDialogProgress
import com.emotaxi.widget.SessionManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_confirm.*
import java.util.*


class ConfirmActivity : AppCompatActivity(), OnMapReadyCallback, SetonGoogleDistanceListener {
    private var mMap: GoogleMap? = null

    //private lateinit var fusedLocationClient: FusedLocationProviderClient
    /* var latitude: Double = 0.0
     var longitude: Double = 0.0
     var dlatitude: Double = 0.0
     var dlongitude: Double = 0.0 */
    private val locationRequestCode = 1000

    var latitudestr: String? = null
    var longitudestr: String? = null
    var dlatitudestr: String? = null
    var dlongitudestr: String? = null
    var carReachtime: String? = null
    var customDialogProgress: CustomDialogProgress? = null

    var tv_car_no: TextView? = null
    var tv_avg_price: TextView? = null
    var tv_reach_time: TextView? = null
    var tv_amount: TextView? = null
    var sourceAddress: String? = null
    var destinationAddress: String? = null
    var rl_swipe: RelativeLayout? = null
    var distanceTime = ""


    companion object {
        var rl_main: RelativeLayout? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)
        tv_car_no = findViewById(R.id.tv_car_no)
        tv_avg_price = findViewById(R.id.tv_avg_price)
        tv_reach_time = findViewById(R.id.tv_reach_time)
        tv_amount = findViewById(R.id.tv_amount)
        rl_swipe = findViewById(R.id.rl_swipe)

        updateLocalization()
        //   rl_main = findViewById(R.id.rl_main)
        // fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        latitudestr = intent.getStringExtra("latitude")
        longitudestr = intent.getStringExtra("longitude")
        dlatitudestr = intent.getStringExtra("dlatitude")
        dlongitudestr = intent.getStringExtra("dlongitude")
        carReachtime = intent.getStringExtra("carReachtime")
        sourceAddress = intent.getStringExtra("sourceAddress")
        destinationAddress = intent.getStringExtra("destinationAddress")
        Log.e("Input Destination", destinationAddress + "new " + sourceAddress)
        /* rl_swipe!!.setOnClickListener {
             //  rl_main!!.alpha = 0.5f
             AddPaymentBottomSheetDialog.newInstance("","").show(supportFragmentManager!!,"TAG")
            *//* FragmentPaymentDialog.newInstance(
                latitudestr!!,
                longitudestr!!,
                dlatitudestr!!,
                dlongitudestr!!,
                sourceAddress!!,
                destinationAddress!!,
                tv_car_no!!.text.toString()!!
            ).show(supportFragmentManager!!, "TAG")*//*
        }*/
        // course()

        swipe_button!!.setOnStateChangeListener {
            if (it) {
                 AddPaymentBottomSheetDialog.newInstance(
                    latitudestr!!,
                    longitudestr!!,
                    dlatitudestr!!,
                    dlongitudestr!!,
                    sourceAddress!!,
                    destinationAddress!!,
                    tv_car_no!!.text.toString()!!
                ).show(supportFragmentManager!!, "TAG")
                /* FragmentPaymentDialog.newInstance(
                     latitudestr!!,
                     longitudestr!!,
                     dlatitudestr!!,
                     dlongitudestr!!,
                     sourceAddress!!,
                     destinationAddress!!,
                     tv_car_no!!.text.toString()!!
                 ).show(supportFragmentManager!!, "TAG")*/
            }
        }

        image_back.setOnClickListener {
            finish()
        }
    }

    private fun updateLocalization() {
        Log.e("TAG", SessionManager.readString(this, Constant.language, "")!!)
        val config = resources.configuration
        val locale = Locale("fr")
        Locale.setDefault(locale)
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun getURL(from: LatLng, to: LatLng): String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/directions/json?$params\$&key" + "=AIzaSyAA11xopQ1pUTPpZdRnRnQD8mEWvWlSQSI"
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        // getEstimation()
        mMap = googleMap
        setDataOnView()
        /*val builder: LatLngBounds.Builder = LatLngBounds.Builder()
        builder.include(LatLng(45.40362716968502!!, -75.7986890311448!!))
            .include(
                LatLng(45.51282615850129!!, -75.63515982768273!!)
            )
        val cameraUpdate: CameraUpdate =
            CameraUpdateFactory.newLatLngBounds(builder.build(), 16)
        mMap!!.moveCamera(cameraUpdate)*/
    }

    private fun drawPath(url: String) {
        val downloadTask = DownloadTask(mMap, this)
        downloadTask.execute(url)
    }

    override fun onResume() {
        Toast.makeText(this, "Call Resume funtion", Toast.LENGTH_SHORT).show()
        super.onResume()
    }


    /* fun getEstimation() {
         //  customDialogProgress = CustomDialogProgress(this)
         // customDialogProgress?.show()
         var jsonobject = com.google.gson.JsonObject()
         var origin = com.google.gson.JsonObject()
         var destination = com.google.gson.JsonObject()
         origin.addProperty("latitude", 45.40362716968502)
         origin.addProperty("longitude", -75.79868903114482)
         origin.addProperty("derniere_mise_a_jour", "42 Rue Paul-Verlaine, Gatineau, QC, Canada")
         destination.addProperty("latitude", 45.40645225420341)
         destination.addProperty("longitude", -75.78916409388052)
         destination.addProperty(
             "derniere_mise_a_jour",
             "942-976 Chemin d'Aylmer, Gatineau, QC J9H 5V3, Canada"
         )
         jsonobject.add("origine", origin)
         jsonobject.add("destination", destination)
         Log.e("TAG estiamtion Price", jsonobject.toString())
         WebServiceClient?.client.create(BackEndApi::class.java)
             .getEstimation(
                 "Basic RU1POmk0RnVsamRuZ01XU3VtTFM=",
                 jsonobject
             )
             .enqueue(object : Callback<EstimationModel> {
                 override fun onFailure(call: Call<EstimationModel>, t: Throwable) {
                     //  customDialogProgress?.dismiss()
                     Log.e("TAG Error", t.message.toString())
                     setDataOnView(estimationModel = EstimationModel())
                 }

                 override fun onResponse(
                     call: Call<EstimationModel>,
                     response: Response<EstimationModel>
                 ) {
                     //  customDialogProgress?.dismiss()
                     Log.e("TAG Response", response?.code().toString())
                     response?.body()?.distance?.toString()?.let { Log.e("TAG Response", it) }
                     if (response?.code() == 200) {
                         setDataOnView(response?.body()!!)
                     } else if (response?.code() == 401) {
                         Toast.makeText(
                             this@ConfirmActivity,
                             response?.message(),
                             Toast.LENGTH_SHORT
                         ).show()
                     } else {
                         Toast.makeText(
                             this@ConfirmActivity,
                             response?.message(),
                             Toast.LENGTH_SHORT
                         ).show()
                     }
                 }

             })
     }*/

    private fun setDataOnView() {//estimationModel: EstimationModel
        //  tv_current_location?.setText(sourceAddress)
        //  input_destination?.setText(destinationAddress)
        /* val sLocation = Location("locationA")
         sLocation.latitude = 45.40362716968502
         sLocation.longitude = -75.79868903114482

         val dLocation = Location("locationB")
         dLocation.latitude = 45.40645225420341
         dLocation.longitude = -75.78916409388052*/

        // val distanceinmeters: Float = sLocation.distanceTo(dLocation)
        /* if (estimationModel != null && !estimationModel!!.equals("")) {
             tv_reach_time?.text =
                 estimationModel?.distancevalue.toString() + " " + estimationModel?.distance
             tv_car_no?.text =
                 estimationModel?.durationvalue.toString() + " " + estimationModel?.duration.toString()
             tv_amount?.text = estimationModel?.prix.toString()
             tv_avg_price?.text =
                 estimationModel?.prix.toString() + " - " + (estimationModel?.prix + 2).toString()
         } else {*/
        //  tv_reach_time!!.text = distanceinmeters!!.toString()
        // }

        /* val url = getURL(
             LatLng(45.40362716968502!!, -75.7986890311448),
             LatLng(45.51282615850129, -75.63515982768273)
         ) */

        val url = getURL(
            LatLng(latitudestr!!.toDouble(), longitudestr!!.toDouble()),
            LatLng(dlatitudestr!!.toDouble(), dlongitudestr!!.toDouble())
        )
        Log.e("Direction API Path", url)
        // addSourcemarkerInfo()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.10).toInt() // offset from edges of the map 10% of screen

        drawPath(url)
        val builder: LatLngBounds.Builder = LatLngBounds.Builder()
        builder.include(LatLng(latitudestr!!.toDouble(), longitudestr!!.toDouble()))
            .include(LatLng(dlatitudestr!!.toDouble(), dlongitudestr!!.toDouble()))
        val cameraUpdate: CameraUpdate =
            CameraUpdateFactory.newLatLngBounds(builder.build(), width, height, 300)
        mMap!!.moveCamera(cameraUpdate)
    }

//    private fun addSourcemarkerInfo() {
//        /* var valueMap1 = MarkerOptions()
//             .position(
//                 LatLng(
//                     latitudestr!!.toDouble(), longitudestr!!.toDouble()
//                 )
//             )
//             .title(sourceAddress)
//             // .snippet(sourceAddress)
//             .icon(BitmapDescriptorFactory.fromResource(R.mipmap.gren))
//         val adapter = CustomInfoWindowAdapter(this@ConfirmActivity)*/
//        // mMap!!.setInfoWindowAdapter(adapter)
//        // mMap!!.addMarker(valueMap1)//.showInfoWindow()
//        var spilt = sourceAddress!!.split(" ")
//        var address1 = spilt[0]
//        var address2 = spilt[1] + " " +  spilt[2]
//        var valueMap1 = MarkerOptions().position(
//            LatLng(
//                latitudestr!!.toDouble(), longitudestr!!.toDouble()
//            )
//        ).icon(
//            BitmapDescriptorFactory.fromBitmap(
//                getMarkerBitmapFromView(R.mipmap.car_new, address2, address1, "0")
//            )
//        )
//        mMap!!.addMarker(valueMap1)
//    }

    private fun addDestinationmarkerInfoWindow() {
        /*  var valueMap = MarkerOptions()
              .position(
                  LatLng(
                      dlatitudestr!!.toDouble(),
                      dlongitudestr!!.toDouble()
                  )
              )
              .title(destinationAddress)
              //.snippet("42 Rue Paul-Verlaine, Gatineau")
              .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue))
          val adapter1 = CustomInfoWindowAdapter(this@ConfirmActivity)
          //  mMap!!.setInfoWindowAdapter(adapter1)
          mMap!!.addMarker(valueMap)//.showInfoWindow()*/
        var spilt = destinationAddress!!.split(" ")
        var address1 = spilt[0]
        var address2 = spilt[1] + " " + spilt[2]
        var valueMap = MarkerOptions().position(
            LatLng(
                dlatitudestr!!.toDouble(), dlongitudestr!!.toDouble()
            )
        ).icon(
            BitmapDescriptorFactory.fromBitmap(
                getMarkerDestinationBitmapFromView(R.mipmap.car_new, address2, address1, "0")
            )
        )
        mMap!!.addMarker(valueMap)
    }

    private fun getMarkerBitmapFromView(
        @DrawableRes resId: Int,
        address: String,
        knownName: String,
        time: String
    ): Bitmap? {
        val customMarkerView =
            (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.view_custom_marker,
                null
            )
        /*  val markerImageView =
              customMarkerView.findViewById(R.id.profile_image) as ImageView
          markerImageView.setImageResource(resId)*/
        var tv_add_num: TextView = customMarkerView.findViewById(R.id.tv_add_num)
        var tv_address: TextView = customMarkerView.findViewById(R.id.tv_address)
        var tv_arrive_time: TextView = customMarkerView.findViewById(R.id.tv_arrive_time)
        tv_address!!.text = address
        tv_add_num!!.text = knownName
        tv_arrive_time!!.text = carReachtime!!.replace("mins", "")
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(
            18,
            18,
            customMarkerView.measuredWidth,
            customMarkerView.measuredHeight
        )
        customMarkerView.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            customMarkerView.measuredWidth, customMarkerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        val drawable: Drawable? = customMarkerView.background
        if (drawable != null) drawable.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    private fun getMarkerDestinationBitmapFromView(
        @DrawableRes resId: Int,
        address: String,
        knownName: String,
        time: String
    ): Bitmap? {
        Toast.makeText(this, distanceTime, Toast.LENGTH_SHORT).show()
        val customMarkerView =
            (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.view_cutome_destination_marker,
                null
            )
        /*  val markerImageView =
              customMarkerView.findViewById(R.id.profile_image) as ImageView
          markerImageView.setImageResource(resId)*/
        var tv_add_num: TextView = customMarkerView.findViewById(R.id.tv_add_num)
        var tv_address: TextView = customMarkerView.findViewById(R.id.tv_address)
        var tv_arrive_time: TextView = customMarkerView.findViewById(R.id.tv_arrive_time)
        tv_address!!.text = address
        tv_add_num!!.text = knownName
        tv_arrive_time!!.text = distanceTime
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(
            18,
            18,
            customMarkerView.measuredWidth,
            customMarkerView.measuredHeight
        )
        customMarkerView.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            customMarkerView.measuredWidth, customMarkerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        val drawable: Drawable? = customMarkerView.background
        if (drawable != null) drawable.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    override fun onGoogleResponseListener(distance: String, time: String) {
        if (distance != null && distance != "" && time != null && time != "") {
            distanceTime = time
            addDestinationmarkerInfoWindow()
            tv_reach_time!!.text = distance
            var calculatedprice =
                (distance.replace(" km", "").replace(" m", "").replace(",", "")
                    .toDouble() * 1.75) + 4.40
            var percentofprice = (calculatedprice * 15) / 100
            var caldispricesum = calculatedprice + percentofprice
            tv_avg_price?.text = String.format("%.2f", caldispricesum)
            //   var discountedPrice = (caldispricesum * 25) / 100
            //  var discountedPrice = (caldispricesum * 10) / 100
            var total_amount = caldispricesum - 0.80
            //   var total_amount = caldispricesum - discountedPrice
            val formatted = String.format("%.2f", total_amount)
            tv_car_no!!.text = "$formatted$"

            var spilt = sourceAddress!!.split(" ")
            var address1 = spilt[0]
            var address2 = spilt[1] + " " + spilt[2]
            var valueMap1 = MarkerOptions().position(
                LatLng(
                    latitudestr!!.toDouble(), longitudestr!!.toDouble()
                )
            ).icon(
                BitmapDescriptorFactory.fromBitmap(
                    getMarkerBitmapFromView(R.mipmap.car_new, address2, address1, time)
                )
            )
            mMap!!.addMarker(valueMap1)
        }
    }

    override fun onRestart() {
        super.onRestart()
        swipe_button.clearFocus()
        Toast.makeText(this, "Call Restart funtion", Toast.LENGTH_SHORT).show()
    }


}