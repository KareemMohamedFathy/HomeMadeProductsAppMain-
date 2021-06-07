package com.homemadeproductsapp.DB



import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties

data class Store(
        var store_id:String,
        var store_name:String="",
        var store_logo:String="",
        var store_description:String="",
        var mainCategoryName: String? = "",
        var shippingTime: String? = "",

        var owner_id: String? = ""
)
