package com.application.keykeeper

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import data.storageExampleItems as items
import structure.CredentialsAdapter
import structure.CredentialsItem
import structure.PopupWindowFactory

class StorageFragment: Fragment() {
    private lateinit var viewOfLayout: View
    private lateinit var searchView: SearchView
    private lateinit var textView: TextView
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<CredentialsItem>
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarAddItemButton: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_storage, container, false)
        searchView = viewOfLayout.findViewById(R.id.storage_search_view)
        listView = viewOfLayout.findViewById(R.id.storage_list_view)
        textView = viewOfLayout.findViewById(R.id.storage_text_view)
        textView.visibility = View.INVISIBLE

        // Find toolbar outside this view in the activity
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbarAddItemButton = toolbar.findViewById(R.id.toolbar_add_storage_item_button)

        // Setup the adapter for the list view. It creates menu items from a list of data and
        // populates the view with them.
        adapter = CredentialsAdapter(viewOfLayout.context, R.layout.storage_item, items)

        // Setup filtering of the list view using the searchView.
        listView.adapter = adapter
        listView.setOnItemClickListener { adapterView, _, i, _ ->
            // TODO: Find another fix for this error
            @Suppress("UNCHECKED_CAST")
            onSelectItem(adapterView as AdapterView<CredentialsAdapter>, i)
        }
        searchView.setOnQueryTextListener(onQueryTextListener())

        // Allow the search bar to be opened from anywhere in its area, and not only the icon.
        searchView.setOnClickListener { searchView.isIconified = false }

        // Setup toolbar button
        toolbarAddItemButton.setOnClickListener {
            Log.v("setOnClickListener", "Clicked Add Item Button!")
            openCreateItemPopup()
        }

        return viewOfLayout
    }

    private fun onQueryTextListener(): SearchView.OnQueryTextListener {
        return object: SearchView.OnQueryTextListener {
            // When input is finalized
            override fun onQueryTextSubmit(query: String): Boolean {
                // If some match in the label part of the item.
                if (items.map{ it.label }.contains(query)) {
                    adapter.filter.filter(query)
                    textView.visibility = View.INVISIBLE
                } else {
                    textView.visibility = View.VISIBLE
                }
                return false
            }
            // When input changes
            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                textView.visibility = View.INVISIBLE
                return false
            }
        }
    }

    private fun onSelectItem(adapter: AdapterView<CredentialsAdapter>, index: Int) {
        val item = adapter.getItemAtPosition(index)!! as CredentialsItem
        val context = adapter.context
        val window = PopupWindowFactory.create(R.layout.storage_item_popup, context, viewOfLayout)
        val view = window.contentView
        val editButton = view.findViewById<Button>(R.id.storage_item_popup_edit_button)
        val closeButton = view.findViewById<ImageButton>(R.id.storage_item_popup_close_button)
        val usernameGroup = view.findViewById<LinearLayout>(R.id.storage_item_popup_username_group)
        val usernameLabel = view.findViewById<TextView>(R.id.storage_item_popup_username_text)
        val passwordGroup = view.findViewById<LinearLayout>(R.id.storage_item_popup_password_group)
        val passwordLabel = view.findViewById<TextView>(R.id.storage_item_popup_password_text)
        val notesGroup = view.findViewById<LinearLayout>(R.id.storage_item_popup_notes_group)
        val notesLabel = view.findViewById<TextView>(R.id.storage_item_popup_notes_text)
        val showPasswordButton = passwordGroup.findViewById<ImageButton>(R.id.storage_item_popup_show_password_button)

        // Display hidden password
        passwordLabel.text = item.password.replace(".".toRegex(), "*")
        usernameLabel.text = item.userName

        // Disable notes group if no notes exist
        if (item.notes == null || item.notes.isEmpty()) {
            notesGroup.visibility = View.GONE
        } else {
            notesGroup.visibility = View.VISIBLE
            notesLabel.text = item.notes
        }

        // Close window when clicked
        closeButton.setOnClickListener { window.dismiss() }

        // Open edit popup when clicked
        editButton.setOnClickListener {
            openEditItemPopup(adapter, index).setOnDismissListener {
                // Dismiss this if still open when the editor closes, possibly contains invalid data
                // TODO: Fix overlaying backgrounds
                window.dismiss()
            }
        }

        // Copy to clipboard
        usernameGroup.setOnClickListener {
            val clipboard =
                ContextCompat.getSystemService(context, ClipboardManager::class.java)
            val clip: ClipData = ClipData.newPlainText("Copied Username", item.userName)
            clipboard!!.setPrimaryClip(clip)
        }
        passwordGroup.setOnClickListener {
            val clipboard =
                ContextCompat.getSystemService(context, ClipboardManager::class.java)
            // Todo de-encrypt the password first
            val clip: ClipData = ClipData.newPlainText("Copied Password", item.password)
            clipboard!!.setPrimaryClip(clip)
        }


        // Toggle between displaying password, store state in tag value
        showPasswordButton.setOnClickListener {
            if (showPasswordButton.tag == "revealed") {
                showPasswordButton.tag = "hidden"
                passwordLabel.text = item.password.replace(Regex("."), "*")
            } else {
                showPasswordButton.tag = "revealed"
                // Todo de-encrypt the password
                passwordLabel.text = item.password

            }
        }
    }

    private fun openEditItemPopup(adapter: AdapterView<CredentialsAdapter>, index: Int): PopupWindow {
        val item = adapter.getItemAtPosition(index)!! as CredentialsItem
        val context = adapter.context
        val window = PopupWindowFactory.create(R.layout.storage_item_popup_edit, context, viewOfLayout)
        // Allow editing within the window
        window.isFocusable = true
        window.update()
        val view = window.contentView
        // Find components
        val applyButton = view.findViewById<Button>(R.id.storage_item_popup_apply_button)
        val closeButton = view.findViewById<ImageButton>(R.id.storage_item_popup_close_button)
        val labelInput = view.findViewById<EditText>(R.id.storage_item_popup_label_input)
        val urlInput = view.findViewById<EditText>(R.id.storage_item_popup_url_input)
        val usernameInput = view.findViewById<EditText>(R.id.storage_item_popup_username_input)
        val passwordInput = view.findViewById<EditText>(R.id.storage_item_popup_password_input)
        val notesInput = view.findViewById<EditText>(R.id.storage_item_popup_notes_input)

        // Set initial text
        labelInput.setText(item.label)
        urlInput.setText(item.url)
        usernameInput.setText(item.userName)
        passwordInput.setText(item.password)
        notesInput.setText(item.notes)

        // Close window when clicked
        closeButton.setOnClickListener { window.dismiss() }
        applyButton.setOnClickListener {
            // Remove old item
            adapter.adapter.remove(item)
            // TODO: Implement input validation
            val newItem = CredentialsItem(
                labelInput.text.toString(),
                urlInput.text.toString(),
                usernameInput.text.toString(),
                passwordInput.text.toString(),
                notesInput.text.toString()
            )
            // TODO: handle apply by applying changes to the database
            // Insert new item
            adapter.adapter.insert(newItem, index)
            window.dismiss()
        }

        return window
    }
    private fun openCreateItemPopup(): PopupWindow {
        val context = adapter.context
        val window = PopupWindowFactory.create(R.layout.storage_item_popup_add, context, viewOfLayout)
        // Allow editing within the window
        window.isFocusable = true
        window.update()
        val view = window.contentView
        // Find components
        val addButton = view.findViewById<Button>(R.id.storage_item_popup_add_button)
        val closeButton = view.findViewById<ImageButton>(R.id.storage_item_popup_close_button)
        val labelInput = view.findViewById<EditText>(R.id.storage_item_popup_label_input)
        val urlInput = view.findViewById<EditText>(R.id.storage_item_popup_url_input)
        val usernameInput = view.findViewById<EditText>(R.id.storage_item_popup_username_input)
        val passwordInput = view.findViewById<EditText>(R.id.storage_item_popup_password_input)
        val notesInput = view.findViewById<EditText>(R.id.storage_item_popup_notes_input)

        // Close window when clicked
        closeButton.setOnClickListener { window.dismiss() }
        addButton.setOnClickListener {
            // TODO: Implement input validation
            val newItem = CredentialsItem(
                labelInput.text.toString(),
                urlInput.text.toString(),
                usernameInput.text.toString(),
                passwordInput.text.toString(),
                notesInput.text.toString()
            )
            // TODO: handle apply by applying changes to the database
            // Insert new item
            adapter.add(newItem)
            window.dismiss()
        }

        return window
    }
}