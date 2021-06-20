package com.homemadeproductsapp.AllStores.CategoriesFilter

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant

class MainCategoriesFragment : Fragment() {
    private lateinit var buttonAccessories:Button
    private lateinit var buttonFood:Button
    private lateinit var buttonJewellery:Button
    private lateinit var buttonToys:Button
    private lateinit var buttonBooks:Button
    private lateinit var buttonHomeCrafts:Button
    private lateinit var buttonClothes:Button
    private lateinit var buttonBack:ImageView
    private lateinit var categoryBoth: CategoryBoth

    private lateinit var view1:View
    private lateinit var onMoveGo: onMoveGo
    override fun onAttach(context: Context) {
        super.onAttach(context)
    onMoveGo=context as onMoveGo
        categoryBoth=context as CategoryBoth
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         view1= inflater.inflate(R.layout.fragment_main_categories, container, false)
        return view1
    }
    private fun setupSharedPreference() {
        StoreSession.init(requireContext())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view1)
        setupClickListeners()

    }

    override fun onResume() {
        super.onResume()


    }

    private fun setupClickListeners() {
        buttonAccessories.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                categoryBoth.mainCategory="Accessories"
                onMoveGo.onNext()
            }
        }
        )


        buttonBooks.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {

                categoryBoth.mainCategory="Books"

                onMoveGo.onNext()

            }
        }
        )



        buttonClothes.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                categoryBoth.mainCategory="Clothing"

                onMoveGo.onNext()

            }
        }
        )


        buttonFood.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                categoryBoth.mainCategory="Food"

                 onMoveGo.onNext()

            }
        }
        )


        buttonHomeCrafts.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                categoryBoth.mainCategory="Home crafts"

                onMoveGo.onNext()

            }
        }
        )



        buttonToys.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                categoryBoth.mainCategory="Toys"

                onMoveGo.onNext()

            }
        }
        )


        buttonJewellery.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                categoryBoth.mainCategory="Jewellery"

                onMoveGo.onNext()

            }
        }
        )
        buttonBack.setOnClickListener{onMoveGo.onBack()}






    }

    private fun bindViews(view1: View) {
        buttonAccessories=view1.findViewById(R.id.buttonAccessories)
        buttonBooks=view1.findViewById(R.id.buttonBooks)
        buttonClothes=view1.findViewById(R.id.buttonClothes)
        buttonFood=view1.findViewById(R.id.buttonFood)
        buttonHomeCrafts=view1.findViewById(R.id.buttonHomeCrafts)
        buttonToys=view1.findViewById(R.id.buttonToys)
        buttonJewellery=view1.findViewById(R.id.buttonJewellery)
        buttonBack = view1.findViewById(R.id.back)

    }

}
interface onMoveGo {
    fun onBack()
    fun onNext()




}
