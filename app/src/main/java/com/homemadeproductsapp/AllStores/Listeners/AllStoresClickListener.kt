package com.homemadeproductsapp.AllStores.Listeners

import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.DB.Store
import java.io.Serializable

interface AllStoresClickListener:Serializable {
    fun onClick(store: Store)

}