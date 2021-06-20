package com.homemadeproductsapp.DB.Local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.DB.Product
import com.mindorks.notesapp.data.local.pref.PrefConstant
import java.lang.reflect.Type

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
    fun writeProduct(hal: Product, value: String) {
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        val connectionsJSONString = Gson().toJson(hal)
        editor.putString(value, connectionsJSONString)
        editor.commit()

    }
    fun readProduct( value: String): ArrayList<Product>? {
        val x=sharedPreferences?.getString(value, null)
        val type: Type = object : TypeToken<Product?>() {}.type
        return Gson().fromJson<ArrayList<Product>>(x, type)
    }

    fun writeFeed(hal: ArrayList<Feed>, value: String) {
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        val connectionsJSONString = Gson().toJson(hal)
        editor.putString(value, connectionsJSONString)
        editor.commit()

    }
    fun readList( value: String): ArrayList<Product>? {
        val x=sharedPreferences?.getString(value, null)
        val type: Type = object : TypeToken<ArrayList<Product?>?>() {}.type
        return Gson().fromJson<ArrayList<Product>>(x, type)

    }

    fun readString(key: String): String? {
        return sharedPreferences?.getString(key, "")
    }

}