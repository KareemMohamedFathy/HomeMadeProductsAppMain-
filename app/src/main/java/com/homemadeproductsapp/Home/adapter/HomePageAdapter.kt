package com.homemadeproductsapp.Home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.DB.Store
import com.homemadeproductsapp.Home.NameClickListener
import com.homemadeproductsapp.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomePageAdapter(val storeTimeLineMap: HashMap<String, Feed>, val storesList: ArrayList<String>, val storesListMap: HashMap<String, Store>,val nameClickListener: NameClickListener) : RecyclerView.Adapter<HomePageAdapter.ViewHolder>() {
    private lateinit var  context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePageAdapter.ViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.home_page_adapter_layout, parent, false)
        context=parent.context

        return HomePageAdapter.ViewHolder(view)

    }

    override fun onBindViewHolder(holder: HomePageAdapter.ViewHolder, position: Int) {
        val store_id=storesList[position]
        holder.captionTextView.text= storeTimeLineMap[store_id]?.caption

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val date1:Date?=sdf.parse(storeTimeLineMap[store_id]!!.addDate)
        val date2 :Date?= sdf.parse(sdf.format(Date()))
        val diff: Long = date2!!.getTime() - date1!!.getTime()

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24



        if(days>=1){
            holder.addDateTextView.text= ("$days d . ")
        }
          else if(hours>=1)
         {
        holder.addDateTextView.text= ("$hours h . ")
          }
        else if(minutes<60){
            holder.addDateTextView.text= ("$minutes m . ")
        }
       else if(seconds>=1){
            holder.addDateTextView.text= ( "$seconds s . ")
        }





        holder.storeNameTextView.text= storesListMap[store_id]!!.store_name.toString()
        holder.storeNameAgainTextView.text= storesListMap[store_id]!!.store_name.toString()
        Glide.with(context).load(storesListMap[store_id]!!.store_logo).into(holder.imagePathStoreLogoImageView)
        Glide.with(context).load(storeTimeLineMap[store_id]?.imagePathProduct).into(holder.imagePathProductImageView)

       holder.storeNameTextView.setOnClickListener(object :View.OnClickListener{
           override fun onClick(v: View?) {
               nameClickListener.NameClickListener(storesListMap[store_id]!!)
           }

       })

        holder.storeNameAgainTextView.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                nameClickListener.NameClickListener(storesListMap[store_id]!!)
            }

        })


    }

    override fun getItemCount(): Int {
   return storesList.size
    }
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
      var  captionTextView:TextView=itemView.findViewById(R.id.textViewCaption)
      var  addDateTextView:TextView=itemView.findViewById(R.id.textViewAddDate)
      var  storeNameTextView:TextView=itemView.findViewById(R.id.textViewStoreName)
        var  storeNameAgainTextView:TextView=itemView.findViewById(R.id.textViewStoreNameAgain)

        var  imagePathProductImageView:ImageView=itemView.findViewById(R.id.imageViewProductPhoto)
       var imagePathStoreLogoImageView:ImageView=itemView.findViewById(R.id.imageViewStoreLogo)


    }


}