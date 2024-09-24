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
import cs.skku.edu.pa2.data.DataUser

class RestaurantAdapter(
    val context: Context,
    private val items: ArrayList<DataUser.Reservations>,
    val restaurant: MutableList<DataRestaurant>
) :
    BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return items[position].reservation_id!!.toLong()
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
        textView4.text = "People: " + items[position].number_of_people
        textView5.text = items[position].time + " " + items.get(position).date
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