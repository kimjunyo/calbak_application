package cs.skku.edu.pa2.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cs.skku.edu.pa2.R
import cs.skku.edu.pa2.data.DataUser
import cs.skku.edu.pa2.util.ReadAssets.Companion.readJsonFromAssets

class MainActivity : AppCompatActivity() {
    companion object {
        const val ID = "id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val idInput = findViewById<EditText>(R.id.idInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)

        val jsonString = readJsonFromAssets(this, "user_info.json")
        val userType = object : TypeToken<ArrayList<DataUser>>() {}.type
        val dataUserList: ArrayList<DataUser> = Gson().fromJson(jsonString, userType)

        loginBtn.setOnClickListener {
            for (i: Int in 0 until dataUserList.size) {
                if (dataUserList[i].id.toString() == idInput.text.toString() && dataUserList[i].passwd.toString() == passwordInput.text.toString()) {
                    val loginActivity = Intent(this, LoginActivity::class.java).apply {
                        putExtra(ID, idInput.text.toString())
                    }
                    startActivity(loginActivity)
                    return@setOnClickListener
                }
            }
            Toast.makeText(applicationContext, "Login fail", Toast.LENGTH_SHORT).show()
        }

    }
}