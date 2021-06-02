package com.homemadeproductsapp.DB


import com.google.firebase.database.IgnoreExtraProperties
import java.time.LocalDate
import java.time.Month
import java.util.*


@IgnoreExtraProperties
data class Producer(
    var id: String? = "",
    var name: String? = "",
    var mobileno: String? = "",
    var personalPhotoPath: String? = "",
    var email: String? = "",
    var store_id :String?="",
    var password: String? = ""

)