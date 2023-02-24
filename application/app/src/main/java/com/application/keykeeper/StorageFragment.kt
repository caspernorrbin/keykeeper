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
import kotlin.random.Random
import structure.*

class StorageFragment: Fragment() {
    private lateinit var viewOfLayout: View
    private lateinit var searchView: SearchView
    private lateinit var textView: TextView
    private lateinit var listView: ListView
    private lateinit var adapter: CredentialsAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarAddItemButton: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_storage, container, false)
        searchView = viewOfLayout.findViewById(R.id.storage_search_view)
        listView = viewOfLayout.findViewById(R.id.storage_list_view)
        textView = viewOfLayout.findViewById(R.id.storage_text_view)

        // Find toolbar outside this view in the activity
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbarAddItemButton = toolbar.findViewById(R.id.toolbar_add_storage_item_button)

        // Setup the adapter for the list view. It creates menu items from a list of data and
        // populates the view with them.
        adapter = CredentialsAdapter(viewOfLayout.context, R.layout.storage_item, arrayListOf())
        fetchAndUpdateListView()

        // Setup filtering of the list view using the searchView.
        listView.adapter = adapter
        listView.setOnItemClickListener { adapterView, _, i, _ ->
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

    private fun fetchAndUpdateListView() {
        Model.Communication.getItems(requireContext()) { successful, message, items ->
            if (!successful) {
                Utils.showStatusMessage(textView, message, true)
            }
            adapter.clear()
            adapter.addAll(items.toList())
        }
    }

    private fun onQueryTextListener(): SearchView.OnQueryTextListener {
        return object: SearchView.OnQueryTextListener {
            // When input is finalized
            override fun onQueryTextSubmit(query: String): Boolean {
                // If some match in the label part of the item.
                if (adapter.getItems().map{ it.label }.contains(query)) {
                    adapter.filter.filter(query)
                    Utils.hideStatusMessage(textView)
                } else {
                    Utils.showStatusMessage(textView, "No match", false)
                }
                return false
            }
            // When input changes
            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                Utils.hideStatusMessage(textView)
                return false
            }
        }
    }

    private fun onSelectItem(adapter: AdapterView<CredentialsAdapter>, index: Int) {
        val item = adapter.getItemAtPosition(index)!! as CredentialsItem
        val context = adapter.context
        val window = PopupWindowFactory.create(R.layout.storage_item_popup, context, viewOfLayout)
        val view = window.contentView
        val closeButton = view.findViewById<ImageButton>(R.id.storage_item_popup_close_button)
        val usernameGroup = view.findViewById<LinearLayout>(R.id.storage_item_popup_username_group)
        val usernameLabel = view.findViewById<TextView>(R.id.storage_item_popup_username_text)
        val passwordGroup = view.findViewById<LinearLayout>(R.id.storage_item_popup_password_group)
        val passwordLabel = view.findViewById<TextView>(R.id.storage_item_popup_password_text)
        val notesGroup = view.findViewById<LinearLayout>(R.id.storage_item_popup_notes_group)
        val notesLabel = view.findViewById<TextView>(R.id.storage_item_popup_notes_text)
        val statusMessage = view.findViewById<TextView>(R.id.storage_item_popup_status_message)
        val showPasswordButton = view.findViewById<ImageButton>(R.id.storage_item_popup_show_password_button)
        val editButton = view.findViewById<Button>(R.id.storage_item_popup_edit_button)
        val deleteButton = view.findViewById<Button>(R.id.storage_item_popup_delete_button)


        // Display hidden password
        passwordLabel.text = item.password.replace(".".toRegex(), "*")
        usernameLabel.text = item.username

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
            Utils.hideStatusMessage(statusMessage)
            openEditItemPopup(adapter, index).setOnDismissListener {
                // Dismiss this if still open when the editor closes, possibly contains invalid data
                // TODO: Fix overlaying backgrounds
                window.dismiss()
            }
        }

        // Delete confirmation
        deleteButton.setOnClickListener {
            Utils.hideStatusMessage(statusMessage)
            if (deleteButton.tag == "delete") {
                deleteButton.tag = "confirm"
                deleteButton.setText(R.string.storage_popup_confirm_delete)
                deleteButton.setBackgroundColor(context.resources.getColor(R.color.light_dangerous))
            } else {
                Model.Communication.deleteItem(item) { successful, message ->
                    if (successful) {
                        deleteButton.tag = "delete"
                        deleteButton.setText(R.string.storage_popup_delete)
                        deleteButton.setBackgroundColor(context.resources.getColor(R.color.dangerous))
                        fetchAndUpdateListView()
                        window.dismiss()
                    } else {
                        Utils.showStatusMessage(statusMessage, message, true)
                    }
                }
            }
        }

        // Copy to clipboard
        usernameGroup.setOnClickListener {
            val clipboard =
                ContextCompat.getSystemService(context, ClipboardManager::class.java)
            val clip: ClipData = ClipData.newPlainText("Copied Username", item.username)
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
        val statusMessage = view.findViewById<TextView>(R.id.storage_item_popup_status_message)
        val randomPassword = view.findViewById<EditText>(R.id.storage_item_popup_randomize_password_button)

        // Set initial text
        labelInput.setText(item.label)
        urlInput.setText(item.uri)
        usernameInput.setText(item.username)
        passwordInput.setText(item.password)
        notesInput.setText(item.notes)

        // Close window when clicked
        closeButton.setOnClickListener { window.dismiss() }
        applyButton.setOnClickListener {
            // Hide old message
            Utils.hideStatusMessage(statusMessage)

            // TODO: Implement input validation
            val updateItem = CredentialsItem(
                item.id,
                labelInput.text.toString(),
                urlInput.text.toString(),
                usernameInput.text.toString(),
                passwordInput.text.toString(),
                notesInput.text.toString()
            )

            Model.Communication.updateItem(updateItem) { successful, message ->
                println("Update item request: (successful: $successful), (message: $message)")

                if(successful) {
                    fetchAndUpdateListView()
                    // Close pop-up
                    window.dismiss()
                } else {
                    Utils.showStatusMessage(statusMessage, message, true)
                }
            }
        }

        randomPassword.setOnClickListener {
            val randomizedPassword = createRandomPassword() // creates a random password of 32 chars.
            passwordInput.setText(randomizedPassword)
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
        val statusMessage = view.findViewById<TextView>(R.id.storage_item_popup_status_message)
        val randomPassword = view.findViewById<ImageButton>(R.id.storage_item_popup_randomize_password_button)

        // Close window when clicked
        closeButton.setOnClickListener { window.dismiss() }
        addButton.setOnClickListener {
            // TODO: Implement input validation
            val newItem = CredentialsItem(
                0,
                labelInput.text.toString(),
                urlInput.text.toString(),
                usernameInput.text.toString(),
                passwordInput.text.toString(),
                notesInput.text.toString()
            )

            // Send request to create new item on the server
            Model.Communication.createItem(newItem) { successful, message ->
                println("Create item request: (successful: $successful), (message: $message)")

                if(successful) {
                    fetchAndUpdateListView()
                    // Close pop-up
                    window.dismiss()
                } else {
                    Utils.showStatusMessage(statusMessage, message, true)
                }
            }
        }

        randomPassword.setOnClickListener {
            val randomizedPassword = createRandomPassword() // creates a random password of 32 chars.
            passwordInput.setText(randomizedPassword)
        }

        return window
    }

    // creates a random password from 'characters'
    private fun createRandomPassword(): String {
        // The characters to use: (a-z, A-Z, 0-9). Just add more characters if needed
        val characters: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        var password = ""
        for (i in 0..31) { // create 32 character randomized password from 'characters'
            password += characters[Random.nextInt(characters.size)]
        }
        return password
    }
}