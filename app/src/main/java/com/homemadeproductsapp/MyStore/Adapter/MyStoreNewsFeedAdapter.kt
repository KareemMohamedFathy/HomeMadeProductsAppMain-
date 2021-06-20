import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.MyStore.Listeners.NewsFeedClickListener
import com.homemadeproductsapp.R

class MyStoreNewsFeedAdapter(private val timeLinePhotos: ArrayList<Feed>, private val newsFeedClickListener: NewsFeedClickListener) : RecyclerView.Adapter<MyStoreNewsFeedAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStoreNewsFeedAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.products_adapter_layout, parent, false)
        return MyStoreNewsFeedAdapter.ViewHolder(view)




    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            Glide.with(holder.itemView).load(timeLinePhotos[position].imagePathProduct).into(holder.imageView1)
        holder.imageView1.setOnClickListener(object :View.OnClickListener {
            override fun onClick(v: View?) {
                newsFeedClickListener.onClick(timeLinePhotos[position])
            }
        })




    }





    override fun getItemCount(): Int {
        return timeLinePhotos.size


    }
    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var imageView1: ImageView =itemView.findViewById(R.id.imageView1)
       // var imageView2: ImageView =itemView.findViewById(R.id.imageView2)



    }


}
