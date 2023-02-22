package structure

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.PopupWindow
import androidx.annotation.LayoutRes

object PopupWindowFactory {
    fun create(@LayoutRes resource: Int, context: Context, parent: View, root: ViewGroup? = null): PopupWindow {
        val view = LayoutInflater.from(context).inflate(resource, root)
        val window = PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        window.showAtLocation(parent, Gravity.CENTER, 0, 0)
        return window
    }
}