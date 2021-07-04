package com.homemadeproductsapp.MyStore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.MyStore.Adapter.MyStoreItemsAdapter
import com.homemadeproductsapp.MyStore.ItemsAndFeed.EditProductActivity
import com.homemadeproductsapp.MyStore.Listeners.EditItemClickListener
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant


class ItemsFragment : Fragment() {
private lateinit var storeIdExists:String
private  var listItems=ArrayList<Product>()
    var view1: View? = null
    private lateinit var  recyclerViewNotes:RecyclerView
    private lateinit var dataCommunication: dataCommunication
    private lateinit var editItemClickListener:EditItemClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataCommunication=context as dataCommunication
        editItemClickListener=context as EditItemClickListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view1= inflater.inflate(R.layout.fragment_items, container, false)
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        recyclerViewNotes = view1!!.findViewById<RecyclerView>(R.id.recyclerViewItems)

        storeIdExists= args!!.getString("store_id").toString()
        getDataFromDbForProducts()


    }
   private fun getDataFromDbForProducts() {
       listItems.clear()

        val reference = FirebaseDatabase.getInstance().reference

        val query = reference.child("Product").orderByChild("store_id").equalTo(storeIdExists)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
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

                        val imagePathProduct = dsp.child("imagePathProduct").value.toString()
                        val subCategory = dsp.child("subcategory").value.toString()
                        val imagePathsuri = dsp.child("uriPaths").value as ArrayList<String>


                        val p: Product = Product(name, id, copies.toInt(), available, price.toDouble(), description, imagePathProduct, storeIdExists, subCategory,imagePathsuri)
                        listItems.add(p)

                    }


                }
                dataForCategories()



            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    private fun dataForCategories() {
        val args = arguments

        val category= args?.getString("Category")




        val filterList=ArrayList<Product>()
        for(cat in listItems) {
            if(category==cat.subcategory||category=="All Items"){
                filterList.add(cat)
            }

        }
        val editItemClickListener:EditItemClickListener=object :EditItemClickListener{
            override fun EditItem(product: Product) {
                Log.d("hayo","hayo")
                val connectionsJSONString = Gson().toJson(product)
                val intent= Intent(requireActivity(),EditProductActivity::class.java)
                intent.putExtra("product",connectionsJSONString)
                intent.putExtra("store_id",storeIdExists)
                intent.putExtra(PrefConstant.MAINCATEGORY,category)
                startActivity(intent)
                requireActivity().finish()

            }
        }

        val itemsAdapter = MyStoreItemsAdapter(filterList,dataCommunication,editItemClickListener)
        val linearLayoutManager = GridLayoutManager(requireActivity().applicationContext,2)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewNotes.layoutManager = linearLayoutManager
        recyclerViewNotes.adapter = itemsAdapter

//        recyclerViewNotes.adapter!!.notifyDataSetChanged()


    }
    override fun onResume() {
        super.onResume()
      //  listItems.clear()
       // getDataFromDbForProducts()
        recyclerViewNotes.adapter?.notifyDataSetChanged()


    }

}