package com.homemadeproductsapp.AllStores

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.homemadeproductsapp.AllStores.Adapters.StoreAdapter
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.MyStore.Adapter.MyStoreItemsAdapter
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant
import java.io.Serializable
import java.lang.reflect.Type


class StoreFragment : Fragment() ,Serializable {
    var view1: View? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view1 = inflater.inflate(R.layout.fragment_store, container, false)
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments

        val category= args?.getString("Category")
       //args!!.remove("Category");

        var list=ArrayList<Product>()
// convert java object to JSON format,
// and returned as JSON formatted string
// convert java object to JSON format,
// and returned as JSON formatted string

        var sharedPreferences: SharedPreferences? = null
        sharedPreferences = context?.applicationContext!!.getSharedPreferences(PrefConstant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)



        val x=sharedPreferences.getString("PRODUCTS_LIST", null)
        val type: Type = object : TypeToken<ArrayList<Product?>?>() {}.type
        list = Gson().fromJson<ArrayList<Product>>(x, type)

        Log.d("istrue",list.size.toString())




       val filterList=ArrayList<Product>()
        for(cat in list) {
            if(category==cat.subcategory||category=="All Items"){
            filterList.add(cat)
            }

        }
        val itemsAdapter = StoreAdapter(filterList)
        val recyclerViewNotes = view1!!.findViewById<RecyclerView>(R.id.recyclerViewStores)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewNotes.layoutManager = linearLayoutManager
        recyclerViewNotes.adapter = itemsAdapter

    }
}

