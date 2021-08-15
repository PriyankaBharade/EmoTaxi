package com.emotaxi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emotaxi.retrofit.Constant
import kotlinx.android.synthetic.main.activity_get_started.*

class GetStartedActivity : AppCompatActivity() {
    var callBackFrom = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)
        callBackFrom = intent.getStringExtra(Constant.CallbackFrom).toString()

        btn_skip.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btn_account.setOnClickListener {
            startActivity(
                Intent(this, SignUpActivity::class.java)
                    .putExtra(Constant.CallbackFrom, Constant.Splash)
            )
        }
    }
}