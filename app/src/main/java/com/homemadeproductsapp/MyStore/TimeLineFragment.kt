package com.homemadeproductsapp.MyStore

import MyStoreNewsFeedAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.homemadeproductsapp.AllStores.PhotoDetails
import com.homemadeproductsapp.AppConst
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.Details.DetailsActivity
import com.homemadeproductsapp.MyStore.Listeners.NewsFeedClickListener
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant
import java.lang.reflect.Type

class TimeLineFragment : Fragment() {
    var view1: View? = null
    private lateinit var storeIdExists:String
    private var timeLinePhotos=ArrayList<Feed>()


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
        storeIdExists= args!!.getString("store_id").toString()

        getDataFromDbForTimeLine()

    }

    private fun getDataFromDbForTimeLine() {

        val reference = FirebaseDatabase.getInstance().reference

        val query = reference.child("Feed").orderByChild("store_id").equalTo(storeIdExists)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                if (dataSnapshot.exists()) {

                    for (dsp in dataSnapshot.children) {

                        val caption = dsp.child("caption").value.toString()

                        val imagePathProduct = dsp.child("imagePathProduct").value.toString()
                        val date = dsp.child("addDate").value.toString()
                        val id = dsp.child("id").value.toString()

                        val feed = Feed(caption, id, imagePathProduct, storeIdExists, date)

                        timeLinePhotos.add(feed)


                    }


                } else {

                }

                handleTabs()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun handleTabs() {
        val args = arguments
        val name=args!!.getString("StoreName")
        val storeLogo=args!!.getString("storeImagePath")

        val list=ArrayList<Feed>()


        Log.d("run",timeLinePhotos.size.toString()+"oioio")

        val newsFeedClickListener=object : NewsFeedClickListener {
            override fun onClick(feed: Feed) {
                if(feed.caption!!.isNotEmpty()&&feed.addDate!!.isNotEmpty()&&feed.imagePathProduct!!.isNotEmpty()) {
                    val intent = Intent(activity, DetailsActivity::class.java)
                    intent.putExtra(AppConst.CAPTION, feed.caption)
                    intent.putExtra(AppConst.DATE, feed.addDate)
                    intent.putExtra(AppConst.IMAGEPATH, feed.imagePathProduct)
                    intent.putExtra(AppConst.STORENAME, name)
                    intent.putExtra(AppConst.STOREIMAGEPATH, storeLogo)

                    startActivity(intent)

                }
            }

        }
        timeLinePhotos.reverse()
        val newsFeedAdapter = MyStoreNewsFeedAdapter(timeLinePhotos, newsFeedClickListener)
        val recyclerViewNotes = view1!!.findViewById<RecyclerView>(R.id.recyclerViewTimeLine)
        val linearLayoutManager = GridLayoutManager(context,2)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewNotes.layoutManager = linearLayoutManager
        recyclerViewNotes.adapter = newsFeedAdapter

    }


}