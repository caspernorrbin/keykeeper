package structure
import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CredentialsItem(val label: String, val url: String, val userName: String, val password: String, val notes: String?) : Parcelable {
    // Used when filtering, filter is applied per world (space) separate different keywords
    override fun toString(): String {
        return this.label + " " + this.userName
    }

    @IgnoredOnParcel
    public var image: Bitmap? = null
}