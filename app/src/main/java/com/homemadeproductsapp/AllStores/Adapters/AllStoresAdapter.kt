package com.homemadeproductsapp.AllStores.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.homemadeproductsapp.AllStores.Listeners.AllStoresClickListener
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.DB.Store
import com.homemadeproductsapp.MyStore.Adapter.MyStoreItemsAdapter
import com.homemadeproductsapp.MyStore.Listeners.NewsFeedClickListener
import com.homemadeproductsapp.R
import java.io.Serializable

class AllStoresAdapter(private val list: ArrayList<Store>, private val  allStoresClickListener: AllStoresClickListener): RecyclerView.Adapter<AllStoresAdapter.ViewHolder>(),Serializable{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllStoresAdapter.ViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.all_stores_adapter_layout, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: AllStoresAdapter.ViewHolder, position: Int) {

        holder.textViewStoreName.setText(list[position].store_name)
        holder.textViewCategory.setText(list[position].mainCategoryName)
        holder.textViewStoreDescription.setText(list[position].store_description)
        holder.textViewStoreShippingTime.setText("    "+list[position].shippingTime+" Days")
        Glide.with(holder.itemView).load(list[position].store_logo).into(holder.imageViewStoreLogo)
        holder.itemView.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                allStoresClickListener.onClick(list[position])

            }

        }

        )

    }

    override fun getItemCount(): Int {
     return list.size
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var textViewStoreName : TextView =itemView.findViewById(R.id.textViewStoreName)
        var textViewStoreDescription : TextView =itemView.findViewById(R.id.textViewStoreDescription)
        var textViewStoreShippingTime: TextView =itemView.findViewById(R.id.textViewShippingTime)
        var textViewCategory: TextView =itemView.findViewById(R.id.textViewCategoryName)
        var imageViewStoreLogo: ImageView =itemView.findViewById(R.id.imageViewStoreLogo)



    }
}
