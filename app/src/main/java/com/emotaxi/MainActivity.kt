package com.emotaxi

import android.Manifest
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.se.omapi.Session
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.emotaxi.drawPath.DownloadTask
import com.emotaxi.model.VehicleListModel
import com.emotaxi.retrofit.BackEndApi
import com.emotaxi.retrofit.Constant
import com.emotaxi.retrofit.SetonGoogleDistanceListener
import com.emotaxi.retrofit.WebServiceClient
import com.emotaxi.utils.SessionManager
import com.emotaxi.widget.CustomDialogProgress
import com.emotaxi.widget.DataManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_payment_dialog.*
import kotlinx.android.synthetic.main.item_view_header.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback, DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, NavigationView.OnNavigationItemSelectedListener,
    SetonGoogleDistanceListener {
    private var mMap: GoogleMap? = null
    var tv_goto: TextView? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latitude: Double = 0.0
    var currentlatitude: Double = 0.0
    var currentlongitude: Double = 0.0
    var vehiclelatitude: Double = 0.0
    var vehiclelongitude: Double = 0.0
    var longitude: Double = 0.0
    var dlatitude: Double = 0.0
    var dlongitude: Double = 0.0
    var input_current: TextView? = null
    var input_destination: TextView? = null
    var tv_arrive_time: TextView? = null
    var tv_address: TextView? = null
    var tv_add_num: TextView? = null
    private val locationRequestCode = 1000
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    var placesClient: PlacesClient? = null
    var checked: Boolean = true
    var customDialogProgress: CustomDialogProgress? = null
    var imgCall: ImageView? = null
    var image_menu: ImageView? = null
    var image_source_pin: ImageView? = null
    var image_destination_pin: ImageView? = null
    var center_marker: ImageView? = null
    var image_green: ImageView? = null
    var image_blue: ImageView? = null
    var tv_time: TextView? = null
    var main_layout: RelativeLayout? = null
    var drawerlayout: DrawerLayout? = null
    var navigationView: NavigationView? = null

    var day = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0
    var myHour: Int = 0
    var myMinute: Int = 0
    var ok_btn_click: String = "done"
    var addressformated1: String = ""
    var houseNumber: String = ""
    var context: Context? = null
    var mapFragment : SupportMapFragment?= null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_goto = findViewById(R.id.tv_goto)
        input_current = findViewById(R.id.input_current)
        input_destination = findViewById(R.id.input_destination)
        imgCall = findViewById(R.id.imgCall)
        image_menu = findViewById(R.id.image_menu)
        navigationView = findViewById(R.id.navigationView)
        image_source_pin = findViewById(R.id.image_source_pin)
        main_layout = findViewById(R.id.main_layout)
        image_destination_pin = findViewById(R.id.image_destination_pin)
        center_marker = findViewById(R.id.center_marker)
        tv_time = findViewById(R.id.tv_time)
        image_blue = findViewById(R.id.image_blue)
        image_green = findViewById(R.id.image_green)
        drawerlayout = findViewById(R.id.drawer_layout)
        //  Places.initialize(applicationContext, "AIzaSyBHdGhD9IcGYsHtzc5RSRXdFRBlvfayUH4")
        updateLocalization()
        Places.initialize(applicationContext, "AIzaSyD3DBbP5SoQMms35U88ZL-rfCmnZ9jlHA4")
        placesClient = Places.createClient(this)
        mapFragment =
            supportFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        var navigationView: NavigationView = findViewById(R.id.navigationView)
        var navView: View = navigationView.getHeaderView(0)
        var imgCircular: CircleImageView = navView.findViewById(R.id.imgCircular)
        var tv_name: TextView = navView.findViewById(R.id.tv_name)
        var tv_email: TextView = navView.findViewById(R.id.tv_email)
        if (DataManager.dataManager.getGetProfileModel(this) != null &&
            !DataManager.dataManager.getGetProfileModel(this).equals("")
        ) {
            tv_name.text = DataManager.dataManager.getGetProfileModel(this).data[0].username
            tv_email.text = DataManager.dataManager.getGetProfileModel(this).data[0].email
            Glide.with(this).load(
                DataManager.dataManager.getGetProfileModel(this)
                    .data[0].profileImage
            ).into(imgCircular)

        }
        // getNetworkApp()
        /* val current = LocalDateTime.now().toString()
         val formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss.SSS")
         val formatted = current.format(formatter)
         var newdate = SimpleDateFormat("YYYY/mm/dd HH:mm:ss")
         tv_time!!.text = newdate.format(formatted.parse(current))*/
        Log.e("TAG Booking ID" , "" + SessionManager.readString(this, Constant.booking_id,""))
        image_privacy.setOnClickListener {
            val url = "https://www.emo.taxi/privacy-policy/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        direction_floating.setOnClickListener {
            /*  SessionManager.writeString(this,Constant.Price, price.toString())
              SessionManager.writeString(this,Constant.PickUpLatitude,latitude.toString())
              SessionManager.writeString(this,Constant.PickUpLongitude,longitude.toString())*/
            startActivity(
                Intent(this, BookingDetailsActivity::class.java)
                    .putExtra(
                        "latitude",
                        SessionManager.readString(
                            this,
                            Constant.PickUpLatitude,
                            latitude.toString()
                        )
                    )
                    .putExtra(
                        "longitude",
                        SessionManager.readString(
                            this,
                            Constant.PickUpLatitude,
                            latitude.toString()
                        )
                    )
                    .putExtra(
                        "price",
                        SessionManager.readString(
                            this,
                            Constant.PickUpLatitude,
                            latitude.toString()
                        )
                    )
                    /*  .putExtra("email", input_email!!.text!!.toString())*/
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
        if (SessionManager.readString(this, Constant.booking_id, "") != null
            && SessionManager.readString(this, Constant.booking_id, "").toString().isNotEmpty()
        ) {
            direction_floating.visibility = View.VISIBLE
        } else {
            direction_floating.visibility = View.GONE
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        /*btn_ok!!.setOnClickListener {
            when (ok_btn_click) {
                "source" -> {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //  ActivityCompat#requestPermissions
                        //  here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //return
                    }
                    mMap!!.isMyLocationEnabled = true
                    // mMap!!.uiSettings!!.isScrollGesturesEnabled = false
                    btn_ok?.visibility = View.GONE
                    ll_info_window?.visibility = View.VISIBLE
                    ll_center_marker_layout?.visibility = View.GONE
                    if (input_destination?.text?.toString()?.length!! >= 0) {
                        startActivity(
                            Intent(this@MainActivity, ConfirmActivity::class.java)
                                .putExtra("longitude", longitude.toString())
                                .putExtra("latitude", latitude.toString())
                                .putExtra("dlongitude", dlongitude.toString())
                                .putExtra("dlatitude", dlatitude.toString())
                                .putExtra("sourceAddress", input_current?.text?.toString())
                                .putExtra(
                                    "destinationAddress",
                                    input_destination?.text?.toString()
                                )
                        )
                    }
                }
                "destination" -> {
                    mMap!!.isMyLocationEnabled = true
                    //  mMap!!.uiSettings!!.isScrollGesturesEnabled = false
                    btn_ok?.visibility = View.GONE
                    ll_info_window?.visibility = View.VISIBLE
                    ll_center_marker_layout?.visibility = View.GONE
                    if (input_current?.text?.toString()?.length!! >= 0) {
                        startActivity(
                            Intent(this@MainActivity, ConfirmActivity::class.java)
                                .putExtra("longitude", longitude.toString())
                                .putExtra("latitude", latitude.toString())
                                .putExtra("dlongitude", dlongitude.toString())
                                .putExtra("dlatitude", dlatitude.toString())
                                .putExtra("sourceAddress", input_current?.text?.toString())
                                .putExtra(
                                    "destinationAddress",
                                    input_destination?.text?.toString()
                                )
                        )
                    }
                }
                else -> {
                    when {
                        input_current?.text?.toString()?.length!! <= 0 -> {
                            input_current?.error = "Please select your location"
                        }
                        input_destination?.text?.toString()?.length!! <= 0 -> {
                            input_destination?.error = "Please select destination location"
                        }
                        tv_time!!.text!!.toString()!!.equals("Now") -> {
                            Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            startActivity(
                                Intent(this@MainActivity, ConfirmActivity::class.java)
                                    .putExtra("longitude", longitude.toString())
                                    .putExtra("latitude", latitude.toString())
                                    .putExtra("dlongitude", dlongitude.toString())
                                    .putExtra("dlatitude", dlatitude.toString())
                                    .putExtra("sourceAddress", input_current?.text?.toString())
                                    .putExtra(
                                        "destinationAddress",
                                        input_destination?.text?.toString()
                                    )
                            )
                        }
                    }
                }
            }
        }*/

        image_menu?.setOnClickListener {
            drawerlayout?.openDrawer(GravityCompat.START)
        }

        /*    image_source_pin?.setOnClickListener {
                center_marker?.visibility = View.VISIBLE
                *//*   val aniSlide = AnimationUtils.loadAnimation(
                       applicationContext, R.anim.zoomin
                   )
                   image_blue?.startAnimation(aniSlide)
                   aniSlide.repeatCount = 5*//*

//                val animation: Animation =
//                    AlphaAnimation(1, 0) // Change alpha from fully visible to invisible
//                animation.duration = 500 // duration - half a second
//                animation.interpolator = LinearInterpolator() // do not alter
//                animation.repeatCount = Animation.INFINITE // Repeat animation
//                animation.repeatMode = Animation.REVERSE // Reverse animation at the
//                image_blue?.startAnimation(animation)
                checked = true
                mMap?.setOnCameraIdleListener {
                    val midLatLng: LatLng = mMap?.cameraPosition?.target!!
                    latitude = midLatLng?.latitude
                    longitude = midLatLng?.longitude
                    getAddress()
                }
            }*/

        /*image_destination_pin?.setOnClickListener {
            val aniSlide = AnimationUtils.loadAnimation(
                applicationContext, R.anim.zoomout
            )
            image_green?.startAnimation(aniSlide)
            center_marker?.visibility = View.VISIBLE
            checked = false
            mMap?.setOnCameraIdleListener {
                val midLatLng: LatLng = mMap?.cameraPosition?.target!!
                latitude = midLatLng?.latitude
                longitude = midLatLng?.longitude
                getAddress()
            }
        }*/

        navigationView!!.setNavigationItemSelectedListener(this)

        input_current!!.setOnClickListener {
            checked = true
            val earth = 6378.137
            //radius of the earth in kilometer
            val pi = Math.PI
            val m = 1 / (2 * pi / 360 * earth) / 1000 //1 meter in degree
            val new_latitude = latitude + 6000 * m

            val pi1 = Math.PI
            val m1 = 1 / (2 * pi / 360 * earth) / 1000 //1 meter in degree
            val new_longitude = longitude + (6000 * m) / Math.cos(latitude * (pi / 180))
            var bound: RectangularBounds = RectangularBounds.newInstance(
                LatLng(latitude, longitude),
                LatLng(
                    new_latitude,
                    new_longitude
                )
            )
            val fields =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
            // Start the autocomplete intent.
            val intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .setCountry("CA")
                    //.setLocationRestriction(bound)
                    //.setTypeFilter(TypeFilter.REGIONS)
                    .build(this)///
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        input_destination?.setOnClickListener {
            checked = false
            val earth = 6378.137
            //radius of the earth in kilometer
            val pi = Math.PI
            val m = 1 / (2 * pi / 360 * earth) / 1000 //1 meter in degree
            val new_latitude = latitude + 50000 * m

            val pi1 = Math.PI
            val m1 = 1 / (2 * pi / 360 * earth) / 1000 //1 meter in degree
            val new_longitude = longitude + (50000 * m) / Math.cos(latitude * (pi / 180))
            var bound: RectangularBounds = RectangularBounds.newInstance(
                LatLng(latitude, longitude),
                LatLng(
                    new_latitude,
                    new_longitude
                )
            )
            val fields =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountry("CA")
                //.setCountry("IN")
                // .setLocationRestriction(bound)
                .build(this)//
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        imgCall?.setOnClickListener {
            dialogCall()
        }
    }


    private fun addSourcemarkerInfo(latitude: Double, longitude: Double) {
        Log.e("TAG", "Call Addsourcce" + latitude + " " + longitude)
        if (mMap != null) {
            mMap!!.clear()
        }
        mMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    latitude,
                    longitude
                ), 12f
            )
        )
        /*var valueMap1 = MarkerOptions()
            .position(
                LatLng(
                    latitude, longitude
                )
            )
            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue))*/
    }

    private fun dialogCall() {
        var dialog = Dialog(this)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.call_dialog)
        dialog.setCancelable(false)
        val window: Window = dialog.window!!
        val wlp: WindowManager.LayoutParams = window.attributes
        wlp.gravity = Gravity.TOP
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val no: TextView = dialog.findViewById(R.id.no)
        val yes: TextView = dialog.findViewById(R.id.yes)
        val cardView: CardView = dialog.findViewById(R.id.cardView)
        val rightSwipe: Animation = AnimationUtils.loadAnimation(this, R.anim.left_right)
        cardView.startAnimation(rightSwipe)
        main_layout!!.alpha = 0.5f
        yes.setOnClickListener {
            main_layout!!.alpha = 1f
            dialog?.dismiss()
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:819-684-7777")
            startActivity(intent)
        }

        no.setOnClickListener {
            main_layout!!.alpha = 1f
            var rightSwipe: Animation = AnimationUtils.loadAnimation(this, R.anim.right_left)
            cardView.startAnimation(rightSwipe)
            rightSwipe?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    dialog?.dismiss()
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }

            })
        }
        dialog?.show()
    }

    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        //  mMap?.isMyLocationEnabled = true
        mMap!!.uiSettings!!.isCompassEnabled = false
        //   mMap!!.uiSettings!!.isScrollGesturesEnabled = false
        checkPermission()
    }

    fun getVehicleList(
        lat: Double,
        lng: Double,
        addressformated: String,
        knownName: String
    ) {
        /*  "45.47644548698537",
          "-75.7023787985124",*/
        customDialogProgress = CustomDialogProgress(this)
        customDialogProgress?.show()
        WebServiceClient?.client.create(BackEndApi::class.java)
            .vehicleList(
                "application/json",
                "Basic RU1POmk0RnVsamRuZ01XU3VtTFM=",
                /*"45.47644548698537",
                "-75.7023787985124",*/
                lat.toString(),
                lng.toString(),
                "7",
                "10"
            )
            .enqueue(object : Callback<VehicleListModel> {
                override fun onFailure(call: Call<VehicleListModel>, t: Throwable) {
                    customDialogProgress?.dismiss()
                    Toast.makeText(
                        this@MainActivity,
                        t?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    // Log.e("TAG Error", t.message.toString())
                }

                override fun onResponse(
                    call: Call<VehicleListModel>,
                    response: Response<VehicleListModel>
                ) {
                    // Log.e("TAG Error", response?.message())
                    /*  Toast.makeText(
                          this@MainActivity,
                          response?.code().toString(),
                          Toast.LENGTH_SHORT
                      ).show()*/
                    customDialogProgress?.dismiss()
                    if (response?.code() == 200) {
                        // setDataOnView()
                        if (response?.body()?.vehicles?.size!! > 0) {
                            addMarkers(response?.body()?.vehicles!!)
                            vehiclelongitude =
                                response?.body()?.vehicles!!.get(0).longitude
                            vehiclelatitude =
                                response?.body()?.vehicles!!.get(0).latitude
                            drawPath(
                                getURL(
                                    LatLng(currentlatitude, currentlongitude),
                                    LatLng(vehiclelatitude, vehiclelongitude)
                                )
                            )
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "No Vehicle found Around you",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else if (response?.code() == 401) {
                        Toast.makeText(
                            this@MainActivity,
                            response?.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
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


    private fun drawPath(url: String) {
        val downloadTask = DownloadTask(mMap, this)
        downloadTask.execute(url)
    }


    /* fun getNetworkApp() {
         customDialogProgress = CustomDialogProgress(this)
         customDialogProgress?.show()
         var map = HashMap<String, String>()
         map.put("user_id", "18")
         WebServiceClient?.client2.create(BackEndApi::class.java)
             .update(map)
             .enqueue(object : Callback<TokenModel> {
                 override fun onFailure(call: Call<TokenModel>, t: Throwable) {
                     customDialogProgress?.dismiss()
                     Toast.makeText(
                         this@MainActivity,
                         t?.message,
                         Toast.LENGTH_SHORT
                     ).show()
                     Log.e("TAG Error", t.message.toString())
                 }

                 override fun onResponse(
                     call: Call<TokenModel>,
                     response: Response<TokenModel>
                 ) {
                     Log.e("TAG Error", response?.message())
                 }

             })
     }*/

    private fun addMarkers(vehicles: List<VehicleListModel.Vehicle>) {
        if (vehicles!!.size > 0) {
            for (i in 0 until vehicles.size!!) {
                val height = 120
                val width = 60
                val bitmapdraw: BitmapDrawable =
                    resources.getDrawable(R.mipmap.car_new) as BitmapDrawable
                val b: Bitmap = bitmapdraw.bitmap
                val smallMarker: Bitmap = Bitmap.createScaledBitmap(b, width, height, false)
                mMap!!.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                vehicles[i].latitude,
                                vehicles[i].longitude
                            )
                        )
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    // .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_new))
                )
            }
            val builder: LatLngBounds.Builder = LatLngBounds.Builder()
            builder.include(LatLng(vehicles[0].latitude, vehicles[0].longitude)).include(
                LatLng(
                    vehicles[vehicles.size - 1].latitude,
                    vehicles[vehicles.size - 1].longitude
                )
            )
            /*    val cameraUpdate: CameraUpdate =
                    CameraUpdateFactory.newLatLngBounds(builder.build(), 100)
                mMap!!.moveCamera(cameraUpdate)*/
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
                    longitude = location.longitude
                    latitude = location.latitude
                    getAddress(latitude, longitude)

                } else {
                    var alert = AlertDialog.Builder(this)
                    alert.setMessage("Please go to the setting screen and turn on your location first")
                    alert.setPositiveButton("Okay" , DialogInterface.OnClickListener { dialog, which ->
                        startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        dialog.dismiss()
                    })
                    alert.show()
                }
            }
        }
    }

    fun getAddress(lat: Double, lng: Double) {
        try {
            var addressformated = ""
            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(this, Locale.getDefault())
            addresses = geocoder.getFromLocation(
                lat,
                lng,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val address: String =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            //  Log.e("TAG ADRESS", addresses[0].premises.toString())
            Log.e("TAG ADRESS12345", addresses[0].toString())
            val city: String = addresses[0].getLocality()
            val state: String = addresses[0].getAdminArea()
            val country: String = addresses[0].getCountryName()
            val postalCode: String = addresses[0].getPostalCode()
            val knownName: String = addresses[0].getFeatureName()
            if (addresses[0].thoroughfare == null) {
                val thoroughfare: String = addresses[0].getAddressLine(0)
                Log.e("TAG ADRESS12345", thoroughfare)
            } else {
                val thoroughfare: String = addresses[0].thoroughfare
                Log.e("TAG ADRESS12345", thoroughfare)
            }
            //  tv_moving_address.text = address
            if (checked!!) {
                if (addresses[0].thoroughfare == null) {
                    var sAddressSplit = addresses[0].getAddressLine(0)!!.split(" ")!!
                        .toTypedArray()
                    if (sAddressSplit!!.size > 1) {
                        addressformated = sAddressSplit[1] + " " + sAddressSplit[2]
                    }
                } else {
                    addressformated = addresses[0].thoroughfare
                }
                input_current?.text = address
                tv_address?.text = address
                currentlatitude = latitude
                currentlongitude = longitude
                addressformated1 = addressformated
                houseNumber = knownName
                addSourcemarkerInfo(lat, lng)
                getVehicleList(lat, lng, addressformated1, knownName)
                var valueMap1 =
                    MarkerOptions().position(LatLng(currentlatitude, currentlongitude)).icon(
                        BitmapDescriptorFactory.fromBitmap(
                            getMarkerBitmapFromView(
                                R.mipmap.car_new,
                                addressformated1,
                                houseNumber,
                                "0"
                            )
                        )
                    )
                mMap!!.addMarker(valueMap1)

            } else {
                input_destination?.text = address
            }
        } catch (e: Exception) {
            Log.e("TAG Errore", e.message.toString())
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
                            longitude = location.longitude
                            latitude = location.latitude
                            getAddress(latitude, longitude)
                        }
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        val location = place.latLng
                        if (checked!!) {
                            latitude = place!!.latLng!!.latitude
                            longitude = place!!.latLng!!.longitude
                            input_current?.text = place!!.address
                            getAddress(latitude, longitude)
                            //onSourcePincallbackClick(latitude, longitude)
                        } else {
                            input_destination?.text = place.address
                            dlatitude = place.latLng!!.latitude!!
                            dlongitude = place.latLng!!.longitude!!
                            getAddress(dlatitude, dlongitude)
                            /* startActivity(
                                 Intent(
                                     this@MainActivity,
                                     BookingDetailsActivity::class.java
                                 )  .putExtra("latitude",latitude?.toDouble()!!)
                                     .putExtra("longitude", longitude?.toDouble()!!)
                             )*/
                            startActivity(
                                Intent(this@MainActivity, ConfirmActivity::class.java)
                                    .putExtra("longitude", longitude.toString())
                                    .putExtra("latitude", latitude.toString())
                                    .putExtra("dlongitude", dlongitude.toString())
                                    .putExtra("dlatitude", dlatitude.toString())
                                    .putExtra("sourceAddress", input_current?.text?.toString())
                                    .putExtra(
                                        "destinationAddress",
                                        input_destination?.text?.toString()
                                    )
                            )
                            // onDestnationCallback(latitude, longitude)
                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        // Log.i(TAG, status.statusMessage)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = day
        myYear = year
        myMonth = month
        val calendar: Calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this@MainActivity, this@MainActivity, hour, minute,
            DateFormat.is24HourFormat(this)
        )
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute
        var dateStr = "$myYear/$myMonth/$myDay $myHour:$myMinute"
        var date = SimpleDateFormat("YYYY/mm/dd hh:mm")
        var newdate = SimpleDateFormat("YYYY/mm/dd HH:mm:ss")
        tv_time!!.text = newdate.format(date.parse(dateStr))
        SessionManager.writeString(this, "selected_date", tv_time!!.text.toString())
    }

    fun onTimeClick(view: View) {
        //  btn_ok?.visibility = View.VISIBLE
        ok_btn_click = "time"
        val calendar: Calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        val datePickerDialog =
            DatePickerDialog(this@MainActivity, this@MainActivity, year, month, day)
        datePickerDialog.show()
    }

    fun onSourcePinClick(view: View) {
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
        // mMap!!.isMyLocationEnabled = false
        //   mMap!!.uiSettings!!.isScrollGesturesEnabled = true
        mMap!!.uiSettings!!.isCompassEnabled = false
        ll_center_marker_layout?.visibility = View.VISIBLE
        btn_ok?.visibility = View.VISIBLE
        ok_btn_click = "source"
        ll_info_window?.visibility = View.GONE
        tv_moving_address!!.setBackgroundColor(resources.getColor(R.color.colorBlue))
        moving_view!!.setBackgroundColor(resources.getColor(R.color.colorBlue))
        center_marker!!.setImageResource(R.mipmap.blue)
        checked = true
        mMap?.setOnCameraIdleListener {
            val midLatLng: LatLng = mMap?.cameraPosition?.target!!
            latitude = midLatLng?.latitude
            longitude = midLatLng?.longitude
            getAddress(latitude, longitude)
        }
    }


    /*  fun onSourcePincallbackClick(lat: Double, log: Double) {
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
          mMap!!.isMyLocationEnabled = false
          // mMap!!.uiSettings!!.isScrollGesturesEnabled = true
          mMap!!.uiSettings!!.isCompassEnabled = false
          ll_center_marker_layout?.visibility = View.VISIBLE
          btn_ok?.visibility = View.VISIBLE
          ok_btn_click = "source"
          ll_info_window?.visibility = View.GONE
          tv_moving_address!!.setBackgroundColor(resources.getColor(R.color.colorBlue))
          moving_view!!.setBackgroundColor(resources.getColor(R.color.colorBlue))
          center_marker!!.setImageResource(R.mipmap.blue)
          checked = true
          latitude = lat
          longitude = log
          getAddress(latitude, longitude)
          *//* mMap?.animateCamera(
             CameraUpdateFactory.newLatLngZoom(
                 LatLng(
                     latitude,
                     longitude
                 ), 16f
             )
         )*//*
    }*/

    fun onDestinationPinClick(view: View) {
        val aniSlide = AnimationUtils.loadAnimation(
            applicationContext, R.anim.zoomout
        )
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
            return
        }
        mMap!!.isMyLocationEnabled = false
        // mMap!!.uiSettings!!.isScrollGesturesEnabled = true
        mMap!!.uiSettings!!.isCompassEnabled = false
        // image_green?.startAnimation(aniSlide)
        ll_center_marker_layout?.visibility = View.VISIBLE
        btn_ok?.visibility = View.VISIBLE
        ok_btn_click = "destination"
        ll_info_window?.visibility = View.GONE
        tv_moving_address!!.setBackgroundColor(resources.getColor(R.color.colorGreen))
        moving_view!!.setBackgroundColor(resources.getColor(R.color.colorGreen))
        center_marker!!.setImageResource(R.mipmap.gren)
        checked = false
        mMap?.setOnCameraIdleListener {
            val midLatLng: LatLng = mMap?.cameraPosition?.target!!
            dlatitude = midLatLng?.latitude
            dlongitude = midLatLng?.longitude
            getAddress(dlatitude, dlongitude)
        }
    }

    /*  fun onDestnationCallback(lat: Double, log: Double) {
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
          mMap!!.isMyLocationEnabled = false
          // mMap!!.uiSettings!!.isScrollGesturesEnabled = true
          mMap!!.uiSettings!!.isCompassEnabled = false
          // image_green?.startAnimation(aniSlide)
          ll_center_marker_layout?.visibility = View.VISIBLE
          btn_ok?.visibility = View.VISIBLE
          ok_btn_click = "destination"
          ll_info_window?.visibility = View.GONE
          tv_moving_address!!.setBackgroundColor(resources.getColor(R.color.colorGreen))
          moving_view!!.setBackgroundColor(resources.getColor(R.color.colorGreen))
          center_marker!!.setImageResource(R.mipmap.gren)
          checked = false
          dlatitude = lat
          dlongitude = log
          getAddress(dlatitude, dlongitude)
          mMap?.animateCamera(
              CameraUpdateFactory.newLatLngZoom(
                  LatLng(
                      dlatitude,
                      dlongitude
                  ), 16f
              )
          )
      }*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navProfile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.english -> {
                startActivity(Intent(this, SelectLanguageActivity::class.java))
                /* SessionManager.writeString(this, Constant.language, "en")
                 val config = resources.configuration
                 val locale = Locale("en")
                 Locale.setDefault(locale)
                 config.locale = locale
                 resources.updateConfiguration(config, resources.displayMetrics)
                 drawerlayout!!.closeDrawers()
                 recreate()*/
            }
            R.id.french -> {
                SessionManager.writeString(this, Constant.language, "fr")
                val config = resources.configuration
                val locale = Locale("fr")
                Locale.setDefault(locale)
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
                drawerlayout!!.closeDrawers()
                recreate()
            }
            /*  R.id.navAddCard -> {
                  startActivity(Intent(this, CardActivity::class.java))
              }*/
            /* R.id.navSetting -> {

             }*/
            R.id.privacy -> {
                val url = "https://www.emo.taxi/privacy-policy/"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }

        }
        item.isChecked = true
        drawerlayout!!.closeDrawers()
        return true
    }


    override fun onRestart() {
        super.onRestart()
        input_destination!!.text = ""
        mapFragment!!.getMapAsync(this)
        updateLocalization()
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
        tv_add_num = customMarkerView.findViewById(R.id.tv_add_num)
        tv_address = customMarkerView.findViewById(R.id.tv_address)
        tv_arrive_time = customMarkerView.findViewById(R.id.tv_arrive_time)
        tv_address!!.text = address
        tv_add_num!!.text = knownName
        tv_arrive_time!!.text = time.replace("mins", "")
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
        Log.e("TAG Time", time)
        if (time != null && time != "") {
            var valueMap1 =
                MarkerOptions().position(LatLng(currentlatitude, currentlongitude)).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getMarkerBitmapFromView(
                            R.mipmap.car_new,
                            addressformated1,
                            houseNumber,
                            time
                        )
                    )
                )
            mMap!!.addMarker(valueMap1)
        }
    }

    private fun updateLocalization() {
        Log.e("SelectedLanguage", "" + SessionManager.readString(this, Constant.language, ""))
        val config = resources.configuration
        val locale = Locale(SessionManager.readString(this, Constant.language, ""))
        Locale.setDefault(locale)
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}

