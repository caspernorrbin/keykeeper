package com.application.keykeeper

import android.os.Build
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import at.favre.lib.crypto.bcrypt.BCrypt
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom
import java.security.MessageDigest
import java.util.Base64

@RequiresApi(Build.VERSION_CODES.O)

/**
 * Handles all encryption and hashing of symmetric keys, items, and passwords
 *
 *  An example on how the functions can be used:
 *    val symkey = Encryption.generateSymkey()
 *
 *    val plainText = "A nice message"
 *    val encryptedItem = Encryption.encryptItem(symkey, plainText)
 *    val encryptedKey = Encryption.encryptSymkey(passwordHash, symkey)
 *
 *    val decryptedKey = Encryption.decryptSymkey(passwordHash, encryptedKey)
 *    val decryptedItem = Encryption.decryptItem(decryptedKey, encryptedItem)
 *
 */
object Encryption {
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    // Splits the IV and the symmetric key from the encoded string
    private fun splitIvSym(encodedString: String) : Pair<ByteArray, SecretKey> {
        val ivString = encodedString.substring(0, 24)
        val keyString = encodedString.substring(24, encodedString.length)

        val iv = Base64.getDecoder().decode(ivString)
        val symkey = SecretKeySpec(Base64.getDecoder().decode(keyString), ALGORITHM)
        return Pair(iv, symkey)
    }

    // Generates a Cipher which will be used for encrypting the data
    private fun generateEncryptCipher(symkey: SecretKey, symiv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, symkey, IvParameterSpec(symiv))
        }
    }

    // Generates a Cipher for decrypting data that was encrypted with the key and the IV
    private fun generateDecryptCipher(symkey: SecretKey, symiv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, symkey, IvParameterSpec(symiv))
        }
    }

    // Generates a symmetric key (SecretKey) and a cipher IV which will be used to encrypt and decrypt the data
    public fun generateSymkey(): String {
        val random = SecureRandom()
        var randomArray = ByteArray(32) // 256 bit key length
        random.nextBytes(randomArray)

        // Generate symmetric key
        val symkey: SecretKey = SecretKeySpec(randomArray, ALGORITHM)

        // Generate cipher
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, symkey)
        }

        // Encode symmetric key and IV
        val symString: String = Base64.getEncoder().encodeToString(symkey.encoded)
        val ivString: String = Base64.getEncoder().encodeToString(cipher.iv)

        // Return the encoded symmetric key and IV, IV is always 24 characters long
        return "$ivString$symString"
    }

    // Encrypts an item using the symmetric key
    public fun encryptItem(symkeyString: String, item: String): String {
        // Extract the IV and symmetric key and generate a cipher
        val (iv, symkey) = splitIvSym(symkeyString)
        val cipher = generateEncryptCipher(symkey, iv)

        // Encrypt and encode the item
        val encryptedMessage: ByteArray = cipher.doFinal(item.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedMessage)
    }

    // Decrypts an item using the symmetric key
    public fun decryptItem(symkeyString: String, item: String): String {
        // Extract the IV and symmetric key and generate a cipher
        val (iv, symkey) = splitIvSym(symkeyString)
        val cipher = generateDecryptCipher(symkey, iv)

        // Decrypt and decode the item
        val decryptedMessage = cipher.doFinal(Base64.getDecoder().decode(item))
        return String(decryptedMessage)
    }

    // Encrypts the symmetric key using the master password
    public fun encryptSymkey(master: String, symkeyString: String): String {
        // Hash the master password to get the correct length
        val digest = MessageDigest.getInstance("SHA-256")
        val pHash = digest.digest(master.toByteArray())

        // Generate new symmetric key that will be used to encrypt the original symmetric key
        val key: SecretKey = SecretKeySpec(pHash, ALGORITHM)

        // Generate cipher
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, key)
        }

        // Encrypt the original symmetric key
        val encryptedKey: ByteArray = cipher.doFinal(symkeyString.toByteArray())

        // Encode the original encrypted symmetric key and IV
        val keyString: String = Base64.getEncoder().encodeToString(encryptedKey)
        val ivString: String = Base64.getEncoder().encodeToString(cipher.iv)

        return "$ivString$keyString"
    }

    // Decrypts the symmetric key using the master password
    public fun decryptSymkey(master: String, symkeyString: String): String {
        // Hash the master password to get the correct length
        val digest = MessageDigest.getInstance("SHA-256")
        val pHash = digest.digest(master.toByteArray())

        // Generate new symmetric key that will be used to decrypt the original symmetric key
        val key = SecretKeySpec(pHash, ALGORITHM)

        // Extract and decode the IV
        val ivString = symkeyString.substring(0, 24)
        val iv = Base64.getDecoder().decode(ivString)

        // Extract the original encrypted symmetric key
        val keyString = symkeyString.substring(24, symkeyString.length)

        // Create a cipher and decrypt the original symmetric key
        val cipher = generateDecryptCipher(key, iv)
        val symkey = cipher.doFinal(Base64.getDecoder().decode(keyString))
        
        return String(symkey)
    }

    // Hashes the master password using the email as a salt
    public fun hashAuthentication(master: String, email: String): String {
        val saltRounds = 12

        // Hash the email address to get the correct length for the salt
        val digest = MessageDigest.getInstance("MD5")
        val emailHash = digest.digest(email.toByteArray())

        // Hash the master password
        val hashed = BCrypt.withDefaults().hash(saltRounds, emailHash, master.toByteArray())

        return String(hashed)
    }

    // Compares the master password with the hashed password
    public fun comparePasswordHash(master: String, hashedPW: String): Boolean {
        val result = BCrypt.verifyer().verify(master.toByteArray(), hashedPW.toByteArray())
        return result.verified
    }
}
