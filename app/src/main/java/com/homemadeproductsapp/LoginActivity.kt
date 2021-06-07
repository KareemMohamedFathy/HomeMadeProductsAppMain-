package com.homemadeproductsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.MyStore.MyStoreActivity
import com.mindorks.notesapp.data.local.pref.PrefConstant

class LoginActivity : AppCompatActivity() {
    private lateinit var textViewSignUp: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var editTextEmail:EditText
    private lateinit var editTextPassword:EditText
    private lateinit var buttonSignIn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindViews()
        auth = FirebaseAuth.getInstance()
setupSharedPreference()
        checkisLogged()


        setupClickListeners()

    }
    private fun setupSharedPreference() {
        StoreSession.init(this)
    }
    private fun checkisLogged() {
       val status= StoreSession.read(PrefConstant.LOGGED)
        if(status==true){
            Toast.makeText(this@LoginActivity, "Successfully Logged In", Toast.LENGTH_LONG).show()
            val intent= Intent(this@LoginActivity, MyStoreActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
    private fun saveSession() {
        StoreSession.write(PrefConstant.LOGGED, true)
    }

    private fun bindViews() {
        buttonSignIn=findViewById(R.id.buttonLogin)
        editTextEmail=findViewById(R.id.editTextEmail)
        editTextPassword=findViewById(R.id.editTextPassword)
        textViewSignUp=findViewById(R.id.textViewsignUp)
    }

    private fun setupClickListeners() {
        var clickAction=object: View.OnClickListener{
            override fun onClick(v: View?) {
                val email=editTextEmail.text.toString()
                val password=editTextPassword.text.toString()
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@LoginActivity, OnCompleteListener { task ->
                    if(task.isSuccessful) {
saveSession()
                        Toast.makeText(this@LoginActivity, "Successfully Logged In", Toast.LENGTH_LONG).show()
                        val intent= Intent(this@LoginActivity, MyStoreActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else {

                        Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_LONG).show()
                    }
                })

            }
        }
        buttonSignIn.setOnClickListener(clickAction)




         val clickAction1=object: View.OnClickListener{
            override fun onClick(v: View?) {
                        val intent= Intent(this@LoginActivity, RegisterActivity::class.java)
                        startActivity(intent)
                        finish()
                    }


        }
        textViewSignUp.setOnClickListener(clickAction1)


    }
}