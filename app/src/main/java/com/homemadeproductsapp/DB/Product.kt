package com.homemadeproductsapp.DB


import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Product(
    var name: String? = "",
    var id: String? = "",
    var productionDate: String? = "",
   var copies: Int? ,
    var available: String? ,
    var shippingTime: String? = "",


    var price: Double?,
    var description: String? = "",
    var imagePathProduct :String? = "",
    var storeid :String? = "",
    var imagePathItem :String? = ""

)