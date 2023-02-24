package structure

import android.content.Context
import android.util.Log
import communication.Account
import communication.Item
import org.json.JSONArray

object Model {
    object Communication  {
        fun createAccount(email: String, password: String, callback: (success: Boolean, message: String) -> Unit) {
            Account.sendCreateAccountRequest(email, password) { success, message ->
                // TODO: Add accounts to local storage to allow offline logins
                callback(success, message)
            }
        }


        fun login(email: String, password: String, callback: (success: Boolean, message: String) -> Unit) {
            Account.sendLoginRequest(email, password) { success, message ->
                // TODO: Add accounts to local storage to allow offline logins
                callback(success, message)
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
            Item.sendCreateItemRequest(item) { success, message ->
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
            Item.sendUpdateItemRequest(updatedItem) { success, message ->
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
                    return CredentialsItem.getArrayDeserializer().deserialize(text)
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