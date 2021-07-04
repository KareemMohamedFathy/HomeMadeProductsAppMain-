package com.homemadeproductsapp.MyStore

import MyStoreNewsFeedAdapter
import android.content.Context
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
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.MyStore.Listeners.NewsFeedClickListener
import com.homemadeproductsapp.R

class TimeLineFragment : Fragment() {
    var view1: View? = null
    private lateinit var storeIdExists:String
    private lateinit var dataCommunication: dataCommunication
    private lateinit var newsFeedClickListener: NewsFeedClickListener
    private lateinit var recyclerViewNotes:RecyclerView

    private var timeLinePhotos=ArrayList<Feed>()
    private var timeLinePhotosHash=HashSet<Feed>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataCommunication=context as dataCommunication
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
        storeIdExists= args!!.getString("store_id").toString()
        getDataFromDbForTimeLine()

        //   getDataFromDbForTimeLine()

    }

    private fun getDataFromDbForTimeLine() {
        timeLinePhotos.clear()

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
                    handleTabs()


                } else {

                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
     //   recyclerViewNotes.adapter!!.notifyDataSetChanged()

    }

    private fun handleTabs() {
        val args = arguments
        val name=args!!.getString("StoreName")
        val storeLogo=args!!.getString("storeImagePath")

        val list=ArrayList<Feed>()



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

        timeLinePhotos.reverse()
        recyclerViewNotes = view1!!.findViewById<RecyclerView>(R.id.recyclerViewTimeLine)

        val newsFeedAdapter = MyStoreNewsFeedAdapter(timeLinePhotos, newsFeedClickListener,dataCommunication.switchMode)
        val linearLayoutManager = GridLayoutManager(context,2)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewNotes.layoutManager = linearLayoutManager
        recyclerViewNotes.adapter = newsFeedAdapter

    }

    override fun onResume() {


//       recyclerViewNotes.adapter?.notifyDataSetChanged()

        super.onResume()

    }



}
