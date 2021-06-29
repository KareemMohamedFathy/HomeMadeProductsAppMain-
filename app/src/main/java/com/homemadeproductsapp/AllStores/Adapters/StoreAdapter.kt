package com.homemadeproductsapp.AllStores.Adapters

import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.homemadeproductsapp.AllStores.Listeners.OnProductClickListener
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.R
import java.security.acl.Group

class StoreAdapter(
    private val list: List<Product>,
    private val OnProductClickListener: OnProductClickListener
) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    private lateinit var   context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.store_adapter, parent, false)
        context=parent.context
        return ViewHolder(view)

    }

    private  var pos: Int=0

    val count=itemCount

    private  var arrayListpos =IntArray(count)


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.imageViewImageSwitcher.removeAllViews()
        holder.imageViewImageSwitcher.setFactory(ViewSwitcher.ViewFactory { // TODO Auto-generated method stub

            val imageView = ImageView(context)
            imageView.scaleType = ImageView.ScaleType.FIT_XY

            val params: ViewGroup.LayoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )

            imageView.layoutParams = params
            imageView


        })



        holder.textViewDescription.setText(list[position].description)
        holder.textViewName.setText(list[position].name)
        holder.textViewPrice.setText("" + list[position].price + "EGP")
        holder.imageViewImageSwitcher.setImageURI(list[position].uriPaths!!.get(arrayListpos[position]).toUri())

        holder.imageViewNext.setOnClickListener {

            if ( arrayListpos[position] < list[position].uriPaths!!.size-1){
                arrayListpos[position]++
                holder.imageViewImageSwitcher.setImageURI(
                    list[position].uriPaths!!.get(arrayListpos[position]).toUri()
                )
            }
            else{
                arrayListpos[position]=0
                holder.imageViewImageSwitcher.setImageURI(
                    list[position].uriPaths!!.get(arrayListpos[position]).toUri()
                )

            }
        }

        //switch to previous image clicking this button
        holder.imageViewBack.setOnClickListener {
            if (arrayListpos[position] > 0){
                arrayListpos[position]--
                holder.imageViewImageSwitcher.setImageURI(
                    list[position].uriPaths!!.get(arrayListpos[position]).toUri()
                )
            }
            else{
                arrayListpos[position]=list[position].uriPaths!!.size-1
                holder.imageViewImageSwitcher.setImageURI(
                    list[position].uriPaths!!.get(arrayListpos[position]).toUri()
                )

            }

        }
        holder.itemView.setOnClickListener {
            OnProductClickListener.onProductClick(list[position])
        }
        if(list[position].copies==0){
            holder.itemView.isEnabled=false
            holder.itemView.alpha=0.4f
           holder.textViewOutOfStock.visibility=androidx.constraintlayout.widget.Group.VISIBLE
            holder.textViewOutOfStock.alpha=1.0f

        }
        else{
            holder.itemView.isEnabled=true
            holder.itemView.alpha=1.0f
            holder.textViewOutOfStock.visibility=androidx.constraintlayout.widget.Group.GONE


        }

    }




    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var textViewName :TextView=itemView.findViewById(R.id.textViewName)
        var textViewDescription :TextView=itemView.findViewById(R.id.textViewDescription)
        var textViewPrice:TextView =itemView.findViewById(R.id.textViewPrice)
//        var imageViewProduct:ImageView =itemView.findViewById(R.id.imageViewProduct)
        var imageViewImageSwitcher: ImageSwitcher =itemView.findViewById(R.id.imageViewImageSwitcher)
        var imageViewNext:ImageView =itemView.findViewById(R.id.ImageViewNext)
        var imageViewBack:ImageView =itemView.findViewById(R.id.ImageViewBack)
        var textViewOutOfStock:TextView=itemView.findViewById(R.id.textViewOutOfStock)






    }

    override fun getItemCount(): Int {
        return list.size
    }

}