package com.homemadeproductsapp.Details

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.homemadeproductsapp.MyStore.dataCommunication
import com.homemadeproductsapp.R

class DetailsFragment : DialogFragment() {

    private  lateinit var storeName:String
    private  lateinit var caption:String
    private  lateinit var imagePathProduct:String
    private  lateinit var imagePathStoreLogo:String
    private  lateinit var addDate:String
    private lateinit var storeNameTextView: TextView
    private lateinit var captionTextView: TextView
    private lateinit var addDateTextView: TextView
    private lateinit var imagePathProductImageView: ImageView
    private lateinit var imagePathStoreLogoImageView: ImageView
    private lateinit var view1:View
    private lateinit var dataCommunication: dataCommunication
    private lateinit var buttonClose:ImageView

    override fun onAttach(context: Context) {
        super.onAttach(context)
    dataCommunication=context as dataCommunication
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
         view1= inflater.inflate(R.layout.activity_details, container, false)
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        setupClickListeners()
        setData()

    }

    private fun setupClickListeners() {
        buttonClose.setOnClickListener{
            dismiss()
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do custom work here
                    dismiss()
                }
            }
            )


    }


    private fun setData() {
        captionTextView.setText(dataCommunication.feed.caption)
        addDateTextView.setText(dataCommunication.feed.addDate)
        storeNameTextView.setText(dataCommunication.store_name)
        Glide.with(this).load(dataCommunication.store_logo).into(imagePathStoreLogoImageView)
        Glide.with(this).load(dataCommunication.feed.imagePathProduct).into(imagePathProductImageView)

    }


    private fun bindViews() {
        captionTextView=view1.findViewById(R.id.textViewCaption)
        addDateTextView=view1.findViewById(R.id.textViewAddDate)
        storeNameTextView=view1.findViewById(R.id.textViewStoreName)
        imagePathProductImageView=view1.findViewById(R.id.imageViewProductPhoto)
        imagePathStoreLogoImageView=view1.findViewById(R.id.imageViewStoreLogo)
        buttonClose=view1.findViewById(R.id.close)

    }
}