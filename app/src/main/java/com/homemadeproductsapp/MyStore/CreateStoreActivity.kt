
package com.homemadeproductsapp.MyStore

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.homemadeproductsapp.BuildConfig
import com.homemadeproductsapp.DB.Category
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Store
import com.homemadeproductsapp.FileSelectorFragment
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreateStoreActivity : AppCompatActivity(), OnOptionClickListener, AdapterView.OnItemSelectedListener {
    private lateinit var editTextName:EditText
    private lateinit var editTextDescription:EditText
    private lateinit var group: Group
    private lateinit var progressBar:ProgressBar
    private lateinit var group2: Group

    private lateinit var buttonSubmit:Button

    private  val auth: FirebaseAuth=FirebaseAuth.getInstance()

    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private  var curUser=auth.currentUser!!.uid
    private lateinit var imageViewAdd:ImageView
    private var picturePath = ""
    private lateinit var imageLocation: File
    private lateinit var category: String
    val mainCategories= arrayOf("Clothing","Food","Accessories","Home crafts","Books","Toys","Jewellery")




    companion object {
        private const val MY_PERMISSION_CODE = 124
        private const val REQUEST_CODE_CAMERA = 1
        private const val REQUEST_CODE_GALLERY = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_store)
        bindViews()
        setupSharedPreference()
        spinnerStart()
        setupToolbarText()
        setupClickListeners()

    }
    private fun setupSharedPreference() {
        StoreSession.init(this)
    }

    private fun spinnerStart() {
        val spin = findViewById<Spinner>(R.id.spinnerCategory)

        spin.onItemSelectedListener = this
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                mainCategories)
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item)

        spin.adapter = adapter
    }

    private fun setupToolbarText() {
        if (supportActionBar != null) {
            getSupportActionBar()!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar()!!.setCustomView(R.layout.actionbar);
            val view = supportActionBar!!.customView
            var textViewTitle: TextView =view.findViewById(R.id.action_bar_title)
            textViewTitle.setText("Create Your Store")
            var back:ImageView=view.findViewById(R.id.action_bar_Image)
            back.setOnClickListener(object :View.OnClickListener{
                override fun onClick(v: View?) {
                val intent:Intent=Intent(this@CreateStoreActivity,MyStoreActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }

            )

        }
    }

    private fun setupClickListeners() {
        buttonSubmit.setOnClickListener (
            object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val name = editTextName.text.toString()
                    val description = editTextDescription.text.toString()
                    if(name.length>30){
                        Toast.makeText(this@CreateStoreActivity,"Max number of character for store name is 30",Toast.LENGTH_SHORT).show()
                    }
                   else if(description.length>100){
                        Toast.makeText(this@CreateStoreActivity,"Max number of character for store description is 100",Toast.LENGTH_SHORT).show()
                    }
                    else {

                        val imagepath = picturePath
                        addToDb(name, description, imagepath)
                        saveCategory(category)
                        val intent = Intent(this@CreateStoreActivity, MyStoreActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }
        )
        imageViewAdd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (checkAndRequestPermissions()) {
                    openPicker()

                }
            }
        })
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
                        Glide.with(this).load(imageUrl).skipMemoryCache(false).into(imageViewAdd)
                            progressBar.visibility=Group.GONE
                            group.visibility=Group.VISIBLE
                            group2.visibility=Group.GONE


                        }
                    })


                ?.addOnFailureListener(OnFailureListener { e ->
                    print(e.message)
                })
        }
    }


    private fun saveCategory(category: String) {
        StoreSession.write(PrefConstant.MAINCATEGORY, category)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(mFileName, ".jpg", storageDir)
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
    private fun openPicker() {
        val dialog = FileSelectorFragment.newInstance()
        dialog.show(supportFragmentManager, FileSelectorFragment.TAG)
    }




    private fun addToDb(
        name: String,
        description: String,
        imagepath: String
    ) {


            firebaseDatabase= FirebaseDatabase.getInstance()
            dbReference = firebaseDatabase.getReference("Store")
            val storeId = dbReference.push().key.toString()
            //Creating an empty arraylist
            val store= Store(storeId,name,imagepath,description,category,curUser)
            dbReference.child(storeId).setValue(store)

            firebaseDatabase= FirebaseDatabase.getInstance()
            dbReference = firebaseDatabase.getReference("User")
            dbReference.child(curUser).child("store_id").setValue(storeId)





        }

    private fun bindViews() {
        group=findViewById(R.id.group1)
        group2=findViewById(R.id.group2)
        progressBar=findViewById(R.id.progressBar)

        editTextName=findViewById(R.id.editTextName)
       editTextDescription=findViewById(R.id.editTextDescription)

        buttonSubmit=findViewById(R.id.submit_button)
        imageViewAdd=findViewById(R.id.imageViewAdd)
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
                val photoURI = FileProvider.getUriForFile(this@CreateStoreActivity, BuildConfig.APPLICATION_ID + ".provider", photoFile)
                imageLocation = photoFile
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
            }
        }
    }

    override fun onGalleryClick() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                    val selectedImage = data?.data
                    picturePath = selectedImage.toString()
                    group2.visibility=Group.VISIBLE
                    group.visibility=Group.INVISIBLE
                    progressBar.visibility= Group.VISIBLE
                    uploadImageToFirebase(picturePath.toUri())

                  /*  val contentResolver = applicationContext.contentResolver
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    picturePath.toUri()?.let { contentResolver.takePersistableUriPermission(it, takeFlags) }
                    Glide.with(this).load(picturePath.toUri()).into(imageViewAdd)
                */

                }
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        category=mainCategories[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
    finish()
    }

}
interface OnOptionClickListener {
    fun onCameraClick()
    fun onGalleryClick()
}