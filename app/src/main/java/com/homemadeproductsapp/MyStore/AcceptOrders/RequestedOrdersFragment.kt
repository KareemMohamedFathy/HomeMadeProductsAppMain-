package com.homemadeproductsapp.MyStore.AcceptOrders

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.homemadeproductsapp.DB.Cart
import com.homemadeproductsapp.DB.User
import com.homemadeproductsapp.DB.Order
import com.homemadeproductsapp.MyStore.AcceptOrders.Adapter.RequestedOrderAdapter
import com.homemadeproductsapp.MyStore.AcceptOrders.Listener.OrderAcceptClickListener
import com.homemadeproductsapp.R
class RequestedOrdersFragment : DialogFragment(), AdapterView.OnItemSelectedListener {
    private lateinit var recyclerViewOrdersStatus: RecyclerView
    private lateinit var view1:View
    private lateinit var store_id:String
   private lateinit var spinnerOrderStatus: Spinner

    private lateinit var orderAcceptClickListener: OrderAcceptClickListener
    private lateinit var backButton: ImageView
    private lateinit var filter:String
    private  var usersList:ArrayList<User> = ArrayList()
    private  var orderList:ArrayList<Order> = ArrayList()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        orderAcceptClickListener=context as OrderAcceptClickListener
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

         view1= inflater.inflate(R.layout.fragment_requested_orders, container, false)
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dismiss()
                }
            }
            )


        store_id= requireArguments().getString("store_id","")

        bindViews()
        setupClickListeners()
//getUserData("2")
        getMyStoreOrders()

    }

    private fun setupClickListeners() {
        backButton.setOnClickListener{dismiss()}
    }

    private fun getMyStoreOrders() {
        val dbref=FirebaseDatabase.getInstance().reference
        val ids: ArrayList<String> = ArrayList()

        dbref.child("Order").orderByChild("store_id").equalTo(store_id).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(dsp in snapshot.children){
                        val date=dsp.child("date").value.toString()
                        val order_id=dsp.child("order_id").value.toString()
                        val store_id=dsp.child("store_id").value.toString()
                        val user_id=dsp.child("user_id").value.toString()
                        val order_status=dsp.child("order_status").value.toString()

                        val amountMap: HashMap<String, Int> = HashMap()
                        val priceMap: HashMap<String, Double> = HashMap()
                        val picMap: HashMap<String, String> = HashMap()
                        ids.add(user_id)

                        val cart =dsp.child("cart")
                       var cartPrice= cart.child("totalPrice").value.toString().toDouble()
                        amountMap.putAll(cart.child("itemsIdAmountList").value as HashMap<String, Int>)
                        priceMap.putAll(cart.child("itemsIdPriceList").value as HashMap<String, Double>)
                        picMap.putAll(cart.child("itemsIdPicList").value as HashMap<String, String>)
                        cartPrice=cart.child("totalPrice").value.toString().toDouble()
                        var realCart= Cart(store_id,amountMap,priceMap,picMap,cartPrice)
                        val order:Order= Order(order_id,store_id,realCart,user_id,date,order_status)
                        orderList.add(order)

                    }


                }
                var idx=0
                val size=ids.size

                for(user_id in ids) {

                    getUserData(user_id,idx,size)
                    idx++
                }
              /*  Handler(Looper.getMainLooper()).postDelayed({
                    setupRecyclerView()
                }, 100)
*/

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setupRecyclerView() {


        orderAcceptClickListener=object : OrderAcceptClickListener {
            override fun checkOrderDetails(order: Order) {
                val orderDetailsFragment = OrderDetailsFragment()
                val bundle = Bundle()
                bundle.putSerializable("order",order)
                orderDetailsFragment.arguments=bundle

                orderDetailsFragment.setStyle(
                    DialogFragment.STYLE_NORMAL,
                    R.style.DialogFragmentTheme
                );
                orderDetailsFragment.show(activity!!.supportFragmentManager, "Jean Boy2")


            }

        }
        filter= spinnerOrderStatus.selectedItem.toString()
        val filterArray:ArrayList<Order> =ArrayList()
        if(filter!="All") {
            for (cat in orderList) {
                if (cat.order_status == filter) {
                    filterArray.add(cat)
                }
            }
        }
        else{
            filterArray.addAll(orderList)
        }

        val adapter=RequestedOrderAdapter(filterArray,usersList,orderAcceptClickListener,requireContext())
        val linearLayoutManager=LinearLayoutManager(context)
        linearLayoutManager.orientation=LinearLayoutManager.VERTICAL
        recyclerViewOrdersStatus.layoutManager=linearLayoutManager
        recyclerViewOrdersStatus.adapter=adapter


    }

    private fun getUserData(userId: String, idx: Int, size: Int) {
        val dbref=FirebaseDatabase.getInstance().reference


        FirebaseDatabase.getInstance().getReference("User").child(userId).addListenerForSingleValueEvent(object :ValueEventListener{


            override fun onDataChange(snapshot: DataSnapshot) {

                val name = snapshot.child("name").value.toString()
                val email = snapshot.child("email").value.toString()
                val phoneNo = snapshot.child("mobileno").value.toString()
                val profileImagePath = snapshot.child("personalPhotoPath").value.toString()

                val user = User(userId, name, phoneNo, profileImagePath, email, store_id)
                usersList.add(user)


if(idx+1==size)
                setupRecyclerView()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
    private fun bindViews() {
        recyclerViewOrdersStatus=view1.findViewById(R.id.recyclerViewOrderStatus)
        backButton=view1.findViewById(R.id.back)
        spinnerOrderStatus=view1.findViewById(R.id.spinnerOrderStatus)
        spinnerOrderStatus.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.All_Order_Status,
            R.layout.spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerOrderStatus.adapter = adapter
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
       setupRecyclerView()

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


}