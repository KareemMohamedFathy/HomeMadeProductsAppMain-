package com.homemadeproductsapp.DB


import android.net.Uri
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Product(

        var name: String? = "",
        var id: String? = "",
        var copies: Int?,
        var available: String?,


        var price: Double?,
        var description: String? = "",
        var imagePathProduct:String? = "",
        var store_id:String? = "",
        var subcategory: String?="",
        var uriPaths: java.util.ArrayList<String>? =ArrayList()


)