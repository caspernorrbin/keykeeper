package communication
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.jsonBody
import communication.structure.ServerMessage
import org.json.JSONObject
import com.application.keykeeper.Encryption
import communication.ServerResponseDeserializer


@RequiresApi(Build.VERSION_CODES.O)
/**
 * Represents an Account that can be used when logging in, creating an account and identifying
 * the current user.
 */
object Account {

    // Indicates whether the Account has successfully logged in or not.
    private var loggedIn: Boolean

    // If loggedIn is set to true, this variable contains a sessionCookie to use when performing
    // authenticated requests as the user.
    private var sessionCookie: String

    private var symkey: String

    init {
        loggedIn = false
        sessionCookie = ""
        symkey = ""
    }

    // Returns true if the user is logged in, false if not
    fun isLoggedIn(): Boolean {
        return this.loggedIn
    }

    // Returns a JSONObject of the data associated with the account.

    private fun jsonAccountData(email: String, passwordHash: String,
                                includeSymKey: Boolean = false): JSONObject {
        // Create object containing data to be sent to the server.
        val accountData = JSONObject()

        // Insert data.
        accountData.put("email", email)

        accountData.put("password", passwordHash) // send hashed master

        if(includeSymKey) {
            val symkey = Encryption.generateSymkey()
            println("New symkey: $symkey")
            accountData.put("symkey", Encryption.encryptSymkey(passwordHash, symkey)) // save encrypted key
        }

        return accountData
    }

    // Returns a Fuel Request handle for sending an authenticated POST request to the specified URL
    // with json data.
    // Note: Should only be used if the user is loggedIn
    fun sendAuthenticatedPostRequest(url: String): Request {
        return Fuel.post(url).header("Cookie", sessionCookie)
            .header("Content-Type", "application/json")
    }


    // Returns a Fuel Request handle for sending an authenticated GET request to the specified URL.
    // Note: Should only be used if the user is loggedIn
    fun sendAuthenticatedGetRequest(url: String): Request {
        return Fuel.get(url).header("Cookie", sessionCookie)
    }

    // Sends a request to login to the server and calls the callback function with the server
    // response. If the login was successful, the loggedIn property is set to true.
    fun sendLoginRequest(email: String, password: String,
                                callback: (successful: Boolean, responseBody: String) -> Unit) {

        val passwordHash = Encryption.hashAuthentication(password, email)
        val jsonPostData = this.jsonAccountData(email, passwordHash, false)

        // Make request.
        Fuel.post("http://10.0.2.2:8080/api/auth/login")
            .header("Content-Type", "application/json")
            .jsonBody(jsonPostData.toString())
            .responseObject(ServerMessage.getDeserializer()) { _, response, result ->
                val (serverResponse, _) = result

                // Set loggedIn
                if(response.statusCode == 200) {
                    this.loggedIn = true
                    this.symkey = Encryption.decryptSymkey(passwordHash, serverResponse!!)
                    println("Decrypted symkey: ${this.symkey}")
                    sessionCookie = response.headers["Set-Cookie"].first()
                    println("Got session cookie: $sessionCookie")
                }

                when(response.statusCode) {
                    200 -> "Logged in"
                    400 -> "Wrong email/password. Try again."
                    else -> "Something went wrong when communicating with the server. Try again later."
                }?.let { callback.invoke(response.statusCode == 200, it) }
            }
    }

    // Sends a request to the server to create a new account.
    fun sendCreateAccountRequest(email: String, password: String,
                                        callback: (success: Boolean, message: String) -> Unit) {

        val passwordHash = Encryption.hashAuthentication(password, email)
        val jsonPostData = this.jsonAccountData(email, passwordHash, true)

        // Make request.
        Fuel.post("http://10.0.2.2:8080/api/account/create")
            .header("Content-Type", "application/json")
            .jsonBody(jsonPostData.toString())
            .responseObject(ServerMessage.getDeserializer()) { _, response, result ->
                val (serverResponse, _) = result

                when(response.statusCode) {
                    200 -> serverResponse?.message
                    400 -> "Failed to create account"
                    else -> "Something went wrong when communicating with the server. Try again later."
                }?.let { callback.invoke(response.statusCode == 200, it) }
            }
    }
}
