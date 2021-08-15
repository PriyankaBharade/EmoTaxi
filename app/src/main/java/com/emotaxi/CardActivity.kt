package com.emotaxi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class CardActivity : AppCompatActivity() {
    var tv_title: TextView? = null
    var imageback: ImageView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        imageback = findViewById(R.id.imageback)
        tv_title = findViewById(R.id.tv_title)
        tv_title?.text = getString(R.string.add_card)
        imageback?.setOnClickListener {
            finish()
        }
    }
}