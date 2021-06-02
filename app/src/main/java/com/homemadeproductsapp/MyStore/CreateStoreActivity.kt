package com.homemadeproductsapp.MyStore

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.homemadeproductsapp.BuildConfig
import com.homemadeproductsapp.DB.Category
import com.homemadeproductsapp.DB.Store
import com.homemadeproductsapp.FileSelectorFragment
import com.homemadeproductsapp.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreateStoreActivity : AppCompatActivity(), OnOptionClickListener {
    private lateinit var editTextName:EditText
    private lateinit var editTextCategory:EditText
    private lateinit var editTextDescription:EditText
    private lateinit var buttonSubmit:Button

    private  val auth: FirebaseAuth=FirebaseAuth.getInstance()

    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private  var curUser=auth.currentUser!!.uid
    private lateinit var imageViewAdd:ImageView
    private var picturePath = ""
    private lateinit var imageLocation: File

    companion object {
        private const val MY_PERMISSION_CODE = 124
        private const val REQUEST_CODE_CAMERA = 1
        private const val REQUEST_CODE_GALLERY = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_store)
        bindViews();
        setupClickListeners()

    }

    private fun setupClickListeners() {
        buttonSubmit.setOnClickListener (
            object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val name = editTextName.text.toString()
                    val category = editTextCategory.text.toString()
                    val description = editTextDescription.text.toString()
                    val imagepath=picturePath
                    addToDb(name, category, description,imagepath)
                    val intent = Intent(this@CreateStoreActivity,MyStoreActivity::class.java)
                    startActivity(intent)


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




    private fun addToDb(name: String, category: String, description: String, imagepath: String) {
            Log.d("MyStoreActivity","fraud1")

            firebaseDatabase= FirebaseDatabase.getInstance()
            dbReference = firebaseDatabase.getReference("Category")
           val categoryId = dbReference.push().key.toString()
            val categoryNew=Category(category,"",categoryId)
            dbReference.child(categoryId).setValue(categoryNew)

            firebaseDatabase= FirebaseDatabase.getInstance()
            dbReference = firebaseDatabase.getReference("Store")
            val storeId = dbReference.push().key.toString()
            //Creating an empty arraylist
            val store= Store(storeId,name,imagepath,description,categoryId,curUser)
            dbReference.child(storeId).setValue(store)

            firebaseDatabase= FirebaseDatabase.getInstance()
            dbReference = firebaseDatabase.getReference("Producer")
            dbReference.child(curUser).child("store_id").setValue(storeId)





        }

    private fun bindViews() {
    editTextName=findViewById(R.id.editTextName)
       editTextCategory=findViewById(R.id.editTextCategory)
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
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    picturePath = imageLocation.path.toString()
                    Glide.with(this).load(imageLocation.absoluteFile).into(imageViewAdd)
                }
                REQUEST_CODE_GALLERY -> {
                    val selectedImage = data?.data
                    picturePath = selectedImage.toString()
                    Glide.with(this).load(picturePath).into(imageViewAdd)
                }
            }
        }
    }

}
interface OnOptionClickListener {
    fun onCameraClick()
    fun onGalleryClick()
}