package com.homemadeproductsapp.AllStores.Order.Adapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import com.homemadeproductsapp.AllStores.Adapters.UpdateTotal
import com.homemadeproductsapp.DB.Cart
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.R


class OrderConfirmAdapter(private val cart: Cart, private val productId: ArrayList<Product>, private val clickListener: UpdateTotal): RecyclerView.Adapter<OrderConfirmAdapter.ViewHolder>() {


private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_order_confirm_adapter, parent, false)
        return ViewHolder(view)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageSwitcherProduct.setImageURI(cart.itemsIdPicList[productId[position].id]!!.toUri())
        holder.textViewName.text= productId[position].name
        holder.textViewDescription.text= productId[position].description
        Log.d("kuso", cart.itemsIdAmountList[productId[position].id]!!.toString())

        var amount:Int= cart.itemsIdAmountList[productId[position].id]!!
        var price:Double= cart.itemsIdPriceList[productId[position].id]!!
        var copies:Int= productId[position].copies!!
        Log.d("amount",amount.toString())
        Log.d("amount", amount.toString())

        holder.imageViewAdd.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                amount= cart.itemsIdAmountList[productId[position].id]!!
                if(amount<copies) {

                    amount++
                    price = productId[position].price!! * amount
                    cart.itemsIdAmountList.put(productId[position].id!!, amount)
                    cart.itemsIdPriceList.put(productId[position].id!!, price)
                    holder.itemNum.setText(amount.toString())
                    holder.textViewPrice.text = price.toString() + " EGP "
                    clickListener.updateTotal(productId[position].price)
                }
                else{
                    Toast.makeText(context,"No more Copies Available to add", Toast.LENGTH_LONG).show()
                }


            }


        })

        holder.imageViewRemove.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                if(productId.size>0)
                 amount= cart.itemsIdAmountList[productId[position].id]!!

                if(amount>0) {
                    amount--
                    price = productId[position].price!! * amount
                    cart.itemsIdAmountList.put(productId[position].id!!,amount)
                    holder.itemNum.setText(amount.toString())
                    cart.itemsIdPriceList.put(productId[position].id!!,price)
                    holder.textViewPrice.text= price.toString()+" EGP "
                    clickListener.updateTotal(-(productId[position].price)!!)

                    if(amount==0){
                        cart.itemsIdAmountList.remove(productId[position].id)
                        cart.itemsIdPicList.remove(productId[position].id)
                        cart.itemsIdPriceList.remove(productId[position].id)

                        productId.removeAt(position)
                        notifyDataSetChanged()
                        notifyItemRemoved(position)
                    }



                }
            }

        })
        holder.itemNum.setText(amount.toString())
        holder.textViewPrice.text= price.toString()+" EGP "

    }

    override fun getItemCount(): Int {
   return productId.size
    }
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var imageSwitcherProduct:ImageView=itemView.findViewById(R.id.imageSwitcherProduct)
        var itemNum:TextView=itemView.findViewById(R.id.itemNum)
        var textViewName:TextView=itemView.findViewById(R.id.textViewName)
        var textViewPrice:TextView=itemView.findViewById(R.id.textViewPrice)
        var imageViewAdd:ImageView=itemView.findViewById(R.id.imageViewAdd)
        var imageViewRemove:ImageView=itemView.findViewById(R.id.imageViewRemove)
        var textViewDescription:TextView=itemView.findViewById(R.id.textViewDescription)

    }


}