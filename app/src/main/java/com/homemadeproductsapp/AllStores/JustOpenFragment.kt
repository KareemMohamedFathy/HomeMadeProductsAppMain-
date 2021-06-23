package com.homemadeproductsapp.AllStores

import android.app.AlertDialog
import android.content.Context
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.homemadeproductsapp.DB.Cart
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.R


class JustOpenFragment : DialogFragment() {
    private lateinit var textViewProductName:TextView
    private lateinit var textViewProductDescription:TextView
    private lateinit var textViewProductPrice:TextView
    private lateinit var textViewProductAmount:TextView
    private lateinit var textViewButtonPrice:TextView
    private lateinit var backToMe: BackToMe

    private lateinit var imageViewAdd:ImageView
    private lateinit var imageViewProductPic:ImageView
    private lateinit var imageViewSub:ImageView
    private lateinit var auth: FirebaseAuth
    private  var position=0


    private lateinit var relativeLayoutaddToCart:FrameLayout
    private lateinit var view1:View
    private  var amount=1
    private   var copies=1
    private   var price=0.00

    private lateinit var product:Product
    private lateinit var dataCommunication: DataCommunication
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var curprice=0.00;
   private lateinit var imageViewProduct:ImageView
   private lateinit var imageViewImageSwitcher: ImageSwitcher
   private lateinit var imageViewNext:ImageView
   private lateinit var cancelImageView:ImageView
  private lateinit var imageViewBack:ImageView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataCommunication=context as DataCommunication
        backToMe=context as BackToMe
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
         view1=inflater.inflate(R.layout.fragment_just_open, container, false)
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        imageViewImageSwitcher.setFactory(ViewSwitcher.ViewFactory { // TODO Auto-generated method stub

            val imageView = ImageView(requireContext())
            imageView.scaleType = ImageView.ScaleType.FIT_XY

            val params: ViewGroup.LayoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            imageView.layoutParams = params
            imageView


        })

        auth = FirebaseAuth.getInstance()
        getData()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        imageViewNext.setOnClickListener {
            if (position < product.uriPaths!!.size-1){
                position++
                imageViewImageSwitcher.setImageURI(product.uriPaths!!.get(position).toUri())
            }
            else{
                position=0;
                //no more images
                imageViewImageSwitcher.setImageURI(product.uriPaths!!.get(position).toUri())
            }
        }

        //switch to previous image clicking this button
        imageViewBack.setOnClickListener {
            if (position > 0){
                position--
                imageViewImageSwitcher.setImageURI(product.uriPaths!!.get(position).toUri())

            }
            else{
                position=product.uriPaths !!.size-1
                imageViewImageSwitcher.setImageURI(product.uriPaths!!.get(position).toUri())

            }

        }

        imageViewAdd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (amount < copies) {
                    amount++
                    imageViewSub.alpha = 1.0F
                    textViewProductAmount.setText(" $amount ")

                    curprice = price

                    curprice *= amount
                    textViewButtonPrice.setText("$curprice EGP")

                    textViewProductPrice.text = ("$curprice EGP")
                }
            }

        })
        imageViewSub.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (amount > 1) {

                    amount--

                    textViewProductAmount.text = (" $amount ")

                    var curprice: Double = price
                    curprice *= amount
                    textViewButtonPrice.text = ("$curprice EGP")
                    textViewProductPrice.text = ("$curprice EGP")
                }
                if (amount == 1) {
                    imageViewSub.alpha = 0.4F
                }
            }

        })


        relativeLayoutaddToCart.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var amountMap: HashMap<String, Int> = HashMap()
                var priceMap: HashMap<String, Double> = HashMap()
                var picMap: HashMap<String, String> = HashMap()

                var store_id: String = ""
                var cartPrice: Double =0.00

                firebaseDatabase = FirebaseDatabase.getInstance()
                dbReference = FirebaseDatabase.getInstance().reference
                dbReference = dbReference.child("User").child(auth.currentUser!!.uid).child("Cart")
                dbReference.addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {


                                if (snapshot.exists()) {

                                    store_id = snapshot.child("store_id").value.toString()

                                    cartPrice= snapshot.child("totalPrice").value.toString().toDouble()

                                    amountMap.putAll(snapshot.child("itemsIdAmountList").value as HashMap<String, Int>)

                                    priceMap.putAll(snapshot.child("itemsIdPriceList").value as HashMap<String, Double>)
                                    picMap.putAll(snapshot.child("itemsIdPicList").value as HashMap<String, String>)
                                }
                                var value = 0
                                if (amountMap[product.id.toString()] != null) {
                                    value = amountMap[product.id.toString()]!!
                                }


                                amountMap.put(product.id.toString(), amount + value)
                                var value1 = 0.00
                                if (priceMap[product.id.toString()] != null) {
                                    value1 = priceMap[product.id]!!
                                }
                                priceMap.put(product.id.toString(), curprice + value1)
                                product.imagePathProduct = product.uriPaths!!.get(0)
                                picMap.put(product.id.toString(), product.imagePathProduct.toString())


                                val cart: Cart = Cart(product.store_id, amountMap, priceMap, picMap, (price*amount)+cartPrice)

                                if (store_id == product.store_id.toString() || store_id.isEmpty()) {
                                    dbReference.setValue(cart)
                                    dataCommunication.cart=cart
                                dismiss()
                                    backToMe.getCartData()

                                } else {

                                    val builder = AlertDialog.Builder(context)
                                    builder.setTitle("Only one Cart")
                                    builder.setMessage("Empty the cart from another store?")

                                    builder.setPositiveButton("Yes") { dialog, which ->
                                        amountMap.clear()
                                        picMap.clear()
                                        priceMap.clear()
                                        amountMap.put(product.id.toString(), amount + value)
                                        picMap.put(product.id.toString(), product.imagePathProduct.toString())
                                        priceMap.put(product.id.toString(), curprice + value1)
                                        dataCommunication.cart=cart
                                        dbReference.setValue(cart)

                                        Toast.makeText(context,
                                                "Cart cleared and new Cart created", Toast.LENGTH_SHORT).show()
                                        dismiss()
                                        backToMe.getCartData()

                                    }

                                    builder.setNegativeButton("No") { dialog, which ->
                                        Toast.makeText(context,
                                                "Old Cart is still there", Toast.LENGTH_SHORT).show()
                                    }
                                    builder.show()
                                }

                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        }
                )
            }

        })
        cancelImageView.setOnClickListener{dismiss()}
    }

    private fun getData() {
    product= dataCommunication.product!!
    textViewProductName.text=product.name
    textViewProductPrice.text=("${product.price} EGP")
        textViewButtonPrice.text=("${product.price} EGP")

        textViewProductDescription.text=product.description.toString()
        copies= product.copies!!.toInt()
        price=product.price!!.toDouble()
        curprice=price
        imageViewImageSwitcher.setImageURI(product.uriPaths!!.get(position).toUri())

    }

    private fun bindViews() {

        textViewProductName=view1.findViewById(R.id.textViewProductName)
        textViewProductDescription=view1.findViewById(R.id.textViewProductDescription)
        textViewProductPrice=view1.findViewById(R.id.textViewProductPrice)
        textViewProductAmount=view1.findViewById(R.id.textViewProductCount)
        imageViewAdd=view1.findViewById(R.id.imageViewIncreaseCount)
        imageViewSub=view1.findViewById(R.id.imageViewDecreaseCount)
        relativeLayoutaddToCart=view1.findViewById(R.id.relativeLayoutAddToCart)
        textViewButtonPrice=view1.findViewById(R.id.textViewButtonPrice)
         imageViewImageSwitcher =view1.findViewById(R.id.imageViewImageSwitcher)
         imageViewProduct =view1.findViewById(R.id.imageViewProduct)
         imageViewNext =view1.findViewById(R.id.ImageViewNext)
         imageViewBack =view1.findViewById(R.id.ImageViewBack)
        cancelImageView=view1.findViewById(R.id.cancelImageView)



    }



}
interface BackToMe{
    fun getCartData()
}
