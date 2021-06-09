package com.homemadeproductsapp.AllStores

import AllStoresFragmentAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.*

import com.homemadeproductsapp.AppConst
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant
import kotlinx.android.synthetic.main.activity_on_store_open.*
import java.io.Serializable

class OnStoreOpenActivity : AppCompatActivity(),Serializable,StoreTimeLineFragment.OnItemClick {
    private lateinit var viewPager: ViewPager2

    val allCategories = arrayOf(arrayOf("Clothing", "shirt", "shorts", "dresses", "jackets", "shoes", "trousers", "socks"
    ),
            arrayOf("Food", "Bakery", "ReadyToCook", "FastFood", "pickles", "powders", "Diet Food", "Frozen Food", "cans"),//food
            arrayOf("Home crafts", "Home accessories", "Home Decor", "woodwork"
    ),//home crafts
            arrayOf(
            "Accessories",
            "Pet Accessories",
            "Hair Accessories",
            "BELTS",
            "SCARVES",
            "HEADBANDS",
            "bags",
            "hats",
            "phone cases"),//accessories
            arrayOf("Books",
                    "book accessories",
                    "literature",
                    "childeren books",
                    "magazines",
                    "guides"), //books
            arrayOf("Toys", "Puzzles","videogames","dolls&&stuffed toys", "card games"
            ),//toys
            arrayOf("Jewellery",
            "necklaces",
            "rings", "braclets"))//jewelery
private  var idx=0
private lateinit var storeid:String
    private  var timeLinePhotos=ArrayList<Feed>()

    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private  var listProducts=ArrayList<Product>()
    private lateinit var storeNamo:String
    private lateinit var storeLogo:String
    private lateinit var category:String
    private  var caption:String=""
    private  var imagePathProduct:String=""
    private  var date:String=""


    private  var size=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_store_open)
        setupToolbarText()

          size=getIntentData()

        getMoreData()
        getStoreData()
        setupSharedPreference()



    }

    private fun setupSharedPreference() {
        StoreSession.init(this)
    }

    private fun setupToolbarText() {
        if (supportActionBar != null) {
           storeNamo= StoreSession.readString(PrefConstant.ALLSTORENAME).toString()
            category= StoreSession.readString(AppConst.STOREMAINCATEGORY).toString()
            storeid=StoreSession.readString(AppConst.STOREID).toString()
            storeLogo=StoreSession.readString(AppConst.STORELOGO).toString()

            getSupportActionBar()!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar()!!.setCustomView(R.layout.actionbar);
            val view = supportActionBar!!.customView
            var textViewTitle: TextView =view.findViewById(R.id.action_bar_title)
            textViewTitle.setText(storeNamo)
            var back: ImageView =view.findViewById(R.id.action_bar_Image)
            back.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    for (i in 0..size){

                    }

                    val intent: Intent = Intent(this@OnStoreOpenActivity, AllStoresActivity::class.java)
                    startActivity(intent)
                }
            }
            )

        }
    }

    private fun getMoreData() {
        if(intent.hasExtra(AppConst.STORELOGO)){
            storeLogo=intent.getStringExtra(AppConst.STORELOGO).toString()
        }
            val reference = FirebaseDatabase.getInstance().reference

            val query = reference.child("Product").orderByChild("store_id").equalTo(storeid)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (dsp in dataSnapshot.children) {
                            Log.d("ah",dsp.value.toString())
                            val name = dsp.child("name").value.toString()
                            val id = dsp.child("id").value.toString()
                            val copies = dsp.child("copies").value.toString()
                            val available = when (copies) {
                                "0" -> "Out Of Stock"
                                else -> "Available"
                            }
                            val price = dsp.child("price").value.toString()
                            val description = dsp.child("description").value.toString()

                            val productImage = dsp.child("imagePathProduct").value.toString()
                            val subCategory = dsp.child("subcategory").value.toString()

                            val p: Product = Product(name, id, copies.toInt(), available, price.toDouble(), description, productImage, storeid,subCategory)
                            listProducts.add(p)



                        }
                    }
                  handleFeed()

                    StoreSession.writeList( listProducts,"PRODUCTS_LIST")

                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


        }
    private fun handleFeed() {
        val reference = FirebaseDatabase.getInstance().reference

        val query = reference.child("Feed").orderByChild("store_id").equalTo(storeid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                if (dataSnapshot.exists()) {

                    for (dsp in dataSnapshot.children) {

                        val caption = dsp.child("caption").value.toString()

                        val imagePathProduct = dsp.child("imagePathProduct").value.toString()
                        val date = dsp.child("addDate").value.toString()
                        val id = dsp.child("id").value.toString()

                        val feed = Feed(caption, id, imagePathProduct, storeid, date)

                        timeLinePhotos.add(feed)



                    }
                }
                Log.d("istrue",timeLinePhotos.size.toString())

                StoreSession.writeFeed( timeLinePhotos,"TIMELINE")

                handleTabs()

            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }



    private fun handleTabs() {
         val   pageAdapter = AllStoresFragmentAdapter(this)

        pageAdapter.addTimeLineFragment(StoreTimeLineFragment(),"Timeline",storeNamo,storeLogo)

        var first=true


        for(cat in allCategories[idx]){
            if(!first)
            pageAdapter.addFragment(StoreFragment(),cat)
            else
                pageAdapter.addFragment(StoreFragment(),"All Items")


            first=false

        }
        viewPager=findViewById(R.id.viewPager2)

        viewPager.adapter = pageAdapter




        val tabs1 = findViewById<TabLayout>(R.id.tabs)
        TabLayoutMediator(tabs1, viewPager) { tab, position ->
            tab.text = pageAdapter.getPageTitle(position)

        }.attach()


    }


    private fun getStoreData(){
        if (intent.hasExtra(AppConst.STORENAME)) {

            storeNamo=intent.getStringExtra(AppConst.STORENAME).toString()
        }
    }

    private fun getIntentData():Int {
            Log.d("suda",category)
            var x=0
            for(cat in allCategories){
                if(cat[0]==category){
                    idx=x
                    Log.d("suda",cat.toString())
                    return cat.size
                }
                x++
            }



        return 0;
    }

    override fun onClick(feed: Feed) {
        val args = Bundle()
        args.putString("caption", feed.caption)
        handleTabs()

        viewPager.currentItem=size+1








    }

    /* override fun onClick(feed:Feed) {
         if(feed.caption!!.isNotEmpty()&&feed.addDate!!.isNotEmpty()&&feed.imagePathProduct!!.isNotEmpty()) {
             val intent = Intent(this@OnStoreOpenActivity, TimeLineFragment::)
             intent.putExtra(AppConst.CAPTION, feed.caption)
             intent.putExtra(AppConst.DATE, feed.addDate)
             intent.putExtra(AppConst.IMAGEPATH, feed.imagePathProduct)
             intent.putExtra(AppConst.STORENAME, storeNamo)
             intent.putExtra(AppConst.STOREIMAGEPATH, storeLogo)

             startActivity(intent)

         }
     }*/


}