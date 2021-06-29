package com.homemadeproductsapp.DB


import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Feed(
        var caption: String? = "",
        var id: String? = "",
        var imagePathProduct :String? = "",
        var store_id :String? = "",

        var addDate :String? = ""


)
