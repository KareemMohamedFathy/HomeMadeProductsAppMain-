package com.homemadeproductsapp.Search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseError
import com.google.firebase.database.*
import com.homemadeproductsapp.AllStores.Adapters.AllStoresAdapter
import com.homemadeproductsapp.AllStores.CategoriesFilter.CategoryFiltersActivity
import com.homemadeproductsapp.AllStores.Listeners.AllStoresClickListener
import com.homemadeproductsapp.AllStores.OnStoreOpenActivity
import com.homemadeproductsapp.AppConst
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Store
import com.homemadeproductsapp.Home.HomeActivity
import com.homemadeproductsapp.MyStore.MyStoreActivity
import com.homemadeproductsapp.PastOrders.OrdersActivity
import com.homemadeproductsapp.R
import com.homemadeproductsapp.profile.ProfileActivity
import com.mindorks.notesapp.data.local.pref.PrefConstant
import java.util.*
import kotlin.collections.ArrayList


class AllStoresFragment : Fragment() {
    private lateinit var view1:View
    private lateinit var recyclerViewStores: RecyclerView
    private lateinit var dbReference: DatabaseReference
    private lateinit var imageViewFilter: ImageView
    private lateinit var firebaseDatabase: FirebaseDatabase
    private  var storesList =ArrayList<Store>();
    private lateinit var query:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         view1= inflater.inflate(R.layout.activity_all_stores_fragment, container, false)
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        query= requireArguments().getString("query","")

        bindViews(view1);
        getDataFromDb()

    }

    private fun getDataFromDb() {
        dbReference= FirebaseDatabase.getInstance().reference
        dbReference=dbReference.child("Store")

        dbReference.addValueEventListener(object : ValueEventListener {
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

                        if(query.toLowerCase(Locale.ROOT) in name.toLowerCase(Locale.ROOT))
                        storesList.add(p)



                    }


                }

                setupSharedPreference()



                val storeClickListener=object : AllStoresClickListener {
                    override fun onClick(store: Store) {

                        saveCategory(store.store_name, store.mainCategoryName.toString(),store.store_id,store.store_logo)
                        val intent= Intent(requireActivity(), OnStoreOpenActivity::class.java)
                        intent.putExtra(AppConst.STORENAME,store.store_name)
                        intent.putExtra(AppConst.STORECATEGORY,store.mainCategoryName)
                        intent.putExtra(AppConst.STOREDESCRIPTION,store.store_description)
                        intent.putExtra(AppConst.STORELOGO,store.store_logo)
                        intent.putExtra(AppConst.WHERETOGO,"AllStoresActivity")


                        intent.putExtra(AppConst.STOREID,store.store_id)

                        startActivity(intent)


                    }

                }

                val allStoresProductAdapter= AllStoresAdapter(storesList,storeClickListener)
                val linearLayoutManager= LinearLayoutManager(requireContext())
                linearLayoutManager.orientation= RecyclerView.VERTICAL
                recyclerViewStores.layoutManager=linearLayoutManager
                recyclerViewStores.adapter=allStoresProductAdapter


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
        StoreSession.init(requireContext())
    }

    private fun bindViews(view1:View) {
        recyclerViewStores=view1.findViewById(R.id.recyclerViewStores)

    }
}