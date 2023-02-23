package structure

import android.content.Context
import android.util.Log

object Storage {
    private const val PREF_NAME = "com.application.keykeeper"

    fun getSessionKey(context: Context): String? {
        return load(context, "session")
    }

    // Stores in `data/data/[package_name]/shared_prefs/[app name].xml`
    fun setSessionKey(context: Context, sessionKey: String): Boolean {
        return save(context, "session", sessionKey)
    }

    fun getRememberedEmail(context: Context): String? {
        return load(context, "rememberedEmail")
    }

    // Stores in `data/data/[package_name]/shared_prefs/[app name].xml`
    fun setRememberedEmail(context: Context, email: String): Boolean {
        return save(context, "rememberedEmail", email)
    }

    fun removeRememberedEmail(context: Context): Boolean {
        return remove(context, "rememberedEmail")
    }

    private fun save(context: Context, key: String, value: String): Boolean {
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

    private fun load(context: Context, key: String): String? {
        return try {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getString(key, null)
        } catch (error: Error) {
            Log.e("Storage.load", error.message.toString())
            null
        }
    }

    private fun remove(context: Context, key: String): Boolean {
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