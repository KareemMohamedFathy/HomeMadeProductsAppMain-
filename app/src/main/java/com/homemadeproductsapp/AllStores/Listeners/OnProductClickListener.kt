package com.homemadeproductsapp.AllStores.Listeners

import com.homemadeproductsapp.DB.Product

interface OnProductClickListener {
    fun onProductClick(product: Product)
}