package communication

import android.content.Context
import structure.Encryption
import structure.Model.Storage

object OfflineAccount {
    fun sendLoginRequest(context: Context, email: String, password: String,
                         callback: (successful: Boolean, responseBody: String) -> Unit) {
        val accountDetails = Storage.getAccountDetails(context)
        if (accountDetails == null) {
            callback(false, "Error fetching account data")
        } else {
            val usedEmail = accountDetails.first
            val passwordHash = accountDetails.second

            val success = Encryption.comparePasswordHash(password, passwordHash)
            if (success && usedEmail == email) {
                val encSymkey = accountDetails.third
                callback(true, Encryption.decryptSymkey(password, encSymkey))
            } else {
                callback(false, "Wrong email/password. Try again.")
            }
        }
    }
}