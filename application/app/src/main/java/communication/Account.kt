package com.application.keykeeper

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson
import org.json.JSONObject

/**
 * Represents a response body sent by the server.
 */
data class ServerMessage(val message: String) {
    class Deserializer: ResponseDeserializable<ServerMessage> {
        override fun deserialize(content: String): ServerMessage
            = Gson().fromJson(content, ServerMessage::class.java)
    }
}

/**
 * Represents an Account that can be used when logging in, creating an account and identifying
 * the current user.
 */
class Account(_email: String, _password: String) {

    private val email: String
    private val passwordHash: String

    // Indicates whether the Account has successfully logged in or not.
    private var loggedIn: Boolean

    init {
        email = _email
        passwordHash = hashPassword(_password)
        loggedIn = false
    }

    // Returns a JSONObject of the data associated with the account.
    private fun jsonAccountData(includeSymKey: Boolean = false): JSONObject {
        // Create object containing data to be sent to the server.
        val accountData = JSONObject()

        // Insert data.
        accountData.put("email", this.email)
        accountData.put("password", this.passwordHash)

        if(includeSymKey) {
            accountData.put("symkey", this.generateSymkey())
        }

        return accountData
    }

    public fun isLoggedIn(): Boolean {
        return this.loggedIn
    }

    // Sends a request to login to the server and calls the callback function with the server
    // response. If the login was successful, the loggedIn property is set to true.
    public fun sendLoginRequest(callback: (successful: Boolean, responseBody: String) -> Unit) {
        val jsonPostData = this.jsonAccountData()

        // Make request.
        Fuel.post("http://10.0.2.2:8080/api/auth/login")
            .header("Content-Type", "application/json")
            .jsonBody(jsonPostData.toString())
            .responseObject(ServerMessage.Deserializer()) { _, response, result ->
                val (serverResponse, _) = result

                // Set loggedIn
                if(response.statusCode == 200) {
                    this.loggedIn = true
                }

                when(response.statusCode) {
                    200 -> serverResponse?.message
                    400 -> "Wrong email/password. Try again."
                    else -> "Something went wrong when communicating with the server. Try again later."
                }?.let { callback.invoke(response.statusCode == 200, it) }
            }
    }

    // Sends a request to the server to create a new account.
    public fun sendCreateAccountRequest(callback: (success: Boolean, message: String) -> Unit) {
        val jsonPostData = this.jsonAccountData(includeSymKey = true)

        // Make request.
        Fuel.post("http://10.0.2.2:8080/api/account/create")
            .header("Content-Type", "application/json")
            .jsonBody(jsonPostData.toString())
            .responseObject(ServerMessage.Deserializer()) { _, response, result ->
                val (serverResponse, _) = result

                when(response.statusCode) {
                    200 -> serverResponse?.message
                    400 -> "Failed to create account"
                    else -> "Something went wrong when communicating with the server. Try again later."
                }?.let { callback.invoke(response.statusCode == 200, it) }
            }
    }
    private fun hashPassword(password: String): String {
        // TODO: Hash password using appropriate hashing method and email as salt.
        return password
    }

    private fun generateSymkey(): String {
        // TODO: Generate a symkey. This should probably be handled by a different class.
        return "this_is_not_a_symkey"
    }

}