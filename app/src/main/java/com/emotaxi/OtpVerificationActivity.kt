package com.emotaxi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.emotaxi.model.SignUpModel
import com.emotaxi.model.TokenModel
import com.emotaxi.retrofit.BackEndApi
import com.emotaxi.retrofit.Constant
import com.emotaxi.retrofit.WebServiceClient
import com.emotaxi.widget.CustomDialogProgress
import com.emotaxi.widget.DataManager
import com.emotaxi.widget.SessionManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_otp_verification.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpVerificationActivity : AppCompatActivity() {
    var user_id  = ""
    var  callBackFrom = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)
        user_id = intent.getStringExtra("user_id").toString()
        callBackFrom = intent.getStringExtra(Constant.CallbackFrom).toString()
        btn_verify.setOnClickListener {
            if (squareField.text!!.isNotEmpty()) {
                otpApiCall()
            } else {
                Snackbar.make(main_otp_view, "Enter Valid Otp", Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    private fun otpApiCall() {
        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress?.show()
        var hashMap = HashMap<String, String>()
        hashMap["otp"] = squareField.text.toString()
        hashMap["user_id"] = user_id
        var hashmapheader = HashMap<String, String>()
        hashmapheader["version"] = "1"
        hashmapheader["device_type"] = "android"
        WebServiceClient.client1.create(BackEndApi::class.java).verifyOtp(hashmapheader,hashMap)
            .enqueue(object : Callback<SignUpModel> {
                override fun onFailure(call: Call<SignUpModel>, t: Throwable) {
                    Log.e("Response", t.message.toString())
                    customDialogProgress?.dismiss()
                }

                override fun onResponse(call: Call<SignUpModel>, response: Response<SignUpModel>) {
                    Log.e("Response", response.message())
                    customDialogProgress?.dismiss()
                    if (response.code() == 200){
                        var data = Gson().toJson(response?.body())
                        if(response.body()?.data!=null
                            && response.body()?.data!![0].userId != null
                            && response.body()?.data!![0].userId.isNotEmpty()){
                            SessionManager.writeString(this@OtpVerificationActivity,Constant.CustomerId,response!!.body()!!.data[0].customerId.toString())
                            SessionManager.writeString(this@OtpVerificationActivity, Constant.USER_DATA,data)
                            if(callBackFrom.equals(Constant.Splash)){
                                startActivity(Intent(this@OtpVerificationActivity, MainActivity::class.java))
                            }else{
                               finish()
                            }

                        }else{
                            Toast.makeText(this@OtpVerificationActivity, response.body()?.message,Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this@OtpVerificationActivity, response.body()?.message,Toast.LENGTH_SHORT).show()
                    }

                }

            })
    }

}