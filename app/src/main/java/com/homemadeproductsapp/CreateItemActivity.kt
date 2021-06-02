package com.homemadeproductsapp

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.MyStore.MyStoreActivity
import java.text.SimpleDateFormat
import java.util.*

class CreateItemActivity : AppCompatActivity() {
    private lateinit var editTextPrice:TextView
    private lateinit var editTextName:TextView
    private lateinit var editTextCopies:TextView
    private lateinit var editTextDescription:TextView
    private lateinit var buttonAddItem:Button
    private lateinit var store_id:String
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var  date:String
    private  val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private  var curUser=auth.currentUser!!.uid


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_item)
    bindViews()
        date()
        getIntentData()
     setupClickListeners()
    }

    private fun date() {

        val textView: TextView  = findViewById(R.id.textViewDate)

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            cal.clear(Calendar.HOUR);
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);
            val myFormat = "dd/MM/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
            textView.text = sdf.format(cal.time)
            val strs = cal.time.toString().split(" ").toTypedArray()
            date=sdf.format(cal.time)
        }

        textView.setOnClickListener {
            DatePickerDialog(this@CreateItemActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun getIntentData() {
        val intent = intent
        if (intent.hasExtra("store_Id")) {
            store_id= intent.getStringExtra("store_Id").toString()
            Log.d("CreateItems",store_id)
        }
    }

    private fun setupClickListeners() {
    val clickAction=object : View.OnClickListener{
        override fun onClick(v: View?) {
            val name=editTextName.text.toString()
            val price=editTextPrice.text.toString()
            val copies=editTextCopies.text.toString()
            val description=editTextDescription.text.toString()
            firebaseDatabase= FirebaseDatabase.getInstance()
            dbReference = firebaseDatabase.getReference("Product")


            val productId = dbReference.push().key.toString()
            val  p: Product=Product(name,productId, date,copies.toInt(),"Yes","2 days",price.toDouble() ,description,"",store_id,"")
            dbReference.child(productId).setValue(p)

            intent= Intent(this@CreateItemActivity,MyStoreActivity::class.java)
            startActivity(intent)






            }


    }
        buttonAddItem.setOnClickListener(clickAction)








    }

    private fun bindViews() {
        editTextPrice=findViewById(R.id.editTextPrice)
        editTextCopies=findViewById(R.id.editTextCopies)
        editTextDescription=findViewById(R.id.editTextDescription)
        editTextName=findViewById(R.id.editTextName)
        buttonAddItem=findViewById(R.id.submit_button)
    }
}