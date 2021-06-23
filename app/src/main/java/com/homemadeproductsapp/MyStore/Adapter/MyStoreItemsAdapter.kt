package com.homemadeproductsapp.MyStore.Adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.R

class MyStoreItemsAdapter(private val list: List<Product>,private val applicationContext: Context) : RecyclerView.Adapter<MyStoreItemsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view =
        LayoutInflater.from(parent.context).inflate(R.layout.item_adapter_layout, parent, false)
    return ViewHolder(view)

    }

    private  var pos: Int=0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageViewImageSwitcher.setFactory(ViewSwitcher.ViewFactory { // TODO Auto-generated method stub

            val imageView = ImageView(applicationContext)
            imageView.scaleType = ImageView.ScaleType.FIT_XY

            val params: ViewGroup.LayoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            imageView.layoutParams = params
            imageView


        })


            holder.textViewDescription.setText(list[position].description)
            holder.textViewName.setText(list[position].name)
            holder.textViewPrice.setText(""+list[position].price+"EGP")

        holder.imageViewImageSwitcher.setImageURI(list[position].uriPaths!!.get(pos).toUri())

        holder.imageViewNext.setOnClickListener {
            if (pos < list[position].uriPaths!!.size-1){
                pos++
                holder.imageViewImageSwitcher.setImageURI(list[position].uriPaths!!.get(pos).toUri())
            }
            else{
                pos=0
                holder.imageViewImageSwitcher.setImageURI(list[position].uriPaths!!.get(pos).toUri())

            }
        }

        //switch to previous image clicking this button
        holder.imageViewBack.setOnClickListener {
            if (pos > 0){
                pos--
                holder.imageViewImageSwitcher.setImageURI(list[position].uriPaths!!.get(pos).toUri())
            }
            else{
            pos=list[position].uriPaths!!.size-1
                holder.imageViewImageSwitcher.setImageURI(list[position].uriPaths!!.get(pos).toUri())

            }

        }

        }




    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var textViewName :TextView=itemView.findViewById(R.id.textViewName)
        var textViewDescription :TextView=itemView.findViewById(R.id.textViewDescription)
        var textViewPrice:TextView =itemView.findViewById(R.id.textViewPrice)
        var imageViewProduct:ImageView =itemView.findViewById(R.id.imageViewProduct)
        var imageViewImageSwitcher:ImageSwitcher =itemView.findViewById(R.id.imageViewImageSwitcher)
        var imageViewNext:ImageView =itemView.findViewById(R.id.ImageViewNext)
        var imageViewBack:ImageView =itemView.findViewById(R.id.ImageViewBack)





    }

    override fun getItemCount(): Int {
        return list.size
    }
}
