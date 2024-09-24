package cs.skku.edu.pa2.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cs.skku.edu.pa2.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CheckListActivity : AppCompatActivity() {
    companion object {
        const val PEOPLES = "0"
        const val DATE = "1234"
        const val RESTAURANT_OPEN = "OPENHOURS"
        const val RESTAURANT_CLOSE = "CLOSEHOURS"
        const val ID = "id"
        const val ITEM = "item"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_list)

        val people = findViewById<EditText>(R.id.editTextText)
        val date = findViewById<EditText>(R.id.editTextDate)
        val btn = findViewById<Button>(R.id.button)

        val open = intent.getStringExtra(DetailActivity.RESTAURANT_OPENHOURS)
        val close = intent.getStringExtra(DetailActivity.RESTAURANT_CLOSEHOURS)
        val id = intent.getStringExtra(DetailActivity.ID)
        val item = intent.getStringExtra(DetailActivity.ITEM)

        btn.setOnClickListener {
            val peopleNum = try {
                people.text.toString().toInt()
            } catch (e: Exception) {
                Toast.makeText(this, "1부터 10까지의 숫자를 입력해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val dateInput = date.text.toString()

            if (peopleNum == null || peopleNum < 1 || peopleNum > 10) {
                Toast.makeText(this, "사람 수는 1부터 10 사이여야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.isLenient = false

            try {
                val inputDate = dateFormat.parse(dateInput)
                val currentDate = Calendar.getInstance().time

                if (inputDate != null && inputDate.after(currentDate)) {
                    val loginActivity = Intent(this, ConfirmCheck::class.java).apply {
                        putExtra(PEOPLES, people.text.toString())
                        putExtra(DATE, dateInput)
                        putExtra(RESTAURANT_OPEN, open)
                        putExtra(RESTAURANT_CLOSE, close)
                        putExtra(ITEM, item.toString())
                        putExtra(ID, id.toString())
                    }
                    startActivity(loginActivity)
                } else {
                    Toast.makeText(this, "날짜는 오늘 이후여야 합니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ParseException) {
                Toast.makeText(this, "잘못된 날짜 형식입니다. yyyy-MM-dd 형식으로 입력하세요.", Toast.LENGTH_SHORT)
                    .show()
            }


        }
    }
}