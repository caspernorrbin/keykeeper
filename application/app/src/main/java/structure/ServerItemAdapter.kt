package structure

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.application.keykeeper.R
import kotlinx.coroutines.*
import java.io.InputStream
import java.net.URL


class ServerItemAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    @IdRes private val labelResource: Int,
    private val items: List<ServerItem>
): ArrayAdapter<ServerItem>(context, layoutResource, labelResource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView?: LayoutInflater.from(context).inflate(layoutResource, parent, false)
        val item = getItem(position)
        val label = view.findViewById<TextView>(R.id.server_item_label)
        item?.let {
            label.text = item.name
        }

        return view
    }
}