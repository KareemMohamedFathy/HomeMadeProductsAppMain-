package com.homemadeproductsapp.MyStore


import MyStoreNewsFeedAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.homemadeproductsapp.AppConst
import com.homemadeproductsapp.DB.Category
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.Details.DetailsActivity
import com.homemadeproductsapp.MyStore.Adapter.MyStoreItemsAdapter
import com.homemadeproductsapp.MyStore.ItemsAndFeed.CreateItemActivity
import com.homemadeproductsapp.MyStore.ItemsAndFeed.CreateNewsFeedActivity
import com.homemadeproductsapp.MyStore.ItemsAndFeed.TypeSelectorFragment
import com.homemadeproductsapp.MyStore.Listeners.NewsFeedClickListener
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant
import kotlinx.android.synthetic.main.activity_my_store.*
import kotlinx.android.synthetic.main.item_adapter_layout.*


class MyStoreActivity : AppCompatActivity(),OnProductClickListener {
    companion object {
       private const val ADD_STORE_CODE = 100
    }
    private  var listItems=ArrayList<Product>()
    private  var timeLinePhotos=ArrayList<Feed>()

    private lateinit var recyclerViewItems:RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private  lateinit var storeIdExists:String
    private  lateinit var storeNameExists:String
    private  lateinit var imagePathExists:String


    private lateinit var mainGroup:Group
    private lateinit var subGroup:Group
    private lateinit var textViewstoreName: TextView
    private lateinit var textViewstoreDescription: TextView

    private lateinit var buttonCreateStore: Button
    private lateinit var imageViewLogo: ImageView

    private lateinit var buttonAddItems:FloatingActionButton




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_store)
        bindViews()
        setupSharedPreference()
        setupToolbarText()

        storeIdExists=""
        auth = FirebaseAuth.getInstance()
        mainGroup.visibility = View.INVISIBLE
        val lol =   FirebaseDatabase.getInstance().getReference("User").child(auth.currentUser!!.uid).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            storeIdExists = it.child("store_id").value.toString()

            if (storeIdExists.isEmpty()) {
                mainGroup.visibility = View.INVISIBLE
                subGroup.visibility = View.VISIBLE
            }

            getStoreData()

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }




    }

    private fun setupToolbarText() {
        if (supportActionBar != null) {
            getSupportActionBar()!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar()!!.setCustomView(R.layout.actionbar);
            val view = supportActionBar!!.customView
            var textViewTitle:TextView=view.findViewById(R.id.action_bar_title)
            textViewTitle.setText("My Store")
            var back:ImageView=view.findViewById(R.id.action_bar_Image)
            back.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    finishAndRemoveTask();
                }

            }

            )

        }
    }

    private fun setupSharedPreference() {
        StoreSession.init(this)
    }

    private fun saveCategory(category: String) {
        StoreSession.write(PrefConstant.MAINCATEGORY, category)
    }

    private fun getStoreData() {
        val lol =   FirebaseDatabase.getInstance().getReference("Store").child(storeIdExists).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            val name:String=it.child("store_name").value.toString()
            val description:String=it.child("store_description").value.toString()
            storeNameExists=it.child("store_name").value.toString()
            imagePathExists=it.child("store_logo").value.toString()
            val category:String=it.child("mainCategoryName").value.toString()
            saveCategory(category)


            val imagepath=it.child("store_logo").value.toString()
            Log.d("hahaha",imagepath)
            Log.d("hahaha1",imagePathExists)


            textViewstoreName.setText(name)
            textViewstoreDescription.setText(description)

            Glide.with(this)
                    .load(imagepath)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageViewLogo);
            getDataFromDbForProducts()
            getDataFromDbForTimeLine()


        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }


    }



    private fun setupTabs(){
        Log.d("MyStore", "weo" + storeIdExists)
        if(!storeIdExists.isEmpty())
        mainGroup.visibility = View.VISIBLE


        firebaseDatabase= FirebaseDatabase.getInstance()




        recyclerViewItems=findViewById(R.id.recyclerViewTab)
        val myStoreProductsAdapter = MyStoreItemsAdapter(listItems)
        val linearLayoutManager = LinearLayoutManager(this@MyStoreActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewItems.layoutManager = linearLayoutManager
        recyclerViewItems.adapter = myStoreProductsAdapter
        Log.d("checkcheck", listItems.size.toString())

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {


            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {

                    val myStoreProductsAdapter = MyStoreItemsAdapter(listItems)
                    val linearLayoutManager = LinearLayoutManager(this@MyStoreActivity)
                    linearLayoutManager.orientation = RecyclerView.VERTICAL
                    recyclerViewItems.layoutManager = linearLayoutManager
                    recyclerViewItems.adapter = myStoreProductsAdapter
                }
                if (tab?.position == 1) {

                    val newsFeedClickListener=object :NewsFeedClickListener{
                        override fun onClick(feed: Feed) {
                            if(feed.caption!!.isNotEmpty()&&feed.addDate!!.isNotEmpty()&&feed.imagePathProduct!!.isNotEmpty()) {
                                val intent = Intent(this@MyStoreActivity, DetailsActivity::class.java)
                                intent.putExtra(AppConst.CAPTION, feed.caption)
                                intent.putExtra(AppConst.DATE, feed.addDate)
                                intent.putExtra(AppConst.IMAGEPATH, feed.imagePathProduct)
                                intent.putExtra(AppConst.STORENAME, storeNameExists)
                                intent.putExtra(AppConst.STOREIMAGEPATH, imagePathExists)

                                startActivity(intent)
                            }
                        }

                    }

                    val myStoreProductsAdapter = MyStoreNewsFeedAdapter(timeLinePhotos,newsFeedClickListener)
                    val linearLayoutManager = LinearLayoutManager(this@MyStoreActivity)
                    linearLayoutManager.orientation = RecyclerView.VERTICAL
                    recyclerViewItems.layoutManager = linearLayoutManager
                    recyclerViewItems.adapter = myStoreProductsAdapter
                    recyclerViewItems.adapter = myStoreProductsAdapter


                }
            }


            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
        setupClickListeners()






    }

    private fun getDataFromDbForProducts() {

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
                        val p: Product = Product(name, id, copies.toInt(), available, price.toDouble(), description, imagePathProduct, storeIdExists)
                        listItems.add(p)

                    }


                }

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

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
                setupTabs()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun setupClickListeners() {
        buttonCreateStore.setOnClickListener(
                object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        startActivityForResult(Intent(this@MyStoreActivity, CreateStoreActivity::class.java), ADD_STORE_CODE)
                        insertCategories()


                    }
                }
        )
      buttonAddItems.setOnClickListener(object : View.OnClickListener {
          override fun onClick(v: View?) {
              openPicker()

          }
      }
      )

    }

    private fun insertCategories() {
        // dbReference = firebaseDatabase.getReference("Category")


        dbReference = firebaseDatabase.getReference("Category");

        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e("Count ", "" + dataSnapshot.getChildrenCount());
                val count:Int=dataSnapshot.childrenCount.toInt()

                if (count==0) {
                    val allCategories = arrayOf(arrayOf("Clothing", "shirt", "shorts", "dresses", "jackets", "shoes", "trousers", "socks"
                    ), arrayOf("Food", "Bakery", "ReadyToCook", "FastFood", "pickles", "powders", "Diet Food", "Frozen Food", "cans"), arrayOf("Home crafts", "Home accessories", "Home Decor", "woodwork"
                    ), arrayOf(
                            "Accessories",
                            "Pet Accessories",
                            "Hair Accessories",
                            "BELTS",
                            "SCARVES",
                            "HEADBANDS",
                            "bags",
                            "hats",
                            "phone cases"),

                            arrayOf("Books",
                                    "book accessories",
                                    "literature",
                                    "childeren books",
                                    "magazines",
                                    "guides"),
                            arrayOf(
                                    "Toys",
                                    "Puzzles",
                                    "videogames",
                                    "dolls&&stuffed toys",
                                    "card games"

                            ), arrayOf("Jewellery",
                            "necklaces",
                            "rings",
                            "braclets"))

                    for (i in 0..6) {

                        val mainCategory = allCategories[i][0]
                        var pos = 0
                        for (cat in allCategories[i]) {
                            if (pos != 0) {
                                val categoryId = dbReference.push().key.toString()
                                val categoryNew = Category(mainCategory, cat, categoryId)
                                dbReference.child(categoryId).setValue(categoryNew)

                            }
                            pos++
                        }

                    }


                    //notifyDataSetChanged();
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        )
    }




    private fun bindViews() {
        mainGroup=findViewById(R.id.maingroup)
        subGroup=findViewById(R.id.subgroup)
        buttonCreateStore=findViewById(R.id.buttonCreateStore)
        buttonAddItems=findViewById(R.id.buttonAddItems)
        textViewstoreName=findViewById(R.id.storeName)
        textViewstoreDescription=findViewById(R.id.storeDescription)
        imageViewLogo=findViewById(R.id.logoPic)

    }

    override fun onProductClick() {
   val intent:Intent=Intent(this@MyStoreActivity, CreateItemActivity::class.java)
        intent.putExtra("store_id", storeIdExists)
        Log.d("MyStoreAcs", storeIdExists)
       startActivity(intent)
    }

    override fun onFeedClick() {
        val intent:Intent=Intent(this@MyStoreActivity, CreateNewsFeedActivity::class.java)
        intent.putExtra("store_id", storeIdExists)

        startActivity(intent)

    }
    private fun openPicker(){
        val dialog= TypeSelectorFragment.newInstance()
        dialog.show(supportFragmentManager, TypeSelectorFragment.TAG)
    }


}
interface OnProductClickListener {
    fun onProductClick()
    fun onFeedClick()
}

