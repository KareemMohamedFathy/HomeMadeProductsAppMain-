package com.homemadeproductsapp.PastOrders

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.homemadeproductsapp.DB.Cart
import com.homemadeproductsapp.DB.Order
import com.homemadeproductsapp.PastOrders.Listener.OrderClickListener
import com.homemadeproductsapp.PastOrders.adapter.AllOrdersAdapter
import com.homemadeproductsapp.R

class AllOrdersFragment : Fragment() {
    private lateinit var recyclerViewAllOrders: RecyclerView
    private lateinit var view1:View
    private  var listOrder:ArrayList<Order> = ArrayList<Order>()
    private  var listStorePics:HashMap<String,String> = HashMap()
    private lateinit var orderClickListener: OrderClickListener
    private lateinit var dataCommunication: dataCommunication
    private  var listStoreNames:HashMap<String,String> = HashMap()

    private lateinit var auth: FirebaseAuth
    override fun onAttach(context: Context) {
        super.onAttach(context)
    orderClickListener=context as OrderClickListener
        dataCommunication=context as dataCommunication
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
          view1= inflater.inflate(R.layout.fragment_all_orders, container, false)
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth= FirebaseAuth.getInstance()
        getAllOrders()
        bindViews(view1)


    }

    private fun getAllOrders() {

        val hashSet:HashSet<String> = HashSet()
        var cartPrice: Double =0.00

        val dbref=FirebaseDatabase.getInstance().reference
  val query = dbref.child("Order").orderByChild("user_id").equalTo(auth.currentUser!!.uid)
        query.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(dsp in snapshot.children){
                        val amountMap: HashMap<String, Int> = HashMap()
                        val priceMap: HashMap<String, Double> = HashMap()
                        val picMap: HashMap<String, String> = HashMap()

                        val order_id=dsp.child("order_id").value.toString()
                        var store_id=dsp.child("store_id").value.toString()
                        val cart =dsp.child("cart")
                        val user_id=dsp.child("user_id").value.toString()
                        val date=dsp.child("date").value.toString()
                        store_id = cart.child("store_id").value.toString()
                        cartPrice= cart.child("totalPrice").value.toString().toDouble()
                        amountMap.putAll(cart.child("itemsIdAmountList").value as HashMap<String, Int>)
                        priceMap.putAll(cart.child("itemsIdPriceList").value as HashMap<String, Double>)
                        picMap.putAll(cart.child("itemsIdPicList").value as HashMap<String, String>)
                        cartPrice=cart.child("totalPrice").value.toString().toDouble()
                        var realCart=Cart(store_id,amountMap,priceMap,picMap,cartPrice)
                        hashSet.add(store_id)
                        val order=Order(order_id, store_id,realCart,user_id, date)
                        listOrder.add(order)
                    }
                }
                val query = dbref.child("Store").addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                  if(snapshot.exists()){
                      for(dsp in snapshot.children){
                          val id=dsp.child("store_id").value.toString()
                          val photo=dsp.child("store_logo").value.toString()
                          val name=dsp.child("store_name").value.toString()
                          if(hashSet.contains(id)){
                          listStorePics.put(id,photo)
                              listStoreNames.put(id,name)
                          }
                      }
                  }
                   handleRec()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun handleRec() {

        val orderClickListener=object :OrderClickListener{
            override fun checkOrderDetails(order: Order) {
                orderClickListener.checkOrderDetails(order)
            }

        }


        val adapter=AllOrdersAdapter(listOrder,listStorePics,listStoreNames,orderClickListener)
        val linearLayoutManager=LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewAllOrders.adapter=adapter
        recyclerViewAllOrders.layoutManager=linearLayoutManager

    }

    private fun bindViews(view1: View) {
        recyclerViewAllOrders=view1.findViewById(R.id.recyclerViewAllOrders)
    }


}