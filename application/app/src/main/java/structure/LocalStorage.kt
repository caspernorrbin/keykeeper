package structure

import android.content.Context
import android.util.Log
import org.json.JSONArray

object LocalStorage {
    // Stores in `data/data/[package_name]/shared_prefs/[app name].xml`
    private const val PREF_NAME = "com.application.keykeeper"

    fun save(context: Context, key: String, value: String): Boolean {
        return try {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString(key, value)
            editor.apply()
            true
        } catch (error: Error) {
            Log.e("Storage.save", error.message.toString())
            false
        }
    }

    fun load(context: Context, key: String): String? {
        return try {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getString(key, null)
        } catch (error: Error) {
            Log.e("Storage.load", error.message.toString())
            null
        }
    }

    fun remove(context: Context, key: String): Boolean {
        return try {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.remove(key)
            editor.apply()
            true
        } catch (error: Error) {
            Log.e("Storage.remove", error.message.toString())
            false
        }
    }
}