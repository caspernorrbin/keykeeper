package structure
import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class CredentialsItem(val id: Number, val label: String, val uri: String, val username: String, val password: String, val notes: String?) : Parcelable {
    // Used when filtering, filter is applied per world (space) separate different keywords
    override fun toString(): String {
        return this.label + " " + this.username
    }

    @IgnoredOnParcel
    var image: Drawable? = null

    fun toJSON(): JSONObject {
        val json = JSONObject()
        // Insert data.
        json.put("id", id)
        json.put("label", label)
        json.put("uri", uri)
        json.put("username", username)
        json.put("password", password)
        json.put("notes", notes)
        return json
    }

    companion object {
        fun getArrayDeserializer(): Deserializer<Array<CredentialsItem>>  {
            return Deserializer(Array<CredentialsItem>::class)
        }

        fun getDeserializer(): Deserializer<CredentialsItem>  {
            return Deserializer(CredentialsItem::class)
        }
    }
}