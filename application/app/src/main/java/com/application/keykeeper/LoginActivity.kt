package com.application.keykeeper

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var buttonLogin: Button
    private lateinit var statusMessage: TextView
    private lateinit var buttonCreateAccount: Button

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
        emailInput = findViewById(R.id.login_email_input)
        passwordInput = findViewById(R.id.login_password_input)
        buttonLogin = findViewById(R.id.login_button);
        statusMessage = findViewById(R.id.login_status_message)
        buttonCreateAccount = findViewById(R.id.login_create_button)

        // Add onClick listeners.
        buttonLogin.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            // Check if valid email and non-empty password
            if(email.isValidEmail() && password.isNotEmpty()) {
                val ac = Account(email, password)

                ac.sendLoginRequest { successful, message ->
                    if(successful) {
                        hideStatusMessage()
                        navigateToMain()
                    } else {
                        // Display error message that input is invalid.
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

    private fun String.isValidEmail(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}