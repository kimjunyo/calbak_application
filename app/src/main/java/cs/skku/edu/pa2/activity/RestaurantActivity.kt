package cs.skku.edu.pa2.activity

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cs.skku.edu.pa2.R
import cs.skku.edu.pa2.adapter.RestaurantReservationAdapter
import cs.skku.edu.pa2.data.DataRestaurant
import cs.skku.edu.pa2.util.ReadAssets.Companion.readJsonFromAssets

class RestaurantActivity : AppCompatActivity() {
    companion object {
        const val ITEM = "0"
        const val ID = "id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        val jsonString = readJsonFromAssets(this, "restaurant_info.json")
        val userType = object : TypeToken<ArrayList<DataRestaurant>>() {}.type
        val dataUserList: ArrayList<DataRestaurant> = Gson().fromJson(jsonString, userType)

        val ids = intent.getStringExtra(LoginActivity.ID)

        val adapter = RestaurantReservationAdapter(this, dataUserList)

        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItem = dataUserList[position].id
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(ITEM, selectedItem.toString())
                putExtra(ID, ids.toString())
            }

            startActivity(intent)
        }
    }
}