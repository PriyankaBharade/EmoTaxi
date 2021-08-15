package com.emotaxi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.emotaxi.drawPath.DownloadTask
import com.emotaxi.model.EstimationModel
import com.emotaxi.model.VehicleListModel
import com.emotaxi.retrofit.BackEndApi
import com.emotaxi.retrofit.SetonGoogleDistanceListener
import com.emotaxi.retrofit.WebServiceClient
import com.emotaxi.widget.CustomDialogProgress
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LocationTrackingActivity : AppCompatActivity(), OnMapReadyCallback,
    SetonGoogleDistanceListener {
    private var mMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /* var latitude: Double = 0.0
     var longitude: Double = 0.0
     var dlatitude: Double = 0.0
     var dlongitude: Double = 0.0*/
    private val locationRequestCode = 1000

    var latitudestr: String? = null
    var longitudestr: String? = null
    var dlatitudestr: String? = null
    var dlongitudestr: String? = null
    var customDialogProgress: CustomDialogProgress? = null
    var tv_current_location: EditText? = null
    var input_destination: EditText? = null
    var sourceAddress: String? = null
    var destinationAddress: String? = null
    var rl_swipe: RelativeLayout? = null

    companion object {
        var tv_avg_price: TextView? = null
        var tv_reach_time: TextView? = null
        var tv_amount: TextView? = null
        var tv_car_no: TextView? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_tracking)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.googleMap) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        tv_current_location = findViewById(R.id.tv_current_location)
        input_destination = findViewById(R.id.input_destination)
        tv_car_no = findViewById(R.id.tv_car_no)
        tv_avg_price = findViewById(R.id.tv_avg_price)
        tv_reach_time = findViewById(R.id.tv_reach_time)
        tv_amount = findViewById(R.id.tv_amount)
        rl_swipe = findViewById(R.id.rl_swipe)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        latitudestr = intent.getStringExtra("latitude")
        longitudestr = intent.getStringExtra("longitude")
        dlatitudestr = intent.getStringExtra("dlatitude")
        dlongitudestr = intent.getStringExtra("dlongitude")
        sourceAddress = intent.getStringExtra("sourceAddress")
        destinationAddress = intent.getStringExtra("destinationAddress")

        rl_swipe?.setOnClickListener {
           /* FragmentPaymentDialog.newInstance(
                latitudestr!!,
                longitudestr!!,
                dlatitudestr!!,
                dlongitudestr!!,
                sourceAddress!!,
                destinationAddress!!
            ).show(supportFragmentManager!!, "TAG")*/
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        checkPermission()
        // setDataOnView(estimationModel = EstimationModel())
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
                    /* longitude = location.longitude
                     latitude = location.latitude*/
                    getVehicleList()
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
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this) { location ->
                        if (location != null) {
                            /* longitude = location.longitude
                             latitude = location.latitude*/
                            getVehicleList()
                            // getAddress()
                        }
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getVehicleList() {
        customDialogProgress = CustomDialogProgress(this)
        customDialogProgress?.show()
        WebServiceClient?.client.create(BackEndApi::class.java)
            .vehicleList(
                "application/json",
                "Basic RU1POmk0RnVsamRuZ01XU3VtTFM=",
                "45.47644548698537",
                "-75.7023787985124",
                "20",
                "10"
            )
            .enqueue(object : Callback<VehicleListModel> {
                override fun onFailure(call: Call<VehicleListModel>, t: Throwable) {
                    customDialogProgress?.dismiss()
                    Log.e("TAG", t.message.toString())
                }

                override fun onResponse(
                    call: Call<VehicleListModel>,
                    response: Response<VehicleListModel>
                ) {
                    customDialogProgress?.dismiss()
                    if (response?.code() == 200) {
                        // setDataOnView()
                        if (response?.body()?.vehicles?.size!! > 0) {
                            addMarkers(response?.body()?.vehicles!!)
                        }
                    } else if (response?.code() == 401) {
                        Toast.makeText(
                            this@LocationTrackingActivity,
                            response?.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@LocationTrackingActivity,
                            response?.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
    }

    private fun setDataOnView(estimationModel: EstimationModel) {
        tv_current_location?.setText(sourceAddress)
        input_destination?.setText(destinationAddress)

        /*  val sLocation = Location(LocationManager.GPS_PROVIDER).apply {
              latitude = latitudestr?.toDouble()!!
              longitude = longitudestr?.toDouble()!!
          }
          val dLocation = Location(LocationManager.GPS_PROVIDER).apply {
              latitude = dlatitudestr?.toDouble()!!
              longitude = dlongitudestr?.toDouble()!!
          }
          val distanceinmeters: Float = sLocation.distanceTo(dLocation)
          val time = getTimeTaken(sLocation, dLocation)*/
        /* if (estimationModel != null) {
             tv_reach_time?.text =
                 estimationModel?.distancevalue.toString() + " " + estimationModel?.distance
             tv_car_no?.text =
                 estimationModel?.durationvalue.toString() + " " + estimationModel?.duration.toString()
             tv_amount?.text = estimationModel?.prix.toString()
             tv_avg_price?.text =
                 estimationModel?.prix.toString() + " - " + (estimationModel?.prix + 2).toString()
         }*/
        /* val url = getURL(
             LatLng(latitudestr?.toDouble()!!, longitudestr?.toDouble()!!),
             LatLng(
                 dlatitudestr?.toDouble()!!,
                 dlongitudestr?.toDouble()!!
             )*/
        val url = getURL(
            LatLng(45.40362716968502!!, -75.7986890311448),
            LatLng(45.51282615850129, -75.63515982768273)
        )
        mMap!!.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        45.40362716968502!!,
                        -75.7986890311448
                    )
                )
                .snippet("42 Rue Paul-Verlaine, Gatineau")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue))
        )
        mMap!!.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        45.51282615850129, -75.63515982768273
                    )
                )
                .snippet("942-976 Chemin d'Aylmer")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.gren))
        )
        drawPath(url)
    }

    private fun drawPath(url: String) {
        // Getting URL to the Google Directions API
        // Getting URL to the Google Directions API
        val downloadTask = DownloadTask(mMap, this@LocationTrackingActivity)
        downloadTask.execute(url)
        /*  val options = PolylineOptions()
          options.color(Color.GRAY)
          options.width(15f)
          async {
              val result = URL(url).readText()
              val parser: Parser = Parser()
              val stringBuilder: StringBuilder = StringBuilder(result)
              val json: JsonObject = parser.parse(stringBuilder) as JsonObject
              uiThread {
                  if (json != null) {
                      val routes = json.array<JsonObject>("routes")
                      if (routes != null && routes.size > 0) {
                          val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                          val polypts =
                              points.flatMap {
                                  decodePoly(
                                      it.obj("polyline")?.string("points")!!
                                  )
                              }
                          options.add(
                              LatLng(
                                  latitudestr?.toDouble()!!, longitudestr?.toDouble()!!
                              )
                          )
                          LatLngBounds.Builder().include(
                              LatLng(
                                  latitudestr?.toDouble()!!, longitudestr?.toDouble()!!
                              )
                          )
                          for (point in polypts) {
                              options.add(point)
                              // LatLngBounds.Builder().include(point)
                          }
                          options.add(
                              LatLng(
                                  dlatitudestr?.toDouble()!!,
                                  dlongitudestr?.toDouble()!!
                              )
                          )
                          //  LatLngBounds.Builder().include(LatLng(22.7532848, 75.8936962))
                          //val bounds = LatLngBounds.Builder().build()
                          mMap!!.addPolyline(options)
                      } else {
                          *//*Toast.makeText(
                            this@TrackOrderMapActivity,
                            "Route not found",
                            Toast.LENGTH_SHORT
                        ).show()*//*
                    }
                } else {
                    *//* Toast.makeText(
                         this@TrackOrderMapActivity,
                         "Route not found",
                         Toast.LENGTH_SHORT
                     ).show()*//*
                }
            }
        }*/
    }

    fun addMarkers(vehicles: MutableList<VehicleListModel.Vehicle>) {
        if (vehicles.size > 0) {
            //tv_reach_time?.text = vehicles[0].kms + " miles"
            for (i in 0 until vehicles.size) {
                mMap!!.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                vehicles[i].latitude,
                                vehicles[i].longitude
                            )
                        )
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.active_car))
                )
            }

            val builder: LatLngBounds.Builder = LatLngBounds.Builder()
            builder.include(LatLng(vehicles[0].latitude, vehicles[0].longitude)).include(
                LatLng(
                    vehicles[vehicles.size - 1].latitude,
                    vehicles[vehicles.size - 1].longitude
                )
            )
            val cameraUpdate: CameraUpdate =
                CameraUpdateFactory.newLatLngBounds(builder.build(), 100)
            mMap!!.moveCamera(cameraUpdate)
            getEstimation()
        }
    }


    /* fun drawPath() {
         val options = PolylineOptions()
         options.color(Color.GREEN)
         options.width(5f)
         var latlng1: LatLng = LatLng(latitudestr?.toDouble()!!, longitudestr?.toDouble()!!)
         var latlng2: LatLng = LatLng(dlatitudestr?.toDouble()!!, dlongitudestr?.toDouble()!!)
         val url = getURL(latlng1, latlng2)
     }

     private fun getURL(from: LatLng, to: LatLng): String {
         val origin = "origin=" + from.latitude + "," + from.longitude
         val dest = "destination=" + to.latitude + "," + to.longitude
         val sensor = "sensor=false"
         val params = "$origin&$dest&$sensor"
         return "https://maps.googleapis.com/maps/api/directions/json?$params"
     }

     */
    /**
     * Method to decode polyline points
     * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     *//*
    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }

        return poly
    }

    private fun DistanceTo(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
        unit: String
    ): Double {
        val rlat1 = Math.PI * lat1 / 180.0f
        val rlat2 = Math.PI * lat2 / 180.0f
        val rlon1 = Math.PI * lon1 / 180.0f
        val rlon2 = Math.PI * lon2 / 180.0f

        val theta = lon1 - lon2
        val rtheta = Math.PI * theta / 180.0f

        var dist =
            Math.sin(rlat1) * Math.sin(rlat2) + Math.cos(rlat1) * Math.cos(rlat2) * Math.cos(rtheta)
        dist = Math.acos(dist)
        dist = dist * 180.0f / Math.PI
        dist = dist * 60.0f * 1.1515f

        if (unit === "K") {
            dist = dist * 1.609344f
        }
        if (unit === "M") {
            dist = dist * 1.609344 * 1000f
        }
        if (unit === "N") {
            dist = dist * 0.8684f
        }
        return dist
    }


    fun getTimeTaken(source: Location, dest: Location?): String? {
        val meter: Float = source.distanceTo(dest)
        val kms = meter / 1000
        val kms_per_min = 0.5
        val mins_taken = kms / kms_per_min
        val totalMinutes = mins_taken.toInt()
        Log.d("ResponseT", "meter :$meter kms : $kms mins :$mins_taken")
        return if (totalMinutes < 60) {
            "$totalMinutes mins"
        } else {
            var minutes = Integer.toString(totalMinutes % 60)
            minutes = if (minutes.length == 1) "0$minutes" else minutes
            (totalMinutes / 60).toString() + " hour " + minutes + "mins"
        }
    }*/

    fun getEstimation() {
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
                    //  setDataOnView(estimationModel = EstimationModel())
                }

                override fun onResponse(
                    call: Call<EstimationModel>,
                    response: Response<EstimationModel>
                ) {
                    //  customDialogProgress?.dismiss()
                    Log.e("TAG Response", response?.code().toString())
                    response?.body()?.distance?.toString()?.let { Log.e("TAG Response", it) }
                    if (response?.code() == 200) {
                        // setDataOnView(response?.body()!!)
                    } else if (response?.code() == 401) {
                        Toast.makeText(
                            this@LocationTrackingActivity,
                            response?.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@LocationTrackingActivity,
                            response?.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
    }

    private fun getURL(from: LatLng, to: LatLng): String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/directions/json?$params\$&key" + "=AIzaSyAA11xopQ1pUTPpZdRnRnQD8mEWvWlSQSI"
    }


    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }

    override fun onGoogleResponseListener(distance: String, time: String) {
        Log.e("Distance", distance)
        Log.e("time", time)

    }


}