package com.application.keykeeper

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat


class CreateAccountActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var buttonCreate: Button
    private lateinit var statusMessage: TextView
    private lateinit var buttonBack: ImageButton

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
        setContentView(R.layout.activity_create_acount)

        // Find components
        buttonCreate = findViewById(R.id.buttonCreateAccount)
        buttonBack = findViewById(R.id.create_account_back_button)
        emailInput = findViewById(R.id.inputCreateAccountEmail)
        statusMessage = findViewById(R.id.create_account_status_message)
        passwordInput = findViewById(R.id.inputCreateAccountPassword)

        // Assign onClick listeners
        buttonCreate.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if(email.isValidEmail()) {
                // Crate and account create handler and send a create request to the server.
                val ac = Account(emailInput.text.toString(), passwordInput.text.toString())

                // TODO: Handle some response from the sendCreateRequest.
                ac.sendCreateAccountRequest { successful, message ->
                    if(successful) {
                        // TODO: Perhaps also show a success message instead of just switching to
                        // the login view?
                        hideStatusMessage()
                        navigateToLogin()
                    } else {
                        showStatusMessage(message, true)
                    }
                }

            } else {
                // TODO: Display error message that the email was invalid.
                showStatusMessage("Invalid email.", true)
            }
        }
        buttonBack.setOnClickListener {
            this.onBackPressed()
        }
    }

    override fun onBackPressed() {
        hideStatusMessage()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this@CreateAccountActivity, LoginActivity::class.java)
        hideStatusMessage()
        startActivity(intent)
        finish()
    }

    private fun String.isValidEmail(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}