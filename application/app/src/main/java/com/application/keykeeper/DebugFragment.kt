package com.application.keykeeper

import com.application.keykeeper.AccountCreate

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DebugFragment: Fragment() {

    private lateinit var viewOfLayout: View

    fun String.isValidEmail(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_debug, container, false)

        // Add onClick listener for buttonCreateAccount.
        var buttonCreate = viewOfLayout.findViewById(R.id.buttonCreateAccount) as Button;
        buttonCreate.setOnClickListener {

            val emailInput: EditText = viewOfLayout.findViewById(R.id.inputCreateAccountEmail)
            val passwordInput: EditText = viewOfLayout.findViewById(R.id.inputCreateAccountPassword)

            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if(email.isValidEmail()) {
                // Crate and account create handler and send a create request to the server.
                val ac = AccountCreate(emailInput.text.toString(), passwordInput.text.toString());

                // TODO: Handle some response from the sendCreateRequest.
                ac.sendCreateRequest();

                // TODO: If the sendCreateRequest was done successfully, the view should switch to
                // a "logged-in" state.
            } else {
                // TODO: Display error message that the email was invalid.
            }
        }

        return viewOfLayout
    }
}