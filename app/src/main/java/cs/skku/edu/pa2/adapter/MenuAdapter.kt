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

class MenuAdapter(val context: Context, private val restaurant: ArrayList<DataRestaurant.Menus>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return restaurant.size
    }

    override fun getItem(position: Int): Any {
        return restaurant[position]
    }

    override fun getItemId(position: Int): Long {
        return restaurant[position].id!!.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater =
            LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.restaurantlist, null)
        var imgView = view.findViewById<ImageView>(R.id.imageView2)
        var textView = view.findViewById<TextView>(R.id.textView)
        var textView4 = view.findViewById<TextView>(R.id.textView4)
        textView.text = restaurant[position].name
        textView4.text = "$" + restaurant[position].price.toString()
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