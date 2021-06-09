package com.homemadeproductsapp.DB.Local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.DB.Product
import com.mindorks.notesapp.data.local.pref.PrefConstant

object StoreSession {

    private var sharedPreferences: SharedPreferences? = null
    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.applicationContext.getSharedPreferences(PrefConstant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        }
    }

    fun write(key: String, value: Boolean) {
        val editor = sharedPreferences?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    fun read(key: String): Boolean? {
        return sharedPreferences?.getBoolean(key, false)
    }



    fun write(key: String, value: String) {
        val editor = sharedPreferences?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }
    fun writeList(hal: ArrayList<Product>, value: String) {
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        val connectionsJSONString = Gson().toJson(hal)
        editor.putString(value, connectionsJSONString)
        editor.commit()

    }
    fun writeFeed(hal: ArrayList<Feed>, value: String) {
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        val connectionsJSONString = Gson().toJson(hal)
        editor.putString(value, connectionsJSONString)
        editor.commit()

    }


    fun readString(key: String): String? {
        return sharedPreferences?.getString(key, "")
    }

}