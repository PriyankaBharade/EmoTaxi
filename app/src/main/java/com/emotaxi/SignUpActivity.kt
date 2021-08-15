package com.emotaxi

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.emotaxi.model.SignUpModel
import com.emotaxi.retrofit.BackEndApi
import com.emotaxi.retrofit.Constant
import com.emotaxi.retrofit.WebServiceClient
import com.emotaxi.widget.CustomDialogProgress
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity(),
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private var mCredentialsApiClient: GoogleApiClient? = null
    private val RC_HINT = 1000
    var  callBackFrom = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        callBackFrom = intent.getStringExtra(Constant.CallbackFrom).toString()
        mCredentialsApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .enableAutoManage(this, this)
            .addApi(Auth.CREDENTIALS_API)
            .build()
        requestHint()

        tv_privacy_policy.setOnClickListener {
            val url = "https://www.emo.taxi/privacy-policy/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        btn_skip.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btn_signup.setOnClickListener {
           // startActivity(Intent(this@SignUpActivity,OtpVerificationActivity::class.java))
            if (input_mobile.text.length >= 10){
                signUpApiCall()
            }else{
                Snackbar.make(mainView,"Enter valid mobile number",Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    private fun signUpApiCall(){
       var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress?.show()
        var hashMap = HashMap<String,String>()
        hashMap.put("mobile_no",input_mobile.text.toString())
        var hashmapheader = HashMap<String, String>()
        hashmapheader["version"] = "1"
        hashmapheader["device_type"] = "android"
        WebServiceClient.client1.create(BackEndApi::class.java).signUpApi(hashmapheader,hashMap)
            .enqueue(object : Callback<SignUpModel>{
                override fun onFailure(call: Call<SignUpModel>, t: Throwable) {
                    Log.e("Response" , t.message.toString())
                    customDialogProgress?.dismiss()
                }

                override fun onResponse(call: Call<SignUpModel>, response: Response<SignUpModel>) {
                    customDialogProgress?.dismiss()
                    if (response.code() == 200){
                        if(response.body()?.data!=null
                            && response.body()?.data!![0].userId != null
                            && response.body()?.data!![0].userId.isNotEmpty()){
                            Snackbar.make(mainView,response.body()?.message.toString(),Snackbar.LENGTH_SHORT).show()
                            startActivity(Intent(this@SignUpActivity,
                                OtpVerificationActivity::class.java)
                                .putExtra("user_id", response.body()?.data!![0].userId)
                                .putExtra(Constant.CallbackFrom,callBackFrom))
                                 finish()
                        }else{
                            Snackbar.make(mainView,response.body()?.message.toString(),Snackbar.LENGTH_SHORT).show()
                        }
                    }else{
                        Snackbar.make(mainView,response.body()?.message.toString(),Snackbar.LENGTH_SHORT).show()
                    }
                }

            })
    }


    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val intent = Auth.CredentialsApi.getHintPickerIntent(
            mCredentialsApiClient, hintRequest
        )
        startIntentSenderForResult(
            intent.intentSender,
            RC_HINT, null, 0, 0, 0
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                val cred: Credential = data?.getParcelableExtra(Credential.EXTRA_KEY)!!
                input_mobile.setText(cred.id)
            } else {
                input_mobile.setText("+1")
                input_mobile.setSelection(2)
            }
        }
    }

    override fun onConnected(p0: Bundle?) {
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }
}