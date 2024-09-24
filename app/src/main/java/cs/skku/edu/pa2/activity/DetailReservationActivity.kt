package cs.skku.edu.pa2.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

class DetailReservationActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val ID = "id"
    }

    private lateinit var mMap: GoogleMap

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO API 넣기
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_6)

        val item = intent.getStringExtra(LoginActivity.ITEM).toString().toInt()
        val id = intent.getStringExtra(LoginActivity.ID).toString()

        val restaurantName = findViewById<TextView>(R.id.restaurantName)
        val peopleNum = findViewById<TextView>(R.id.peopleNum)
        val dateReservation = findViewById<TextView>(R.id.dateReservation)
        val times = findViewById<TextView>(R.id.times)
        val image = findViewById<ImageView>(R.id.imageView4)
        val cancel = findViewById<Button>(R.id.cancel)

        val jsonString = try {
            readJsonFromInternalStorage(this, "user_info.json")
        } catch (e: Exception) {
            readJsonFromAssets(this, "user_info.json")
        }
        val userType = object : TypeToken<ArrayList<DataUser>>() {}.type
        val dataUserList: ArrayList<DataUser> = Gson().fromJson(jsonString, userType)

        val restaurant = readJsonFromAssets(this, "restaurant_info.json")
        val restaurantType = object : TypeToken<ArrayList<DataRestaurant>>() {}.type
        val dataRestaurant: ArrayList<DataRestaurant> = Gson().fromJson(restaurant, restaurantType)

        var res: MutableList<DataUser.Reservations> = mutableListOf()

        for (i: Int in 0..<dataUserList.size) {
            if (dataUserList[i].id == id) {
                res = dataUserList[i].reserved
            }
        }

        var et = "New York"

        times.text = res[item].time.toString()
        dateReservation.text = res[item].date.toString()
        peopleNum.text = res[item].number_of_people.toString()
        for (i: Int in 0..<dataRestaurant.size) {
            if (dataRestaurant[i].id!!.toInt() == res[item].restaurant_id) {
                restaurantName.text = dataRestaurant[i].restaurant

                et = dataRestaurant[i].location.toString()

                val imageName = dataRestaurant[i].image
                val imageResId =
                    this.resources.getIdentifier(imageName, "drawable", this.packageName)
                if (imageResId != 0) {
                    image.setImageResource(imageResId)
                } else {
                    image.setImageResource(R.drawable.ic_launcher_background)
                }
            }
        }

        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(
            R.id.mapview
        ) as SupportMapFragment
        mapFragment.getMapAsync(this)

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


        cancel.setOnClickListener {
            Toast.makeText(applicationContext, "예약이 취소되었습니다.", Toast.LENGTH_SHORT).show()

            val gson = Gson()
            val userType = object : TypeToken<List<DataUser>>() {}.type
            val jsonString = try {
                readJsonFromInternalStorage(this, "user_info.json")
            } catch (e: Exception) {
                readJsonFromAssets(this, "user_info.json")
            }

            val users: MutableList<DataUser> = gson.fromJson(jsonString, userType)

            for (i: Int in 0..<users.size) {
                if (users[i].id.equals(id)) {
                    users[i].reserved.removeAt(item)
                }
            }
            val toJson = gson.toJson(users)

            writeJsonToInternalStorage(this, "user_info.json", toJson)

            val intent = Intent(this, LoginActivity::class.java).apply {
                putExtra(ID, id)
            }
            startActivity(intent)
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