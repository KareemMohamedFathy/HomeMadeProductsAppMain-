package com.homemadeproductsapp.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Store
import com.homemadeproductsapp.DB.User
import com.homemadeproductsapp.FileSelectorFragment
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UpdateStoreFragment : Fragment() {
        private lateinit var editTextName: EditText
        private lateinit var editTextEmail: EditText
        private lateinit var editTextDescription: EditText
        private lateinit var imageViewStorePhoto: ImageView
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
        private lateinit var onMoveClick:onMoveClick1

        private lateinit var auth: FirebaseAuth
        private lateinit var dbReference: DatabaseReference
        private lateinit var firebaseDatabase: FirebaseDatabase
    override fun onAttach(context: Context) {
        super.onAttach(context)
    onMoveClick=context as onMoveClick1
    }


        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view1= inflater.inflate(R.layout.fragment_update_store, container, false)
            return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    bindViews(view1)
        getStoreData()
        setupClickListeners()

    }
    private fun setupClickListeners() {
        imageViewStorePhoto.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (checkAndRequestPermissions()) {
                    openPicker()
                }
            }


        })
        buttonUpdateInfo.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                auth = FirebaseAuth.getInstance()
                val id = auth.currentUser!!.uid
                val name = editTextName.text.toString()
                val description = editTextDescription.text.toString()
                val category = StoreSession.readString(PrefConstant.MAINCATEGORY).toString()

                val storeid = StoreSession.readString(PrefConstant.STOREID)
                val path = StoreSession.readString(PrefConstant.STORELOGO)


                val store= Store(storeid.toString(),name, path.toString(),description,category,id)
                Log.d("store",store.toString())
                val lol = FirebaseDatabase.getInstance().getReference("Store")
                        .child(storeid!!).setValue(store)
                onMoveClick.onBack()
            }


        })
        imageViewBack.setOnClickListener(View.OnClickListener {
            onMoveClick.onBack()
        })

    }

    private fun bindViews(view1: View) {
        editTextName = view1.findViewById(R.id.editTextName)
        editTextDescription = view1.findViewById(R.id.editTextDescription)
        imageViewStorePhoto = view1.findViewById(R.id.storePic)
        buttonUpdateInfo = view1.findViewById(R.id.buttonUpdateProfile)
        imageViewBack=view1.findViewById(R.id.back)
    }

    override fun onResume() {
        super.onResume()
        getStoreData()
    }

    private fun getStoreData() {
        editTextName.setText(StoreSession.readString(PrefConstant.STORENAME))
        editTextDescription.setText(StoreSession.readString(PrefConstant.STOREDESCRIPTION))
        var path = StoreSession.readString(PrefConstant.STORELOGO).toString()

        if (path.isNotEmpty()) {
            Glide.with(requireContext()).load(path).into(imageViewStorePhoto)
        }


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
interface onMoveClick1 {
    fun onBack()


}

