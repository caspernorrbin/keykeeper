package structure

import org.json.JSONObject

data class ServerItem(val name: String, val url: String, val isRemovable: Boolean = true) {
    override fun toString(): String {
        return this.name
    }

    override fun equals(other: Any?): Boolean {
        return other is ServerItem && other.name == this.name && other.url == this.url
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    fun toJSON(): JSONObject {
        val json = JSONObject()
        // Insert data.
        json.put("name", name)
        json.put("url", url)
        json.put("isRemovable", isRemovable)
        return json
    }

    companion object {
        fun getArrayDeserializer(): Deserializer<Array<ServerItem>>  {
            return Deserializer(Array<ServerItem>::class)
        }

        fun getDeserializer(): Deserializer<ServerItem>  {
            return Deserializer(ServerItem::class)
        }
    }
}