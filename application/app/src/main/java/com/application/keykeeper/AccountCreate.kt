package com.application.keykeeper

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import org.json.JSONObject

class AccountCreate(_email: String, _password: String) {

    private val email: String
    private val passwordHash: String

    init {
        email = _email
        passwordHash = hashPassword(_password)
    }

    public fun sendCreateRequest() {
        // Create object containing data to be sent to the server.
        val jsonPostData = JSONObject()
        jsonPostData.put("email", this.email)
        jsonPostData.put("passwordHash", hashPassword(this.passwordHash))

        // Make request.
        val httpAsync = Fuel.post("http://10.0.2.2:8000")
            .header("Content-Type", "application/json")
            .jsonBody(jsonPostData.toString())
            .response { _, response, _ ->
                println("Got response with status code: " + response.statusCode)
            }
    }

    private fun hashPassword(password: String): String {
        // TODO: Hash password using appropriate hashing method and email as salt.
        return "this_is_not_yet_hashed"
    }

    private fun generateSymkey(): String {
        // TODO: Generate a symkey. This should probably be handled by a different class.
        return "this_is_not_a_symkey"
    }

}