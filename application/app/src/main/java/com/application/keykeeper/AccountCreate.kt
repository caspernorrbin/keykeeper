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
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@RequiresApi(Build.VERSION_CODES.M)
class AccountCreate(_email: String, _password: String) {

    private val email: String
    private val passwordHash: String

    init {
        email = _email
        passwordHash = hashPassword(_password)

        println(String.format("AccountCreate: { email: %s, password: %s }", email, passwordHash))
        //val encryptedMessage =
        //println(String.format("Encrypted: %s, Decrypted: %s }", encryptedMessage, decryptedMessage))
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

    // create a cipher for encryption
    /*@RequiresApi(Build.VERSION_CODES.M)
    private val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE, generateSymkey())
    }*/

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hashPassword(password: String): String {
        // TODO: Hash password using appropriate hashing method and email as salt.
        // generate a symmetric key for the AES method
        val symkey = generateSymkey()

        //cipher.init(Cipher.ENCRYPT_MODE, symkey)
        //val ciphertext: ByteArray = cipher.doFinal(password.toByteArray())
        //val iv: ByteArray = cipher.iv // TODO: might want some input on what this is..

        val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, symkey)
        }
/*        cipher.init(Cipher.DECRYPT_MODE, symkey)
        val passHash: ByteArray = cipher.doFinal(ciphertext)
        val iv2: ByteArray = cipher.iv
        return passHash.toString()*/
        val encryptedMessage: ByteArray = encryptCipher.doFinal(password.toByteArray())
        val decryptCipher = getDecryptCipher(encryptCipher.iv, symkey)
        val decryptedMessage = decryptCipher.doFinal(encryptedMessage)

        return "encrypted: ${encryptedMessage.toString()}, decrypted: ${decryptedMessage.toString()}"
        //return "ciphertext: $ciphertext iv: $iv" // TODO: this is just to see what these values are
    }

    /*private fun encryptCipher(): String {
        return ""
    }*/

    // get a cipher used for decryption of data (it pairs with 'encryptCipher')
    private fun getDecryptCipher(iv: ByteArray, symkey: SecretKey): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, symkey, IvParameterSpec(iv))
        }
    }

    // This is to check if key exists. However, this might not be needed
    /*@RequiresApi(Build.VERSION_CODES.M)
    private fun getKey(): SecretKey {
        val existingKey = false // TODO search if key exists
        return existingKey?.secretKey ?: generateSymkey()
    }*/

    // Generate a symmetric key for AES
    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateSymkey(): SecretKey {
        // TODO: Generate a symkey. This should probably be handled by a different class.
        //val keygen = KeyGenerator.getInstance(ALGORITHM) // encryption method
        //keygen.init(256)
        //return keygen.generateKey()
        return KeyGenerator.getInstance(ALGORITHM).apply{
            init(
                KeyGenParameterSpec.Builder(
                    "secret",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    // Encrypt ByteArray (message), outputStream used to know the iv
/*    @RequiresApi(Build.VERSION_CODES.M)
    fun encrypt(bytes: ByteArray, outputStream: OutputStream): ByteArray {
        val encryptedBytes = encryptCipher.doFinal(bytes)
        outputStream.use {
            it.write(encryptCipher.iv.size)
            it.write(encryptCipher.iv)
            it.write(encryptedBytes.size)
            it.write(encryptedBytes)
        }
        return encryptedBytes
    }*/

    // Decrypt the Input stream
 /*   fun decrypt(inputStream: InputStream): ByteArray {
        return inputStream.use {
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)

            val encryptedBytesSize = it.read()
            val encryptedBytes = ByteArray(encryptedBytesSize)
            it.read(encryptedBytes)

            getDecryptCipher(iv).doFinal(encryptedBytes)
        }
    }*/

    // Values used to us the same encryption method at every point
    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

}