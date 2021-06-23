package com.homemadeproductsapp.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.homemadeproductsapp.BuildConfig
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.User
import com.homemadeproductsapp.FileSelectorFragment
import com.homemadeproductsapp.MyStore.OnOptionClickListener
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class UpdateProfileFragment : Fragment() {
    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPhoneNo: EditText
    private lateinit var imageViewPersonalPhoto: ImageView
    private lateinit var imageViewBack: ImageView

    private lateinit var buttonUpdateInfo: Button
    private lateinit var view1: View
    private var picturePath = ""
    private lateinit var imageLocation: File



    companion object {
        private const val MY_PERMISSION_CODE = 124
        private const val REQUEST_CODE_CAMERA = 1
        private const val REQUEST_CODE_GALLERY = 2
    }
    private lateinit var onMoveClick:onMoveClick

    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view1 = inflater.inflate(R.layout.fragment_update_profile, container, false)
        return view1
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onMoveClick=context as onMoveClick
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSharedPreference()
        requireActivity()
                .onBackPressedDispatcher
                .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        // Do custom work here

                        onMoveClick.onBack()

                    }
                }
                )

        bindViews(view1)
        getUserData()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        imageViewPersonalPhoto.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (checkAndRequestPermissions()) {
                    val name = editTextName.text.toString()
                    val phonoNo = editTextPhoneNo.text.toString()
                    StoreSession.write(PrefConstant.USERNAME,name)
                    StoreSession.write(PrefConstant.USERPHONONO,phonoNo)

                    openPicker()
               }
            }


        })
        buttonUpdateInfo.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                auth = FirebaseAuth.getInstance()
                //val id = auth.currentUser!!.uid
                val name = editTextName.text.toString()
                //val email = StoreSession.readString(PrefConstant.USEREMAIL).toString()
                val phonoNo = editTextPhoneNo.text.toString()
               // val storeid = StoreSession.readString(PrefConstant.STOREID)
               val path = StoreSession.readString(PrefConstant.USERPROFILEPHOTO)

             //   val curUser: User = User(id, name, phonoNo, path, email, storeid)
                val lol = FirebaseDatabase.getInstance().getReference("User")
                    .child(auth.currentUser!!.uid).child("name").setValue(name)
                FirebaseDatabase.getInstance().getReference("User")


                FirebaseDatabase.getInstance().getReference("User")
                    .child(auth.currentUser!!.uid).child("personalPhotoPath").setValue(path)
                onMoveClick.onBack()
            }


        })
        imageViewBack.setOnClickListener(View.OnClickListener {
            onMoveClick.onBack()
        })

    }

    private fun bindViews(view1: View) {
        editTextName = view1.findViewById(R.id.editTextName)
        editTextPhoneNo = view1.findViewById(R.id.editTextMobileNumber)
        imageViewPersonalPhoto = view1.findViewById(R.id.personalPic)
        buttonUpdateInfo = view1.findViewById(R.id.buttonUpdateProfile)
        imageViewBack=view1.findViewById(R.id.back)
    }

    override fun onResume() {
        super.onResume()
        getUserData()
    }

    private fun getUserData() {
        editTextName.setText(StoreSession.readString(PrefConstant.USERNAME))
        editTextPhoneNo.setText(StoreSession.readString(PrefConstant.USERPHONONO))
        var path = StoreSession.readString(PrefConstant.USERPROFILEPHOTO).toString()

        if (path.isNotEmpty()) {
            Glide.with(requireContext()).load(path).into(imageViewPersonalPhoto)
        }


    }

    private fun setupSharedPreference() {
        StoreSession.init(requireContext())
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName = "JPEG_" + timeStamp + "_"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(mFileName, ".jpg", storageDir)
    }


    private fun checkAndRequestPermissions(): Boolean {
        val permissionCAMERA =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val listPermissionsNeeded = ArrayList<String>()
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(), listPermissionsNeeded.toTypedArray<String>(),
                MY_PERMISSION_CODE
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
        dialog.show(requireActivity().supportFragmentManager, FileSelectorFragment.TAG)
    }

}
interface onMoveClick {
    fun onBack()


}
