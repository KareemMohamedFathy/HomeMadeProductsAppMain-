package com.homemadeproductsapp.AllStores

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseError
import com.google.firebase.database.*
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.homemadeproductsapp.AllStores.Adapters.AllStoresAdapter
import com.homemadeproductsapp.AllStores.CategoriesFilter.CategoryFiltersActivity
import com.homemadeproductsapp.AllStores.Listeners.AllStoresClickListener
import com.homemadeproductsapp.AppConst
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Store
import com.homemadeproductsapp.Home.HomeActivity
import com.homemadeproductsapp.MyStore.MyStoreActivity
import com.homemadeproductsapp.PastOrders.OrdersActivity
import com.homemadeproductsapp.profile.ProfileActivity
import com.homemadeproductsapp.R
import com.homemadeproductsapp.Search.SearchResultsActivity
import com.mindorks.notesapp.data.local.pref.PrefConstant
import java.io.Serializable


class AllStoresActivity : AppCompatActivity(),Serializable {
    private lateinit var recyclerViewStores: RecyclerView
    private lateinit var dbReference: DatabaseReference
    private lateinit var imageViewFilter:ImageView
    private lateinit var firebaseDatabase: FirebaseDatabase
    private  var storesList =ArrayList<Store>();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_stores)
    handleBottomNavigationView()
setupToolbarText()
        bindViews();
handleClickListeners()
        getDataFromDb()
    }

    private fun handleClickListeners() {
        imageViewFilter.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                intent=Intent(this@AllStoresActivity,CategoryFiltersActivity::class.java)
                startActivity(intent)

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



                val storeClickListener=object :AllStoresClickListener{
                    override fun onClick(store: Store) {

                        saveCategory(store.store_name, store.mainCategoryName.toString(),store.store_id,store.store_logo)
                        val intent=Intent(this@AllStoresActivity,OnStoreOpenActivity::class.java)
                        intent.putExtra(AppConst.STORENAME,store.store_name)
                        intent.putExtra(AppConst.STORECATEGORY,store.mainCategoryName)
                       intent.putExtra(AppConst.STOREDESCRIPTION,store.store_description)
                        intent.putExtra(AppConst.STORELOGO,store.store_logo)
                        intent.putExtra(AppConst.WHERETOGO,"AllStoresActivity")


                        intent.putExtra(AppConst.STOREID,store.store_id)

                        startActivity(intent)


                    }

                }

                val allStoresProductAdapter=AllStoresAdapter(storesList,storeClickListener)
                val linearLayoutManager=LinearLayoutManager(this@AllStoresActivity)
                linearLayoutManager.orientation=RecyclerView.VERTICAL
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
        StoreSession.init(this)
    }

    private fun bindViews() {
        recyclerViewStores=findViewById(R.id.recyclerViewStores)
      //  imageViewFilter=findViewById(R.id.ImageViewFilter)

    }

    private fun handleBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setSelectedItemId(R.id.page_4);


        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_2 -> {
                    val intent = Intent(this@AllStoresActivity, MyStoreActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }
                R.id.page_3 -> {
                    val intent = Intent(this@AllStoresActivity, OrdersActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }
                R.id.page_1 -> {
                    val intent = Intent(this@AllStoresActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }
                R.id.page_5 -> {
                    val intent = Intent(this@AllStoresActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }

                else -> false
            }

        }
    }
    private fun setupToolbarText() {
        if (supportActionBar != null) {

            getSupportActionBar()!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar()!!.setCustomView(R.layout.actionbarallstore);

            val view = supportActionBar!!.customView
            imageViewFilter=view.findViewById(R.id.ImageViewFilter)

//            var textViewTitle: TextView =view.findViewById(R.id.action_bar_title)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
//    #c2dde6
        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            onActionViewExpanded()
            requestFocusFromTouch();

            setQueryHint("Search for products or stores");
            // Assumes current activity is the searchable activity
            // val textView:TextView = this.findViewById(id) as TextView
            val id: Int = getContext().getResources().getIdentifier("android:id/search_src_text", null, null)
            val searchText:TextView = findViewById(id)
            val id2 = getContext().getResources().getIdentifier("android:id/search_edit_frame", null, null)
            val searchEditFrame: LinearLayout = findViewById(id2) as LinearLayout
            val layoutParams: LinearLayout.LayoutParams = searchEditFrame.layoutParams as LinearLayout.LayoutParams
            layoutParams.marginStart = -50


            setSubmitButtonEnabled(true)
            setIconified(false);
            clearFocus()
            setIconifiedByDefault(false);
            // Do not iconify the widget; expand it by default
            searchText.setTextColor(Color.WHITE)
            searchText.setHintTextColor(Color.LTGRAY)
            searchText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F)

            setSearchableInfo(searchManager.getSearchableInfo(ComponentName(getApplicationContext(), SearchResultsActivity::class.java)))
        }
        return true
    }




}