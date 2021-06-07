package com.homemadeproductsapp.MyStore.ItemsAndFeed

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.homemadeproductsapp.MyStore.OnProductClickListener
import com.homemadeproductsapp.R
import java.util.zip.Inflater

class TypeSelectorFragment: BottomSheetDialogFragment() {
    private lateinit var onProductClickListener: OnProductClickListener
    private lateinit var textViewProduct: TextView
    private lateinit var textViewNewsFeed: TextView

    companion object {
        const val TAG = "TypeSelectorFragment"
        fun newInstance() = TypeSelectorFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onProductClickListener=context as OnProductClickListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.producttype_selector,container,false)
        bindViews(view)
        setupClickListeners()
        return view

    }

    private fun setupClickListeners() {
        textViewNewsFeed.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                onProductClickListener.onFeedClick()
                dismiss()
            }
        }
        )
                textViewProduct.setOnClickListener(object :View.OnClickListener{
                    override fun onClick(v: View?) {
                        onProductClickListener.onProductClick()
                        dismiss()
                    }
                }

        )

    }

    private fun bindViews(view: View?) {
        textViewProduct=view!!.findViewById(R.id.textViewProduct)
        textViewNewsFeed=view.findViewById(R.id.textViewNewsFeed)

    }


}