package com.homemadeproductsapp.AllStores.CategoriesFilter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseError
import com.google.firebase.database.*
import android.view.View
import com.homemadeproductsapp.AllStores.Adapters.AllStoresAdapter
import com.homemadeproductsapp.AllStores.AllStoresActivity
import com.homemadeproductsapp.AllStores.Listeners.AllStoresClickListener
import com.homemadeproductsapp.AllStores.OnStoreOpenActivity
import com.homemadeproductsapp.AppConst
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Store
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant

class SearchResultsActivity : AppCompatActivity() {
    private lateinit var recyclerViewResults: RecyclerView
    private lateinit var imageViewBack: ImageView
    private lateinit var mainCategory:String
    private lateinit var subCategory:String
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private  var storesList=ArrayList<Store>()
   private var list=ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        bindViews()
        readDataFromIntent()
        setupClickListeners()

        if(("All $mainCategory")!=subCategory){
            getStoreWithSub()
        }
        else{
            getDataFromStore()
        }
    }

    private fun setupClickListeners() {
        imageViewBack.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@SearchResultsActivity,AllStoresActivity::class.java))
            }

        })
    }

    private fun bindViews() {
        recyclerViewResults=findViewById(R.id.recyclerViewResults)
        imageViewBack=findViewById(R.id.backButton)
    }

    private fun getStoreWithSub() {
        firebaseDatabase= FirebaseDatabase.getInstance()
        dbReference=firebaseDatabase.reference
       dbReference.child("Product").orderByChild("subcategory").equalTo(subCategory).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (dsp in snapshot.children) {
                        val store_id = dsp.child("store_id").value.toString()
                        list.add(store_id.toString())
                    }
                    getDataFromDb()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
    private fun getDataFromDb() {
        dbReference= FirebaseDatabase.getInstance().reference
        dbReference=dbReference.child("Store")

        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (dsp in dataSnapshot.children) {

                        val name = dsp.child("store_name").value.toString()
                        val storeid = dsp.child("store_id").value.toString()

                        val description = dsp.child("store_description").value.toString()

                        val imagePathProduct = dsp.child("store_logo").value.toString()
                        val category = dsp.child("mainCategoryName").value.toString()

                        val p: Store = Store(storeid,name,imagePathProduct,description,category,"")

                        if(mainCategory==category)
                            storesList.add(p)


                    }


                }

                setupSharedPreference()



                val storeClickListener=object : AllStoresClickListener {
                    override fun onClick(store: Store) {

                        saveCategory(store.store_name, store.mainCategoryName.toString(),store.store_id,store.store_logo)
                        val intent= Intent(this@SearchResultsActivity, OnStoreOpenActivity::class.java)
                        intent.putExtra(AppConst.STORENAME,store.store_name)
                        intent.putExtra(AppConst.STORECATEGORY,store.mainCategoryName)
                        intent.putExtra(AppConst.STOREDESCRIPTION,store.store_description)
                        intent.putExtra(AppConst.STORELOGO,store.store_logo)

                        intent.putExtra(AppConst.STOREID,store.store_id)

                        startActivity(intent)


                    }

                }

                val allStoresProductAdapter= AllStoresAdapter(storesList,storeClickListener)
                val linearLayoutManager= LinearLayoutManager(this@SearchResultsActivity)
                linearLayoutManager.orientation=RecyclerView.VERTICAL
                recyclerViewResults.layoutManager=linearLayoutManager
                recyclerViewResults.adapter=allStoresProductAdapter


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            fun onCancelled(firebaseError: FirebaseError?) {}
        })


    }





    private fun getDataFromStore() {
        dbReference= FirebaseDatabase.getInstance().reference
        val query=dbReference.child("Store").orderByChild("mainCategoryName").equalTo(mainCategory)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (dsp in dataSnapshot.children) {
                        val arr: ArrayList<Any?> =  arrayListOf(dsp.value)

                        val name = dsp.child("store_name").value.toString()
                        val storeid = dsp.child("store_id").value.toString()

                        val description = dsp.child("store_description").value.toString()

                        val imagePathProduct = dsp.child("store_logo").value.toString()
                        val category = dsp.child("mainCategoryName").value.toString()

                        val p: Store = Store(storeid,name,imagePathProduct,description,category,"")
                            storesList.add(p)



                    }


                }

                setupSharedPreference()



                val storeClickListener=object : AllStoresClickListener {
                    override fun onClick(store: Store) {

                        saveCategory(store.store_name, store.mainCategoryName.toString(),store.store_id,store.store_logo)
                        val intent= Intent(this@SearchResultsActivity, OnStoreOpenActivity::class.java)
                        intent.putExtra(AppConst.STORENAME,store.store_name)
                        intent.putExtra(AppConst.STORECATEGORY,store.mainCategoryName)
                        intent.putExtra(AppConst.STOREDESCRIPTION,store.store_description)
                        intent.putExtra(AppConst.STORELOGO,store.store_logo)

                        intent.putExtra(AppConst.STOREID,store.store_id)

                        startActivity(intent)


                    }

                }

                val allStoresProductAdapter= AllStoresAdapter(storesList,storeClickListener)
                val linearLayoutManager= LinearLayoutManager(this@SearchResultsActivity)
                linearLayoutManager.orientation=RecyclerView.VERTICAL
                recyclerViewResults.layoutManager=linearLayoutManager
                recyclerViewResults.adapter=allStoresProductAdapter


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            fun onCancelled(firebaseError: FirebaseError?) {}
        })


    }




    private fun saveCategory(storename: String,category: String,store_id:String,storelogo:String) {
        StoreSession.write(PrefConstant.ALLSTORENAME, storename)

        StoreSession.write(AppConst.STOREMAINCATEGORY, category)
        StoreSession.write(AppConst.STOREID, store_id)
        StoreSession.write(AppConst.STORELOGO, storelogo)

    }
    private fun setupSharedPreference() {
        StoreSession.init(this)
    }


    private fun readDataFromIntent() {
        mainCategory=intent.getStringExtra(PrefConstant.CHOOSENCATEGORY).toString()
        subCategory=intent.getStringExtra("subcategory").toString()


    }
}