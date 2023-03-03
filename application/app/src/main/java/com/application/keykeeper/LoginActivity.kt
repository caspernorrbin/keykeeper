package com.application.keykeeper

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import structure.*
import java.io.File

class LoginActivity : AppCompatActivity() {
    private lateinit var bodyLayout: LinearLayout
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var buttonLogin: Button
    private lateinit var statusMessage: TextView
    private lateinit var buttonCreateAccount: Button
    private lateinit var loadingIcon: ImageView
    private lateinit var rememberCheckBox: CheckBox
    private lateinit var buttonChangeServer: Button

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
        buttonChangeServer = findViewById(R.id.change_server)

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
        buttonChangeServer.setOnClickListener{
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
    private fun openChangeServerPopup(): PopupWindow {

        val window = PopupWindowFactory.create(R.layout.login_change_server, this, window.decorView.rootView)

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

        // Close window when clicked
        closeButton.setOnClickListener { window.dismiss() }

        // Get the string array of spinner items from strings.xml
        val spinnerItems = resources.getStringArray(R.array.change_server_spinner_items).toMutableList()

        // Create an ArrayAdapter to populate the spinner with items
        val serverItemAdapter = ServerItemAdapter(this, R.layout.server_item, items)
        changeServerSpinner.adapter = serverItemAdapter

        changeServerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Enable the EditText when the specific item is selected
                serverName.isEnabled = spinnerItems[position] == "Enter the Server"
                urlInput.isEnabled = spinnerItems[position] == "Enter the Server"
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        // Set the click listener for the button
        confirmButton.setOnClickListener {
            // Get the text entered in the EditText
            val newItem = serverName.text.toString().trim()

            // Check if the text is not empty
            if (newItem.isNotEmpty()) {
                // Add the item to the spinner items list
                spinnerItems.add(newItem)

                // Clear the EditText
                serverName.setText("")

                // Save the added item to the change_server_spinner_items string array in strings.xml
                var stringArrayXml = "<string-array name=\"change_server_spinner_items\">\n"
                for (item in spinnerItems) {
                    stringArrayXml += "\t<item>$item</item>\n"
                }
                stringArrayXml += "</string-array>"
                val stringFile = File(applicationContext.filesDir, "strings.xml")
                if (!stringFile.exists()) {
                    // Create a new strings.xml file if it doesn't exist
                    stringFile.createNewFile()
                    stringFile.writeText("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n</resources>")
                }
                // Replace the existing change_server_spinner_items string array with the updated one
                val pattern = "<string-array name=\"change_server_spinner_items\">[\\s\\S]*?</string-array>"
                stringFile.writeText(stringFile.readText().replace(Regex(pattern), stringArrayXml))
            }
        }
        return window
    }

}

val items = arrayListOf<ServerItem>(
    ServerItem("Default", "..."),
    ServerItem("Add New", "...")
)