package com.homemadeproductsapp.AllStores.CategoriesFilter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.homemadeproductsapp.AllStores.CategoriesFilter.listeners.SubCategoriesListener
import com.homemadeproductsapp.R

class SubCategoriesAdapter(private val list: List<String>,private  val searchResultsClickListener: SubCategoriesListener): RecyclerView.Adapter<SubCategoriesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var textViewCategoryName : TextView =itemView.findViewById(R.id.textViewCategoryName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sub_categories, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewCategoryName.text=list[position]
        holder.textViewCategoryName.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                searchResultsClickListener.displayResults(list[position])
            }

        })
    }

    override fun getItemCount(): Int {
   return list.size
    }


}