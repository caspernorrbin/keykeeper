package com.application.keykeeper

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import structure.Utils


class CreateAccountActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var buttonCreate: Button
    private lateinit var statusMessage: TextView
    private lateinit var buttonBack: ImageButton

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
                Account.sendCreateAccountRequest(email, password) { successful, message ->
                    if(successful) {
                        // TODO: Perhaps also show a success message instead of just switching to
                        // the login view?
                        Utils.hideStatusMessage(statusMessage)
                        navigateToLogin()
                    } else {

                        Utils.showStatusMessage(statusMessage, message, true)
                    }
                }

            } else {
                Utils.showStatusMessage(statusMessage, "Invalid email.", true)
            }
        }
        buttonBack.setOnClickListener {
            this.onBackPressed()
        }
    }

    override fun onBackPressed() {
        Utils.hideStatusMessage(statusMessage)
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this@CreateAccountActivity, LoginActivity::class.java)
        Utils.hideStatusMessage(statusMessage)
        startActivity(intent)
        finish()
    }

    private fun String.isValidEmail(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}