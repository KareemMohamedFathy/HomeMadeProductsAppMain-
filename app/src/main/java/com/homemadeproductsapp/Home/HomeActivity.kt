package com.homemadeproductsapp.Home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.homemadeproductsapp.AllStores.AllStoresActivity
import com.homemadeproductsapp.AllStores.OnStoreOpenActivity
import com.homemadeproductsapp.AppConst
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Store
import com.homemadeproductsapp.Home.adapter.HomePageAdapter
import com.homemadeproductsapp.MyStore.MyStoreActivity
import com.homemadeproductsapp.PastOrders.OrdersActivity
import com.homemadeproductsapp.R
import com.homemadeproductsapp.Search.SearchResultsActivity
import com.homemadeproductsapp.profile.ProfileActivity
import com.mindorks.notesapp.data.local.pref.PrefConstant
import kotlinx.android.synthetic.main.page_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HomeActivity : AppCompatActivity() {
    private  var storesList=ArrayList<String>()
    private  var storesListMap=HashMap<String, Store>()
    private lateinit var dbReference:DatabaseReference
    private  var storeTimeLineMap=HashMap<String, Feed>()
    private  var storeNameMap=HashMap<String, String>()
    private  var storeLogoMap=HashMap<String, String>()
    private lateinit var recyclerViewHome:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar!!.title = "HomePage"

        recyclerViewHome = findViewById(R.id.recyclerViewHome)

        handleBottomNavigationView();
        getAllStores()

    }

    private fun getAllStores() {
        dbReference= FirebaseDatabase.getInstance().reference
        dbReference=dbReference.child("Store")

        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (dsp in dataSnapshot.children) {
                        val arr: ArrayList<Any?> = arrayListOf(dsp.value)

                        val name = dsp.child("store_name").value.toString()
                        val storeid = dsp.child("store_id").value.toString()

                        val description = dsp.child("store_description").value.toString()

                        val imagePathProduct = dsp.child("store_logo").value.toString()
                        val category = dsp.child("mainCategoryName").value.toString()
                        val owner_id = dsp.child("owner_id").value.toString()

                        val p: Store = Store(storeid, name, imagePathProduct, description, category, owner_id)

                        storeNameMap.put(storeid, name)
                        storeLogoMap.put(storeid, imagePathProduct)
                        storesListMap.put(storeid, p)

                    }


                }
                getFirstFeed()


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getFirstFeed() {
        var map:HashMap<String, String> =HashMap()

        dbReference= FirebaseDatabase.getInstance().reference
        dbReference=dbReference.child("Feed")

        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dsp in snapshot.children) {
                    val store_id = dsp.child("store_id").value.toString()
                    val date = dsp.child("addDate").value.toString()
                    if (map[store_id] == null) {
                        val caption = dsp.child("caption").value.toString()

                        val imagePathProduct = dsp.child("imagePathProduct").value.toString()
                        val id = dsp.child("id").value.toString()
                        val feed = Feed(caption, id, imagePathProduct, store_id, date)
                        map.put(store_id, date)
                        storeTimeLineMap.put(store_id, feed)

                        continue
                    }
                    val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                    val strDate: Date? = sdf.parse(date)
                    val mapData: Date? = sdf.parse(map[store_id].toString())
                    if (map[store_id] != null && strDate!!.after(mapData)) {

                        val caption = dsp.child("caption").value.toString()

                        val imagePathProduct = dsp.child("imagePathProduct").value.toString()
                        val id = dsp.child("id").value.toString()
                        val feed = Feed(caption, id, imagePathProduct, store_id, date)
                        map.put(store_id, date)
                        storeTimeLineMap.put(store_id, feed)
                    }
                }

                setupRecyclerView()


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



        }


    private fun setupRecyclerView() {
        storesList.clear()
        val map:TreeMap<String,String> = TreeMap()
        for(cat in storeTimeLineMap){
            map.put(cat.key, cat.value.addDate!!)
        }

        for(cat in map){
            storesList.add(cat.key)
        }
        storesList.reverse()

        setupSharedPreference()
        val nameClickListener=object :NameClickListener{
            override fun NameClickListener(store: Store) {
                saveCategory(store.store_name, store.mainCategoryName.toString(), store.store_id, store.store_logo)
                val intent=Intent(this@HomeActivity, OnStoreOpenActivity::class.java)
                intent.putExtra(AppConst.STORENAME, store.store_name)
                intent.putExtra(AppConst.STORECATEGORY, store.mainCategoryName)
                intent.putExtra(AppConst.STOREDESCRIPTION, store.store_description)
                intent.putExtra(AppConst.STORELOGO, store.store_logo)
                intent.putExtra(AppConst.STOREID, store.store_id)
                intent.putExtra(AppConst.WHERETOGO, "HomeActivity")
                startActivity(intent)

            }

        }
        val adapter=HomePageAdapter(storeTimeLineMap, storesList, storesListMap, nameClickListener)
        val linearLayoutManager=LinearLayoutManager(this)
        linearLayoutManager.orientation=LinearLayoutManager.VERTICAL
        recyclerViewHome.layoutManager=linearLayoutManager
        recyclerViewHome.adapter=adapter
    }
    private fun saveCategory(storename: String, category: String, store_id: String, storelogo: String) {
        StoreSession.write(PrefConstant.ALLSTORENAME, storename)
        StoreSession.write(AppConst.STOREMAINCATEGORY, category)
        StoreSession.write(AppConst.STOREID, store_id)
        StoreSession.write(AppConst.STORELOGO, storelogo)

    }
    private fun setupSharedPreference() {
        StoreSession.init(this)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
//    #c2dde6
        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            maxWidth=Integer.MAX_VALUE
            onActionViewExpanded()
            requestFocusFromTouch();

            setQueryHint("Search for products or stores");
            // Assumes current activity is the searchable activity
           // val textView:TextView = this.findViewById(id) as TextView
            val id: Int = getContext().getResources().getIdentifier("android:id/search_src_text", null, null)
            val searchText:TextView = findViewById(id)
            maxWidth=Integer.MAX_VALUE
            setSubmitButtonEnabled(true)
            setIconified(false);
            clearFocus()
            setIconifiedByDefault(false);
           // Do not iconify the widget; expand it by default
            searchText.setTextColor(Color.WHITE)
            searchText.setHintTextColor(Color.LTGRAY)
            searchText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F)


            val id2 = getContext().getResources().getIdentifier("android:id/search_edit_frame", null, null)
            val searchEditFrame: LinearLayout = findViewById(id2) as LinearLayout
            val layoutParams: LinearLayout.LayoutParams = searchEditFrame.layoutParams as LinearLayout.LayoutParams
            layoutParams.marginStart = 0


            setSearchableInfo(searchManager.getSearchableInfo(ComponentName(getApplicationContext(), SearchResultsActivity::class.java)))
        }
        return true
    }



    private fun handleBottomNavigationView() {
        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setSelectedItemId(R.id.page_1);

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_2 -> {

                    val intent = Intent(this@HomeActivity, MyStoreActivity::class.java)
                    startActivity(intent)

                    true
                }
                R.id.page_3 -> {
                    val intent = Intent(this@HomeActivity, OrdersActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page_4 -> {
                    val intent = Intent(this@HomeActivity, AllStoresActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page_5 -> {
                    val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }

        }
    }
}