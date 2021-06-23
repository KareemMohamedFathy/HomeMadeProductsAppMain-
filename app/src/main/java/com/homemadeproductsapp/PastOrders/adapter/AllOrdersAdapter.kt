package com.homemadeproductsapp.PastOrders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.homemadeproductsapp.DB.Order
import com.homemadeproductsapp.PastOrders.Listener.OrderClickListener
import com.homemadeproductsapp.R

class AllOrdersAdapter(
    private val list: ArrayList<Order>,
    private val listStorePics: HashMap<String, String>,
    private val listStoreNames: HashMap<String, String>,
   private val orderClickListener: OrderClickListener
):RecyclerView.Adapter<AllOrdersAdapter.ViewHolder>(){




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
   val view=LayoutInflater.from(parent.context).inflate(R.layout.all_prev_orders,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.textViewOrderId.text="Order_ID :     "+list[position].order_id
        holder.textViewDate.text=list[position].date
        holder.textViewOrderPrice.text= list[position].cart.totalPrice.toString()+" EGP "
       holder.textViewStoreName.text=listStoreNames[list[position].store_id]
        holder.itemView.setOnClickListener(
            object :View.OnClickListener{
                override fun onClick(v: View?) {
                    orderClickListener.checkOrderDetails(list[position])
                }

            }
        )
        Glide.with(holder.itemView).load(listStorePics[list[position].store_id]).into(holder.imageViewLogo)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var textViewOrderId:TextView=itemView.findViewById(R.id.textViewOrderID)
        var textViewStoreName:TextView=itemView.findViewById(R.id.textViewStoreName)
        var textViewOrderPrice:TextView=itemView.findViewById(R.id.textViewOrderPrice)
        var textViewDate:TextView=itemView.findViewById(R.id.textViewOrderDate)
        var imageViewLogo: ImageView =itemView.findViewById(R.id.storeLogo)



    }
}