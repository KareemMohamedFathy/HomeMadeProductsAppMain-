package com.homemadeproductsapp.AllStores.CategoriesFilter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.homemadeproductsapp.AllStores.CategoriesFilter.adapter.SubCategoriesAdapter
import com.homemadeproductsapp.AllStores.CategoriesFilter.listeners.SubCategoriesListener
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant
import kotlinx.android.synthetic.main.activity_search_results.*

class SubCategoriesFragment : Fragment() {

    val allCategories = arrayOf(arrayOf("Clothing", "shirt", "shorts", "dresses", "jackets", "shoes", "trousers", "socks"
    ),
        arrayOf("Food", "Bakery", "ReadyToCook", "FastFood", "pickles", "powders", "Diet Food", "Frozen Food", "cans,","other"),//food
        arrayOf("Home crafts", "Home accessories", "Home Decor", "woodwork"
            ,"other" ),//home crafts
        arrayOf(
            "Accessories",
            "Pet Accessories",
            "Hair Accessories",
            "BELTS",
            "SCARVES",
            "HEADBANDS",
            "bags",
            "hats",
            "phone cases"
            ,"other"),//accessories
        arrayOf("Books",
            "book accessories",
            "literature",
            "childeren books",
            "magazines",
            "guides"
            ,"other"), //books
        arrayOf("Toys", "Puzzles", "videogames", "dolls&&stuffed toys", "card games"
            ,"other"),//toys
        arrayOf("Jewellery",
            "necklaces",
            "rings", "bracelets","other"))


    private lateinit var view1:View
    private lateinit var imageViewBack:ImageView
    private lateinit var category:String
    private lateinit var subCategoriesListener: SubCategoriesListener
    private lateinit var backtoMain: backtoMain
    private lateinit var categoryBoth: CategoryBoth

    private lateinit var recyclerViewSubCategories: RecyclerView
    private  var list = ArrayList<String>()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        subCategoriesListener=context as SubCategoriesListener
        backtoMain=context as backtoMain
        categoryBoth =context as CategoryBoth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         view1= inflater.inflate(R.layout.fragment_sub_categories, container, false)
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity()
                .onBackPressedDispatcher
                .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        // Do custom work here

                        backtoMain.onBackToCat()

                    }
                }
                )
        getData()

        setupRecyclerView()
        setupClickListeners()


    }

    private fun setupClickListeners() {
      val  SubCategoriesListener=object : SubCategoriesListener {
            override fun displayResults(category: String) {

                subCategoriesListener.displayResults(category)

            }

        }
        imageViewBack=view1.findViewById(R.id.back)
        imageViewBack.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
         backtoMain.onBackToCat()
            }

        })
    }

    private fun setupRecyclerView() {
        recyclerViewSubCategories=view1.findViewById(R.id.recyclerViewCategories)
        val pageAdapter= SubCategoriesAdapter(list,subCategoriesListener)
        val linearLayoutManager= LinearLayoutManager(requireContext())
        linearLayoutManager.orientation=RecyclerView.VERTICAL
        recyclerViewSubCategories.layoutManager=linearLayoutManager
        recyclerViewSubCategories.adapter=pageAdapter


    }

    override fun onResume() {
        super.onResume()
        list.clear()
        getData()

        setupRecyclerView()
        setupClickListeners()

    }


    private fun getData() {
        category= categoryBoth.mainCategory
        for(cat in allCategories){
            if(cat[0]==category){
                list.addAll(cat)
            }
        }
        list.removeAt(0)
        list.add("All "+category)
    }



}

interface backtoMain{
    fun onBackToCat()
}
