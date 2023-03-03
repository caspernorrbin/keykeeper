package structure

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import communication.Account
import communication.Item
import org.json.JSONArray

@RequiresApi(Build.VERSION_CODES.O)

object Model {
    private var symkey: String = ""

    object Communication  {

        fun createAccount(email: String, password: String, callback: (success: Boolean, message: String) -> Unit) {
            val passwordHash = Encryption.hashAuthentication(password, email)
            val symkey = Encryption.generateSymkey()
            val encSymkey = Encryption.encryptSymkey(password, symkey)
            Account.sendCreateAccountRequest(email, passwordHash, encSymkey) { success, message ->
                // TODO: Add accounts to local storage to allow offline logins
                callback(success, message)
            }
        }


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

        fun getItems(context: Context, callback: (success: Boolean, message: String, data: Array<CredentialsItem>) -> Unit) {
            Item.sendGetItemsRequest() { success, message, data ->
                if (success) {
                    val credentials = data.map { it.toCredentialsItem() }
                    Storage.setItems(context, credentials)
                }
                val res = Storage.getItems(context) ?: arrayOf()
                Log.v("getItems.size", res.size.toString())
                callback(success, message, res)
            }
        }

        fun createItem(item: CredentialsItem, callback: (success: Boolean, message: String) -> Unit) {
            val encItem = Encryption.encryptItem(symkey, item)
            Item.sendCreateItemRequest(encItem) { success, message ->
                // TODO: Anything to do here?
                callback(success, message)
            }
        }

        fun deleteItem(item: CredentialsItem, callback: (success: Boolean, message: String) -> Unit) {
            Item.sendDeleteItemRequest(item) { success, message ->
                // TODO: Anything to do here?
                callback(success, message)
            }
        }

        fun updateItem(updatedItem: CredentialsItem, callback: (success: Boolean, message: String) -> Unit) {
            val updatedEncItem = Encryption.encryptItem(symkey, updatedItem)
            Item.sendUpdateItemRequest(updatedEncItem) { success, message ->
                // TODO: Anything to do here?
                callback(success, message)
            }
        }
    }

    object Storage {
        fun getSessionKey(context: Context): String? {
            return LocalStorage.load(context, "session")
        }

        fun setSessionKey(context: Context, sessionKey: String): Boolean {
            return LocalStorage.save(context, "session", sessionKey)
        }

        fun getRememberedEmail(context: Context): String? {
            return LocalStorage.load(context, "rememberedEmail")
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

        fun addServerItem(context: Context, item: ServerItem): Boolean {
            return try {
                // Get previous items
                var items = getServerItems(context);
                if (items == null) {
                    items = arrayOf()
                }
                // Add item to list
                val itemList = items.toMutableList()
                itemList.add(item)
                // Convert to JSON List
                val list = items.map { it.toJSON() }
                val text = JSONArray(list).toString()
                LocalStorage.save(context, "serverItems", text)
            } catch (error: Throwable) {
                Log.e("setItems", error.message.toString())
                false
            }
        }
        fun removeServerItem(context: Context, item: ServerItem): Boolean {
            return try {
                // Get previous items
                var items = getServerItems(context) ?: return false;
                // Add item to list
                val itemList = items.toMutableList()
                if (!itemList.remove(item)) { return false }
                // Convert to JSON List
                val list = items.map { it.toJSON() }
                val text = JSONArray(list).toString()
                LocalStorage.save(context, "serverItems", text)
            } catch (error: Throwable) {
                Log.e("setItems", error.message.toString())
                false
            }
        }

        fun getServerItems(context: Context): Array<ServerItem>? {
            val text = LocalStorage.load(context, "serverItems")
            if (text != null) {
                try {
                    return ServerItem.getArrayDeserializer().deserialize(text)
                } catch (error: Throwable) {
                    Log.e("getItems", error.message.toString())
                }
            }
            return null
        }

        fun setRememberedEmail(context: Context, email: String): Boolean {
            return LocalStorage.save(context, "rememberedEmail", email)
        }

        fun removeRememberedEmail(context: Context): Boolean {
            return LocalStorage.remove(context, "rememberedEmail")
        }
    }
}