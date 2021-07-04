package com.homemadeproductsapp.AllStores

import AllStoresFragmentAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.homemadeproductsapp.AllStores.Adapters.NotifyCart
import com.homemadeproductsapp.AllStores.Adapters.OrderConfirmFragment
import com.homemadeproductsapp.AllStores.Adapters.OrderDone
import com.homemadeproductsapp.AllStores.Listeners.NewsFeedClickListener
import com.homemadeproductsapp.AllStores.Listeners.OnProductClickListener
import com.homemadeproductsapp.AllStores.Order.OrderDoneFragment
import com.homemadeproductsapp.AppConst
import com.homemadeproductsapp.DB.Cart
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.Details.DetailsFragment
import com.homemadeproductsapp.Home.HomeActivity
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant
import kotlinx.android.synthetic.main.activity_on_store_open.*
import java.lang.reflect.Type

class OnStoreOpenActivity : AppCompatActivity(),DataCommunication,OnProductClickListener,BackToMe,NotifyCart,OrderDone,NewsFeedClickListener{
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPager1: ViewPager2
    private lateinit var productId:String
    private lateinit var group: Group
    private  var whereYouWonnaGo=""


    private var fromOrder:Boolean=false


    val allCategories = arrayOf(
        arrayOf(
            "Clothing", "shirt", "shorts", "dresses", "jackets", "shoes", "trousers", "socks"
        ),
        arrayOf(
            "Food",
            "Bakery",
            "ReadyToCook",
            "FastFood",
            "pickles",
            "powders",
            "Diet Food",
            "Frozen Food",
            "cans,",
            "other"
        ),//food
        arrayOf(
            "Home crafts", "Home accessories", "Home Decor", "woodwork", "other"
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
            "phone cases", "other"
        ),//accessories
        arrayOf(
            "Books",
            "book accessories",
            "literature",
            "childeren books",
            "magazines",
            "guides", "other"
        ), //books
        arrayOf(
            "Toys", "Puzzles", "videogames", "dolls&&stuffed toys", "card games", "other"
        ),//toys
        arrayOf(
            "Jewellery",
            "necklaces",
            "rings", "bracelets", "other"
        )
    )
    private lateinit var auth: FirebaseAuth

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
    private lateinit var imageViewCart:ImageView
    private lateinit var textViewAmount:TextView
    private  var numberOfItems:Int=0



    private  var size=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_store_open)
        bindViews()
        setupToolbarText()
        auth = FirebaseAuth.getInstance()

        size=getIntentData()

        getMoreData()
        getStoreData()
        setupSharedPreference()
        setupClickListeners()
        getCartData()


        checkCallSource()


    }

    private fun checkCallSource() {
        if(intent.hasExtra(AppConst.CALLFROMSEARCH)){
            val type: Type = object : TypeToken<Product?>() {}.type
            product=Gson().fromJson<Product>(intent.getStringExtra("Product"), type)
            orderFragment()
        }
    }

    private fun setupClickListeners() {
           imageViewCart.setOnClickListener(object : View.OnClickListener {
               override fun onClick(v: View?) {
                   if (cart != null && cart!!.store_id == storeid) {

                       val orderConfirmFragment = OrderConfirmFragment()

                       orderConfirmFragment.setStyle(
                           DialogFragment.STYLE_NORMAL,
                           R.style.DialogFragmentTheme
                       );
                       orderConfirmFragment.show(supportFragmentManager, "Jean Boy2")
                   }
               }

           })

    }

    override fun getCartData() {
        var amountMap: HashMap<String, Int> = HashMap()
        var priceMap: HashMap<String, Double> = HashMap()
        var picMap: HashMap<String, String> = HashMap()
        var cartPrice:Double=0.00
        numberOfItems=0
        var store_id: String = ""

        dbReference=FirebaseDatabase.getInstance().reference
        val query=dbReference.child("User").child(auth.currentUser!!.uid).child("Cart")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    store_id = snapshot.child("store_id").value.toString()

                    cartPrice = snapshot.child("totalPrice").value.toString().toDouble()

                    amountMap.putAll(snapshot.child("itemsIdAmountList").value as HashMap<String, Int>)

                    priceMap.putAll(snapshot.child("itemsIdPriceList").value as HashMap<String, Double>)
                    picMap.putAll(snapshot.child("itemsIdPicList").value as HashMap<String, String>)
                }
                if (store_id.isNotEmpty() && amountMap.isNotEmpty() && priceMap.isNotEmpty() && picMap.isNotEmpty()) {
                    for (x in amountMap) {
                        numberOfItems += x.value

                    }
                    val cart1: Cart = Cart(store_id, amountMap, priceMap, picMap, cartPrice)
                    cart = cart1
                }
                 checkCartStatus()

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }




      fun checkCartStatus() {

        if(cart!=null&&cart!!.store_id==storeid){
            imageViewCart.alpha=1.0F
            textViewAmount.setText(numberOfItems.toString())
        }
        else{
            imageViewCart.alpha= 0.4F
            textViewAmount.setText("0")

        }

    }

    private fun bindViews() {
        group=findViewById(R.id.TabGroup)
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
            getSupportActionBar()!!.setCustomView(R.layout.actionbarstore);
            val view = supportActionBar!!.customView
            var textViewTitle: TextView =view.findViewById(R.id.action_bar_title)
            imageViewCart=view.findViewById(R.id.action_bar_Image_Cart)
            textViewAmount=view.findViewById(R.id.itemNum)
            textViewAmount.setText(numberOfItems.toString())
            textViewTitle.setText(storeNamo)
            var back: ImageView =view.findViewById(R.id.action_bar_Image)
            back.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    if(whereYouWonnaGo=="AllStoresActivity") {
                        val intent: Intent = Intent(
                                this@OnStoreOpenActivity,
                                AllStoresActivity::class.java
                        )
                        finish()
                        startActivity(intent)
                    }
                    else{
                        val intent: Intent = Intent(
                                this@OnStoreOpenActivity,
                                HomeActivity::class.java
                        )
                        finish()
                        startActivity(intent)

                    }
                }
            }
            )

        }
    }

    private fun getMoreData() {
        if(intent.hasExtra(AppConst.STORELOGO)){
            storeLogo=intent.getStringExtra(AppConst.STORELOGO).toString()
        }
        if(intent.hasExtra(AppConst.WHERETOGO)){
            whereYouWonnaGo=intent.getStringExtra(AppConst.WHERETOGO).toString()
        }
            val reference = FirebaseDatabase.getInstance().reference

            val query = reference.child("Product").orderByChild("store_id").equalTo(storeid)
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

                            val productImage = dsp.child("imagePathProduct").value.toString()
                            val subCategory = dsp.child("subcategory").value.toString()
                            val imagePathsuri = dsp.child("uriPaths").value as ArrayList<String>

                            val p: Product = Product(
                                name,
                                id,
                                copies.toInt(),
                                available,
                                price.toDouble(),
                                description,
                                productImage,
                                storeid,
                                subCategory,
                                imagePathsuri
                            )
                            listProducts.add(p)


                        }
                    }
                    handleFeed()

                    StoreSession.writeList(listProducts, "PRODUCTS_LIST")

                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


        }
    private fun handleFeed() {
        val reference = FirebaseDatabase.getInstance().reference

        val query = reference.child("Feed").orderByChild("store_id").equalTo(storeid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
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

                StoreSession.writeFeed(timeLinePhotos, "TIMELINE")

                handleTabs()

            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }



    private fun handleTabs() {

            val pageAdapter = AllStoresFragmentAdapter(this)
            pageAdapter.addTimeLineFragment(
                StoreTimeLineFragment(),
                "Timeline",
                storeNamo,
                storeLogo
            )

            var first = true
        pageAdapter.addFragment(StoreFragment(), "All Items")


            for (cat in allCategories[idx]) {
                    pageAdapter.addFragment(StoreFragment(), cat)



            }

            viewPager = findViewById(R.id.viewPager2)

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
            var x=0
            for(cat in allCategories){
                if(cat[0]==category){
                    idx=x
                    return cat.size
                }
                x++
            }



        return 0;
    }


    override  var product: Product?=null
    override  var lastPager: Int=0
    override var cart: Cart? =null
    override fun onProductClick(product: Product) {
            orderFragment()

    }
   private fun orderFragment(){

       val justOpenFragment=JustOpenFragment()
       justOpenFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
       justOpenFragment.show(supportFragmentManager, "Jean Boy")


    }

    override fun orderDone() {
        val orderDoneFragment=OrderDoneFragment()
        orderDoneFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
        orderDoneFragment.show(supportFragmentManager, "Jean Boy2")
    }


    override fun onBackPressed() {
        if(whereYouWonnaGo=="AllStoresActivity") {
            val intent: Intent = Intent(this@OnStoreOpenActivity, AllStoresActivity::class.java)
            finish()
            startActivity(intent)
        }
        else {
            val intent: Intent = Intent(this@OnStoreOpenActivity, HomeActivity::class.java)
            finish()
            startActivity(intent)
        }
        super.onBackPressed()
    }


    override lateinit var store_logo: String
    override lateinit var store_name: String
    override lateinit var feed: Feed
    override fun onClick(feed: Feed) {
        val detailsFragment = DetailsFragmentForStore()

        detailsFragment.setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.DialogFragmentTheme
        );
        detailsFragment.show(supportFragmentManager, "Jean 3")

    }




}
interface DataCommunication {
    var product: Product?
    var lastPager:Int
    var cart:Cart?
    var store_logo:String
    var store_name:String
    var  feed:Feed

}