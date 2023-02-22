package structure
import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CredentialsItem(val id: Number, val label: String, val uri: String, val username: String, val password: String, val notes: String?) : Parcelable {
    // Used when filtering, filter is applied per world (space) separate different keywords
    override fun toString(): String {
        return this.label + " " + this.username
    }

    @IgnoredOnParcel
    public var image: Bitmap? = null
}