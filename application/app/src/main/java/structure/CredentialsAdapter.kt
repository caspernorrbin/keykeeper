package structure

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.LayoutRes
import com.application.keykeeper.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


class CredentialsAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val items: List<CredentialsItem>
): ArrayAdapter<CredentialsItem>(context, layoutResource, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView?: LayoutInflater.from(context).inflate(layoutResource, parent, false)
        val item = getItem(position)
        val titleLabel = view.findViewById<TextView>(R.id.storage_item_title_label)
        val urlLabel = view.findViewById<TextView>(R.id.storage_item_url_label)
        val image = view.findViewById<ImageView>(R.id.storage_item_image)
        titleLabel.text = item?.label
        urlLabel.text = item?.uri

        // Apply data from the item to the component view.
        if (item != null && item.image == null) {
            // Network request can not be made in the main context
            CoroutineScope(Dispatchers.Default).launch {
                // FIXME: BUG where the first image is wrong
                // FIXME: Runs multiple times for each item
                item.image = getImageFromUrl(item.uri)
                if (item.image != null) {
                    // View can only be modified in the main context
                    withContext(Dispatchers.Main) {
                        image.setImageBitmap(item.image)
                    }
                }
            }
        } else if (item != null) {
            image.setImageBitmap(item.image)
        }

        return view
    }

    private fun getImageFromUrl(urlString: String?): Bitmap? {
        return try {
            // Match for protocol and site
            val groups = Regex("(.*)://([^/]*)/?").find(urlString!!)?.groups!!
            val protocol = groups[1]?.value
            val site = groups[2]?.value
            val url = URL(protocol, site, "/favicon.ico")
            // Establish connection
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            BitmapFactory.decodeStream(connection.inputStream)
        } catch (e: Exception) {
            Log.e("getIconFromUrl", e.toString() + ' ' + e.message)
            null
        }
    }

     fun getItems(): List<CredentialsItem> {
        return this.items
    }
}