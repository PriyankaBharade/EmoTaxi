package com.emotaxi.widget

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.emotaxi.AddCardActivity
import com.emotaxi.BookingDetailsActivity
import com.emotaxi.R
import com.emotaxi.SignUpActivity
import com.emotaxi.adapter.CardListAdapter
import com.emotaxi.model.BookingModel
import com.emotaxi.model.CardListModel
import com.emotaxi.retrofit.BackEndApi
import com.emotaxi.retrofit.Constant
import com.emotaxi.retrofit.WebServiceClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.fragment_add_payment_bottom_sheet_dialog.*
import kotlinx.android.synthetic.main.fragment_payment_dialog.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private const val LATITUDE = "latitude"
private const val DLATITUDE = "dlatitude"
private const val LONGITUDE = "longitude"
private const val DLONGITUDE = "dlongitude"
private const val SADDRESS = "saddress"
private const val DADDRESS = "daddress"
private const val PRICE = "price"

class AddPaymentBottomSheetDialog : BottomSheetDialogFragment(),
    CardListAdapter.SetOnCloseListener {
    private var latitude: String? = null
    private var dlatitude: String? = null
    private var longitude: String? = null
    private var dlongitude: String? = null
    private var saddress: String? = null
    private var daddress: String? = null
    private var price: String? = null

    private var citys: String? = null
    private var cityd: String? = null
    private var states: String? = null
    private var stated: String? = null
    private var addressformated: String? = null
    private var addresdformated: String? = null
    var civiques = ""
    var civiqued = ""

    private var country: String? = null
    private var postalcode: String? = null
    var customDialogProgress: CustomDialogProgress? = null
    var cardListAdapter: CardListAdapter? = null
    var cardArrayList = ArrayList<CardListModel.Cardinfo>()

    var rv_card: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getString(LATITUDE)
            dlatitude = it.getString(DLATITUDE)
            longitude = it.getString(LONGITUDE)
            dlongitude = it.getString(DLONGITUDE)
            saddress = it.getString(SADDRESS)
            daddress = it.getString(DADDRESS)
            price = it.getString(PRICE)
        }

        getAddress(latitude?.toDouble()!!, longitude?.toDouble()!!)
        //  getAddress(45.40362716968502, -75.79868903114482)
        getAddress1(dlatitude?.toDouble()!!, dlongitude?.toDouble()!!)
        //  getAddress1(45.40645225420341, -75.78916409388052)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_payment_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_card = view.findViewById(R.id.rv_card)
        tv_cancel.setOnClickListener { dialog!!.dismiss() }

        //  rv_list.adapter = PaymentAdapter()

        tv_add_new.setOnClickListener {
            startActivity(Intent(requireContext(), AddCardActivity::class.java))
        }

        tv_booking_without_card.setOnClickListener {
            course("")
        }

        /*if (DataManager.dataManager!=null &&
            DataManager.dataManager.getSignUpModel(requireActivity())!=null &&
            DataManager.dataManager.getSignUpModel(requireActivity()).data[0] != null
            && DataManager.dataManager.getSignUpModel(requireActivity()).data[0].userId != null
            && DataManager.dataManager.getSignUpModel(requireActivity()).data[0].userId.isNotEmpty()
        ) {
            getCardOnStripe()
        }else{
            Toast.makeText(
                requireContext(),
                "Please SignUp Before Booking",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(requireActivity(), SignUpActivity::class.java)
                .putExtra(Constant.CallbackFrom,Constant.PaymentDialog))
        }*/
    }

    fun getCardOnStripe() {
        var hashmap = HashMap<String, String>()
        hashmap["user_id"] = DataManager.dataManager.getSignUpModel(requireContext()).data[0].userId
        hashmap["customer_id"] =
            SessionManager.readString(requireContext(), Constant.CustomerId, "").toString()
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
                    progress_bar.visibility = View.GONE
                    cardArrayList.clear()
                    rv_card!!.adapter = null
                    if (response.body()?.data != null) {
                        if (response.body()?.data!![0].cardinfo != null &&
                            response.body()?.data!![0].cardinfo.size > 0
                        ) {
                            tv_no_record.visibility = View.GONE
                            cardArrayList?.addAll(response.body()?.data!![0].cardinfo)
                            cardListAdapter = CardListAdapter(
                                requireContext(),
                                cardArrayList,
                                this@AddPaymentBottomSheetDialog
                            )
                            rv_card!!.adapter = cardListAdapter
                        } else {
                            tv_no_record.visibility = View.GONE
                        }
                    } else {
                        tv_no_record.visibility = View.GONE
                        Toast.makeText(
                            requireContext(), response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
    }

    override fun onResume() {
        super.onResume()
        // Log.e("TAG","AddPaymentBottomSheetDialog")
        if (DataManager.dataManager != null &&
            DataManager.dataManager.getSignUpModel(requireActivity()) != null &&
            DataManager.dataManager.getSignUpModel(requireActivity()).data[0] != null
            && DataManager.dataManager.getSignUpModel(requireActivity()).data[0].userId != null
            && DataManager.dataManager.getSignUpModel(requireActivity()).data[0].userId.isNotEmpty()
        ) {
            getCardOnStripe()
        } else {
            Toast.makeText(
                requireContext(),
                "Please SignUp Before Booking",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(
                Intent(requireActivity(), SignUpActivity::class.java)
                    .putExtra(Constant.CallbackFrom, Constant.PaymentDialog)
            )
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(
            lat: String,
            log: String,
            dlat: String,
            dlog: String,
            sadd: String,
            dadd: String,
            price: String
        ) =
            AddPaymentBottomSheetDialog().apply {
                arguments = Bundle().apply {
                    putString(LATITUDE, lat)
                    putString(LONGITUDE, log)
                    putString(DLATITUDE, dlat)
                    putString(DLONGITUDE, dlog)
                    putString(SADDRESS, sadd)
                    putString(DADDRESS, dadd)
                    putString(PRICE, price)
                }
            }
    }

    fun getAddress(lat: Double, lng: Double) {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(activity, Locale.getDefault())
        if (geocoder != null) {
            addresses = geocoder.getFromLocation(
                lat,
                lng,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            /* addressformated =
                 addresses[0].getAddressLine(0) */// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            citys = addresses[0].locality
            states = addresses[0].adminArea
            country = addresses[0].countryName
            //  postalcode = addresses[0].getPostalCode()
            civiques = addresses[0].getFeatureName()
            if (addresses[0].thoroughfare == null) {
                var sAddressSplit = addresses[0].getAddressLine(0)!!.split(" ")!!
                    .toTypedArray()
                if (sAddressSplit!!.size > 1) {
                    addressformated = sAddressSplit[1] + " " + sAddressSplit[2]
                }
            } else {
                addressformated = addresses[0].thoroughfare
            }
            Log.e(
                "TAg Address",
                citys + " " + states + " " + country + " " + postalcode + " " + civiques + " " + addressformated
            )
        }
    }

    fun getAddress1(lat: Double, lng: Double) {
        /*  var latitude: String = "45.40362716968502"
             var longitude: String = "-75.79868903114482"
             var dlatitude: String = "45.40645225420341"
             var dlongitude: String = "-75.78916409388052"*/
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(activity, Locale.getDefault())
        if (geocoder != null) {
            addresses = geocoder.getFromLocation(
                lat,
                lng,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            /* addresdformated =
                 addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()*/
            cityd = addresses[0].locality
            stated = addresses[0].adminArea
            country = addresses[0].countryName
            postalcode = addresses[0].postalCode
            civiqued = addresses[0].featureName
            if (addresses[0].thoroughfare == null) {
                var dAddressSplit = addresses[0].getAddressLine(0)!!.split(" ")!!.toTypedArray()
                if (dAddressSplit!!.size > 1) {
                    addresdformated = dAddressSplit[1] + " " + dAddressSplit[2]
                }
            } else {
                addresdformated =
                    addresses[0].thoroughfare
            }
            Log.e(
                "TAg Address",
                cityd + " " + stated + " " + country + " " + postalcode + " " + civiqued + " " + addresdformated
            )
        }
    }

    override fun onCloseListener(
        cardinfo: CardListModel.Cardinfo,
        clickOnView: Boolean,
        position: Int
    ) {
        if (clickOnView) {
            cardArrayList.removeAt(position)
            removecardCardOnStripe(cardinfo)
            cardListAdapter!!.notifyItemRemoved(position)
        } else {
            checkout("", "12345", "1", cardinfo.id, true)
        }
    }

    fun checkout(token: String, bookingId: String, type: String, card_id: String, check: Boolean) {
        customDialogProgress = CustomDialogProgress(requireContext())
        customDialogProgress!!.show()
        var hashmap = HashMap<String, String>()
        hashmap["booking_id"] = bookingId
        hashmap["stripe_token"] = token!!
        hashmap["type"] = type
        hashmap["card_id"] = card_id
        hashmap["customer_id"] =
            SessionManager.readString(requireContext(), Constant.CustomerId, "").toString()
        //   Log.e("TAG Price before", " price " + price)
        //  var priceindouble = price!!.replace("$", "").toDouble()
        //   Log.e("TAG Price after", "" + priceindouble + " price " + price)
        //   var newPrice = Math.round(priceindouble).toString()
        //  Log.e("TAG Price round", price!!.replace("$", "")!!.replace(".", ""))
        if (price!!.contains(",")) {
            hashmap["total_amount"] = price!!.replace("$", "")!!.replace(",", ".")!!
        } else {
            hashmap["total_amount"] = price!!.replace("$", "")!!
        }
        hashmap["user_id"] = ""
        hashmap["email"] =
            SessionManager.readString(requireContext(), Constant.email, "").toString()
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
                    /* val jsonObject =
                         JSONObject(Gson().toJson(response.body().toString()))*/
                    // Log.e("TAG Response", jsonObject.toString())
                    if (response.code() == 200) {
                        if (jsonObject.optInt("status") == 1) {
                            if (jsonObject.optJSONObject("data") != null
                                && jsonObject.optJSONObject("data")
                                    .optJSONObject("response") != null
                                && jsonObject.optJSONObject("data").optJSONObject("response")
                                    .optString("id") != null
                            ) {
                                SessionManager.writeString(
                                    requireActivity(), Constant.token,
                                    jsonObject.optJSONObject("data").optJSONObject("response")
                                        .optString("id")
                                )
                                course("")
                            }
                        } else {
                            alert(jsonObject.optString("message"))
                        }
                    } else {
                        alert(response!!.message())
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    alert(t.message.toString())
                    customDialogProgress?.dismiss()
                }
            })
    }


    fun course(token: String) {
        /*origin.addProperty("latitude", 45.40362716968502)
        origin.addProperty("longitude", -75.79868903114482)
        destination.addProperty("latitude", 45.40645225420341)
        destination.addProperty("longitude", -75.78916409388052)*/
        /* var dateStr: String =
             "\"" + SessionManager.readString(requireActivity(), "selected_date", "") + "\" "*/
        var civiques: String = "\"$civiques\" "
        var civiqued: String = "\"$civiqued\" "
        var citys: String = "\"$citys\" "
        var cityd: String = "\"$cityd\" "
        var saddress1: String = "\"$addressformated\" "
        var daddress1: String = "\"$addresdformated\" "
        var country: String = "\"$country\" "
        // var postalcode: String = "\"$postalcode\" "
        var name: String = "Test"
        var name1: String = "\"$name\" "
        var phoneNumbre: String =
            DataManager.dataManager.getSignUpModel(requireContext()).data.get(0).mobileNo
        var phoneNumbre1: String = "\"$phoneNumbre\" "
        var price: String = "\"$price\" "
        // var sAddressSplit = addressformated!!.split(" ")!!.toTypedArray()
        // var dAddressSplit = addresdformated!!.split(" ")!!.toTypedArray()
        /*  var latitude: String = "45.40362716968502"
          var longitude: String = "-75.79868903114482"
          var dlatitude: String = "45.40645225420341"
          var dlongitude: String = "-75.78916409388052"*/
        //   var addressformatedsplit = ""
        //  var addresdformatedsplit = ""
        /* if (sAddressSplit!!.size > 1) {
             addressformatedsplit = sAddressSplit[1] + " " + sAddressSplit[2]
         }
         if (dAddressSplit!!.size > 1) {
             addresdformatedsplit = dAddressSplit[1] + " " + dAddressSplit[2]
         }*/
        var addressformated1 = "\"$addressformated\" "
        var addressdformated1 = "\"$addresdformated\" "
        Log.e(
            "TAg Click Add Data",
            addressformated1 + " " + addressdformated1
        )
        var value = "{\n" +
                "  \"nb_personne_minimum\": 0,\n" +
                "  \"location\": {\n" +
                "    \"geo\": {\n" +
                "      \"latitude\": " + latitude + ",\n" +
                "      \"longitude\": " + longitude + ",\n" +
                "      \"derniere_mise_a_jour\": \"\"\n" +
                "    },\n" +
                "    \"adresse\": {\n" +
                "      \"civique\": " + civiques + ",\n" +
                "      \"adresse\": " + addressformated1 + ",\n" +
                "      \"ville\": " + citys + ",\n" +
                "      \"region\": " + country + ",\n" +
                "      \"appartement\": \"\",\n" +
                "      \"formatted_adresse\": " + saddress1 + "\n" +
                "    }\n" +
                "  },\n" +
                "  \"destination_location\": {\n" +
                "    \"geo\": {\n" +
                "      \"latitude\": " + dlatitude + ",\n" +
                "      \"longitude\": " + dlongitude + ",\n" +
                "      \"derniere_mise_a_jour\": \"\"\n" +
                "    },\n" +
                "    \"adresse\": {\n" +
                "      \"civique\": " + civiqued + ",\n" +
                "      \"adresse\": " + addressdformated1 + ",\n" +
                "      \"ville\": " + cityd + ",\n" +
                "      \"region\": " + country + ",\n" +
                "      \"appartement\": \"\",\n" +
                "      \"formatted_adresse\": " + daddress1 + "\n" +
                "    }\n" +
                "  },\n" +
                "  \"nom_client\": " + name1 + ",\n" +
                "  \"photo_client\":  \"\" ,\n" +
                "  \"remarque\": " + price + ",\n" +
                "  \"telephone\": " + phoneNumbre1 + ",\n" +
                "  \"type_vehicule_requis\": 10,\n" +
                "  \"liste_restriction\": [\n" +
                "    0\n" +
                "  ],\n" +
                "  \"callback_reference_id\": \"\"\n" +
                "}"
        var gsonstr = Gson().fromJson(value, com.google.gson.JsonObject::class.java)
        Log.e("TAG Value", gsonstr.toString())
        WebServiceClient?.client.create(BackEndApi::class.java)
            .course(
                "Basic RU1POmk0RnVsamRuZ01XU3VtTFM=",
                gsonstr
            )
            .enqueue(object : Callback<BookingModel> {
                override fun onFailure(call: Call<BookingModel>, t: Throwable) {
                    customDialogProgress?.dismiss()
                    Log.e("TAG Booking API Error", t.message.toString())
                    Toast.makeText(
                        requireActivity(),
                        "TAG Booking API Error" + t?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onResponse(
                    call: Call<BookingModel>,
                    response: Response<BookingModel>
                ) {
                    customDialogProgress?.dismiss()
                    // Log.e("TAG Response", response?.code().toString())
                    // Log.e("TAG Response", response!!.body()!!.id.toString())
                    //response?.body()?.distance?.toString()?.let { Log.e("TAG Response", it) }
                    if (response?.code() == 200) {
                        SessionManager.writeString(
                            activity!!,
                            Constant.booking_id,
                            response!!.body()!!.id.toString()
                        )
                        bookingComplete()
                    } else if (response?.code() == 401) {
                        Toast.makeText(
                            requireActivity(),
                            "TAG Booking 401" + response?.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "TAG Booking Else" + response?.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
    }


    fun bookingComplete() {
        var dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.item_booking_done)
        var btn_ok: Button = dialog.findViewById(R.id.btn_ok)
        btn_ok.setOnClickListener {
            SessionManager.writeString(requireActivity(), Constant.Price, price.toString())
            SessionManager.writeString(
                requireActivity(),
                Constant.PickUpLatitude,
                latitude.toString()
            )
            SessionManager.writeString(
                requireActivity(),
                Constant.PickUpLongitude,
                longitude.toString()
            )
            startActivity(
                Intent(requireActivity(), BookingDetailsActivity::class.java)
                    .putExtra("latitude", latitude)
                    .putExtra("longitude", longitude)
                    .putExtra("price", price)
                    .putExtra(
                        "email",
                        SessionManager.readString(requireContext(), Constant.email, "")
                    )
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            dialog.dismiss()
        }
        dialog.show()
    }


    fun removecardCardOnStripe(cardinfo: CardListModel.Cardinfo) {
        var customDialogProgress = CustomDialogProgress(requireContext())
        customDialogProgress?.show()
        var hashmap = HashMap<String, String>()
        hashmap["card_id"] = cardinfo.id
        hashmap["user_id"] = DataManager.dataManager.getSignUpModel(requireContext()).data[0].userId
        hashmap["customer_id"] =
            SessionManager.readString(requireContext(), Constant.CustomerId, "").toString()
        var hashmapheader = HashMap<String, String>()
        hashmapheader["version"] = "1"
        hashmapheader["device_type"] = "android"
        WebServiceClient.client1.create(BackEndApi::class.java)
            .removecard(hashmapheader, hashmap).enqueue(object : Callback<CardListModel> {
                override fun onFailure(call: Call<CardListModel>, t: Throwable) {
                    customDialogProgress?.dismiss()
                }

                override fun onResponse(
                    call: Call<CardListModel>,
                    response: Response<CardListModel>
                ) {
                    if (response?.code() != 200) {
                        customDialogProgress?.dismiss()
                        // cardArrayList.remove(cardinfo)
                    } else {
                        Toast.makeText(
                            requireContext(), response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
    }

    fun alert(msg: String) {
        var alert = AlertDialog.Builder(requireContext())
        alert.setMessage(msg)
        alert.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        alert.show()
    }
}