package com.emotaxi.utils

import android.app.Application
import com.stripe.android.PaymentConfiguration

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_TYooMQauvdEDq54NiTphI7jx"
        )
    }
}