package com.homemadeproductsapp.Search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.homemadeproductsapp.AllStores.Adapters.StoreAdapter
import com.homemadeproductsapp.AllStores.Listeners.OnProductClickListener
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.R
import java.util.*
import kotlin.collections.ArrayList

class AlltemsFragment : Fragment() {
    private lateinit var query:String
    private lateinit var view1:View
    private lateinit var recyclerViewItems:RecyclerView
    private  var listProducts:ArrayList<Product> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        view1= inflater.inflate(R.layout.activity_all_items_fragment, container, false)

        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        query= requireArguments().getString("query","")

        bindViews(view1);
        getDataFromDb()

    }

    private fun bindViews(view1: View) {
        recyclerViewItems=view1.findViewById(R.id.recyclerViewItems)
    }

    fun  getDataFromDb(){
      val reference = FirebaseDatabase.getInstance().reference

      val query1 = reference.child("Product")
        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (dsp in dataSnapshot.children) {
                        val name = dsp.child("name").value.toString()
                        val id = dsp.child("id").value.toString()
                        val copies = dsp.child("copies").value.toString()
                        val available = when (copies) {
                            "0" -> "Out Of Stock"
                            else -> "Available"
                        }
                        val price = dsp.child("price").value.toString()
                        val description = dsp.child("description").value.toString()
                        val storeid = dsp.child("store_id").value.toString()

                        val productImage = dsp.child("imagePathProduct").value.toString()
                        val subCategory = dsp.child("subcategory").value.toString()
                        val imagePathsuri = dsp.child("uriPaths").value as ArrayList<String>

                        val p: Product = Product(
                                name,
                                id,
                                copies.toInt(),
                                available,
                                price.toDouble(),
                                description,
                                productImage,
                                storeid,
                                subCategory,
                                imagePathsuri
                        )
                        if(query.toLowerCase(Locale.ROOT) in name.toLowerCase(Locale.ROOT))
                        listProducts.add(p)


                    }
                }
//                handleFeed()

              //  StoreSession.writeList(listProducts, "PRODUCTS_LIST")
                setupRecyclerView()
                Log.d("kuso",listProducts.size.toString())

            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }

    private fun setupRecyclerView() {
        val OnProductClickListener=object : OnProductClickListener {
            override fun onProductClick(product: Product) {
                Log.d("hayo","hayo")

                /*       if(feed.caption!!.isNotEmpty()&&feed.addDate!!.isNotEmpty()&&feed.imagePathProduct!!.isNotEmpty()) {
                         val intent = Intent(activity,PhotoDetails::class.java)
                           intent.putExtra(AppConst.CAPTION, feed.caption)
                           intent.putExtra(AppConst.DATE, feed.addDate)
                           intent.putExtra(AppConst.IMAGEPATH, feed.imagePathProduct)
                           intent.putExtra(AppConst.STORENAME, name)
                           intent.putExtra(AppConst.STOREIMAGEPATH, storeLogo)

                           startActivity(intent)

                       }
             */      }

        }

        val itemsAdapter = StoreAdapter(listProducts,OnProductClickListener)
        val linearLayoutManager = GridLayoutManager(context, 2)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewItems.layoutManager = linearLayoutManager
        recyclerViewItems.adapter = itemsAdapter

    }


}