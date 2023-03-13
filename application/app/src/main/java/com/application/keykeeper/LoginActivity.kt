package com.application.keykeeper

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import structure.*

class LoginActivity : AppCompatActivity() {
    private lateinit var bodyLayout: LinearLayout
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var buttonLogin: Button
    private lateinit var statusMessage: TextView
    private lateinit var buttonCreateAccount: Button
    private lateinit var loadingIcon: ImageView
    private lateinit var rememberCheckBox: CheckBox
    private lateinit var changeServerButton: ImageButton
    private lateinit var serverConnectLabel: TextView
    private lateinit var offlineModeBox: CheckBox

    private val addServerItem: ServerItem = ServerItem("Add new", "", false)
    private val defaultServerItem: ServerItem = ServerItem("KeyKeeper Server", BuildConfig.SERVER_URL, false)
    private var selectedServer: ServerItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Find elements
        bodyLayout = findViewById(R.id.login_body_layout)
        emailInput = findViewById(R.id.login_email_input)
        passwordInput = findViewById(R.id.login_password_input)
        buttonLogin = findViewById(R.id.login_button)
        statusMessage = findViewById(R.id.login_status_message)
        buttonCreateAccount = findViewById(R.id.login_create_button)
        loadingIcon = findViewById(R.id.login_loading_icon)
        rememberCheckBox = findViewById(R.id.login_remember_checkbox)
        changeServerButton = findViewById(R.id.login_change_server_button)
        serverConnectLabel = findViewById(R.id.login_server_connect_label)
        offlineModeBox = findViewById(R.id.login_offline_mode)

        // Apply connected server if stored
        selectedServer = updateServerConnect()

        // Apply remembered email if stored
        Model.Storage.getRememberedEmail(applicationContext)?.let {
            emailInput.setText(it)
            rememberCheckBox.isChecked = true
        }

        swapBodyLoading(false)

        // Add onClick listeners.
        buttonLogin.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            // Check if valid email and non-empty password
            if(email.isValidEmail() && password.isNotEmpty() && selectedServer != null) {
                swapBodyLoading(true)

                val offlineMode = offlineModeBox.isChecked
                Model.Communication.setSelectedServer(selectedServer!!)

                Model.Communication.login(this, offlineMode, email, password) { successful, message ->
                    if(successful) {
                        // If checked save email
                        if (rememberCheckBox.isChecked) {
                            Model.Storage.setRememberedEmail(applicationContext, email)
                        } else {
                            Model.Storage.removeRememberedEmail(applicationContext)
                        }
                        Utils.hideStatusMessage(statusMessage)
                        navigateToMain()
                    } else {
                        // Display error message that input is invalid.
                        swapBodyLoading(false)
                        Utils.showStatusMessage(statusMessage, message, true)
                    }
                }
            } else {
                // Display error message that input is invalid.
                if(!email.isValidEmail()) {
                    Utils.showStatusMessage(statusMessage, "Invalid email format.", true)
                } else if(password.isEmpty()) {
                    Utils.showStatusMessage(statusMessage, "Invalid password.", true)
                }
            }
        }

        buttonCreateAccount.setOnClickListener {
            navigateToCreateAccount()
        }
        changeServerButton.setOnClickListener{
            openChangeServerPopup()
        }
    }

    @Deprecated("Outdated")
    override fun onBackPressed() {
        // Prevent going back as it would close the app
    }

    private fun navigateToMain() {
        val intent = Intent(this@LoginActivity, Main::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToCreateAccount() {
        Utils.hideStatusMessage(statusMessage)

        val intent = Intent(this@LoginActivity, CreateAccountActivity::class.java)
        startActivity(intent)
    }

    private fun swapBodyLoading(loading: Boolean) {
        if (loading) {
            // Hide keyboard, must be done before input is invisible
            currentFocus?.let {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(it.windowToken, 0)
            }

            Utils.hideStatusMessage(statusMessage)

            bodyLayout.visibility = View.INVISIBLE
            loadingIcon.visibility = View.VISIBLE
            // Start animating loading icon
            (loadingIcon.drawable as Animatable).start()

            // Necessary to disable buttons to hide keyboard if they are selected
            buttonLogin.isEnabled = false
            buttonCreateAccount.isEnabled = false
            changeServerButton.isEnabled = false
        } else {
            // Stop animating loading icon
            (loadingIcon.drawable as Animatable).stop()
            bodyLayout.visibility = View.VISIBLE
            loadingIcon.visibility = View.GONE

            buttonLogin.isEnabled = true
            buttonCreateAccount.isEnabled = true
            changeServerButton.isEnabled = true
        }
    }

    private fun updateServerConnect(): ServerItem {
        val server = Model.Storage.getSelectedServer(applicationContext) ?: defaultServerItem
        serverConnectLabel.text = getString(R.string.login_server_connect, server.name)
        return server
    }

    private fun openChangeServerPopup(): PopupWindow {
        val window = PopupWindowFactory.create(R.layout.login_change_server_popup, this, window.decorView.rootView)

        // Allow editing within the window
        window.isFocusable = true
        window.update()
        val view = window.contentView

        // Find components
        val closeButton = view.findViewById<ImageButton>(R.id.change_server_popup_close_button)
        val changeServerSpinner = view.findViewById<Spinner>(R.id.change_server_spinner)
        val serverName = view.findViewById<EditText>(R.id.change_server_popup_server_name)
        val urlInput = view.findViewById<EditText>(R.id.change_server_popup_url_input)
        val confirmButton = view.findViewById<Button>(R.id.change_server_popup_button)
        val removeButton = view.findViewById<Button>(R.id.change_server_remove)
        val statusLabel = view.findViewById<TextView>(R.id.change_server_status_message)

        // Close window when clicked
        closeButton.setOnClickListener { window.dismiss() }
        val items = Model.Storage.getServerItems(view.context) ?: arrayOf()
        val itemList = items.toMutableList()
        itemList.add(0, defaultServerItem)
        itemList.add(addServerItem)
        // Remove null elements
        val serverItems = itemList.filterNotNull()
        val serverItemAdapter = ServerItemAdapter(view.context, R.layout.server_item, R.id.server_item_label, serverItems)
        changeServerSpinner.adapter = serverItemAdapter

        // Set default selection
        var index = serverItems.indexOfFirst { it == selectedServer }
        if (index == -1) {
            index = 0
        }
        changeServerSpinner.setSelection(index)

        // Make the Edit texts visible when specific
        changeServerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = serverItems[position]

                if (selectedItem.name == "Add new") {
                    serverName.visibility = View.VISIBLE
                    urlInput.visibility = View.VISIBLE
                } else {
                    serverName.visibility = View.GONE
                    urlInput.visibility = View.GONE
                }

                removeButton.visibility = if (selectedItem.isRemovable) View.VISIBLE else View.GONE
                Utils.hideStatusMessage(statusLabel)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                changeServerSpinner.setSelection(0)
            }
        }

        // Set the click listener for the button
        confirmButton.setOnClickListener {
            Utils.hideStatusMessage(statusLabel)
            // Get the selected item and check if it is "Add new"
            var selectedItem = serverItems[changeServerSpinner.selectedItemPosition]
            if (selectedItem == addServerItem) {
                // Get the new server name and URL from the EditTexts
                val newServerName = serverName.text.toString()
                val newServerURL = urlInput.text.toString()

                if (newServerName.isEmpty()) {
                    Utils.showStatusMessage(statusLabel, "Cannot add server with no name", true)
                    return@setOnClickListener
                }

                if (newServerURL.isEmpty()) {
                    Utils.showStatusMessage(statusLabel, "Cannot add server with no url", true)
                    return@setOnClickListener
                }

                if (serverItems.any { it.name == newServerName }) {
                    Utils.showStatusMessage(statusLabel, "Servers must have unique names", true)
                    return@setOnClickListener
                }

                // Create a new ServerItem object and add it to the server list
                val newServerItem = ServerItem(newServerName, newServerURL, true)
                if (Model.Storage.addServerItem(view.context, newServerItem)) {
                    selectedItem = newServerItem
                } else {
                    Utils.showStatusMessage(statusLabel, "Failed to add server item", true)
                }
            }

            if (Model.Storage.setSelectedServer(view.context, selectedItem)) {
                selectedServer = updateServerConnect()
                // Close window
                window.dismiss()
            } else {
                Utils.showStatusMessage(statusLabel, "Failed to set selected server", true)
            }
        }

        //Remove selected item from spinner when remove button is clicked
        removeButton.setOnClickListener {
            val selectedItem = serverItems[changeServerSpinner.selectedItemPosition]
            if (selectedItem.isRemovable) {
                Utils.hideStatusMessage(statusLabel)
                // Confirm delete
                if (removeButton.tag == "delete") {
                    removeButton.tag = "confirm"
                    removeButton.setText(R.string.storage_popup_confirm_delete)
                    removeButton.setBackgroundColor(resources.getColor(R.color.dark_dangerous))
                } else {
                    if (Model.Storage.removeServerItem(view.context, selectedItem)) {
                        // Remove the selected item from the adapter
                        serverItemAdapter.remove(selectedItem)
                        changeServerSpinner.setSelection(0)
                        // Remove selected
                        if (selectedItem == selectedServer && Model.Storage.removeSelectedServer(view.context)) {
                            selectedServer = updateServerConnect()
                        }
                    } else {
                        // Show an error message if the server item couldn't be removed
                        Utils.showStatusMessage(statusLabel, "Failed to remove the selected server item", true)
                    }
                }
            }
        }
        return window
    }
}