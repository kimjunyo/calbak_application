package cs.skku.edu.pa2.util

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class ReadAssets {
    companion object {
        fun readJsonFromInternalStorage(context: Context, fileName: String): String {
            return context.openFileInput(fileName).bufferedReader().use { it.readText() }
        }


        fun readJsonFromAssets(context: Context, fileName: String): String {
            val stringBuilder = StringBuilder()
            val inputStream = context.assets.open(fileName)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            bufferedReader.use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
            return stringBuilder.toString()
        }
    }

}