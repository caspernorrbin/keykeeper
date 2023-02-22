package com.application.keykeeper

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.text.TextUtils
import com.application.keykeeper.Encryption

@RequiresApi(Build.VERSION_CODES.M)


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

    //@RequiresApi(Build.VERSION_CODES.M)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun hashPassword(password: String): String {
        val passwordHash = Encryption.hashAuthentication(password, "a@gmail.com")
        if (Encryption.comparePasswordHash(password, passwordHash)) {
            println("Passwords match, hashing works")
        }

        val symkey = Encryption.generateSymkey()

        val plainText = "A nice message"
        val encryptedItem = Encryption.encryptItem(symkey, plainText)
        val encryptedKey = Encryption.encryptSymkey(passwordHash, symkey)

        val decryptedKey = Encryption.decryptSymkey(passwordHash, encryptedKey)
        val decryptedItem = Encryption.decryptItem(decryptedKey, encryptedItem)

        if(plainText == decryptedItem) {
            println("Messages match, Encryption works")
        }

        return passwordHash
    }
}
