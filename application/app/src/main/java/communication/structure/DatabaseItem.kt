package communication
import structure.CredentialsItem
import structure.Deserializer


data class DatabaseItem(val id: Int, val account_id: Int, val item_name: String, val username: String, val password: String, val uri: String, val notes: String?) {
    fun toCredentialsItem(): CredentialsItem {
        return CredentialsItem(this.id, this.item_name, this.uri, this.username, this.password, this.notes)
    }

    companion object {
        fun getArrayDeserializer(): Deserializer<Array<DatabaseItem>>  {
            return Deserializer()
        }

        fun getDeserializer(): Deserializer<DatabaseItem>  {
            return Deserializer()
        }
    }
}