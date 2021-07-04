package com.homemadeproductsapp.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.homemadeproductsapp.BuildConfig
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
    private lateinit var group:Group

    private lateinit var buttonUpdateInfo: Button
    private lateinit var view1: View
    private var picturePath = ""
    private lateinit var imageLocation: File
    private lateinit var datacommunication: datacommunication



    companion object {
        private const val MY_PERMISSION_CODE = 124
        private const val REQUEST_CODE_CAMERA = 1
        private const val REQUEST_CODE_GALLERY = 2
    }
    private lateinit var onMoveClick:onMoveClick
    private lateinit var textViewHelper:TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var progressBar:ProgressBar

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
        datacommunication=context as datacommunication
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  setupSharedPreference()
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
                    datacommunication.user.name=name
                    datacommunication.user.mobileno=phonoNo

                    openPicker()
               }
            }


        })
        buttonUpdateInfo.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                auth = FirebaseAuth.getInstance()
                //val id = auth.currentUser!!.uid
                val name = editTextName.text.toString()
                val phonoNo = editTextPhoneNo.text.toString()
                val path=datacommunication.user.personalPhotoPath

                if(name.length>30){
                    Toast.makeText(requireContext(),"Max number of character for store name is 30", Toast.LENGTH_SHORT).show()
                }
                else if(phonoNo.length>11){
                    Toast.makeText(requireContext(),"Max number of character for mobile number is 11", Toast.LENGTH_SHORT).show()
                }

                else {

                    //   val curUser: User = User(id, name, phonoNo, path, email, storeid)
                    val lol = FirebaseDatabase.getInstance().getReference("User")
                            .child(auth.currentUser!!.uid).child("name").setValue(name)
                    FirebaseDatabase.getInstance().getReference("User")

                     FirebaseDatabase.getInstance().getReference("User")
                            .child(auth.currentUser!!.uid).child("mobileno").setValue(phonoNo)
                    FirebaseDatabase.getInstance().getReference("User")


                    FirebaseDatabase.getInstance().getReference("User")
                            .child(auth.currentUser!!.uid).child("personalPhotoPath").setValue(path)
                    onMoveClick.onBack()
                }
                }


        })
        imageViewBack.setOnClickListener(View.OnClickListener {
            onMoveClick.onBack()
        })

    }

    private fun bindViews(view1: View) {
        editTextName = view1.findViewById(R.id.editTextName)
        editTextPhoneNo = view1.findViewById(R.id.editTextMobileNumber)
        group=view1.findViewById(R.id.group1)
        progressBar=view1.findViewById(R.id.progressBar)
        textViewHelper=view1.findViewById(R.id.helperText1)

        imageViewPersonalPhoto = view1.findViewById(R.id.personalPic)
        buttonUpdateInfo = view1.findViewById(R.id.buttonUpdateProfile)
        imageViewBack=view1.findViewById(R.id.back)
    }

    override fun onResume() {
        super.onResume()

            getUserData()


    }

    private fun getUserData() {
        editTextName.setText(datacommunication.user.name)
        editTextPhoneNo.setText(datacommunication.user.mobileno)
        val path = datacommunication.user.personalPhotoPath.toString()

        if (path.isNotEmpty()) {
            if(!("firebasestorage" in path) ) {
                progressBar.visibility=View.VISIBLE
                group.visibility=View.GONE
                textViewHelper.visibility=View.VISIBLE

                uploadImageToFirebase(path.toUri())
            }
        else {
                Glide.with(requireContext()).load(path).into(imageViewPersonalPhoto)
            }
        }


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

    private fun uploadImageToFirebase(fileUri: Uri) {

        if (fileUri != null) {

            val fileName = UUID.randomUUID().toString() +".jpg"
            Log.d("haha","hahahah")

            val database = FirebaseDatabase.getInstance()
            val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")

            refStorage.putFile(fileUri)
                    .addOnSuccessListener(
                            OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                                taskSnapshot.storage.downloadUrl.addOnSuccessListener {

                                    val imageUrl = it
                                    picturePath=imageUrl.toString()
                                    Log.d("haha",imageUrl.toString())
                                    Glide.with(this).load(imageUrl).skipMemoryCache(false).into(imageViewPersonalPhoto)
                                    datacommunication.user.personalPhotoPath=picturePath


                                    progressBar.visibility=View.GONE
                                    group.visibility=View.VISIBLE
                                    textViewHelper.visibility=View.GONE

                                }
                            })


                    ?.addOnFailureListener(OnFailureListener { e ->
                        print(e.message)
                    })
        }
    }



}
interface onMoveClick {
    fun onBack()


}
