package com.homemadeproductsapp.AllStores.Adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.homemadeproductsapp.AllStores.DataCommunication
import com.homemadeproductsapp.AllStores.Order.Adapter.OrderConfirmAdapter
import com.homemadeproductsapp.DB.Order
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.MainActivity
import com.homemadeproductsapp.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class OrderConfirmFragment : DialogFragment() {
    private lateinit var view1:View
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var buttonConfrimOrder: Button
    private lateinit var dataCommunication:DataCommunication
    private  var total=0.00
   private val products:ArrayList<Product> = ArrayList<Product>()

    private lateinit var textViewTotal:TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var notifyCart: NotifyCart
    private lateinit var buttonBack: ImageView
    private lateinit var orderDone: OrderDone


    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataCommunication=context as DataCommunication
        notifyCart=context as NotifyCart
        orderDone=context as OrderDone

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         view1=inflater.inflate(R.layout.fragment_order_confirm, container, false)

        view1.setFocusableInTouchMode(true)
        view1.requestFocus()
        view1.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    if (total > 0) {
                        val dbref = FirebaseDatabase.getInstance().reference
                        dbref.child("User").child(auth.currentUser!!.uid).child("Cart").setValue(
                            dataCommunication.cart
                        )


                        dismiss()
                        notifyCart.getCartData()
                    } else {
                        val dbref = FirebaseDatabase.getInstance().reference
                        dataCommunication.cart = null

                        dbref.child("User").child(auth.currentUser!!.uid).child("Cart").removeValue()
                        dismiss()
                        notifyCart.getCartData()

                    }

                }

            }
            false
        })
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth= FirebaseAuth.getInstance()
        bindViews(view1)
        getTotal()
        setupRecyclerView()
        setupClickListeners()

    }

    private fun setupClickListeners() {
        buttonConfrimOrder.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {


                val dbref = FirebaseDatabase.getInstance().reference
                dbref.child("User").child(auth.currentUser!!.uid).child("Cart").setValue(
                    dataCommunication.cart
                )
                val dbref2 = FirebaseDatabase.getInstance().reference.child("Product")
                    for(cat in products){
                        val num= cat.copies!! - dataCommunication.cart!!.itemsIdAmountList[cat.id]!!
                        dbref2.child(cat.id!!).child("copies").setValue(num)
                    }


                val dbref1=FirebaseDatabase.getInstance().reference
                val id=dbref.push().key.toString()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                val order: Order =Order(id, dataCommunication.cart!!.store_id, dataCommunication.cart!!,auth.currentUser!!.uid,currentDate.toString(),"Pending")
                dbref1.child("Order").child(id).setValue(order)
                dataCommunication.cart=null
                dbref.child("User").child(auth.currentUser!!.uid).child("Cart").removeValue()
                orderDone.orderDone()

            }

        })
        buttonBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (total > 0) {
                    val dbref = FirebaseDatabase.getInstance().reference
                    dbref.child("User").child(auth.currentUser!!.uid).child("Cart").setValue(
                        dataCommunication.cart
                    )


                    dismiss()
                    notifyCart.getCartData()
                } else {
                    val dbref = FirebaseDatabase.getInstance().reference
                    dataCommunication.cart = null

                    dbref.child("User").child(auth.currentUser!!.uid).child("Cart").removeValue()
                    dismiss()
                    notifyCart.getCartData()

                }
            }

        })
    }

    private fun getTotal() {
        for(cat in dataCommunication.cart!!.itemsIdPriceList){
            total+=cat.value
            textViewTotal.setText(total.toString() + " EGP ")
            dataCommunication.cart!!.totalPrice=total
        }
        if(total>0.00){
            buttonConfrimOrder.alpha=1.0f
            buttonConfrimOrder.isClickable=true
        }
    }

    private fun setupRecyclerView() {
        val productId:ArrayList<String> = ArrayList<String>()
        val productHash:HashSet<String> = HashSet<String>()


        val cart=dataCommunication.cart!!.itemsIdAmountList
        for(ids in cart) {
            productId.add(ids.key)
            productHash.add(ids.key)

        }
        val dbreference=FirebaseDatabase.getInstance().reference
        dbreference.child("Product").orderByChild("store_id").equalTo(dataCommunication.cart!!.store_id).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (dsp in snapshot.children) {
                            val name = dsp.child("name").value.toString()
                            val id = dsp.child("id").value.toString()
                            val copies = dsp.child("copies").value.toString()
                            val available = when (copies) {
                                "0" -> "Out Of Stock"
                                else -> "Available"
                            }
                            val price = dsp.child("price").value.toString()
                            val description = dsp.child("description").value.toString()
                            val imagePathProduct = dsp.child("imagePathProduct").value.toString()
                            val subCategory = dsp.child("subcategory").value.toString()
                            val imagePathsuri = dsp.child("uriPaths").value as ArrayList<String>


                            val p: Product = Product(
                                name,
                                id,
                                copies.toInt(),
                                available,
                                price.toDouble(),
                                description,
                                imagePathProduct,
                                dataCommunication.cart!!.store_id,
                                subCategory,
                                imagePathsuri
                            )
                            if (productHash.contains(id)) {
                                products.add(p)
                            }

                        }
                    }

                    val clickListener = object : UpdateTotal {
                        override fun updateTotal(amount: Double?) {

                            total += amount!!
                            textViewTotal.setText(total.toString() + " EGP ")
                            dataCommunication.cart!!.totalPrice = total

                            if (total == 0.00) {
                                buttonConfrimOrder.alpha = 0.4f
                                buttonConfrimOrder.isClickable = false

                            }

                        }

                    }

                    val adapter =
                        OrderConfirmAdapter(dataCommunication.cart!!, products, clickListener)
                    val linearLayoutManager = LinearLayoutManager(context)
                    linearLayoutManager.orientation = RecyclerView.VERTICAL
                    recyclerViewProducts.layoutManager = linearLayoutManager
                    recyclerViewProducts.adapter = adapter

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })


    }

    private fun bindViews(view1: View) {
        recyclerViewProducts=view1.findViewById(R.id.recyclerViewProducts)
        buttonConfrimOrder=view1.findViewById(R.id.buttonConfirmOrder)
        textViewTotal=view1.findViewById(R.id.textViewTotalCost)
        buttonBack=view1.findViewById(R.id.back)

    }


}
interface UpdateTotal{
    fun updateTotal(amount: Double?)
}
interface NotifyCart{
      fun getCartData()
}
interface OrderDone{
    fun orderDone()
}

