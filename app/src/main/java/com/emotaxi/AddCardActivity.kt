package com.emotaxi

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emotaxi.model.AddCardModel
import com.emotaxi.model.CreateCustomer
import com.emotaxi.model.TokenModel
import com.emotaxi.retrofit.BackEndApi
import com.emotaxi.retrofit.Constant
import com.emotaxi.retrofit.WebServiceClient
import com.emotaxi.widget.CustomDialogProgress
import com.emotaxi.widget.DataManager
import com.emotaxi.widget.SessionManager
import kotlinx.android.synthetic.main.activity_add_card.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCardActivity : AppCompatActivity() {
    var customDialogProgress: CustomDialogProgress? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        cardValue.setShouldShowPostalCode(false)

        image_back.setOnClickListener {
            finish()
        }

        tv_save.setOnClickListener {
            when {
//                input_name!!.text!!.toString().isEmpty() -> {
//                    input_name.error = "Please enter your name"
//                }
               /* input_phone!!.text!!.toString().isEmpty() -> {
                    input_phone.error = "Please enter your phone number"
                }*/
                input_email!!.text!!.toString().isEmpty() -> {
                    input_email.error = "Please enter your email"
                }
                cardValue!!.card == null -> {
                    Toast.makeText(this, "please enter valid card details", Toast.LENGTH_SHORT).show()
                }
                cardValue!!.card!!.expMonth!! <= 0 -> {
                    Toast.makeText(this, "please enter valid card details", Toast.LENGTH_SHORT).show()
                }
                cardValue!!.card!!.expYear!! <= 0 -> {
                    Toast.makeText(this, "please enter valid card details", Toast.LENGTH_SHORT).show()
                }
                cardValue!!.card!!.cvc!!.isEmpty() -> {
                    Toast.makeText(this, "please enter valid card details", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    generateToken()
                }
            }
        }
    }

    fun generateToken() {
        SessionManager.writeString(this,Constant.email,input_email.text.toString())
        customDialogProgress = CustomDialogProgress(this)
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
                    Toast.makeText(this@AddCardActivity,"Generate token" + response.body(), Toast.LENGTH_SHORT).show()
                    if (response.code() == 200) {
                        if (response!!.body()!!.status == 1) {
                            if (SessionManager.readString(
                                    this@AddCardActivity,
                                    Constant.CustomerId,
                                    ""
                                ) != null
                                && SessionManager.readString(
                                    this@AddCardActivity,
                                    Constant.CustomerId,
                                    ""
                                )!!.isNotEmpty()
                            ) {
                                addcardOnStripe(response!!.body()!!.data!!.tokenId)
                            }else{
                                createCustomerOnStripe(response!!.body()!!.data!!.tokenId)
                            }
                        } else {
                            customDialogProgress?.dismiss()
                            alert(response!!.body()!!.message)
                        }
                    }
                }

                override fun onFailure(call: Call<TokenModel>, t: Throwable) {
                    Log.e("TAG value", t.message!!)
                    Toast.makeText(this@AddCardActivity,"Generate token Errore" + t.message, Toast.LENGTH_SHORT).show()
                    alert(t.message.toString())
                    customDialogProgress?.dismiss()
                }

            })
    }

    fun createCustomerOnStripe(token: String) {
        var hashmap = HashMap<String, String>()
        hashmap["stripe_token"] = token
        hashmap["user_id"] = DataManager.dataManager.getSignUpModel(this@AddCardActivity).data[0].userId
        var hashmapheader = HashMap<String, String>()
        hashmapheader["version"] = "1"
        hashmapheader["device_type"] = "android"
        WebServiceClient.client1.create(BackEndApi::class.java)
            .createCustomer(hashmapheader, hashmap).enqueue(object : Callback<CreateCustomer> {
                override fun onFailure(call: Call<CreateCustomer>, t: Throwable) {
                    Toast.makeText(this@AddCardActivity,"Create Customer On Stripe Errore" + t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<CreateCustomer>,
                    response: Response<CreateCustomer>
                ) {
                    Toast.makeText(this@AddCardActivity,"Create Customer On Stripe" + response.body(), Toast.LENGTH_SHORT).show()
                    if (response.body()?.data != null) {
                    //    customDialogProgress!!.dismiss()
                        SessionManager.writeString(
                            this@AddCardActivity,
                            Constant.CustomerId,
                            response!!.body()!!.data.customerId.toString()
                        )
                        addcardOnStripe(token)
                    } else {
                        customDialogProgress!!.dismiss()
                        Toast.makeText(
                            this@AddCardActivity, response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
    }

    fun addcardOnStripe(token: String) {
        var hashmap = HashMap<String, String>()
        hashmap["stripe_token"] = token
        hashmap["user_id"] = DataManager.dataManager.getSignUpModel(this).data[0].userId
        hashmap["customer_id"] = SessionManager.readString(this, Constant.CustomerId, "").toString()
        var hashmapheader = HashMap<String, String>()
        hashmapheader["version"] = "1"
        hashmapheader["device_type"] = "android"
        WebServiceClient.client1.create(BackEndApi::class.java)
            .addcard(hashmapheader, hashmap).enqueue(object : Callback<AddCardModel> {
                override fun onFailure(call: Call<AddCardModel>, t: Throwable) {
                    customDialogProgress?.dismiss()
                    Toast.makeText(this@AddCardActivity,"Add Card On Stripe Errore" + t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<AddCardModel>,
                    response: Response<AddCardModel>
                ) {
                    Toast.makeText(this@AddCardActivity,"Add Card On Stripe" + response.body(), Toast.LENGTH_SHORT).show()
                    if (response.body()?.data != null) {
                        customDialogProgress?.dismiss()
                        finish()
                        Toast.makeText(
                            this@AddCardActivity, response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        customDialogProgress?.dismiss()
                        Toast.makeText(
                            this@AddCardActivity, response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
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