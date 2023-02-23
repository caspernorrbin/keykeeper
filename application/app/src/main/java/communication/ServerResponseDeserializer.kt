package communication

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Represents a response body sent by the server.
 */
class ServerResponseDeserializer: ResponseDeserializable<String> {
    override fun deserialize(content: String): String
    = Gson().fromJson(content, String::class.java)
}