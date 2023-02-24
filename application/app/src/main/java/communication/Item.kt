package communication

import com.application.keykeeper.BuildConfig
import com.github.kittinunf.fuel.core.extensions.jsonBody
import communication.structure.DatabaseItem
import communication.structure.ServerMessage
import org.json.JSONObject
import structure.CredentialsItem

object Item {

    private const val notLoggedInMessage: String = "User is not logged in."

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

    private fun jsonUpdateData(item: CredentialsItem): JSONObject {
        // Create object containing data to be sent to the server.
        val serverData = JSONObject()

        serverData.put("itemId", item.id)
        serverData.put("data", jsonItemData(item))

        return serverData
    }

    private fun matchResponseCodeToMessage(responseCode: Int, statusOkMessage: String?): String {
        return when(responseCode) {
            200 -> statusOkMessage ?: "No message."
            400 -> "Bad input data. Try again."
            403 -> "Cannot execute request when not logged in."
            else -> "Something went wrong when communicating with the server. Try again later."
        }
    }

    private fun sendAuthenticatedPostRequestWithServerMessageResponse(url: String, jsonData: JSONObject, callback: (successful: Boolean, message: String) -> Unit) {
        Account.sendAuthenticatedPostRequest(url)
            .jsonBody(jsonData.toString())
            .responseObject(ServerMessage.getDeserializer()) { _, response, result ->
                val (serverResponse, _) = result

                callback.invoke(
                    response.statusCode == 200,
                    matchResponseCodeToMessage(response.statusCode, serverResponse?.message)
                )
            }
    }

    fun sendCreateItemRequest(item: CredentialsItem, callback: (successful: Boolean, message: String) -> Unit) {
        val jsonPostData = this.jsonItemData(item)

        if(!Account.isLoggedIn()) {
            callback.invoke(false, notLoggedInMessage)
        } else {
            sendAuthenticatedPostRequestWithServerMessageResponse(
                 BuildConfig.SERVER_URL + "api/item/create",
                jsonPostData,
                callback
            )
        }
    }

    fun sendGetItemsRequest(callback: (successful: Boolean, message: String, items: Array<DatabaseItem>) -> Unit) {
        if(!Account.isLoggedIn()) {
            callback.invoke(false, notLoggedInMessage, arrayOf())
        } else {
            Account.sendAuthenticatedGetRequest(BuildConfig.SERVER_URL + "api/item/getAll")
                .responseObject(DatabaseItem.getArrayDeserializer()) { _, response, result ->
                    val (serverResponse, _) = result

                    callback.invoke(
                        response.statusCode == 200,
                        matchResponseCodeToMessage(response.statusCode, null),
                        serverResponse ?: arrayOf()
                    )
                }
        }
    }

    fun sendDeleteItemRequest(item: CredentialsItem, callback: (successful: Boolean, message: String) -> Unit) {
        val jsonPostData = this.jsonDeleteData(item)

        if(!Account.isLoggedIn()) {
            callback.invoke(false, notLoggedInMessage)
        } else {
            sendAuthenticatedPostRequestWithServerMessageResponse(
                BuildConfig.SERVER_URL + "api/item/delete",
                jsonPostData,
                callback
            )
        }
    }

    fun sendUpdateItemRequest(item: CredentialsItem, callback: (successful: Boolean, message: String) -> Unit) {
        val jsonPostData = this.jsonUpdateData(item)

        if(!Account.isLoggedIn()) {
            callback.invoke(false, notLoggedInMessage)
        } else {
            sendAuthenticatedPostRequestWithServerMessageResponse(
                BuildConfig.SERVER_URL + "api/item/update",
                jsonPostData,
                callback
            )
        }
    }
}