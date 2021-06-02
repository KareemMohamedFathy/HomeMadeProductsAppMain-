package com.homemadeproductsapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.homemadeproductsapp.MyStore.OnOptionClickListener

class FileSelectorFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "FileSelectorFragment"
        fun newInstance() = FileSelectorFragment()
    }

    private lateinit var textViewCamera: TextView
    private lateinit var textViewGallery: TextView
    private lateinit var onOptionClickListener: OnOptionClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onOptionClickListener = context as OnOptionClickListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_selector, container, false)
        bindViews(view)
        setupClickListener()
        return view
    }

    private fun setupClickListener() {
        textViewCamera.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                onOptionClickListener.onCameraClick()
                dismiss()
            }
        })
        textViewGallery.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                onOptionClickListener.onGalleryClick()
                dismiss()
            }
        })
    }

    private fun bindViews(view: View) {
        textViewCamera = view.findViewById(R.id.textViewCamera)
        textViewGallery = view.findViewById(R.id.textViewGallery)
    }
}
