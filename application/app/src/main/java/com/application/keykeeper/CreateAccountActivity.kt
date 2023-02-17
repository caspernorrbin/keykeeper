package com.application.keykeeper

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class CreateAccountActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var buttonCreate: Button
    private lateinit var buttonBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_acount)

        // Find components
        buttonCreate = findViewById(R.id.buttonCreateAccount)
        buttonBack = findViewById(R.id.create_account_back_button)
        emailInput = findViewById(R.id.inputCreateAccountEmail)
        passwordInput = findViewById(R.id.inputCreateAccountPassword)

        // Assign onClick listeners
        buttonCreate.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if(email.isValidEmail()) {
                // Crate and account create handler and send a create request to the server.
                val ac = AccountCreate(emailInput.text.toString(), passwordInput.text.toString())

                // TODO: Handle some response from the sendCreateRequest.
                ac.sendCreateRequest();

                // TODO: If the sendCreateRequest was done successfully, the view should switch to
                // a "logged-in" state.

                navigateToLogin()
            } else {
                // TODO: Display error message that the email was invalid.
            }
        }
        buttonBack.setOnClickListener {
            this.onBackPressed()
        }
    }

    override fun onBackPressed() {
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this@CreateAccountActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun String.isValidEmail(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}