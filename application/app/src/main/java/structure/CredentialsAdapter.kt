package structure

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.navigation.findNavController
import com.application.keykeeper.R
import com.application.keykeeper.StorageFragmentDirections

class CredentialsAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val items: List<CredentialsItem>
): ArrayAdapter<CredentialsItem>(context, layoutResource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var item = getItem(position)
        val view = convertView?: LayoutInflater.from(context).inflate(layoutResource, parent, false)

        // Add onClick listener to navigate to the storage_open_item view with item as argument.
        view.setOnClickListener {
            var controller = it.findNavController()
            var action = StorageFragmentDirections.actionNavStorageFragmentToNavStorageOpenItem(item!!)
            controller.navigate(action)
        }

        // Apply data from the item to the component view.
        var titleLabel = view.findViewById<TextView>(R.id.storage_item_label)
        var userNameLabel = view.findViewById<TextView>(R.id.storage_item_username)
        var passwordLabel = view.findViewById<TextView>(R.id.storage_item_password)
        titleLabel.text = item?.label
        userNameLabel.text = item?.userName
        passwordLabel.text = item?.password

        return view
    }
}