package structure

import android.content.Context
import android.util.Log
import org.json.JSONArray

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

    fun setItems(context: Context, items: Collection<CredentialsItem>): Boolean {
        return try {
            val list = items.map { it.toJSON() }
            val text = JSONArray(list).toString()
            save(context, "items", text)
        } catch (error: Throwable) {
            Log.e("setItems", error.message.toString())
            false
        }
    }

    fun getItems(context: Context): Array<CredentialsItem>? {
        val text = load(context, "items")
        if (text != null) {
            try {
                return CredentialsItem.getArrayDeserializer().deserialize(text)
            } catch (error: Throwable) {
                Log.e("getItems", error.message.toString())
            }
        }
        return null
    }

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