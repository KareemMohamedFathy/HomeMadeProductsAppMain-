package com.homemadeproductsapp.MyStore.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.R

class MyStoreItemsAdapter(private val list: List<Product>) : RecyclerView.Adapter<MyStoreItemsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view =
        LayoutInflater.from(parent.context).inflate(R.layout.item_adapter_layout, parent, false)
    return ViewHolder(view)

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("MyStoreItems",list.size.toString())

            holder.textViewDescription.setText(list[position].description)
            holder.textViewName.setText(list[position].name)
            holder.textViewPrice.setText(""+list[position].price+"EGP")
            holder.textViewStatus.setText(list[position].available)
            holder.textViewCopies.setText("Available Copies : "+list[position].copies)
           Glide.with(holder.itemView).load(list[position].imagePathProduct).into(holder.imageViewProduct)

        }




    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var textViewName :TextView=itemView.findViewById(R.id.textViewName)
        var textViewDescription :TextView=itemView.findViewById(R.id.textViewDescription)
        var textViewPrice:TextView =itemView.findViewById(R.id.textViewPrice)
        var textViewStatus:TextView =itemView.findViewById(R.id.textViewStatus)
        var textViewCopies:TextView =itemView.findViewById(R.id.textViewCopies)
        var imageViewProduct:ImageView =itemView.findViewById(R.id.imageViewProduct)




    }

    override fun getItemCount(): Int {
        return list.size
    }
}
