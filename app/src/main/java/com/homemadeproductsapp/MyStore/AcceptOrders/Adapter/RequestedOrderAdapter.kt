package com.homemadeproductsapp.MyStore.AcceptOrders.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.homemadeproductsapp.DB.Order
import com.homemadeproductsapp.DB.User
import com.homemadeproductsapp.MyStore.AcceptOrders.Listener.OrderAcceptClickListener
import com.homemadeproductsapp.R


class RequestedOrderAdapter(
    private val listOrder: List<Order>,
    private val listUsers: List<User>,
    private val orderAcceptClickListener: OrderAcceptClickListener
) : RecyclerView.Adapter<RequestedOrderAdapter.ViewHolder>() {

private lateinit var  context:Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(
                R.layout.request_order_adapter,
                parent,
                false
            )
        context=parent.context
        return RequestedOrderAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(listUsers[position].personalPhotoPath!!.isNotEmpty())
        Glide.with(context).load(listUsers[position].personalPhotoPath).into(holder.imageViewUserPhoto)
        holder.textViewName.text=listUsers[position].name
        holder.textViewEmail.text=listUsers[position].email
        holder.textViewPhonoNo.text=listUsers[position].mobileno
        holder.textViewOrderID.text=listOrder[position].order_id
        holder.textViewOrderDate.text=listOrder[position].date
        holder.textViewOrderPrice.text= listOrder[position].cart.totalPrice.toString()+" EGP "
        val status=listOrder[position].order_status
        val list: kotlin.Array<out String> = context.resources.getStringArray(R.array.Order_Status);
        val pos=list.indexOf(status)

      holder.spinnerOrderStatus.setSelection(pos)
        holder.itemView.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                Log.d("mikasa","mikasa")
                orderAcceptClickListener.checkOrderDetails(listOrder[position])
            }
        })




        holder.buttonUpdateOrderStatus.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val text: String = holder.spinnerOrderStatus.getSelectedItem().toString()
                listOrder[position].order_status=text
                val dbref=FirebaseDatabase.getInstance().reference.child("Order").child(listOrder[position].order_id.toString()).setValue(listOrder[position])

            }

        })





    }

    override fun getItemCount(): Int {
    return listOrder.size
    }

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
       var imageViewUserPhoto:ImageView=itemView.findViewById(R.id.logoPic)
        var textViewName:TextView=itemView.findViewById(R.id.textViewName)
        var textViewEmail:TextView=itemView.findViewById(R.id.textViewEmail)
        var textViewPhonoNo:TextView=itemView.findViewById(R.id.textViewPhonoNo)
        var textViewOrderID:TextView=itemView.findViewById(R.id.textViewOrderID)
        var textViewOrderDate:TextView=itemView.findViewById(R.id.textViewOrderDate)
        var textViewOrderPrice:TextView=itemView.findViewById(R.id.textViewOrderPrice)
        var spinnerOrderStatus:Spinner = itemView.findViewById(R.id.spinnerOrderStatus)
        var buttonUpdateOrderStatus:TextView=itemView.findViewById(R.id.buttonUpdateOrderStatus)

    }

}