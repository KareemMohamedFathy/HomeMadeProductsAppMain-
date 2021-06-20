package com.homemadeproductsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import android.widget.Toast
import android.text.TextUtils
import android.util.Log

import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.homemadeproductsapp.DB.Local.StoreSession

import com.homemadeproductsapp.DB.User
import com.homemadeproductsapp.MyStore.MyStoreActivity
import com.mindorks.notesapp.data.local.pref.PrefConstant

class RegisterActivity : AppCompatActivity() {
    lateinit var editTextName: EditText
    lateinit var editTextPassword: EditText
    lateinit var editTextPasswordConfirm: EditText
    lateinit var editTextEmail: EditText
    lateinit var editTextMobile: EditText
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    lateinit var buttonSignUp:Button
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        setupSharedPreference()
        bindViews()

        setupClickListeners()

    }
    private fun setupSharedPreference() {
        StoreSession.init(this)
    }

    private fun saveSession() {
        StoreSession.write(PrefConstant.LOGGED, true)
    }

    private fun setupClickListeners() {
        val clickAction = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val name = editTextName.text.toString()
                val password = editTextPassword.text.toString()
                val passwordConfirm = editTextPasswordConfirm.text.toString()
                val email = editTextEmail.text.toString()
               val mobileNo=editTextMobile.text.toString()
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)||TextUtils.isEmpty(passwordConfirm)||TextUtils.isEmpty(name)||TextUtils.isEmpty(mobileNo)) {
                    Toast.makeText(this@RegisterActivity, "Please fill all the fields", Toast.LENGTH_LONG).show()
                }
                else if(password!=passwordConfirm){
                    Toast.makeText(this@RegisterActivity, "Passwords don't match", Toast.LENGTH_LONG).show()

                }
                else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this@RegisterActivity, OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Successfully Registered",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                saveSession()
                                val intent = Intent(this@RegisterActivity, MyStoreActivity::class.java)
                                handleData(name,email,mobileNo)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    task.result.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })

                }

            }
        }
        buttonSignUp.setOnClickListener(clickAction)

    }

    private fun handleData(name: String, email: String, mobileNo: String) {
        firebaseDatabase= FirebaseDatabase.getInstance()


            dbReference=firebaseDatabase.getReference("User")
            val id=auth.currentUser!!.uid
            Log.d("RegisterActivity", id)

            val user=User(id,name,mobileNo,"",email)
           dbReference.child(id).setValue(user)




    }

    private fun bindViews() {
        editTextEmail=findViewById(R.id.editTextEmail)
        editTextName=findViewById(R.id.editTextName)
        editTextPassword=findViewById(R.id.editTextPassword)
        editTextPasswordConfirm=findViewById(R.id.editTextPasswordConfirm)
        buttonSignUp=findViewById(R.id.buttonSignUp)
        editTextMobile=findViewById(R.id.editTextMobileNumber)
    }
}