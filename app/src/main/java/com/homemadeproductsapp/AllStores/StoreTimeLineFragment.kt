package com.homemadeproductsapp.AllStores

import StoreNewsFeedAdapter
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.homemadeproductsapp.AllStores.Listeners.NewsFeedClickListener
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.Details.DetailsFragment

import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant
import java.lang.reflect.Type


class StoreTimeLineFragment : Fragment() {
    var view1: View? = null
    private lateinit var dataCommunication: DataCommunication
    private lateinit var newsFeedClickListener: NewsFeedClickListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
    dataCommunication=context as DataCommunication
        newsFeedClickListener=context as NewsFeedClickListener
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view1 =  inflater.inflate(R.layout.fragment_store_time_line, container, false)
        return view1
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        var name=args!!.getString("StoreName")
        var storeLogo=args!!.getString("storeImagePath")

        var list=ArrayList<Feed>()


        var sharedPreferences: SharedPreferences? = null
        sharedPreferences = context?.applicationContext!!.getSharedPreferences(
            PrefConstant.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )



        val x=sharedPreferences.getString("TIMELINE", null)
        val type: Type = object : TypeToken<ArrayList<Feed?>?>() {}.type
        list = Gson().fromJson<ArrayList<Feed>>(x, type)





        val filterList=ArrayList<Feed>()
        for(cat in list) {
                filterList.add(cat)

        }

        val newsFeedClickListener=object : NewsFeedClickListener {
            override fun onClick(feed: Feed) {
                if(feed.caption!!.isNotEmpty()&&feed.addDate!!.isNotEmpty()&&feed.imagePathProduct!!.isNotEmpty()) {
                    dataCommunication.feed=feed
                    dataCommunication.store_logo= storeLogo.toString()
                    dataCommunication.store_name= name.toString()

                    newsFeedClickListener.onClick(feed)





                }
            }

        }
        filterList.reverse()
        val newsFeedAdapter = StoreNewsFeedAdapter(filterList, newsFeedClickListener)
        val recyclerViewNotes = view1!!.findViewById<RecyclerView>(R.id.recyclerViewTimeLine)
        val linearLayoutManager = GridLayoutManager(context,2)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewNotes.layoutManager = linearLayoutManager
        recyclerViewNotes.adapter = newsFeedAdapter

    }

    interface OnItemClick {
        fun onClick(feed: Feed)
    }

}
