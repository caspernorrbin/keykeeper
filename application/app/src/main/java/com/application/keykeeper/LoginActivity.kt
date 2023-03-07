package com.application.keykeeper

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Display.Mode
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
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
    private lateinit var changeServerButton: Button
    private lateinit var serverConnectLabel: TextView

    // TODO: Add URLs
    private val addServerItem: ServerItem = ServerItem("Add new", "", false)
    private val defaultServerItem: ServerItem = ServerItem("KeyKeeper Server", "http://10.0.2.2:8080/", false)
    private var selectedServer: ServerItem? = null

    private fun showStatusMessage(message: String, isErrorMessage: Boolean = false) {
        // Set appropriate text color
        val colorId = if (isErrorMessage) R.color.fg_error_message else R.color.fg_success_message
        statusMessage.setTextColor(ResourcesCompat.getColor(resources, colorId, null))

        statusMessage.text = message
        statusMessage.visibility = View.VISIBLE
    }

    private fun hideStatusMessage() {
        statusMessage.visibility = View.GONE
    }

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
            if(email.isValidEmail() && password.isNotEmpty()) {
                swapBodyLoading(true)

                // TODO: Pass url from 'selectedServer'
                Model.Communication.login(email, password) { successful, message ->
                    if(successful) {
                        // If checked save email
                        if (rememberCheckBox.isChecked) {
                            Model.Storage.setRememberedEmail(applicationContext, email)
                        } else {
                            Model.Storage.removeRememberedEmail(applicationContext)
                        }

                        hideStatusMessage()
                        navigateToMain()
                    } else {
                        // Display error message that input is invalid.
                        swapBodyLoading(false)
                        showStatusMessage(message, true)
                    }
                }
            } else {
                // Display error message that input is invalid.
                if(!email.isValidEmail()) {
                    showStatusMessage("Invalid email format.", true)
                } else if(password.isEmpty()) {
                    showStatusMessage("Invalid password.", true)
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

    override fun onBackPressed() {
        // Prevent going back as it would close the app
    }

    private fun navigateToMain() {
        val intent = Intent(this@LoginActivity, Main::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToCreateAccount() {
        hideStatusMessage()

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

            hideStatusMessage()

            bodyLayout.visibility = View.INVISIBLE
            loadingIcon.visibility = View.VISIBLE
            // Start animating loading icon
            (loadingIcon.drawable as Animatable).start()

            // Necessary to disable buttons to hide keyboard if they are selected
            buttonLogin.isEnabled = false
            buttonCreateAccount.isEnabled = false

        } else {
            // Stop animating loading icon
            (loadingIcon.drawable as Animatable).stop()
            bodyLayout.visibility = View.VISIBLE
            loadingIcon.visibility = View.GONE

            buttonLogin.isEnabled = true
            buttonCreateAccount.isEnabled = true
        }
    }

    private fun String.isValidEmail(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
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
        //val removeButton = serverItemLayout.findViewById<ImageButton>(R.id.server_item_remove)

        // Close window when clicked
        closeButton.setOnClickListener { window.dismiss() }
        val items = Model.Storage.getServerItems(view.context) ?: arrayOf()
        val itemList = items.toMutableList()
        itemList.add(0, defaultServerItem)
        itemList.add(addServerItem)
        // Remove null elements
        val serverItems = itemList.filter { it -> it != null }
        val serverItemAdapter = ServerItemAdapter(view.context, R.layout.server_item, R.id.server_item_label, serverItems)
        changeServerSpinner.adapter = serverItemAdapter

        // Set default selection
        var index = serverItems.indexOfFirst { it -> it == selectedServer }
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                changeServerSpinner.setSelection(0)
            }
        }

        // Set the click listener for the button
        confirmButton.setOnClickListener {
            // Get the selected item and check if it is "Add new"
            var selectedItem = serverItems[changeServerSpinner.selectedItemPosition]
            if (selectedItem == addServerItem) {
                // Get the new server name and URL from the EditTexts
                val newServerName = serverName.text.toString()
                val newServerURL = urlInput.text.toString()
                // Create a new ServerItem object and add it to the server list
                val newServerItem = ServerItem(newServerName, newServerURL, true)
                if (Model.Storage.addServerItem(view.context, newServerItem)) {
                    selectedItem = newServerItem
                } else {
                    Log.e("addServerItem", "Failed to add server item")
                }
            }

            if (Model.Storage.setSelectedServer(view.context, selectedItem)) {
                selectedServer = updateServerConnect()
                // Close window
                window.dismiss()
            } else {
                // TODO: Add error message
            }
        }
        /*
        //Remove selected item from spinner when remove button is clicked
        removeButton.setOnClickListener {
            val selectedItem = changeServerSpinner.selectedItem as ServerItem
            if (selectedItem != serverItems[0] && selectedItem != serverItems.last()) {
                if (Model.Storage.removeServerItem(view.context, selectedItem)) {
                    serverItems.remove(selectedItem)
                    serverItemAdapter.notifyDataSetChanged()
                }
            }
        }*/
        return window
    }
}