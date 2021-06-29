package com.homemadeproductsapp.profile

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.homemadeproductsapp.AllStores.AllStoresActivity
import com.homemadeproductsapp.BuildConfig
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.Home.HomeActivity
import com.homemadeproductsapp.LoginActivity
import com.homemadeproductsapp.MyStore.MyStoreActivity
import com.homemadeproductsapp.MyStore.OnOptionClickListener
import com.homemadeproductsapp.PastOrders.OrdersActivity
import com.homemadeproductsapp.R
import com.homemadeproductsapp.profile.adapter.ProfileAdapter
import com.mindorks.notesapp.data.local.pref.PrefConstant
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity(),multipleOptionsClick,OnOptionClickListener,onMoveClick,onMoveClick1 {
    private lateinit var viewPager: ViewPager2
    private var picturePath = ""
    private lateinit var imageLocation: File
    private lateinit var auth: FirebaseAuth



    companion object {
        private const val MY_PERMISSION_CODE = 124
        private const val REQUEST_CODE_CAMERA = 1
        private const val REQUEST_CODE_GALLERY = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        handleBottomNavigationView()
        bindViews()
        auth = FirebaseAuth.getInstance()
         }

    private fun bindViews() {
        viewPager=findViewById(R.id.viewPager2)
        val pageAdapter= ProfileAdapter(this)
        viewPager.adapter=pageAdapter
        viewPager.setUserInputEnabled(false);
    }

    private fun handleBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setSelectedItemId(R.id.page_5);

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_2 -> {
                    val intent = Intent(this@ProfileActivity, MyStoreActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }
                R.id.page_4 -> {
                    val intent = Intent(this@ProfileActivity, AllStoresActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }
                R.id.page_1 -> {
                    val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }
                R.id.page_3 -> {
                    val intent = Intent(this@ProfileActivity, OrdersActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }

                else -> false
            }
        }
        }

    override fun onClickProfile() {
        viewPager.currentItem=1
    }

    override fun onClickStore() {
        viewPager.currentItem=2
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
                val photoURI = FileProvider.getUriForFile(this@ProfileActivity, BuildConfig.APPLICATION_ID + ".provider", photoFile)
                imageLocation = photoFile
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
            }
        }
    }

    override fun onGalleryClick() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    picturePath = imageLocation.path.toString()
                    StoreSession.init(this)
                    StoreSession.write(PrefConstant.USERPROFILEPHOTO,picturePath)

                }
                REQUEST_CODE_GALLERY -> {
                    val selectedImage = data?.data
                    picturePath = selectedImage.toString()
                    val contentResolver = applicationContext.contentResolver
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    picturePath.toUri()?.let { contentResolver.takePersistableUriPermission(it, takeFlags) }

                    Log.d("cur",viewPager.currentItem.toString())
                    if(viewPager.currentItem==1) {
                        StoreSession.init(this)
                        StoreSession.write(PrefConstant.USERPROFILEPHOTO, picturePath)
                    }
                    else if(viewPager.currentItem==2){
                        StoreSession.init(this)
                        StoreSession.write(PrefConstant.STORELOGO, picturePath)
                    }


                }
            }
        }
    }
    override fun onClickLogOut() {
        auth.signOut()
        val intent=Intent(this@ProfileActivity,LoginActivity::class.java)
        startActivity(intent)
    }


    override fun onBack() {
    viewPager.currentItem=0
    }
}