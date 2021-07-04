package com.homemadeproductsapp.MyStore


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.homemadeproductsapp.*
import com.homemadeproductsapp.AllStores.AllStoresActivity
import com.homemadeproductsapp.DB.Category
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Order
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.Details.DetailsFragment
import com.homemadeproductsapp.Home.HomeActivity
import com.homemadeproductsapp.MyStore.AcceptOrders.Listener.OrderAcceptClickListener
import com.homemadeproductsapp.MyStore.AcceptOrders.RequestedOrdersFragment
import com.homemadeproductsapp.MyStore.Adapter.MyStoreFragmentAdapter
import com.homemadeproductsapp.MyStore.ItemsAndFeed.*
import com.homemadeproductsapp.MyStore.Listeners.EditItemClickListener
import com.homemadeproductsapp.MyStore.Listeners.NewsFeedClickListener
import com.homemadeproductsapp.PastOrders.OrdersActivity
import com.homemadeproductsapp.R
import com.homemadeproductsapp.profile.ProfileActivity
import com.mindorks.notesapp.data.local.pref.PrefConstant
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_my_store.*
import kotlinx.android.synthetic.main.item_adapter_layout.*


class MyStoreActivity : AppCompatActivity(),OnProductClickListener,dataCommunication,NewsFeedClickListener,OrderAcceptClickListener,EditItemClickListener {
    companion object {
       private const val ADD_STORE_CODE = 100
    }
    private  var edit=false
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
    private  var listItems=ArrayList<Product>()
    private  var timeLinePhotos=ArrayList<Feed>()

    private lateinit var recyclerViewItems:RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private  lateinit var storeIdExists:String
    private  lateinit var storeNameExists:String
    private  lateinit var imagePathExists:String
    private  lateinit var storeCategoryExists:String
    private  var idx=0
    private lateinit var viewPager:ViewPager2


    private lateinit var mainGroup:Group
    private lateinit var subGroup:Group
    private lateinit var textViewstoreName: TextView
    private lateinit var textViewstoreDescription: TextView

    private lateinit var buttonCreateStore: Button
    private lateinit var imageViewLogo: ImageView
    private lateinit var buttonAddItems:FloatingActionButton
    private lateinit var buttonViewRequestedOrders:Button
    private lateinit var switchEditMode:SwitchMaterial




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_store)
        bindViews()

        handleBottomNavigationView()
        setupSharedPreference()
       // setupToolbarText()

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
            storeCategoryExists=category
            saveCategory(category)


            val imagepath=it.child("store_logo").value.toString()



            textViewstoreName.setText(name)
            textViewstoreDescription.setText(description)

            Glide.with(this)
                    .load(imagepath)
                    .into(imageViewLogo);
            getCategoryCount()


            //           getDataFromDbForTimeLine()
            setupClickListeners()
            handleTabs()

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }


    }

    private fun handleTabs() {
        var first=true
        val   pageAdapter = MyStoreFragmentAdapter(this)


        pageAdapter.addTimeLineFragment(
            TimeLineFragment(),
            "Timeline",
            storeNameExists,
            imagePathExists,
            storeIdExists
        )


        for(cat in allCategories[idx]){

            if(!first)
                pageAdapter.addFragment(ItemsFragment(), cat, storeIdExists)
            else
                pageAdapter.addFragment(ItemsFragment(), "All Items", storeIdExists)


            first=false

        }


        viewPager=findViewById(R.id.viewPager2)

        viewPager.adapter = pageAdapter




        val tabs1 = findViewById<TabLayout>(R.id.tabs)

        TabLayoutMediator(tabs1, viewPager) { tab, position ->
            tab.text = pageAdapter.getPageTitle(position)


        }.attach()
        if (!storeIdExists.isEmpty()) {
            mainGroup.visibility = View.VISIBLE
            subGroup.visibility = View.INVISIBLE
        }


    }

    private fun getCategoryCount():Int {
        var x=0
        for(cat in allCategories){
            if(cat[0]==storeCategoryExists){
                idx=x
                return cat.size
            }
            x++
        }



        return 0;
    }

    private fun setupClickListeners() {
        switchEditMode.setOnClickListener(object :View.OnClickListener {
            override fun onClick(v: View?) {
                val read=StoreSession.read(PrefConstant.DONOTSHOWMETHISAGAIN)

                if (switchEditMode.isChecked()) {
                    switchMode = switchEditMode.getTextOn().toString();
                    if(read==null||!read) {
                        val builder = AlertDialog.Builder(this@MyStoreActivity)
                        builder.setTitle("Editor Mode")
                        builder.setMessage("You will now be able edit/delete your products you can edit product by clicking on it")
                        builder.setPositiveButton("Ok") { dialog, which ->
                        }
                        builder.setNegativeButton("Don't show me this again") { dialog, which ->
                            StoreSession.write(PrefConstant.DONOTSHOWMETHISAGAIN,true)
                        }
                        builder.show()
                    }

                }
                else {
                    switchMode = switchEditMode.getTextOff().toString();
                }
                    val previtem=viewPager.currentItem
                handleTabs()
                viewPager.currentItem=previtem
        }
        })

        buttonViewRequestedOrders.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val requestedOrdersFragment = RequestedOrdersFragment()
                val bundle = Bundle()
                bundle.putString("store_id", storeIdExists)
                requestedOrdersFragment.arguments=bundle
                requestedOrdersFragment.setStyle(
                    DialogFragment.STYLE_NORMAL,
                    R.style.DialogFragmentTheme
                );
                requestedOrdersFragment.show(supportFragmentManager, "Jean Boy2")
            }

        })
        buttonCreateStore.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View?) {
                    startActivity(Intent(this@MyStoreActivity, CreateStoreActivity::class.java))
                    finish()
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

        firebaseDatabase= FirebaseDatabase.getInstance()

        dbReference = firebaseDatabase.getReference("Category");

        dbReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e("Count ", "" + dataSnapshot.getChildrenCount());
                val count: Int = dataSnapshot.childrenCount.toInt()

                if (count == 0) {

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
        switchEditMode=findViewById(R.id.switchEditMode)
        buttonViewRequestedOrders=findViewById(R.id.buttonViewRequestedOrders)
    }

    override fun onProductClick() {
   val intent:Intent=Intent(this@MyStoreActivity, CreateItemActivity::class.java)

        intent.putExtra("store_id", storeIdExists)

       startActivity(intent)
        finish()
    }

    override fun onFeedClick() {
        val intent:Intent=Intent(this@MyStoreActivity, CreateNewsFeedActivity::class.java)
        intent.putExtra("store_id", storeIdExists)
        startActivity(intent)
        finish()

    }
    private fun openPicker(){
        val dialog= TypeSelectorFragment.newInstance()
        dialog.show(supportFragmentManager, TypeSelectorFragment.TAG)
    }
    private fun handleBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setSelectedItemId(R.id.page_2);


        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_3 -> {
                    val intent = Intent(this@MyStoreActivity, OrdersActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.page_4 -> {
                    val intent = Intent(this@MyStoreActivity, AllStoresActivity::class.java)
                    startActivity(intent)
                    finish()

                    true

                }
                R.id.page_1 -> {
                    val intent = Intent(this@MyStoreActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }
                R.id.page_5 -> {
                    val intent = Intent(this@MyStoreActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }

                else -> false
            }

        }
    }


    override lateinit var store_logo: String
    override lateinit var store_name: String
    override lateinit var feed: Feed
    override  var switchMode: String="OFF"
    override fun onClick(feed: Feed) {
        if(switchMode=="OFF") {
            val detailsFragment = DetailsFragment()

            detailsFragment.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.DialogFragmentTheme
            );
            detailsFragment.show(supportFragmentManager, "Jean 3")
        }
        else{
            val connectionsJSONString = Gson().toJson(feed)
            val intent= Intent(this@MyStoreActivity, EditNewsFeedActivity::class.java)
            intent.putExtra("feed",connectionsJSONString)
            intent.putExtra("store_id",storeIdExists)
            startActivity(intent)
            finish()
        }
    }

    override fun checkOrderDetails(order: Order) {
    }

    override fun EditItem(product: Product) {

    }
}
interface OnProductClickListener {
    fun onProductClick()
    fun onFeedClick()
}
interface dataCommunication{
    var store_logo:String
    var store_name:String
     var  feed:Feed
     var switchMode:String
}

