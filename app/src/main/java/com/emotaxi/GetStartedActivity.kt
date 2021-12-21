package com.emotaxi

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emotaxi.retrofit.Constant
import kotlinx.android.synthetic.main.activity_get_started.*
import java.util.*

class GetStartedActivity : AppCompatActivity() {
    var callBackFrom = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)
        val locale = Locale("fr")
        Locale.setDefault(locale)
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        createConfigurationContext(configuration)
        callBackFrom = intent.getStringExtra(Constant.CallbackFrom).toString()

        btn_skip.setOnClickListener {
            startActivity(
                Intent(this, SignUpActivity::class.java)
                    .putExtra(Constant.CallbackFrom, Constant.Splash)
            )
            finish()
        }

        btn_account.setOnClickListener {
            /* startActivity(
                 Intent(this, SignUpActivity::class.java)
                     .putExtra(Constant.CallbackFrom, Constant.Splash)
             )*/
        }
    }

    override fun onResume() {
        super.onResume()
    }

}