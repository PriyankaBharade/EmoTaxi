package com.emotaxi

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.emotaxi.ConfirmActivity.Companion.rl_main
import com.emotaxi.adapter.CardListAdapter
import com.emotaxi.model.*
import com.emotaxi.retrofit.BackEndApi
import com.emotaxi.retrofit.Constant
import com.emotaxi.retrofit.WebServiceClient
import com.emotaxi.widget.CustomDialogProgress
import com.emotaxi.widget.DataManager
import com.emotaxi.widget.SessionManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.fragment_payment_dialog.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


private const val LATITUDE = "latitude"
private const val DLATITUDE = "dlatitude"
private const val LONGITUDE = "longitude"
private const val DLONGITUDE = "dlongitude"
private const val SADDRESS = "saddress"
private const val DADDRESS = "daddress"
private const val PRICE = "price"


class FragmentPaymentDialog : DialogFragment(), CardListAdapter.SetOnCloseListener {
    // TODO: Rename and change types of parameters
    private var latitude: String? = null
    private var dlatitude: String? = null
    private var longitude: String? = null
    private var dlongitude: String? = null
    private var saddress: String? = null
    private var daddress: String? = null
    private var addressformated: String? = null
    private var addresdformated: String? = null
    private var citys: String? = null
    private var cityd: String? = null
    private var states: String? = null
    private var stated: String? = null
    private var country: String? = null
    private var postalcode: String? = null
    private var price: String? = null
    var civiques = ""
    var civiqued = ""
    var image_close: ImageView? = null
    var checkbox: CheckBox? = null
    var rl_swipe: RelativeLayout? = null
    var customDialogProgress: CustomDialogProgress? = null
    var setOnDialogDismissListener: SetOnDialogDismissListener? = null
  //  private var stripe: Stripe? = null
    var cardListAdapter: CardListAdapter? = null
    var cardArrayList  = ArrayList<CardListModel.Cardinfo>()
    /*constructor(setOnDialogDismissListener: SetOnDialogDismissListener) {
        this.setOnDialogDismissListener = setOnDialogDismissListener
    }*/

    override fun onStart() {
        super.onStart()
        setWindowParams()
    }

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
        return inflater.inflate(R.layout.fragment_payment_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image_close = view.findViewById(R.id.image_close)
        checkbox = view.findViewById(R.id.checkbox)
        rl_swipe = view.findViewById(R.id.rl_swipe)
        tv_amount.text = price
      /*  stripe = Stripe(
            requireContext(),
            "pk_test_51HvoSsIiMhbBDvFPq6bk1JSnA1xPjIoMw5LbfF8hY2f1sPvKZV94rYsolVuQbhh6HDRzKo7xYbDl12cEsA4gSI2200jBPZKJkn"
        )*/
        image_close?.setOnClickListener {
            this.dialog?.dismiss()
        }
        cardValue!!.setShouldShowPostalCode(false)
        if (SessionManager.readString(
                requireActivity(),
                Constant.name,
                ""
            ) != null && !SessionManager.readString(requireActivity(), Constant.name, "").equals("")
        ) {
            checkbox!!.isChecked = true
            input_name.setText(SessionManager.readString(requireActivity(), Constant.name, ""))
            input_phone.setText(SessionManager.readString(requireActivity(), Constant.number, ""))
            input_email.setText(SessionManager.readString(requireActivity(), Constant.email, ""))
            /* val number = maskCardNumber(
                 SessionManager.readString(requireActivity(), Constant.cardnumber, "").toString(),
                 "xxxx-xxxx-xxxx-####"
             )
             if (number!!.length > 4) {
                 number.substring(number!!.length - 4)
             }*/
            val number =
                SessionManager.readString(requireActivity(), Constant.cardnumber, "").toString()
            cardValue.setCardNumber(
                number
            )
            /* cardValue.setCardNumber(
                 SessionManager.readString(
                     requireActivity(),
                     Constant.cardnumber,
                     ""
                 )
             )*/
            cardValue.setExpiryDate(
                SessionManager.readString(
                    requireActivity(),
                    Constant.expiryMonth,
                    ""
                )!!.toInt(),
                SessionManager.readString(
                    requireActivity(),
                    Constant.expiryYear,
                    ""
                )!!.toInt()
            )

            cardValue.setCvcCode(
                SessionManager.readString(
                    requireActivity(),
                    Constant.cvc,
                    ""
                )
            )
        } else {
            checkbox!!.isChecked = false
        }

        /*checkbox!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SessionManager.writeString(requireActivity(), Constant.name, "")
                SessionManager.writeString(requireActivity(), Constant.email, "")
                SessionManager.writeString(requireActivity(), Constant.number, "")
                SessionManager.writeString(requireActivity(), Constant.cardnumber, "")
                SessionManager.writeString(requireActivity(), Constant.expiryYear, "")
                SessionManager.writeString(requireActivity(), Constant.expiryMonth, "")
                SessionManager.writeString(requireActivity(), Constant.cvc, "")
                SessionManager.writeString(requireActivity(), Constant.postal, "")
                SessionManager.writeString(
                    requireActivity(),
                    Constant.name,
                    input_name!!.text!!.toString()
                )
                SessionManager.writeString(
                    requireActivity(),
                    Constant.email,
                    input_email!!.text!!.toString()
                )
                SessionManager.writeString(
                    requireActivity(),
                    Constant.number,
                    input_phone!!.text!!.toString()
                )
                SessionManager.writeString(
                    requireActivity(),
                    Constant.cardnumber,
                    cardValue.card!!.number!!
                )
                // Log.e("Year", cardValue.card!!.expYear!!.toString())
                // Log.e("Month", cardValue.card!!.expMonth!!.toString())
                SessionManager.writeString(
                    requireActivity(),
                    Constant.expiryYear,
                    "" + cardValue.card!!.expYear!!.toString()
                )
                SessionManager.writeString(
                    requireActivity(),
                    Constant.expiryMonth,
                    "" + cardValue.card!!.expMonth!!
                )
                SessionManager.writeString(requireActivity(), Constant.cvc, cardValue.card!!.cvc!!)
                SessionManager.writeString(
                    requireActivity(),
                    Constant.postal,
                    ""
                )
            } else {
                SessionManager.writeString(requireActivity(), Constant.name, "")
                SessionManager.writeString(requireActivity(), Constant.email, "")
                SessionManager.writeString(requireActivity(), Constant.number, "")
                SessionManager.writeString(requireActivity(), Constant.cardnumber, "")
                SessionManager.writeString(requireActivity(), Constant.expiryYear, "")
                SessionManager.writeString(requireActivity(), Constant.expiryMonth, "")
                SessionManager.writeString(requireActivity(), Constant.cvc, "")
                SessionManager.writeString(requireActivity(), Constant.postal, "")
            }
        }*/

        rl_swipe?.setOnClickListener {
            //course("")
            if (input_name!!.text!!.toString().isEmpty()) {
                input_name.error = "Please enter your name"
            } else if (input_phone!!.text!!.toString().isEmpty()) {
                input_phone.error = "Please enter your phone number"
            } else if (input_email!!.text!!.toString().isEmpty()) {
                input_email.error = "Please enter your email"
            } else if (cardValue!!.card == null) {
                Toast.makeText(activity, "please enter valid card details", Toast.LENGTH_SHORT)
                    .show()
            } else if (cardValue!!.card!!.expMonth!! <= 0) {
                Toast.makeText(activity, "please enter valid card details", Toast.LENGTH_SHORT)
                    .show()
            } else if (cardValue!!.card!!.expYear!! <= 0) {
                Toast.makeText(activity, "please enter valid card details", Toast.LENGTH_SHORT)
                    .show()
            } else if (cardValue!!.card!!.cvc!!.isEmpty()) {
                Toast.makeText(activity, "please enter valid card details", Toast.LENGTH_SHORT)
                    .show()
            } else {
                /*startActivity(
                    Intent(requireActivity(), BookingDetailsActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )*/
                //course("")
                SessionManager.writeString(requireActivity(), Constant.name, "")
                SessionManager.writeString(requireActivity(), Constant.email, "")
                SessionManager.writeString(requireActivity(), Constant.number, "")
                SessionManager.writeString(requireActivity(), Constant.cardnumber, "")
                SessionManager.writeString(requireActivity(), Constant.expiryYear, "")
                SessionManager.writeString(requireActivity(), Constant.expiryMonth, "")
                SessionManager.writeString(requireActivity(), Constant.cvc, "")
                SessionManager.writeString(requireActivity(), Constant.postal, "")
                SessionManager.writeString(
                    requireActivity(),
                    Constant.name,
                    input_name!!.text!!.toString()
                )
                SessionManager.writeString(
                    requireActivity(),
                    Constant.email,
                    input_email!!.text!!.toString()
                )
                SessionManager.writeString(
                    requireActivity(),
                    Constant.number,
                    input_phone!!.text!!.toString()
                )
                SessionManager.writeString(
                    requireActivity(),
                    Constant.cardnumber,
                    cardValue.card!!.number!!
                )
                SessionManager.writeString(
                    requireActivity(),
                    Constant.expiryYear,
                    "" + cardValue.card!!.expYear!!.toString()
                )
                SessionManager.writeString(
                    requireActivity(),
                    Constant.expiryMonth,
                    "" + cardValue.card!!.expMonth!!
                )
                SessionManager.writeString(requireActivity(), Constant.cvc, cardValue.card!!.cvc!!)
                SessionManager.writeString(
                    requireActivity(),
                    Constant.postal,
                    ""
                )
                if (DataManager.dataManager!=null &&
                    DataManager.dataManager.getSignUpModel(requireActivity())!=null &&
                    DataManager.dataManager.getSignUpModel(requireActivity()).data[0] != null
                    && DataManager.dataManager.getSignUpModel(requireActivity()).data[0].userId != null
                    && DataManager.dataManager.getSignUpModel(requireActivity()).data[0].userId.isNotEmpty()
                ) {
                    generateToken()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please SignUp Before Booking",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(requireActivity(), SignUpActivity::class.java)
                        .putExtra(Constant.CallbackFrom,Constant.PaymentDialog))
                }

            }
        }
        if (DataManager.dataManager!=null &&
            DataManager.dataManager.getSignUpModel(requireActivity())!=null &&
            DataManager.dataManager.getSignUpModel(requireActivity()).data[0] != null
            && DataManager.dataManager.getSignUpModel(requireActivity()).data[0].userId != null
            && DataManager.dataManager.getSignUpModel(requireActivity()).data[0].userId.isNotEmpty()
        ) {
            getCardOnStripe()
        }

    }

    private fun setWindowParams() {
        this.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        /* this.dialog?.window?.setLayout(
             LinearLayout.LayoutParams.MATCH_PARENT,
             LinearLayout.LayoutParams.MATCH_PARENT
         )*/
    }

    companion object {
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
            FragmentPaymentDialog().apply {
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

    interface SetOnDialogDismissListener {
        fun onClickDismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        rl_main?.alpha = 1f
    }

    fun generateToken() {
        customDialogProgress = CustomDialogProgress(requireActivity())
        customDialogProgress?.show()
        var hashmap = HashMap<String, String>()
        hashmap["card_number"] = cardValue.card!!.number!!
        hashmap["exp_month"] = cardValue.card!!.expMonth.toString()!!
        hashmap["exp_year"] = cardValue.card!!.expYear.toString()!!
        hashmap["cvv"] = cardValue.card!!.cvc!!
        var hashmapheader = HashMap<String, String>()
        hashmapheader["version"] = "1"
        hashmapheader["device_type"] = "android"
        WebServiceClient?.client1.create(BackEndApi::class.java)
            .getToken(
                hashmapheader,
                hashmap
            ).enqueue(object : Callback<TokenModel> {
                override fun onResponse(
                    call: Call<TokenModel>,
                    response: Response<TokenModel>
                ) {
                    if (response.code() == 200) {
                        if (response!!.body()!!.status == 1) {
                            if (SessionManager.readString(
                                    requireContext(),
                                    Constant.CustomerId,
                                    ""
                                ) != null
                                && SessionManager.readString(
                                    requireContext(),
                                    Constant.CustomerId,
                                    ""
                                )!!.isNotEmpty()
                            ) {
                                if (cardArrayList!!.size > 0){
                                    for (i in 0 until cardArrayList!!.size){
                                        if (cardArrayList!![i]!!.last4 !=
                                            cardValue.card!!.number!!.substring(cardValue.card!!.number!!.length - 4)){
                                            addcardOnStripe(response!!.body()!!.data!!.tokenId)
                                        }else{
                                           // checkout(response!!.body()!!.data!!.tokenId,"12345")
                                            checkout(response!!.body()!!.data!!.tokenId,"12345","2","",false)
                                          // Toast.makeText(requireContext(),"Payment Call" , Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }else{
                                    addcardOnStripe(response!!.body()!!.data!!.tokenId)
                                }
                            } else {
                                createCustomerOnStripe(response!!.body()!!.data!!.tokenId)
                            }
                        } else {
                            customDialogProgress?.dismiss()
                            alert(response!!.body()!!.message)
                             Toast.makeText(
                                 requireContext(),
                                 "" + response!!.body()!!.message,
                                 Toast.LENGTH_SHORT
                             ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<TokenModel>, t: Throwable) {
                    Log.e("TAG value", t.message!!)
                    alert(t.message.toString())
                    /*  Toast.makeText(
                          requireContext(),
                          "Generate Token Errore " + t.message,
                          Toast.LENGTH_SHORT
                      ).show()*/
                    customDialogProgress?.dismiss()
                }

            })
    }

    fun checkout(token: String, bookingId: String, type: String,card_id:String, check: Boolean) {
        var customDialogProgress : CustomDialogProgress?=null
        if (check){
            customDialogProgress = CustomDialogProgress(requireContext())
            customDialogProgress.show()
        }
        var hashmap = HashMap<String, String>()
        hashmap["booking_id"] = bookingId
        hashmap["stripe_token"] = token!!
        hashmap["type"] = type
        hashmap["card_id"] = card_id
        hashmap["customer_id"] = SessionManager.readString(requireContext(),Constant.CustomerId,"").toString()
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
        hashmap["email"] = input_email!!.text!!.toString()
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
                        /*  Toast.makeText(
                              requireContext(),
                              "Checkout API Response" + response!!.body()!!.message,
                              Toast.LENGTH_SHORT
                          ).show()*/
                        if (jsonObject.optInt("status") == 1) {
                            /* Toast.makeText(
                                 requireContext(),
                                 "Checkout API Response" + jsonObject.optString("message"),
                                 Toast.LENGTH_SHORT
                             ).show()*/
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
                             //   Toast.makeText(requireContext(),"Payment Done",Toast.LENGTH_SHORT).show()
                                course("")
                            }
                        } else {
                            alert(jsonObject.optString("message"))
                            /*Toast.makeText(
                                requireContext(),
                                "Checkout API Response" + jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()*/
                        }
                    } else {
                        alert(response!!.message())
                        /*  Toast.makeText(
                              requireContext(),
                              "Checkout API Response" + response!!.message(),
                              Toast.LENGTH_SHORT
                          ).show()*/
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("TAG value", t.message!!)
                    alert(t.message.toString())
                    Toast.makeText(
                        requireContext(),
                        "Checkout API Error" + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    customDialogProgress?.dismiss()
                }
            })
    }


    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         val weakActivity = WeakReference<Activity>(requireActivity())
         // Handle the result of stripe.confirmPayment
         stripe!!.onPaymentResult(
             requestCode,
             data,
             object : ApiResultCallback<PaymentIntentResult> {
                 override fun onSuccess(result: PaymentIntentResult) {
                     val paymentIntent = result.intent
                     val status = paymentIntent.status
                     if (status == StripeIntent.Status.Succeeded) {
                         val gson = GsonBuilder().setPrettyPrinting().create()
                         weakActivity.get()?.let { activity ->
                             displayAlert(
                                 activity,
                                 "Payment succeeded",
                                 gson.toJson(paymentIntent)
                             )
                         }
                     } else if (status == StripeIntent.Status.RequiresPaymentMethod) {
                         weakActivity.get()?.let { activity ->
                             displayAlert(
                                 activity,
                                 "Payment failed",
                                 paymentIntent.lastPaymentError?.message.orEmpty()
                             )
                         }
                     }
                 }

                 override fun onError(e: Exception) {
                     weakActivity.get()?.let { activity ->
                         displayAlert(
                             activity,
                             "Payment failed",
                             e.toString()
                         )
                     }
                 }
             })
     }*/

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

    fun createCustomerOnStripe(token: String) {
        var hashmap = HashMap<String, String>()
        hashmap["stripe_token"] = token
        hashmap["user_id"] = DataManager.dataManager.getSignUpModel(requireContext()).data[0].userId
        var hashmapheader = HashMap<String, String>()
        hashmapheader["version"] = "1"
        hashmapheader["device_type"] = "android"
        WebServiceClient.client1.create(BackEndApi::class.java)
            .createCustomer(hashmapheader, hashmap).enqueue(object : Callback<CreateCustomer> {
                override fun onFailure(call: Call<CreateCustomer>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<CreateCustomer>,
                    response: Response<CreateCustomer>
                ) {
                    if (response.body()?.data != null) {
                        customDialogProgress!!.dismiss()
                        SessionManager.writeString(
                            requireContext(),
                            Constant.CustomerId,
                            response!!.body()!!.data.customerId.toString()
                        )
                        alert("Your account is successfully created on stripe now" +
                                " you are ready for the payment please click on pay button! ")
                    } else {
                        customDialogProgress!!.dismiss()
                        Toast.makeText(
                            requireContext(), response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
    }

    fun addcardOnStripe(token: String) {
        var hashmap = HashMap<String, String>()
        hashmap["stripe_token"] = token
        hashmap["user_id"] = DataManager.dataManager.getSignUpModel(requireContext()).data[0].userId
        hashmap["customer_id"] =
            SessionManager.readString(requireContext(), Constant.CustomerId, "").toString()
        var hashmapheader = HashMap<String, String>()
        hashmapheader["version"] = "1"
        hashmapheader["device_type"] = "android"
        WebServiceClient.client1.create(BackEndApi::class.java)
            .addcard(hashmapheader, hashmap).enqueue(object : Callback<AddCardModel> {
                override fun onFailure(call: Call<AddCardModel>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<AddCardModel>,
                    response: Response<AddCardModel>
                ) {
                    if (response.body()?.data != null) {
                        checkout("","12345","1",response!!.body()!!.data.id,false)
                      /*  Toast.makeText(
                            requireContext(),
                            response!!.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()*/
                    } else {
                        Toast.makeText(
                            requireContext(), response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
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
                    if (response.body()?.data != null) {
                        if (response.body()?.data!![0].cardinfo != null &&
                            response.body()?.data!![0].cardinfo.size > 0
                        ) {
                          //  var cardArrayList = ArrayList<CardListModel.Cardinfo>()
                            cardArrayList?.addAll(response.body()?.data!![0].cardinfo)
                            cardListAdapter = CardListAdapter(
                                requireContext(),
                                cardArrayList,
                                this@FragmentPaymentDialog
                            )
                            rv_card.adapter = cardListAdapter
                        }
                    } else {
                        Toast.makeText(
                            requireContext(), response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
        var name: String = input_name!!.text!!.toString()
        var name1: String = "\"$name\" "
        var phoneNumbre: String = input_phone!!.text!!.toString()
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
                    // customDialogProgress?.dismiss()
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
                        // checkout(token, response?.body()?.id.toString())
                        /* Toast.makeText(
                             requireActivity(),
                             "TAG Booking 200" + response?.message(),
                             Toast.LENGTH_SHORT
                         ).show()*/
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

    fun getAddress(lat: Double, lng: Double) {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(activity, Locale.getDefault())
        if(geocoder != null){
            addresses = geocoder.getFromLocation(
                lat,
                lng,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            /* addressformated =
                 addresses[0].getAddressLine(0) */// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            citys = addresses[0].getLocality()
            states = addresses[0].getAdminArea()
            country = addresses[0].getCountryName()
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
        if(geocoder != null){
            addresses = geocoder.getFromLocation(
                lat,
                lng,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            /* addresdformated =
                 addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()*/
            cityd = addresses[0].getLocality()
            stated = addresses[0].getAdminArea()
            country = addresses[0].getCountryName()
            postalcode = addresses[0].getPostalCode()
            civiqued = addresses[0].getFeatureName()
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
                    .putExtra("latitude", latitude?.toDouble()!!)
                    .putExtra("longitude", longitude?.toDouble()!!)
                    .putExtra("price", price)
                    .putExtra("email", input_email!!.text!!.toString())
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            dialog.dismiss()
        }
        dialog.show()
    }

    fun alert(msg: String) {
        var alert = AlertDialog.Builder(requireContext())
        alert.setMessage(msg)
        alert.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        alert.show()
    }

    fun maskCardNumber(cardNumber: String, mask: String): String? {
        // format the number
        var index = 0
        val maskedNumber = StringBuilder()
        for (i in 0 until mask.length) {
            val c = mask[i]
            if (c == '#') {
                maskedNumber.append(cardNumber[index])
                index++
            } else if (c == 'x') {
                maskedNumber.append(c)
                index++
            } else {
                maskedNumber.append(c)
            }
        }

        // return the masked number
        return maskedNumber.toString()
    }

    override fun onCloseListener(cardinfo: CardListModel.Cardinfo, clickOnView: Boolean,position :Int) {
        if (clickOnView) {
            cardArrayList.removeAt(position)
            removecardCardOnStripe(cardinfo)
        } else {
            checkout("","12345","1",cardinfo.id,true)
        }
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
                        cardListAdapter!!.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            requireContext(), response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
    }


}