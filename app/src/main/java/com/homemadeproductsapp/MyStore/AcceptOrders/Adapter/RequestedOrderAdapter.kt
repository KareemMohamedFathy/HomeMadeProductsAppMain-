package com.homemadeproductsapp.MyStore.AcceptOrders.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.homemadeproductsapp.DB.Order
import com.homemadeproductsapp.DB.User
import com.homemadeproductsapp.MyStore.AcceptOrders.Listener.OrderAcceptClickListener
import com.homemadeproductsapp.R


class RequestedOrderAdapter(
    private val listOrder: ArrayList<Order>,
    private val listUsers: ArrayList<User>,
    private val orderAcceptClickListener: OrderAcceptClickListener, private val context1: Context
) : RecyclerView.Adapter<RequestedOrderAdapter.ViewHolder>() {

private lateinit var  context:Context
private lateinit var recycler: RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(
                R.layout.request_order_adapter,
                parent,
                false
            )
        context=context1
        return RequestedOrderAdapter.ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler=recyclerView
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
        holder.spinnerOrderStatus.text=status

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                orderAcceptClickListener.checkOrderDetails(listOrder[position])
            }
        })





        holder.buttonUpdateOrderStatus.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                val popup = PopupMenu(context1,v)
                // Inflate the menu from xml
                // Inflate the menu from xml

                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu())

                // Setup menu item selection
                // Setup menu item selection
                recycler.alpha=0.2F
                popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem): Boolean {
                        return when (item.getItemId()) {
                        else-> {
                            holder.spinnerOrderStatus.text = item.toString()
                            listOrder[position].order_status=item.toString()
                            val dbref= FirebaseDatabase.getInstance().reference.child("Order").child(listOrder[position].order_id.toString()).setValue(listOrder[position])


                            recycler.alpha=1.0F

                            false
                        }


                        }
                    }
                })
                popup.setOnDismissListener{recycler.alpha=1.0F}
                popup.show()



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
        var spinnerOrderStatus:TextView = itemView.findViewById(R.id.spinnerOrderStatus)
        var buttonUpdateOrderStatus:TextView=itemView.findViewById(R.id.buttonUpdateOrderStatus)

    }

}