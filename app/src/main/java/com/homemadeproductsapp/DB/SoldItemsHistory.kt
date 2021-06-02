package com.homemadeproductsapp.DB


import com.google.firebase.database.IgnoreExtraProperties
import java.time.LocalDate
import java.time.Month
import java.util.*


@IgnoreExtraProperties
data class SoldItemsHistory(
    var item_id: String? = "",
    var price: Double ,
    var dateOfPurchase: String? = ""
)