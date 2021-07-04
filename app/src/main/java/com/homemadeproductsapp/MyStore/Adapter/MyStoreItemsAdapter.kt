package com.homemadeproductsapp.MyStore.Adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.MyStore.Listeners.EditItemClickListener
import com.homemadeproductsapp.MyStore.dataCommunication
import com.homemadeproductsapp.R
import com.homemadeproductsapp.SquareImageView

class MyStoreItemsAdapter(private val list: ArrayList<Product>, private val dataCommunication: dataCommunication, val editItemClickListener: EditItemClickListener) : RecyclerView.Adapter<MyStoreItemsAdapter.ViewHolder>() {

    private lateinit var   context: Context
    private lateinit var circularProgressDrawable:CircularProgressDrawable

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val view =
        LayoutInflater.from(parent.context).inflate(R.layout.item_adapter_layout, parent, false)
context=parent.context
        circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 50f
        circularProgressDrawable.start()


    return ViewHolder(view)

    }

    val count=itemCount

    private  var arrayListpos =IntArray(count)
    private var countkuso=0
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(holder.imageViewImageSwitcher.isEmpty()) {
            holder.imageViewImageSwitcher.setFactory(ViewSwitcher.ViewFactory { // TODO Auto-generated method stub

                val imageView = SquareImageView(context)
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
        Glide.with(context).load(list[position].uriPaths!!.get(arrayListpos[position])).skipMemoryCache(false).placeholder(circularProgressDrawable).into(holder.imageViewImageSwitcher.currentView as ImageView)



    holder.imageViewNext.setOnClickListener {
        if (arrayListpos[position] < list[position].uriPaths!!.size - 1) {
            arrayListpos[position]++
                list[position].uriPaths!!.get(arrayListpos[position]).toUri()
            Glide.with(context).load(list[position].uriPaths!!.get(arrayListpos[position])).skipMemoryCache(false).placeholder(circularProgressDrawable).into(holder.imageViewImageSwitcher.currentView as ImageView)

        } else {
            arrayListpos[position] = 0
                    Glide.with(context).load(list[position].uriPaths!!.get(arrayListpos[position])).skipMemoryCache(false).placeholder(circularProgressDrawable).into(holder.imageViewImageSwitcher.currentView as ImageView)



        }
    }

    //switch to previous image clicking this button
    holder.imageViewBack.setOnClickListener {
        if (arrayListpos[position] > 0) {
            arrayListpos[position]--
            Glide.with(context).load(list[position].uriPaths!!.get(arrayListpos[position])).skipMemoryCache(false).placeholder(circularProgressDrawable).into(holder.imageViewImageSwitcher.currentView as ImageView)

        } else {
            arrayListpos[position] = list[position].uriPaths!!.size - 1
                    Glide.with(context).load(list[position].uriPaths!!.get(arrayListpos[position])).skipMemoryCache(false).placeholder(circularProgressDrawable).into(holder.imageViewImageSwitcher.currentView as ImageView)



        }


}
        if(dataCommunication.switchMode=="ON"){
            holder.removeItem.visibility=View.VISIBLE
            holder.removeItem.setOnClickListener(object :View.OnClickListener{
                override fun onClick(v: View?) {

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Remove Product")
                    builder.setMessage("Are You sure you want to remove this product from your store?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        Toast.makeText(context,
                                "Item Removed", Toast.LENGTH_SHORT).show()
                       FirebaseDatabase.getInstance().reference.child("Product").child(list[position].id.toString()).removeValue()
                        list.removeAt(position)
                        notifyDataSetChanged()
                    }
                    builder.setNegativeButton("No") { dialog, which ->
                    }


                    builder.show()

                }

            })


            holder.itemView.setOnClickListener(object:View.OnClickListener{
                override fun onClick(v: View?) {
               editItemClickListener.EditItem(list[position])
                }
            })

        }
        else{
            holder.removeItem.visibility=View.GONE

        }

        if(list[position].copies==0){
            holder.itemView.alpha=0.6f
            holder.textViewOutOfStock.visibility=androidx.constraintlayout.widget.Group.VISIBLE
            holder.textViewOutOfStock.alpha=1.0f

        }
        else{
            holder.itemView.alpha=1.0f
            holder.textViewOutOfStock.visibility=androidx.constraintlayout.widget.Group.GONE


        }



    }




    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var textViewName :TextView=itemView.findViewById(R.id.textViewName)
        var textViewDescription :TextView=itemView.findViewById(R.id.textViewDescription)
        var textViewPrice:TextView =itemView.findViewById(R.id.textViewPrice)
        var imageViewImageSwitcher:ImageSwitcher =itemView.findViewById(R.id.imageViewImageSwitcher)
        var imageViewNext:ImageView =itemView.findViewById(R.id.ImageViewNext)
        var imageViewBack:ImageView =itemView.findViewById(R.id.ImageViewBack)
        var removeItem:ImageView=itemView.findViewById(R.id.removeItem)
        var textViewOutOfStock:TextView=itemView.findViewById(R.id.textViewOutOfStock)




    }

    override fun getItemCount(): Int {
        return list.size
    }
}
