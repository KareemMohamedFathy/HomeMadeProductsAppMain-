package com.homemadeproductsapp.AllStores

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.homemadeproductsapp.AllStores.Adapters.StoreAdapter
import com.homemadeproductsapp.AllStores.Listeners.OnProductClickListener
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.R
import java.io.Serializable


class StoreFragment : Fragment() ,Serializable {
    var view1: View? = null

    private lateinit var dataCommunication: DataCommunication
    private lateinit var onProductClickListener: OnProductClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
    dataCommunication=context as DataCommunication
        onProductClickListener=context as OnProductClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view1 = inflater.inflate(R.layout.fragment_store, container, false)
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments


        val category= args?.getString("Category")
        var list=ArrayList<Product>()
        StoreSession.init(requireContext())

        list= StoreSession?.readList("PRODUCTS_LIST")!!




       val filterList=ArrayList<Product>()
        for(cat in list) {
            if(category==cat.subcategory||category=="All Items"){
            filterList.add(cat)
            }

        }

        val OnProductClickListener=object : OnProductClickListener {
            override fun onProductClick(product: Product) {
            dataCommunication.product=product
                onProductClickListener.onProductClick(product)

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

        val itemsAdapter = StoreAdapter(filterList, requireActivity().applicationContext,OnProductClickListener)
        val recyclerViewNotes = view1!!.findViewById<RecyclerView>(R.id.recyclerViewStores)
        val linearLayoutManager = GridLayoutManager(context, 1)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewNotes.layoutManager = linearLayoutManager
        recyclerViewNotes.adapter = itemsAdapter

    }
}

