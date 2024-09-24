package cs.skku.edu.pa2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import cs.skku.edu.pa2.R
import cs.skku.edu.pa2.data.DataRestaurant

class RestaurantReservationAdapter(
    val context: Context,
    val restaurant: ArrayList<DataRestaurant>
) :
    BaseAdapter() {
    override fun getCount(): Int {
        return restaurant.size
    }

    override fun getItem(position: Int): Any {
        return restaurant.get(position)
    }

    override fun getItemId(position: Int): Long {
        return restaurant.get(position).id!!.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater =
            LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.restaurantlist, null)
        var imgView = view.findViewById<ImageView>(R.id.imageView2)
        var textView = view.findViewById<TextView>(R.id.textView)
        var textView4 = view.findViewById<TextView>(R.id.textView4)
        var textView5 = view.findViewById<TextView>(R.id.textView5)
        textView.text = restaurant[position].restaurant.toString()
        textView4.text = restaurant[position].location + "/" + restaurant[position].rating.toString()
        textView5.text = restaurant[position].openingHours.open + "~" + restaurant[position].openingHours.close
        val imageName = restaurant[position].image
        val imageResId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        if (imageResId != 0) {
            imgView.setImageResource(imageResId)
        } else {
            imgView.setImageResource(R.drawable.ic_launcher_background)
        }
        return view
    }
}