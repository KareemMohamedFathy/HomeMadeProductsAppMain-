package com.homemadeproductsapp.DB


import com.google.firebase.database.IgnoreExtraProperties
import java.time.LocalDate
import java.time.Month
import java.util.*


@IgnoreExtraProperties
data class PurchasesHistory(
    var dateOfPurchase: String? = "",
    var item_id: String? = "",
    var store_id: String? = "",
    var price: String? = ""

)