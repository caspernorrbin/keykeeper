package com.application.keykeeper

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat

class LoginActivity : AppCompatActivity() {
    private lateinit var bodyLayout: LinearLayout
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var buttonLogin: Button
    private lateinit var statusMessage: TextView
    private lateinit var buttonCreateAccount: Button
    private lateinit var loadingIcon: ImageView


    private fun showStatusMessage(message: String, isErrorMessage: Boolean = false) {
        statusMessage.text = message

        if(isErrorMessage) {
            statusMessage.setTextColor(ResourcesCompat.getColor(resources, R.color.fg_error_message, null))
        } else {
            statusMessage.setTextColor(ResourcesCompat.getColor(resources, R.color.fg_success_message, null))
        }

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
        buttonLogin = findViewById(R.id.login_button);
        statusMessage = findViewById(R.id.login_status_message)
        buttonCreateAccount = findViewById(R.id.login_create_button)
        loadingIcon = findViewById(R.id.login_loading_icon)

        swapBodyLoading(false)

        // Add onClick listeners.
        buttonLogin.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            // Check if valid email and non-empty password
            if(email.isValidEmail() && password.isNotEmpty()) {
                val ac = Account(email, password)
                swapBodyLoading(true)
                ac.sendLoginRequest { successful, message ->
                    if(successful) {
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
}