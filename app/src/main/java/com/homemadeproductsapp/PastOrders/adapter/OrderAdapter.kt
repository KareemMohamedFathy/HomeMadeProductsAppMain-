package com.homemadeproductsapp.PastOrders.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.homemadeproductsapp.DB.Cart
import com.homemadeproductsapp.DB.Order
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.R

class OrderAdapter(private val cart:Cart, private val productId: ArrayList<Product>) : RecyclerView.Adapter<OrderAdapter.ViewHolder>(){




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.order_details,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.imageSwitcherProduct.setImageURI(cart.itemsIdPicList[productId[position].id]!!.toUri())
        holder.textViewName.text= productId[position].name
        val amount:Int= cart.itemsIdAmountList[productId[position].id]!!
        val price:Double= cart.itemsIdPriceList[productId[position].id]!!
        holder.textViewDescription.text=productId[position].description
        holder.itemNum.setText(amount.toString())
        holder.textViewPrice.text= price.toString()+" EGP "

    }

    override fun getItemCount(): Int {
        return productId.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var imageSwitcherProduct: ImageView =itemView.findViewById(R.id.imageSwitcherProduct)
        var itemNum: TextView =itemView.findViewById(R.id.itemNum)
        var textViewName: TextView =itemView.findViewById(R.id.textViewName)
        var textViewPrice: TextView =itemView.findViewById(R.id.textViewPrice)
        var textViewDescription: TextView =itemView.findViewById(R.id.textViewDescription)

    }
}