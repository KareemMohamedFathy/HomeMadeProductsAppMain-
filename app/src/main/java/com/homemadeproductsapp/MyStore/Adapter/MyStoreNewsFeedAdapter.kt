import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.homemadeproductsapp.DB.Feed
import com.homemadeproductsapp.MyStore.Listeners.NewsFeedClickListener
import com.homemadeproductsapp.MyStore.dataCommunication
import com.homemadeproductsapp.R

class MyStoreNewsFeedAdapter(private val timeLinePhotos: ArrayList<Feed>, private val newsFeedClickListener: NewsFeedClickListener,private val switchMode: String) : RecyclerView.Adapter<MyStoreNewsFeedAdapter.ViewHolder>() {
private lateinit var context:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStoreNewsFeedAdapter.ViewHolder {
        context=parent.context
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


        if(switchMode=="ON"){
            holder.removeItem.visibility=View.VISIBLE
            holder.removeItem.setOnClickListener(object :View.OnClickListener{
                override fun onClick(v: View?) {

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Remove Post from timeline")
                    builder.setMessage("Are You sure you want to remove this Post from your store timeline?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        Toast.makeText(context, "Post Removed", Toast.LENGTH_SHORT).show()
                        FirebaseDatabase.getInstance().reference.child("Feed").child(timeLinePhotos[position].id.toString()).removeValue()
                        timeLinePhotos.removeAt(position)
                        notifyDataSetChanged()
                    }
                    builder.setNegativeButton("No") { dialog, which ->
                    }


                    builder.show()

                }

            })

        }
        else{
            holder.removeItem.visibility=View.GONE

        }


    }





    override fun getItemCount(): Int {
        return timeLinePhotos.size


    }
    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var imageView1: ImageView =itemView.findViewById(R.id.imageView1)
        var removeItem: ImageView =itemView.findViewById(R.id.removeItem)



    }


}
