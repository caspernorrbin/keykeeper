package communication

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Represents a response body sent by the server.
 */
data class ServerMessage(val message: String) {
    class Deserializer: ResponseDeserializable<ServerMessage> {
        override fun deserialize(content: String): ServerMessage
                = Gson().fromJson(content, ServerMessage::class.java)
    }
}