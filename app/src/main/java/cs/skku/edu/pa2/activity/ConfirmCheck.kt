package cs.skku.edu.pa2.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cs.skku.edu.pa2.R
import cs.skku.edu.pa2.data.DataMap
import cs.skku.edu.pa2.data.DataRestaurant
import cs.skku.edu.pa2.data.DataUser
import cs.skku.edu.pa2.util.ReadAssets.Companion.readJsonFromAssets
import cs.skku.edu.pa2.util.ReadAssets.Companion.readJsonFromInternalStorage
import cs.skku.edu.pa2.util.WriteAssets.Companion.writeJsonToInternalStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ConfirmCheck : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val ID = "id"
    }

    private lateinit var mMap: GoogleMap

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_check)

        val people = intent.getStringExtra(CheckListActivity.PEOPLES)
        val date = intent.getStringExtra(CheckListActivity.DATE)
        val openHours = intent.getStringExtra(CheckListActivity.RESTAURANT_OPEN)
        val closeHours = intent.getStringExtra(CheckListActivity.RESTAURANT_CLOSE)
        val id = intent.getStringExtra(CheckListActivity.ID)
        val item = intent.getStringExtra(CheckListActivity.ITEM).toString()

        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(
            R.id.mapview
        ) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val restaurant = readJsonFromAssets(this, "restaurant_info.json")
        val restaurantType = object : TypeToken<ArrayList<DataRestaurant>>() {}.type
        val dataRestaurant: ArrayList<DataRestaurant> = Gson().fromJson(restaurant, restaurantType)

        var et = "New"

        for (i: Int in 0..<dataRestaurant.size) {
            if (dataRestaurant[i].id!!.toInt() == item.toInt()) {
                et = dataRestaurant[i].location.toString()
            }
        }

        val client = OkHttpClient()
        val host = "https://api.weatherapi.com/v1/current.json"

        val apiKey = "1f5749b5fd274335a7951715242105"
        val findCity = et
        val path = "?key=$apiKey&q=$findCity"
        val req = Request.Builder().url(host + path).build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.d("error", "asdf")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val data = response.body!!.string()
                    val datParse = Gson().fromJson(data, DataMap::class.java)
                    val findLat = datParse.location.lat!!
                    val findLon = datParse.location.lon!!
                    CoroutineScope(Dispatchers.Main).launch {

                        mMap.clear()
                        val marker = LatLng(findLat, findLon)
                        mMap.addMarker(MarkerOptions().position(marker).title("MAP"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 10F))
                    }
                }
            }
        })

        val tex9 = findViewById<TextView>(R.id.people)
        val tex10 = findViewById<TextView>(R.id.date)
        val tex12 = findViewById<TextView>(R.id.openHours)
        val confirm = findViewById<Button>(R.id.button)
        val cancel = findViewById<Button>(R.id.button3)
        val time = findViewById<EditText>(R.id.editTextDate2)

        tex9.text = people
        tex10.text = date
        tex12.text = "$openHours~$closeHours"

        confirm.setOnClickListener {
            if (!isValidTime(time.text.toString(), openHours.toString(), closeHours.toString())) {
                Toast.makeText(applicationContext, "Time Invalid", Toast.LENGTH_SHORT).show()
            } else {
                val gson = Gson()
                val userType = object : TypeToken<List<DataUser>>() {}.type
                val jsonString = try {
                    readJsonFromInternalStorage(this, "user_info.json")
                } catch (e: Exception) {
                    readJsonFromAssets(this, "user_info.json")
                }
                val users: MutableList<DataUser> = gson.fromJson(jsonString, userType)
                var reservationId = 0
                for (i: Int in 0..<users.size) {
                    if (users[i].id.equals(id)) {
                        val reserved = users[i].reserved
                        for (j: Int in 0..<reserved.size) {
                            if (reserved[j].restaurant_id == item.toInt()) {
                                Toast.makeText(
                                    applicationContext,
                                    "같은 장소를 두 번 예약할 수는 없습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setOnClickListener
                            }
                        }
                        reservationId =
                            (users[i].reserved[users[i].reserved.size - 1].reservation_id!!) + 1
                    }
                }

                val newReservation = DataUser.Reservations(
                    reservationId,
                    item.toInt(),
                    people?.toInt(),
                    date,
                    time.text.toString()
                )

                users.find { it.id == id }?.reserved?.add(newReservation)

                val toJson = gson.toJson(users)

                writeJsonToInternalStorage(this, "user_info.json", toJson)
                val loginActivity = Intent(this, LoginActivity::class.java).apply {
                    putExtra(ID, id.toString())
                }
                startActivity(loginActivity)
            }
        }
        cancel.setOnClickListener {
            val loginActivity = Intent(this, CheckListActivity::class.java)
            startActivity(loginActivity)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isValidTime(input: String, openHours: String, closeHours: String): Boolean {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        return try {
            val time = LocalTime.parse(input, timeFormatter)
            val open: LocalTime = if (openHours == "9:00") {
                LocalTime.parse("09:00", timeFormatter)
            } else {
                LocalTime.parse(openHours, timeFormatter)
            }

            val close = LocalTime.parse(closeHours, timeFormatter)
            !time.isBefore(open) && !time.isAfter(close)
        } catch (e: DateTimeParseException) {
            false
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d("start", "check")

        val marker = LatLng(37.295881, 126.975931)
        mMap.addMarker(MarkerOptions().position(marker).title("MAP"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 10F))
    }
}