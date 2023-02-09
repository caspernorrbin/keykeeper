package structure
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CredentialsItem(val label: String, val userName: String, val password: String) : Parcelable {
    // Used when filtering, filter is applied per world (space) separate different keywords
    override fun toString(): String {
        return this.label + " " + this.userName
    }
}