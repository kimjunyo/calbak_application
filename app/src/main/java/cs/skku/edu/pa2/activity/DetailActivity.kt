package cs.skku.edu.pa2.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
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
import cs.skku.edu.pa2.adapter.MenuAdapter
import cs.skku.edu.pa2.data.DataMap
import cs.skku.edu.pa2.data.DataRestaurant
import cs.skku.edu.pa2.util.ReadAssets.Companion.readJsonFromAssets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val RESTAURANT_OPENHOURS = "OPENHOURS"
        const val RESTAURANT_CLOSEHOURS = "CLOSEHOURS"
        const val ID = "id"
        const val ITEM = "item"
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(
            R.id.mapview
        ) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val item = intent.getStringExtra(RestaurantActivity.ITEM)
        val id = intent.getStringExtra(RestaurantActivity.ID)
        val imgView = findViewById<ImageView>(R.id.imageView3)
        val textView2 = findViewById<TextView>(R.id.textView2)
        val listView = findViewById<ListView>(R.id.listView2)
        val button2 = findViewById<Button>(R.id.button2)

        val jsonString = readJsonFromAssets(this, "restaurant_info.json")
        val userType = object : TypeToken<ArrayList<DataRestaurant>>() {}.type
        val dataRestaurant: ArrayList<DataRestaurant> = Gson().fromJson(jsonString, userType)
        val restaurant = dataRestaurant[item!!.toInt() - 1]
        textView2.text =
            restaurant.restaurant + "\n" + restaurant.type + "/" + restaurant.location + "\n" + restaurant.rating + "\n" + restaurant.description + "\n" + restaurant.openingHours.open + "~" + restaurant.openingHours.close
        val image = restaurant.image
        val resourceId = resources.getIdentifier(image, "drawable", packageName)
        imgView.setImageResource(resourceId)

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


        val menuAdapter = MenuAdapter(this, restaurant.Menu)
        listView.adapter = menuAdapter

        button2.setOnClickListener {
            val intent = Intent(this, CheckListActivity::class.java).apply {
                putExtra(RESTAURANT_OPENHOURS, restaurant.openingHours.open)
                putExtra(RESTAURANT_CLOSEHOURS, restaurant.openingHours.close)
                putExtra(ID, id.toString())
                putExtra(ITEM, item)
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