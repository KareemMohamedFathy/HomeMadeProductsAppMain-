package com.homemadeproductsapp.MyStore.Adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.R

class MyStoreItemsAdapter(private val list: List<Product>,private val applicationContext: Context) : RecyclerView.Adapter<MyStoreItemsAdapter.ViewHolder>() {

    private lateinit var   context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view =
        LayoutInflater.from(parent.context).inflate(R.layout.item_adapter_layout, parent, false)
context=parent.context
    return ViewHolder(view)

    }

    val count=itemCount

    private  var arrayListpos =IntArray(count)
    private var countkuso=0
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(holder.imageViewImageSwitcher.isEmpty()) {
            Log.d("kuso","kuso $countkuso")
            countkuso++
            holder.imageViewImageSwitcher.setFactory(ViewSwitcher.ViewFactory { // TODO Auto-generated method stub

                val imageView = ImageView(context)
                imageView.scaleType = ImageView.ScaleType.FIT_XY

                val params: ViewGroup.LayoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )

                imageView.layoutParams = params
                imageView


            })
        }



    holder.textViewDescription.setText(list[position].description)
    holder.textViewName.setText(list[position].name)
    holder.textViewPrice.setText("" + list[position].price + "EGP")

    holder.imageViewImageSwitcher.setImageURI(
        list[position].uriPaths!!.get(arrayListpos[position]).toUri()
    )

    holder.imageViewNext.setOnClickListener {
        if (arrayListpos[position] < list[position].uriPaths!!.size - 1) {
            arrayListpos[position]++
            holder.imageViewImageSwitcher.setImageURI(
                list[position].uriPaths!!.get(arrayListpos[position]).toUri()
            )
        } else {
            arrayListpos[position] = 0
            holder.imageViewImageSwitcher.setImageURI(
                list[position].uriPaths!!.get(arrayListpos[position]).toUri()
            )

        }
    }

    //switch to previous image clicking this button
    holder.imageViewBack.setOnClickListener {
        if (arrayListpos[position] > 0) {
            arrayListpos[position]--
            holder.imageViewImageSwitcher.setImageURI(
                list[position].uriPaths!!.get(arrayListpos[position]).toUri()
            )
        } else {
            arrayListpos[position] = list[position].uriPaths!!.size - 1
            holder.imageViewImageSwitcher.setImageURI(
                list[position].uriPaths!!.get(arrayListpos[position]).toUri()
            )

        }


}
        }




    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var textViewName :TextView=itemView.findViewById(R.id.textViewName)
        var textViewDescription :TextView=itemView.findViewById(R.id.textViewDescription)
        var textViewPrice:TextView =itemView.findViewById(R.id.textViewPrice)
        var imageViewImageSwitcher:ImageSwitcher =itemView.findViewById(R.id.imageViewImageSwitcher)
        var imageViewNext:ImageView =itemView.findViewById(R.id.ImageViewNext)
        var imageViewBack:ImageView =itemView.findViewById(R.id.ImageViewBack)





    }

    override fun getItemCount(): Int {
        return list.size
    }
}
