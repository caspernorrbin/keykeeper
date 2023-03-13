package structure

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.TextView

object Utils {
    fun showStatusMessage(statusMessage: TextView?, message: String, isErrorMessage: Boolean = false) {
        statusMessage?.let {
            // Set appropriate text color
            val color = if (isErrorMessage) Color.RED else Color.WHITE;
            statusMessage.setTextColor(color)
            statusMessage.text = message
            statusMessage.visibility = View.VISIBLE
        }
    }

    fun hideStatusMessage(statusMessage: TextView?) {
        statusMessage?.let {
            statusMessage.visibility = View.GONE
        }
    }
}