package communication

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import structure.CredentialsItem


data class DatabaseCredentialsItem(val id: Int, val account_id: Int, val item_name: String, val username: String, val password: String, val uri: String, val notes: String?) {
    class ArrayDeserializer: ResponseDeserializable<Array<DatabaseCredentialsItem>> {
        override fun deserialize(content: String): Array<DatabaseCredentialsItem> = Gson().fromJson(content, Array<DatabaseCredentialsItem>::class.java)
    }

    class Deserializer: ResponseDeserializable<DatabaseCredentialsItem> {
        override fun deserialize(content: String): DatabaseCredentialsItem = Gson().fromJson(content, DatabaseCredentialsItem::class.java)
    }

    fun toCredentialsItem(): CredentialsItem {
        return CredentialsItem(this.id, this.item_name, this.uri, this.username, this.password, this.notes)
    }
}