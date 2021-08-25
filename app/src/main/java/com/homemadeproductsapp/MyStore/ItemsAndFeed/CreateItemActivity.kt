package com.homemadeproductsapp.MyStore.ItemsAndFeed


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.homemadeproductsapp.BuildConfig
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.FileSelectorFragment
import com.homemadeproductsapp.MyStore.MyStoreActivity
import com.homemadeproductsapp.MyStore.OnOptionClickListener
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_item.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CreateItemActivity : AppCompatActivity(), OnOptionClickListener,AdapterView.OnItemSelectedListener {
    companion object {
        private const val MY_PERMISSION_CODE = 124
        private const val REQUEST_CODE_CAMERA = 1
        private const val REQUEST_CODE_GALLERY = 2
    }
    private var picturePath = ""
    private lateinit var group: Group
    private lateinit var progressBar:ProgressBar
    private lateinit var group2: Group

    private lateinit var imageLocation: File
    private lateinit var editTextPrice:TextView
    private lateinit var editTextName:TextView
    private lateinit var editTextCopies:TextView
    private lateinit var editTextDescription:TextView
    private lateinit var buttonAddItem:Button
    private lateinit var store_id:String
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var imageViewAddItem: ImageSwitcher
    private lateinit var imageViewAddImageView: ImageView

    private lateinit var mainCategory:String
    private  var subCategories= arrayListOf<String>()
    private lateinit var subcategory:String

    private  val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private  var curUser=auth.currentUser!!.uid
    private  var count=1

    private var images: ArrayList<Uri?>? = null
    private lateinit var nextBtn:ImageView
    private lateinit var  spin:Spinner

    private lateinit var previousBtn:ImageView
    private  var stringUri=ArrayList<String>()
    private lateinit var circularProgressDrawable:CircularProgressDrawable

    //current position/index of selected images
    private var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_item)

        circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 50f
        circularProgressDrawable.start()


        bindViews()

        images = ArrayList()

        //    imageViewImageSwitcher.setFactory { imageViewAddItem}
/*         imageViewImageSwitcher.setFactory(ViewSwitcher.ViewFactory { // TODO Auto-generated method stub

  // Create a new ImageView and set it's properties
             imageViewAddImageView  = ImageView(applicationContext)
             imageViewAddImageView.scaleType = ImageView.ScaleType.FIT_XY
             imageViewAddImageView
          })
  */
          imageViewImageSwitcher.setFactory(ViewSwitcher.ViewFactory { // TODO Auto-generated method stub

// Create a new ImageView and set it's properties
            val imageView = ImageView(this)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageView.layoutParams = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            imageView
        })
    setupSharedPreference()
        setupToolbarText()
        getIntentData()
     setupClickListeners()

    }
    private fun setupSharedPreference() {
        StoreSession.init(this)
    }
    private fun setupToolbarText() {
        if (supportActionBar != null) {
            getSupportActionBar()!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar()!!.setCustomView(R.layout.actionbar);
            val view = supportActionBar!!.customView
            var textViewTitle: TextView =view.findViewById(R.id.action_bar_title)
            textViewTitle.setText("Add Your Product")
            textViewTitle.setTextColor(Color.WHITE)
            var back: ImageView =view.findViewById(R.id.action_bar_Image)
            back.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val intent: Intent = Intent(this@CreateItemActivity, MyStoreActivity::class.java)
                   startActivity(intent)
                    finish()
                }
            }
            )

        }
    }

    private fun getIntentData() {
        val intent = intent

        if (intent.hasExtra("store_id")) {
            store_id= intent.getStringExtra("store_id").toString()
        }
        if(!store_id.isEmpty()) {
            mainCategory = StoreSession.readString(PrefConstant.MAINCATEGORY)!!
            getSubCategories(mainCategory)

        }
    }

    private fun getSubCategories(mainCategory: String) {
        val reference = FirebaseDatabase.getInstance().reference

        val query = reference.child("Category").orderByChild("mainCategory").equalTo(mainCategory)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                subCategories.add("select subcategory")
                if (dataSnapshot.exists()) {
                    for (dsp in dataSnapshot.children) {

                        val subCategory = dsp.child("subCategory").value.toString()
                        subCategories.add(subCategory)

                    }
                }
               setupSpinners()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        )


    }

    private fun setupSpinners() {
        spin = findViewById<Spinner>(R.id.spinnerSubCategory)
        spin.setPrompt("Pick One");


        spin.onItemSelectedListener = this
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                subCategories)
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item)

        spin.adapter = adapter
    }

    private fun setupClickListeners() {
    val clickAction=object : View.OnClickListener{
        override fun onClick(v: View?) {
            val name = editTextName.text.toString()
            val price = editTextPrice.text.toString()
            val copies = editTextCopies.text.toString()
            val description = editTextDescription.text.toString()

            firebaseDatabase = FirebaseDatabase.getInstance()
            dbReference = firebaseDatabase.getReference("Product")
            val productId = dbReference.push().key.toString()
            for (uri in images!!) {
                stringUri.add(uri.toString())
            }
            if (images!!.size==0) {
                Toast.makeText(this@CreateItemActivity, "Plz add at least 1 photo", Toast.LENGTH_SHORT).show()
            }
        else    if (subcategory == "select subcategory") {
                Toast.makeText(this@CreateItemActivity, "Plz choose category", Toast.LENGTH_SHORT).show()
            }
            else    if (TextUtils.isEmpty(name)||TextUtils.isEmpty(price)||TextUtils.isEmpty(description)) {
                Toast.makeText(this@CreateItemActivity, "Plz fill all data", Toast.LENGTH_SHORT).show()
            }
          else  if(name.length>40){
                Toast.makeText(this@CreateItemActivity,"Max number of character for product name is 40",Toast.LENGTH_SHORT).show()
            }
            else if(description.length>150){
                Toast.makeText(this@CreateItemActivity,"Max number of character for product description is 150",Toast.LENGTH_SHORT).show()
            }


            else{
                val p: Product = Product(name, productId, copies.toInt(), "Yes", price.toDouble(), description, picturePath, store_id, subcategory, stringUri)
                dbReference.child(productId).setValue(p)

                intent = Intent(this@CreateItemActivity, MyStoreActivity::class.java)
                startActivity(intent)
                finish()

            }
        }
    }
        buttonAddItem.setOnClickListener(clickAction)
        imageViewAddItem.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (checkAndRequestPermissions()) {
                    openPicker()
                }
            }


        })
        nextBtn.setOnClickListener {
            //Log.d("kusoo", images!![position].toString())

            if (position < images!!.size-1){
                position++
                Glide.with(this).load(images!![position].toString()).skipMemoryCache(false).placeholder(circularProgressDrawable).into(imageViewAddItem.currentView as ImageView)

            }
            else if(images?.size!!>0){
                position=0
                Glide.with(this).load(images!![position].toString()).skipMemoryCache(false).placeholder(circularProgressDrawable).into(imageViewAddItem.currentView as ImageView)

            }
        }


        //switch to previous image clicking this button
        previousBtn.setOnClickListener {
           // Log.d("kusoo", images!![position].toString())
            if (position > 0){
                position--
                Glide.with(this).load(images!![position].toString()).skipMemoryCache(false).placeholder(circularProgressDrawable).into(imageViewAddItem.currentView as ImageView)
            }
            else if(images?.size!!>0){

                position=images!!.size-1
                Glide.with(this).load(images!![position].toString()).skipMemoryCache(false).placeholder(circularProgressDrawable).into(imageViewAddItem.currentView as ImageView)

            }

        }

    }

    private fun openPicker() {
   val dialog=FileSelectorFragment.newInstance()
     dialog.show(supportFragmentManager, FileSelectorFragment.TAG)
    }

    private fun bindViews() {
        group=findViewById(R.id.group1)
        group2=findViewById(R.id.group2)
        progressBar=findViewById(R.id.progressBar)

        editTextPrice=findViewById(R.id.editTextPrice)
        editTextCopies=findViewById(R.id.editTextCopies)
        editTextDescription=findViewById(R.id.editTextDescription)
        editTextName=findViewById(R.id.editTextName)
        buttonAddItem=findViewById(R.id.submit_button)
        imageViewAddItem=findViewById(R.id.imageViewImageSwitcher)
        imageViewAddImageView=findViewById(R.id.imageViewAddItem)
        nextBtn=findViewById(R.id.ImageViewNext)
        previousBtn=findViewById(R.id.ImageViewBack)
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
                val photoURI = FileProvider.getUriForFile(this@CreateItemActivity, BuildConfig.APPLICATION_ID + ".provider", photoFile)
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

            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_GALLERY);


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        images=ArrayList<Uri?>()

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    group2.visibility=Group.VISIBLE
                    group.visibility=Group.INVISIBLE
                    progressBar.visibility= Group.VISIBLE
                     picturePath = imageLocation.path.toString()
                    uploadImageToFirebase(imageLocation.toUri())
                }
                REQUEST_CODE_GALLERY -> {
                    if (data?.getClipData() != null) {
                        position = 0;

                         count = data.clipData!!.itemCount
                        for (i in 0 until count) {
                            group2.visibility=Group.VISIBLE
                            group.visibility=Group.INVISIBLE
                            progressBar.visibility= Group.VISIBLE


                            var imageUri: Uri = data.clipData!!.getItemAt(i).uri
  //                          images!!.add(imageUri)
                            uploadImageToFirebase(imageUri)

                            //    imageViewAddItem.setImageURI(imageUri)
                        }

                    } else if (data?.getData() != null) {
                        // if single image is selected

                        var imageUri: Uri = data.data!!
                        group2.visibility=Group.VISIBLE
                        group.visibility=Group.INVISIBLE
                        progressBar.visibility= Group.VISIBLE

//                        images!!.add(imageUri)
                        uploadImageToFirebase(imageUri)
                        //    imageViewAddItem.setImageURI(images!![0])

                      //  position = 0;

                    }

                }
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        subcategory=subCategories[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

   }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent: Intent = Intent(this@CreateItemActivity, MyStoreActivity::class.java)
        startActivity(intent)
        finish()
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
                            images!!.add(imageUrl)

                            Glide.with(this).load(images!![0].toString()).skipMemoryCache(false).placeholder(circularProgressDrawable).into(imageViewAddItem.currentView as ImageView)
                            if(images!!.size==count){
                                progressBar.visibility=Group.GONE
                                group.visibility=Group.VISIBLE
                                group2.visibility=Group.GONE

                            }


                        }
                    })


                ?.addOnFailureListener(OnFailureListener { e ->
                    print(e.message)
                })
        }
    }

}
