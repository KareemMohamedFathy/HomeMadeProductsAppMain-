package com.homemadeproductsapp.MyStore

import MyStoreProductsAdapter
import android.app.Activity
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.homemadeproductsapp.CreateItemActivity
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.MyStore.Adapter.MyStoreItemsAdapter
import com.homemadeproductsapp.R
import kotlinx.android.synthetic.main.activity_my_store.*
import kotlinx.android.synthetic.main.item_adapter_layout.*


class MyStoreActivity : AppCompatActivity() {
    companion object {
       private const val ADD_STORE_CODE = 100
    }
    private  var listItems=ArrayList<Product>()
    private lateinit var recyclerViewItems:RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private  lateinit var storeIdExists:String
    private lateinit var mainGroup:Group
    private lateinit var subGroup:Group
    private lateinit var storeName: TextView
    private lateinit var buttonCreateStore: Button
    private lateinit var imageViewLogo: ImageView

    private lateinit var buttonAddItems:FloatingActionButton




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_store)
        bindViews()
        storeIdExists=""
        auth = FirebaseAuth.getInstance()
        mainGroup.visibility = View.INVISIBLE

        val lol =   FirebaseDatabase.getInstance().getReference("Producer").child(auth.currentUser!!.uid).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            storeIdExists = it.child("store_id").value.toString()
            if (storeIdExists.isEmpty()) {
                mainGroup.visibility = View.INVISIBLE
                subGroup.visibility = View.VISIBLE
            }

            getStoreData()

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }




    }

    private fun getStoreData() {
        val lol =   FirebaseDatabase.getInstance().getReference("Store").child(storeIdExists).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            val name:String=it.child("store_name").value.toString()
            val imagepath=it.child("store_logo").value.toString()
            storeName.setText(name)
            Glide.with(this).load(imagepath).into(imageViewLogo)

            getDataFromDb()

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }


    }


    private fun setupTabs(){
        Log.d("MyStore", "weo" + storeIdExists)
        if(!storeIdExists.isEmpty())
        mainGroup.visibility = View.VISIBLE


        firebaseDatabase= FirebaseDatabase.getInstance()




        recyclerViewItems=findViewById(R.id.recyclerViewTab)
        val myStoreProductsAdapter = MyStoreItemsAdapter(listItems)
        val linearLayoutManager = LinearLayoutManager(this@MyStoreActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewItems.layoutManager = linearLayoutManager
        recyclerViewItems.adapter = myStoreProductsAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {


            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {

                    val myStoreProductsAdapter = MyStoreItemsAdapter(listItems)
                    val linearLayoutManager = LinearLayoutManager(this@MyStoreActivity)
                    linearLayoutManager.orientation = RecyclerView.VERTICAL
                    recyclerViewItems.layoutManager = linearLayoutManager
                    recyclerViewItems.adapter = myStoreProductsAdapter
                }
                if (tab?.position == 1) {
                    var allNames1:ArrayList<String> = arrayListOf<String>()
                    var allNames2:ArrayList<String> = arrayListOf<String>()
                    var pos=0

                    for(names in listItems){
                        if(pos%2==0)
                        {
                            allNames1.add(names.name!!)

                        }
                        else{
                            allNames2.add(names.name!!)
                        }
                        pos++
                    }
                    Log.d("MyStoreArray",allNames1.toString())
                    Log.d("MyStoreArray",allNames2.toString())


                    val myStoreProductsAdapter = MyStoreProductsAdapter(allNames1,allNames2)
                    val linearLayoutManager = LinearLayoutManager(this@MyStoreActivity)
                    linearLayoutManager.orientation = RecyclerView.VERTICAL
                    recyclerViewItems.layoutManager = linearLayoutManager
                    recyclerViewItems.adapter = myStoreProductsAdapter


                }
            }


            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
        setupClickListeners()





        Log.d("MyStore", "weo" + storeIdExists)

    }

    private fun getDataFromDb() {

        val reference = FirebaseDatabase.getInstance().reference

        val query = reference.child("Product").orderByChild("storeid").equalTo(storeIdExists)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (dsp in dataSnapshot.children) {

                        val name = dsp.child("name").value.toString()
                        val id = dsp.child("id").value.toString()
                        val productionDate = dsp.child("productionDate").value.toString()
                        val copies = dsp.child("copies").value.toString()
                        val available = when (copies) {
                            "0" -> "Out Of Stock"
                            else -> "Available"
                        }
                        val price = dsp.child("price").value.toString()
                        val description = dsp.child("description").value.toString()

                        val imagePathProduct = dsp.child("imagePathProduct").value.toString()
                        val imagePathItem = dsp.child("imagePathItem").value.toString()
                        val p: Product = Product(name, id, productionDate, copies.toInt(), available, "", price.toDouble(), description, imagePathProduct, storeIdExists, imagePathItem)
                        listItems.add(p)


                    }


                }
                setupTabs()

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    private fun setupClickListeners() {
        buttonCreateStore.setOnClickListener(
                object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        Log.d("MyStoreActivity", "fraud")
                        startActivityForResult(Intent(this@MyStoreActivity, CreateStoreActivity::class.java), ADD_STORE_CODE)

                    }
                }
        )
        val clickAction1=object:View.OnClickListener{
            override fun onClick(v: View?) {
                val intent=Intent(this@MyStoreActivity, CreateItemActivity::class.java)
                Log.d("MyStoreActivity", "hit" + storeIdExists)
                intent.putExtra("store_Id", storeIdExists)

                startActivity(intent)
            }
        }
        buttonAddItems.setOnClickListener(clickAction1)

    }

    private fun bindViews() {
        mainGroup=findViewById(R.id.maingroup)
        subGroup=findViewById(R.id.subgroup)
        buttonCreateStore=findViewById(R.id.buttonCreateStore)
        buttonAddItems=findViewById(R.id.buttonAddItems)
        storeName=findViewById(R.id.storeName)
        imageViewLogo=findViewById(R.id.logoPic)

    }



    }

