package communication.structure

import structure.Deserializer

/**
 * Represents a response body sent by the server.
 */
data class ServerMessage(val message: String) {
    companion object {
        fun getDeserializer(): Deserializer<ServerMessage> {
            return Deserializer(ServerMessage::class)
        }
    }
}