package com.application.keykeeper

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import org.json.JSONObject
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class AccountCreate(_email: String, _password: String) {

    private val email: String
    private val passwordHash: String

    init {
        email = _email
        passwordHash = hashPassword(_password)

        println(String.format("AccountCreate: { email: %s, password: %s }", email, passwordHash))
    }

    // Sends a request to the server to create a new account.
    public fun sendCreateRequest() {
        // Create object containing data to be sent to the server.
        val jsonPostData = JSONObject()
        jsonPostData.put("email", this.email)
        jsonPostData.put("passwordHash", this.passwordHash)

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
        // generate a symmetric key for the AES method
        val symkey = generateSymkey()
        val cipher = Cipher.getInstance("AES_256/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, symkey)
        val ciphertext: ByteArray = cipher.doFinal(password.toByteArray())
        val iv: ByteArray = cipher.iv // TODO: might want some input on what this is..

/*        cipher.init(Cipher.DECRYPT_MODE, symkey)
        val passHash: ByteArray = cipher.doFinal(ciphertext)
        val iv2: ByteArray = cipher.iv
        return passHash.toString()*/

        return "ciphertext: $ciphertext iv: $iv" // TODO: this is just to see what these values are
    }

    private fun generateSymkey(): SecretKey {
        // TODO: Generate a symkey. This should probably be handled by a different class.
        val keygen = KeyGenerator.getInstance("AES") // encryption method
        keygen.init(256)
        return keygen.generateKey()
    }

}