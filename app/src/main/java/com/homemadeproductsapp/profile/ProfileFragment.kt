package com.homemadeproductsapp.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.DB.Store
import com.homemadeproductsapp.DB.User
import com.homemadeproductsapp.R
import com.mindorks.notesapp.data.local.pref.PrefConstant
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*


class ProfileFragment : Fragment() {
    private lateinit var textViewName:TextView
    private lateinit var textViewEmail:TextView
    private lateinit var textViewStoreName:TextView
    private lateinit var textViewCategory:TextView
    private lateinit var textViewPhoneNo:TextView
    private lateinit var buttonUpdateProfile:Button
    private lateinit var buttonUpdateStore:Button
    private lateinit var buttonLogOut:Button
    private lateinit var auth: FirebaseAuth
    private lateinit var datacommunication: datacommunication

    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var view1:View
    private lateinit var storeidExists:String
    private lateinit var optionsClick: multipleOptionsClick



    private lateinit var name:String
    private lateinit var email:String
    private lateinit var storeName:String
    private lateinit var category:String
    private lateinit var phoneNo:String
    private lateinit var description:String
    private lateinit var storeLogoImagePath:String

    private lateinit var profileImagePath:String
    private lateinit var profileImageView: ImageView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        optionsClick=context as multipleOptionsClick
        datacommunication=context as datacommunication
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
         view1= inflater.inflate(R.layout.fragment_profile, container, false)
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        getUserData()

    }


    override fun onResume() {
        getUserData()
        super.onResume()
    }

    private fun getUserData() {

        auth = FirebaseAuth.getInstance()


        FirebaseDatabase.getInstance().getReference("User").child(auth.currentUser!!.uid).get().addOnSuccessListener {
            name=it.child("name").value.toString()
            email=it.child("email").value.toString()
            phoneNo=it.child("mobileno").value.toString()
            profileImagePath=it.child("personalPhotoPath").value.toString()
            textViewName.text=name
            textViewEmail.text=email
            textViewPhoneNo.text=phoneNo
            storeidExists=it.child("store_id").value.toString()
            datacommunication.user = User(auth.currentUser!!.uid,name,phoneNo,profileImagePath,email,storeidExists)
            if(!profileImagePath.isEmpty()){
                Glide.with(requireContext()).load(profileImagePath).skipMemoryCache(false).into(profileImageView)
            }
            getStoreData()
         //   setupSharedPreference()
            setUpCLickListeners()

        }
    }
    private fun setupSharedPreference() {
        StoreSession.init(requireContext())
    }
    private fun setUpCLickListeners() {
    buttonUpdateProfile.setOnClickListener(object:View.OnClickListener{
        override fun onClick(v: View?) {
            optionsClick.onClickProfile()
        }

    })

        buttonUpdateStore.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                Log.d("Bagga",buttonUpdateStore.isClickable.toString())
                optionsClick.onClickStore()

            }

        })


        buttonLogOut.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                optionsClick.onClickLogOut()
            }

        })



    }

    private fun getStoreData() {
        firebaseDatabase= FirebaseDatabase.getInstance()



if(storeidExists.isEmpty()) {
    Log.d("kuso","kusooooooo")
    textViewStoreName.text = "N/A"
    description="N/A"
    storeLogoImagePath="N/A"

    textViewStoreCategory.text = "N/A"
buttonUpdateStore.isEnabled=false
buttonUpdateStore.alpha=0.4f
}
        else{
    buttonUpdateStore.isEnabled=true
    buttonUpdateStore.alpha=1.0f
}


    val query = firebaseDatabase.getReference("Store").orderByChild("store_id").equalTo(storeidExists)
    query.addValueEventListener(object : ValueEventListener {

        override fun onDataChange(it: DataSnapshot) {
            if (it.exists()) {
                for (dsp in it.children) {

                    storeName = dsp.child("store_name").value.toString()
                    category = dsp.child("mainCategoryName").value.toString()
                    textViewStoreName.text = storeName
                    storeLogoImagePath = dsp.child("store_logo").value.toString()
                    description = dsp.child("store_description").value.toString()
                    textViewCategory.text = dsp.child("mainCategoryName").value.toString()
                    datacommunication.store = Store(storeidExists,storeName,storeLogoImagePath,description,category,auth.currentUser!!.uid)
                }
            } else {
                textViewStoreName.text = "N/A"
                textViewStoreCategory.text = "N/A"
                description="N/A"
                storeLogoImagePath="N/A"
                buttonUpdateStore.isEnabled=false

            }

//            saveData()
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    })
    }





    private fun bindViews(view: View) {
        textViewCategory=view.findViewById(R.id.textViewStoreCategory)
        textViewPhoneNo=view.findViewById(R.id.textViewPhonoNo)
        textViewEmail=view.findViewById(R.id.textViewEmail)
        textViewName=view.findViewById(R.id.textViewName)
        textViewStoreName=view.findViewById(R.id.textViewStoreName)
        buttonUpdateProfile=view.findViewById(R.id.buttonUpdateProfile)
        buttonUpdateStore=view.findViewById(R.id.buttonUpdateStore)
        buttonLogOut=view.findViewById(R.id.LogOut)
        profileImageView=view.findViewById(R.id.logoPic)
    }


}
interface multipleOptionsClick {
    fun onClickProfile()
    fun onClickStore()
    fun onClickLogOut()

}