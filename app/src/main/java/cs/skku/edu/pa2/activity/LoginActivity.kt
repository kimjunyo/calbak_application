package cs.skku.edu.pa2.activity

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cs.skku.edu.pa2.R
import cs.skku.edu.pa2.adapter.RestaurantAdapter
import cs.skku.edu.pa2.data.DataRestaurant
import cs.skku.edu.pa2.data.DataUser
import cs.skku.edu.pa2.util.ReadAssets.Companion.readJsonFromAssets
import cs.skku.edu.pa2.util.ReadAssets.Companion.readJsonFromInternalStorage
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    companion object {
        const val ID = "id"
        const val ITEM = "item"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginview)

        val reserveBtn = findViewById<Button>(R.id.reserveBtn)
        val idInput2 = findViewById<TextView>(R.id.idInput2)
        val reservationUser = findViewById<ListView>(R.id.reservationUser)

        var id = intent.getStringExtra(MainActivity.ID)
        if (id == null) {
            id = if (intent.getStringExtra(ConfirmCheck.ID) == null) intent.getStringExtra(
                DetailReservationActivity.ID
            )
            else {
                intent.getStringExtra(ConfirmCheck.ID)
            }
        }

        var jsonString: String = try {
            readJsonFromInternalStorage(this, "user_info.json")
        } catch (e: IOException) {
            readJsonFromAssets(this, "user_info.json")
        }
        val userType = object : TypeToken<ArrayList<DataUser>>() {}.type
        val dataUserList: ArrayList<DataUser> = Gson().fromJson(jsonString, userType)
        var reservationList: ArrayList<DataUser.Reservations> = arrayListOf()

        val jsonString2 = readJsonFromAssets(this, "restaurant_info.json")
        val restaurantType = object : TypeToken<ArrayList<DataRestaurant>>() {}.type
        val dataRestaurantList: ArrayList<DataRestaurant> =
            Gson().fromJson(jsonString2, restaurantType)

        var reservationRList = mutableListOf<DataRestaurant>()

        var name = ""
        var age = ""
        var gender = ""

        for (i: Int in 0..<dataUserList.size) {
            if (dataUserList[i].id.toString() == id) {
                reservationList = dataUserList[i].reserved
                name = dataUserList[i].info.name.toString()
                age = dataUserList[i].info.age.toString()
                gender = dataUserList[i].info.gender.toString()
            }
        }

        idInput2.text = "$id-$name($age/$gender)"

        for (i: Int in 0..<reservationList.size) {
            for (j: Int in 0..<dataRestaurantList.size)
                if (reservationList[i].restaurant_id == dataRestaurantList[j].id) {
                    reservationRList.add(dataRestaurantList[j])
                }
        }

        val restaurantAdapter = RestaurantAdapter(this, reservationList, reservationRList)
        reservationUser.adapter = restaurantAdapter

        reservationUser.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, DetailReservationActivity::class.java).apply {
                putExtra(ID, id.toString())
                putExtra(ITEM, position.toString())
            }
            startActivity(intent)
        }

        reserveBtn.setOnClickListener {
            val restaurantActivity = Intent(this, RestaurantActivity::class.java).apply {
                putExtra(ID, id.toString())
            }
            startActivity(restaurantActivity)
        }
    }
}