package com.homemadeproductsapp.DB


import com.google.firebase.database.IgnoreExtraProperties
import java.time.LocalDate
import java.time.Month
import java.util.*


@IgnoreExtraProperties
data class Category(
    var mainCategory: String? = "",
    var subCategory: String? = "",
    var category_id: String? = ""
)