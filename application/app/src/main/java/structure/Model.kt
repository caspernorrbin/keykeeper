package structure

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import communication.Account
import communication.Item
import communication.OfflineAccount
import org.json.JSONArray

@RequiresApi(Build.VERSION_CODES.O)

object Model {
    private var symkey: String = ""
    fun clearValues() {
        symkey = ""
    }

    object Communication  {
        private lateinit var selectedServer: ServerItem
        private lateinit var selectedServerURL: String

        fun setSelectedServer(newSelectedServer: ServerItem) {
            // Split url to ensure correct format
            val groups = Regex("(.+)://([^/]+)").find(newSelectedServer.url)?.groups!!
            val protocol = groups[1]?.value
            val site = groups[2]?.value
            val url = "$protocol://$site/"
            this.selectedServerURL = url
            this.selectedServer = newSelectedServer
        }

        fun createAccount(email: String, password: String, callback: (success: Boolean, message: String) -> Unit) {
            val passwordHash = Encryption.hashAuthentication(password, email)
            val symkey = Encryption.generateSymkey()
            val encSymkey = Encryption.encryptSymkey(password, symkey)
            Account.sendCreateAccountRequest(email, passwordHash, encSymkey, selectedServerURL) { success, message ->
                // TODO: Add accounts to local storage to allow offline logins
                callback(success, message)
            }
        }

        fun updateAccount(context: Context, oldPassword: String, newEmail: String, newPassword: String, callback: (success: Boolean, message: String) -> Unit) {
            val usedEmail = Storage.getUsedEmail(context)
            if (symkey == "" || usedEmail == null) {
                callback(false, "Application error")
            }

            val emailToUse = if (newEmail != "") newEmail else usedEmail!!
            val passwordToUse = if (newPassword != "") newPassword else oldPassword

            val oldPasswordHash = Encryption.hashAuthentication(oldPassword, usedEmail!!)
            val passwordHash = Encryption.hashAuthentication(passwordToUse, emailToUse)

            var encSymkey = ""
            if (newPassword != "") {
                encSymkey = Encryption.encryptSymkey(passwordToUse, symkey)
            }

            Account.sendUpdateAccountRequest(oldPasswordHash, newEmail, passwordHash, encSymkey, selectedServerURL) { success, message ->
                if (success) {
                    Storage.setAccountDetails(context, newEmail, passwordHash, encSymkey)
                }
                callback(success, message)
            }
        }

        fun login(context: Context, offlineMode: Boolean, email: String, password: String, callback: (success: Boolean, message: String) -> Unit) {
            if (offlineMode) {
                OfflineAccount.sendLoginRequest(context, email, password) { success, symOrError ->
                    if (success) {
                        Storage.setOfflineMode(context, true)
                        symkey = Encryption.decryptSymkey(password, symOrError)
                    }

                    callback(success, if (success) "Logged in" else symOrError)
                }
            } else {
                val passwordHash = Encryption.hashAuthentication(password, email)
                Account.sendLoginRequest(email, passwordHash, selectedServerURL) { success, symOrError ->

                    if (success) {
                        Storage.setAccountDetails(context, email, passwordHash, symOrError)
                        Storage.setOfflineMode(context, false)

                        symkey = Encryption.decryptSymkey(password, symOrError)
                    }

                    callback(success, if (success) "Logged in" else symOrError)
                }
            }
        }

        fun getItems(context: Context, callback: (success: Boolean, message: String, data: Array<CredentialsItem>) -> Unit) {
            if (Storage.inOfflineMode(context)) {
                val res = Storage.getItems(context) ?: arrayOf()
                callback(true, "", res)
            } else {
                Item.sendGetItemsRequest(selectedServerURL) { success, message, data ->
                    if (success) {
                        val credentials = data.map { it.toCredentialsItem() }
                        Storage.setItems(context, credentials)
                    }
                    val res = Storage.getItems(context) ?: arrayOf()
                    Log.v("getItems.size", res.size.toString())
                    callback(success, message, res)
                }
            }
        }

        fun createItem(item: CredentialsItem, callback: (success: Boolean, message: String) -> Unit) {
            val encItem = Encryption.encryptItem(symkey, item)
            Item.sendCreateItemRequest(encItem, selectedServerURL) { success, message ->
                // TODO: Anything to do here?
                callback(success, message)
            }
        }

        fun deleteItem(item: CredentialsItem, callback: (success: Boolean, message: String) -> Unit) {
            Item.sendDeleteItemRequest(item, selectedServerURL) { success, message ->
                // TODO: Anything to do here?
                callback(success, message)
            }
        }

        fun updateItem(updatedItem: CredentialsItem, callback: (success: Boolean, message: String) -> Unit) {
            val updatedEncItem = Encryption.encryptItem(symkey, updatedItem)
            Item.sendUpdateItemRequest(updatedEncItem, selectedServerURL) { success, message ->
                // TODO: Anything to do here?
                callback(success, message)
            }
        }
    }

    object Storage {
        // rememberedEmail
        fun getRememberedEmail(context: Context): String? {
            return LocalStorage.load(context, "rememberedEmail")
        }

        fun setRememberedEmail(context: Context, email: String): Boolean {
            return LocalStorage.save(context, "rememberedEmail", email)
        }

        fun removeRememberedEmail(context: Context): Boolean {
            return LocalStorage.remove(context, "rememberedEmail")
        }

        // items
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

        // serverItems
        fun addServerItem(context: Context, item: ServerItem): Boolean {
            return try {
                // Get previous items
                val items = getServerItems(context) ?: arrayOf()
                val jsonArray = JSONArray(items.filterNotNull().map { it.toJSON() })
                // Add new item
                jsonArray.put(item.toJSON())
                LocalStorage.save(context, "serverItems", jsonArray.toString())
            } catch (error: Throwable) {
                Log.e("addServerItem", error.message.toString())
                false
            }
        }

        fun removeServerItem(context: Context, item: ServerItem): Boolean {
            try {
                // Get previous items
                val items = getServerItems(context) ?: return false
                // Remove item
                if (items.any { it == item }) {
                    val newItems = items.filter { it != item }
                    val jsonArray = JSONArray(newItems)
                    return LocalStorage.save(context, "serverItems", jsonArray.toString())
                }
            } catch (error: Throwable) {
                Log.e("removeServerItem", error.message.toString())
            }
            return false
        }

        fun removeServerItems(context: Context): Boolean {
            return LocalStorage.remove(context, "serverItems")
        }

        fun getServerItems(context: Context): Array<ServerItem>? {
            val text = LocalStorage.load(context, "serverItems")
            if (text != null) {
                try {
                    return ServerItem.getArrayDeserializer().deserialize(text)
                } catch (error: Throwable) {
                    Log.e("getServerItems", error.message.toString())
                }
            }
            return null
        }

        // selectedServer
        fun getSelectedServer(context: Context): ServerItem? {
            val text = LocalStorage.load(context, "selectedServer")
            if (text != null) {
                try {
                    return ServerItem.getDeserializer().deserialize(text)
                } catch (error: Throwable) {
                    Log.e("getSelectedServer", error.message.toString())
                }
            }
            return null
        }

        fun setSelectedServer(context: Context, server: ServerItem): Boolean {
            val text = server.toJSON().toString()
            return LocalStorage.save(context, "selectedServer", text)
        }

        fun removeSelectedServer(context: Context): Boolean {
            return LocalStorage.remove(context, "selectedServer")
        }

        // offlineMode
        fun setOfflineMode(context: Context, mode: Boolean): Boolean {
            return LocalStorage.save(context, "offlineMode", mode.toString())
        }

        fun inOfflineMode(context: Context) : Boolean {
            return LocalStorage.load(context, "offlineMode") == true.toString()
        }

        // account, keys
        fun setAccountDetails(context: Context, email: String, passwordHash: String, encSymkey: String): Boolean {
            val emailStatus = LocalStorage.save(context, "usedEmail", email)
            val passwordStatus = LocalStorage.save(context, "passwordHash", passwordHash)
            var symkeyStatus = true
            if (encSymkey != "") {
                symkeyStatus = LocalStorage.save(context, "encSymkey", encSymkey)
            }
            return emailStatus && passwordStatus && symkeyStatus
        }

        fun getAccountDetails(context: Context): Triple<String, String, String>? {
            val email = LocalStorage.load(context, "usedEmail")
            val passwordHash = LocalStorage.load(context, "passwordHash")
            val encSymkey = LocalStorage.load(context, "encSymkey")
            return if (email != null && passwordHash != null && encSymkey != null) Triple(email, passwordHash, encSymkey) else null
        }

        fun getUsedEmail(context: Context): String? {
            return LocalStorage.load(context, "usedEmail")
        }
    }
}