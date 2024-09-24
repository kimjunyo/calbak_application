package cs.skku.edu.pa2.util

import android.content.Context

class WriteAssets {
    companion object{
        fun writeJsonToInternalStorage(context: Context, fileName: String, jsonString: String) {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write(jsonString.toByteArray())
            }
        }
    }
}