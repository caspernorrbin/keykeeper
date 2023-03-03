package structure

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import communication.Account
import communication.Item
import org.json.JSONArray

object Model {
    private var symkey: String = ""

    object Communication  {
        @RequiresApi(Build.VERSION_CODES.O)
        fun createAccount(email: String, password: String, callback: (success: Boolean, message: String) -> Unit) {
            val passwordHash = Encryption.hashAuthentication(password, email)
            val symkey = Encryption.generateSymkey()
            val encSymkey = Encryption.encryptSymkey(password, symkey)
            Account.sendCreateAccountRequest(email, passwordHash, encSymkey) { success, message ->
                // TODO: Add accounts to local storage to allow offline logins
                callback(success, message)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun login(email: String, password: String, callback: (success: Boolean, message: String) -> Unit) {
            val passwordHash = Encryption.hashAuthentication(password, email)
            Account.sendLoginRequest(email, passwordHash) { success, symOrError ->
                // TODO: Add accounts to local storage to allow offline logins

                if (success) {
                    symkey = Encryption.decryptSymkey(password, symOrError)
                    //  TODO: Maybe store encSymkey in permanent storage
                }

                callback(success, if (success) "Logged in" else symOrError)
            }
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun getItems(context: Context, callback: (success: Boolean, message: String, data: Array<CredentialsItem>) -> Unit) {
            Item.sendGetItemsRequest() { success, message, data ->
                if (success) {
                    val credentials = data.map { it.toCredentialsItem() }
                    Storage.setItems(context, credentials)
                }
                val res = Storage.getItems(context) ?: arrayOf()
                callback(success, message, res)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun createItem(item: CredentialsItem, callback: (success: Boolean, message: String) -> Unit) {
            val encItem = Encryption.encryptItem(symkey, item)
            Item.sendCreateItemRequest(encItem, callback)
        }

        fun deleteItem(item: CredentialsItem, callback: (success: Boolean, message: String) -> Unit) {
            Item.sendDeleteItemRequest(item, callback)
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun updateItem(updatedItem: CredentialsItem, callback: (success: Boolean, message: String) -> Unit) {
            val updatedEncItem = Encryption.encryptItem(symkey, updatedItem)
            Item.sendUpdateItemRequest(updatedEncItem, callback)
        }
    }

    object Storage {
        fun getSessionKey(context: Context): String? {
            return LocalStorage.load(context, "session")
        }

        fun setSessionKey(context: Context, sessionKey: String): Boolean {
            return LocalStorage.save(context, "session", sessionKey)
        }

        fun setItems(context: Context, items: Collection<CredentialsItem>): Boolean {
            return try {
                val list = items.map { it.toJSON() }
                val text = JSONArray(list).toString()
                LocalStorage.save(context, "items", text)
            } catch (error: Throwable) {
                Log.e("setItems", error.message.toString())
                false
            }
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun getItems(context: Context): Array<CredentialsItem>? {
            val text = LocalStorage.load(context, "items")
            if (text != null) {
                try {
                    val encItems = CredentialsItem.getArrayDeserializer().deserialize(text)
                    val items = encItems.map { Encryption.decryptItem(symkey, it) }
                    return items.toTypedArray()
                } catch (error: Throwable) {
                    Log.e("getItems", error.message.toString())
                }
            }
            return null
        }

        fun getRememberedEmail(context: Context): String? {
            return LocalStorage.load(context, "rememberedEmail")
        }

        fun setRememberedEmail(context: Context, email: String): Boolean {
            return LocalStorage.save(context, "rememberedEmail", email)
        }

        fun removeRememberedEmail(context: Context): Boolean {
            return LocalStorage.remove(context, "rememberedEmail")
        }
    }
}