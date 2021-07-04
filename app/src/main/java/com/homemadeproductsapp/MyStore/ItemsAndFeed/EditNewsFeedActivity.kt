package com.homemadeproductsapp.MyStore.ItemsAndFeed

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.homemadeproductsapp.BuildConfig
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.FileSelectorFragment
import com.homemadeproductsapp.MyStore.MyStoreActivity
import com.homemadeproductsapp.MyStore.OnOptionClickListener
import com.homemadeproductsapp.R
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class EditNewsFeedActivity : AppCompatActivity(), OnOptionClickListener {

    private lateinit var group: Group
    private lateinit var progressBar: ProgressBar
    private lateinit var group2: Group

    private lateinit var buttonSubmit: Button
    private lateinit var textViewCaption: TextView
    private lateinit var imageViewAddNewsFeed: ImageView
    private lateinit var store_id:String
    private var picturePath = ""
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var circularProgressDrawable: CircularProgressDrawable
    private lateinit var imageLocation: File
    companion object {
        private const val MY_PERMISSION_CODE = 124
        private const val REQUEST_CODE_CAMERA = 1
        private const val REQUEST_CODE_GALLERY = 2
    }
private lateinit var feed:Feed


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_news_feed)
        bindViews()
        circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 50f
        circularProgressDrawable.start()

        setupToolbarText()
        getIntentData()
        setupOnClickListeners()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent: Intent = Intent(this@EditNewsFeedActivity, MyStoreActivity::class.java)
        startActivity(intent)
        finish()

    }

    private fun getIntentData() {
        val intent = intent
        val type: Type = object : TypeToken<Feed?>() {}.type

        val f = intent.getStringExtra("feed")
        feed = Gson().fromJson<Feed>(f, type)

        if (intent.hasExtra("store_id")) {
            store_id= intent.getStringExtra("store_id").toString()
        }
        textViewCaption.text=feed.caption
        Glide.with(this).load(feed.imagePathProduct).into(imageViewAddNewsFeed)

        picturePath=feed.imagePathProduct.toString()
    }

    private fun setupOnClickListeners() {
        buttonSubmit.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val caption=textViewCaption.text.toString()
                firebaseDatabase= FirebaseDatabase.getInstance()
                dbReference = firebaseDatabase.getReference("Feed")
                val captionId = feed.id.toString()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                val timeline: Feed = Feed(caption,captionId,picturePath,store_id,currentDate.toString())
                dbReference.child(captionId).setValue(timeline)

                intent= Intent(this@EditNewsFeedActivity, MyStoreActivity::class.java)
                startActivity(intent)
                finish()

            }

        })
        imageViewAddNewsFeed.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if(checkAndRequestPermissions())
                    openPicker()
            }

        })
    }

    private fun setupToolbarText() {
        if (supportActionBar != null) {
            getSupportActionBar()!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar()!!.setCustomView(R.layout.actionbar);
            val view = supportActionBar!!.customView
            var textViewTitle: TextView =view.findViewById(R.id.action_bar_title)
            textViewTitle.setText("Edit Post")
            var back: ImageView =view.findViewById(R.id.action_bar_Image)
            back.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    val intent: Intent = Intent(this@EditNewsFeedActivity, MyStoreActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            )

        }
    }

    private fun bindViews() {
        group=findViewById(R.id.group1)
        group2=findViewById(R.id.group2)
        progressBar=findViewById(R.id.progressBar)

        buttonSubmit=findViewById(R.id.submit_button)
        textViewCaption=findViewById(R.id.editTextCaption)
        imageViewAddNewsFeed=findViewById(R.id.imageViewAddNewsFeed)
    }
    private fun openPicker() {
        val dialog= FileSelectorFragment.newInstance()
        dialog.show(supportFragmentManager, FileSelectorFragment.TAG)
    }
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(mFileName, ".jpg", storageDir)
    }
    override fun onCameraClick() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {

            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this@EditNewsFeedActivity, BuildConfig.APPLICATION_ID + ".provider", photoFile)
                imageLocation = photoFile
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
            }
        }
    }
    private fun checkAndRequestPermissions(): Boolean {
        val permissionCAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        val listPermissionsNeeded = ArrayList<String>()
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray<String>(), MY_PERMISSION_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openPicker()
                }
            }
        }
    }

    override fun onGalleryClick() {
        val intent= Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    picturePath = imageLocation.path.toString()
                    group2.visibility= Group.VISIBLE
                    group.visibility= Group.INVISIBLE
                    progressBar.visibility= Group.VISIBLE

                    uploadImageToFirebase(imageLocation.toUri())

                }
                REQUEST_CODE_GALLERY -> {
                    val selectedImage = data?.data
                    picturePath = selectedImage.toString()
                    group2.visibility= Group.VISIBLE
                    group.visibility= Group.INVISIBLE
                    progressBar.visibility= Group.VISIBLE

                    uploadImageToFirebase(selectedImage!!)
                }
            }
        }
    }
    private fun uploadImageToFirebase(fileUri: Uri) {

        if (fileUri != null) {

            val fileName = UUID.randomUUID().toString() +".jpg"

            val database = FirebaseDatabase.getInstance()
            val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")

            refStorage.putFile(fileUri)
                .addOnSuccessListener(
                    OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            val imageUrl = it
                            picturePath=imageUrl.toString()


                            Glide.with(this).load(picturePath).skipMemoryCache(false) .placeholder(circularProgressDrawable)
                                .into(imageViewAddNewsFeed)

                            progressBar.visibility= Group.GONE
                            group.visibility= Group.VISIBLE
                            group2.visibility= Group.GONE


                        }
                    })


                ?.addOnFailureListener(OnFailureListener { e ->
                    print(e.message)
                })
        }
    }


}