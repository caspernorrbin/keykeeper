package communication

import com.application.keykeeper.Account
import com.github.kittinunf.fuel.core.extensions.jsonBody
import org.json.JSONObject
import structure.CredentialsItem

object Item {

    private fun jsonItemData(item: CredentialsItem): JSONObject {
        // Create object containing data to be sent to the server.
        val itemData = JSONObject()

        // TODO: Encrypt all information that is sent to the server.
        itemData.put("item_name", item.label)
        itemData.put("username", item.username)
        itemData.put("password", item.password)
        itemData.put("uri", item.uri)
        itemData.put("notes", item.notes)

        return itemData
    }
    private fun jsonDeleteData(item: CredentialsItem): JSONObject {
        // Create object containing data to be sent to the server.
        val itemData = JSONObject()
        itemData.put("itemId", item.id)
        return itemData
    }

    fun sendCreateItemRequest(item: CredentialsItem, callback: (successful: Boolean, message: String) -> Unit) {
        val jsonPostData = this.jsonItemData(item)

        if(!Account.isLoggedIn()) {
            callback.invoke(false, "User not logged in.")
        } else {
            Account.sendAuthenticatedPostRequest("http://10.0.2.2:8080/api/item/create")
                .header("Content-Type", "application/json")
                .jsonBody(jsonPostData.toString())
                .responseObject(ServerMessage.Deserializer()) { _, response, result ->
                    val (serverResponse, _) = result
                    when(response.statusCode) {
                        200 -> serverResponse?.message ?: "No message"
                        400 -> "Bad input data. Try again."
                        403 -> "You're not logged in. Cannot create items when not logged in."
                        else -> "Something went wrong when communicating with the server. Try again later."
                    }.let { callback.invoke(response.statusCode == 200, it) }
                }
        }
    }

    fun sendGetItemsRequest(callback: (successful: Boolean, message: String, items: Array<DatabaseCredentialsItem>) -> Unit) {
        if(!Account.isLoggedIn()) {
            callback.invoke(false, "User not logged in.", arrayOf())
        } else {
            Account.sendAuthenticatedGetRequest("http://10.0.2.2:8080/api/item/getAll")
                .responseObject(DatabaseCredentialsItem.ArrayDeserializer()) { _, response, result ->
                    val (serverResponse, _) = result
                    when(response.statusCode) {
                        200 -> "No message"
                        400 -> "Bad input data. Try again."
                        403 -> "You're not logged in. Cannot access items when not logged in."
                        else -> "Something went wrong when communicating with the server. Try again later."
                    }.let { callback.invoke(response.statusCode == 200, it, serverResponse ?: arrayOf()) }
                }
        }
    }

    fun sendDeleteItemRequest(item: CredentialsItem, callback: (successful: Boolean, message: String) -> Unit) {
        val jsonPostData = this.jsonDeleteData(item)

        if(!Account.isLoggedIn()) {
            callback.invoke(false, "User not logged in.")
        } else {
            Account.sendAuthenticatedPostRequest("http://10.0.2.2:8080/api/item/delete")
                .header("Content-Type", "application/json")
                .jsonBody(jsonPostData.toString())
                .responseObject(ServerMessage.Deserializer()) { _, response, result ->
                    val (serverResponse, _) = result
                    when(response.statusCode) {
                        200 -> serverResponse?.message ?: "No message"
                        400 -> "Bad input data. Try again."
                        403 -> "You're not logged in. Cannot create items when not logged in."
                        else -> "Something went wrong when communicating with the server. Try again later."
                    }.let { callback.invoke(response.statusCode == 200, it) }
                }
        }
    }
}