package com.homemadeproductsapp.AllStores.Order

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.homemadeproductsapp.AllStores.AllStoresActivity
import com.homemadeproductsapp.R
class OrderDoneFragment : DialogFragment() {
    private lateinit var view1:View
    private lateinit var buttonAllStores:Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         view1= inflater.inflate(R.layout.fragment_order_done, container, false)
        view1.setFocusableInTouchMode(true)
        view1.requestFocus()
        view1.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    startActivity(Intent(requireActivity(),AllStoresActivity::class.java))
                    requireActivity().finish()

                }
            }
            false
        })
                    return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonAllStores=view1.findViewById(R.id.buttonAllStores)
        buttonAllStores.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(requireActivity(),AllStoresActivity::class.java))
                requireActivity().finish()
            }
        })
    }

}