package com.homemadeproductsapp.Details

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import com.homemadeproductsapp.AppConst
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.MyStore.MyStoreActivity
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {
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





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setupToolbarText()
        getIntentData()
        bindViews()
        setData()

    }

    private fun setData() {
        captionTextView.setText(caption)
        addDateTextView.setText(addDate)
        storeNameTextView.setText(storeName)
        Log.d("lolhah",imagePathStoreLogo)

        Glide.with(this).load(imagePathStoreLogo).into(imagePathStoreLogoImageView)
        Glide.with(this).load(imagePathProduct).into(imagePathProductImageView)

    }

    private fun bindViews() {
        captionTextView=findViewById(R.id.textViewCaption)
        addDateTextView=findViewById(R.id.textViewAddDate)
        storeNameTextView=findViewById(R.id.textViewStoreName)
        imagePathProductImageView=findViewById(R.id.imageViewProductPhoto)
        imagePathStoreLogoImageView=findViewById(R.id.imageViewStoreLogo)

    }

        private fun getIntentData() {
            val intent = intent
            if (intent.hasExtra(AppConst.STORENAME)) {
                storeName= intent.getStringExtra(AppConst.STORENAME).toString()
           }
            if (intent.hasExtra(AppConst.STOREIMAGEPATH)) {
                imagePathStoreLogo= intent.getStringExtra(AppConst.STOREIMAGEPATH).toString()

            }
            if (intent.hasExtra(AppConst.IMAGEPATH)) {
                imagePathProduct= intent.getStringExtra(AppConst.IMAGEPATH).toString()
            }
            if (intent.hasExtra(AppConst.DATE)) {
                addDate= intent.getStringExtra(AppConst.DATE).toString()
            }
            if (intent.hasExtra(AppConst.CAPTION)) {
                caption= intent.getStringExtra(AppConst.CAPTION).toString()
            }
        }


    private fun setupToolbarText() {
        if (supportActionBar != null) {
            getSupportActionBar()!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar()!!.setCustomView(R.layout.actionbar);
            val view = supportActionBar!!.customView
            var textViewTitle: TextView =view.findViewById(R.id.action_bar_title)
            textViewTitle.setText("Photo Details")
            var back: ImageView =view.findViewById(R.id.action_bar_Image)
            back.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val intent = Intent(this@DetailsActivity, MyStoreActivity::class.java)
                    startActivity(intent)

                }

            }

            )

        }
    }
}