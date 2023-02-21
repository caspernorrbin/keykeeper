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
        // TODO: Hash password using appropriate hashing method and email as salt.
        val password = "1111111111111111"

        // generate a symmetric key for the AES method and create a cipher for encryption
        val symkey = generateSymkey("1234567890123456")
        val symkey2 = generateSymkey("1234567890123457")

        val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, symkey)
        }

        val symiv = encryptCipher.iv

        val encryptCipher2 = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, symkey2, IvParameterSpec(symiv))
        }

        println("symkey: ${Base64.getEncoder().encodeToString(symkey.encoded)}")

        if (encryptCipher.iv.equals(encryptCipher2.iv)) {
            println("IV equal")
        }

        // Encrypt the message
        val encryptedMessage: ByteArray = encryptCipher.doFinal(password.toByteArray())
        val encryptedMessage2: ByteArray = encryptCipher2.doFinal(password.toByteArray())
        val encoded = Base64.getEncoder().encodeToString(encryptedMessage)
        val encoded2 = Base64.getEncoder().encodeToString(encryptedMessage2)

        if (encoded == encoded2) {
            println("equals!")
        }

        // TODO: THESE DO NOT WORK (key length from using password as key I think)
        val (encryptedKey, keyCipher) = encryptSymkey(symkey2, password)
        val decryptedKey = decryptSymkey(encryptedKey, keyCipher, password)
        println("decrypted: ${Base64.getEncoder().encodeToString(decryptedKey.encoded)}")
        val decryptCipher = getDecryptCipher(encryptCipher2.iv, decryptedKey)

        // Decrypt the message
        // val decryptCipher = getDecryptCipher(encryptCipher2.iv, symkey2)
        val decryptedMessage = decryptCipher.doFinal(Base64.getDecoder().decode(encoded))
        val plainText = String(decryptedMessage)

        // Test if the decrypted message is the same as the original message
        if(plainText == password){
            return "Encryption works"
        } else {
            return "Did not decrypt properly"
        }
    }



    // get a cipher used for decryption of data (it pairs with 'encryptCipher')
    private fun getDecryptCipher(iv: ByteArray, symkey: SecretKey): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, symkey, IvParameterSpec(iv))
        }
    }

    // This is to check if key exists. However, this might not be needed
    /*@RequiresApi(Build.VERSION_CODES.M)
    private fun getKey(): SecretKey {
        val existingKey = ... // TODO search if key exists
        return existingKey?.secretKey ?: generateSymkey()
    }*/

    // Generate a symmetric key for AES
    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateSymkey(str: String): SecretKey {
        // Generate a symkey. TODO: This should probably be handled by a different class.
        // return KeyGenerator.getInstance(ALGORITHM).apply{
        //     init(
        //         KeyGenParameterSpec.Builder(
        //             "secret",
        //             KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        //         )
        //             .setBlockModes(BLOCK_MODE)
        //             .setEncryptionPaddings(PADDING)
        //             .setUserAuthenticationRequired(false)
        //             .setRandomizedEncryptionRequired(true)
        //             .build()
        //     )
        // }.generateKey()
        val key = SecretKeySpec(str.toByteArray(), ALGORITHM)
        return key
    }

    // Encrypt ByteArray (message), outputStream used to write to File
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

    // Decrypt the Input stream (from File)
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

    // TODO: FIX THIS (encrypt the symmetric key)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun encryptSymkey(symKey: SecretKey, master: String): Pair<String, Cipher> {
        println("Key: " + (Base64.getEncoder().encodeToString(symKey.encoded)) + " alg: " + symKey.algorithm)
        val key: SecretKey = SecretKeySpec(master.toByteArray(), ALGORITHM)
        val symKeyCipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, key)
        }
        val encryptedMessage: ByteArray = symKeyCipher.doFinal(symKey.encoded) // symKey should be made into a ByteArray
        println("encrypted: " + Base64.getEncoder().encodeToString(encryptedMessage))
        return Pair(Base64.getEncoder().encodeToString(encryptedMessage), symKeyCipher)
    }


    // TODO: FIX THIS (decryption of the symmetric key)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun decryptSymkey(encryptedKey: String, encryptCipher: Cipher, master: String): SecretKey {
        val key = SecretKeySpec(master.toByteArray(), ALGORITHM)
        val decryptCipher = getDecryptCipher(encryptCipher.iv, key)
        val symkey = decryptCipher.doFinal(Base64.getDecoder().decode(encryptedKey))
        //val plainText = String(decryptedMessage)

        return SecretKeySpec(symkey, ALGORITHM)
    }

    // Values used to us the same encryption method for ciphers, keys etc. 
    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

}