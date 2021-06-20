package com.homemadeproductsapp.DB

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Cart(
        var store_id: String? = "",

        var itemsIdAmountList: HashMap<String,Int> = HashMap<String,Int>(),
        var itemsIdPriceList: HashMap<String,Double> = HashMap<String,Double>(),
        var itemsIdPicList: HashMap<String,String> = HashMap<String,String>(),

        var totalPrice:Double



)