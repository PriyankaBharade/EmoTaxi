package com.emotaxi.widget

import android.content.Context
import android.content.SharedPreferences

class SessionManager {
    companion object {
        val PREF_NAME: String = "EmoTaxi"
        val MODE: Int = android.content.Context.MODE_PRIVATE


        fun writeString(context: Context, key: String, value: String) {
            getEditor(context).putString(key, value).commit()
        }


        fun readString(context: Context, key: String, defValue: String): String? {
            return getPreferences(context).getString(key, defValue)
        }

        fun getEditor(context: Context): SharedPreferences.Editor {
            return getPreferences(context).edit();
        }

        fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREF_NAME, MODE);
        }

        fun clear(context: Context) {
            getEditor(context).clear().commit()
           // val intent = Intent(context, SplashScreen::class.java)
          //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
          //  context.startActivity(intent)
        }
    }

}