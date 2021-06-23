import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.homemadeproductsapp.AllStores.StoreTimeLineFragment
import com.homemadeproductsapp.DB.Product
import java.io.Serializable

class AllStoresFragmentAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm),Serializable
{
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    fun addFragment(fragment: DialogFragment, title: String): Fragment {


        mFragmentList.add(fragment)
        mFragmentTitleList.add("" + title + "")
        val args = Bundle()
        args.putString("Category", title)
        fragment.setArguments(args);

        return fragment
    }
    fun addFragment(fragment: Fragment, title: String): Fragment {


        mFragmentList.add(fragment)
        mFragmentTitleList.add("" + title + "")
        val args = Bundle()
        args.putString("Category", title)
        fragment.setArguments(args);

        return fragment
    }
    fun addTimeLineFragment(fragment: Fragment,title: String, storename: String,storeimagePath:String): Fragment {


        mFragmentList.add(fragment)
        mFragmentTitleList.add("" + title + "")
        val args = Bundle()
        args.putString("StoreName", storename)
        args.putString("storeImagePath", storeimagePath)

        fragment.setArguments(args);

        return fragment
    }

    fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }
    fun getPageFragment(position: Int): Fragment? {
        return mFragmentList[position]
    }


    override fun getItemCount(): Int {
        return  mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            else->mFragmentList.get(position)
        }
    }
}