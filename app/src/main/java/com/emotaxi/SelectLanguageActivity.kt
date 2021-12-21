package com.emotaxi

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.emotaxi.retrofit.Constant
import com.emotaxi.widget.SessionManager
import kotlinx.android.synthetic.main.activity_select_language.*
import kotlinx.android.synthetic.main.toolbar_back.*
import java.util.*

class SelectLanguageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_language)
        tv_title.text = getString(R.string.select_language)
        updateLocalization()
        imageback.setOnClickListener {
            finish()
        }
        Log.e("SelectedLanguage", "" + SessionManager.readString(this, Constant.language, ""))
        if (SessionManager.readString(this, Constant.language, "").equals("")
            || SessionManager.readString(this, Constant.language, "").equals("en")
        ) {
            english.isChecked = true
            french.isChecked = false
        } else {
            english.isChecked = false
            french.isChecked = true
        }

        english.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                english.setTextColor(resources.getColor(R.color.purple_500))
                SessionManager.writeString(this, Constant.language, "en")
                val config = resources.configuration
                val locale = Locale("en")
                Locale.setDefault(locale)
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
                recreate()
                french.isChecked = false
            } else {
                english.setTextColor(resources.getColor(R.color.black))
            }
        }

        french.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                french.setTextColor(resources.getColor(R.color.purple_500))
                SessionManager.writeString(this, Constant.language, "fr")
                val config = resources.configuration
                val locale = Locale("fr")
                Locale.setDefault(locale)
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
                recreate()
                english.isChecked = false
            } else {
                french.setTextColor(resources.getColor(R.color.black))
            }
        }
    }

    private fun updateLocalization() {
        val config = resources.configuration
        val locale =
            Locale(SessionManager.readString(this, Constant.language, ""))
        Locale.setDefault(locale)
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}