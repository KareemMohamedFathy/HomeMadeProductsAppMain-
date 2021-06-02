package com.homemadeproductsapp.DB


import com.google.firebase.database.IgnoreExtraProperties
import java.time.LocalDate
import java.time.Month
import java.util.*


@IgnoreExtraProperties
data class Review(
    var review_id: String? = "",
    var comment: String? = "",
    var rating: String? = "",
    var item_id: String=""
)