import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.homemadeproductsapp.DB.Product
import com.homemadeproductsapp.R

class MyStoreProductsAdapter(private val list: List<String>,private val list1: List<String>) : RecyclerView.Adapter<MyStoreProductsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStoreProductsAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.products_adapter_layout, parent, false)
        return MyStoreProductsAdapter.ViewHolder(view)




    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewName1.setText(list[position])
        if(position<list1.size)
        holder.textViewName2.setText(list1[position])

    }





    override fun getItemCount(): Int {
        return list.size
    }
    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var textViewName1 : TextView =itemView.findViewById(R.id.textViewName1)
        var textViewName2 :TextView=itemView.findViewById(R.id.textViewName2)



    }


}
