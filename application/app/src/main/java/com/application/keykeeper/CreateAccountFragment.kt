package com.application.keykeeper

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.appbar.AppBarLayout
import androidx.navigation.findNavController
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class CreateAccountFragment: Fragment() {

    private lateinit var viewOfLayout: View
    private lateinit var appBar: AppBarLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navbar: BottomNavigationView

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_create_acount, container, false)

        // Find activity elements
        val activity = requireActivity()
        appBar = activity.findViewById(R.id.appBarLayout)
        toolbar = activity.findViewById(R.id.toolbar)
        navbar = activity.findViewById(R.id.bottom_navigation_view)
        appBar.visibility = View.VISIBLE
        navbar.visibility = View.INVISIBLE
        toolbar.setTitle(R.string.create_account_header)

        // Add onClick listener for buttonCreateAccount.
        var buttonCreate = viewOfLayout.findViewById<Button>(R.id.buttonCreateAccount);
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

                // Navigate to login view
                var controller = viewOfLayout.findNavController()
                var action = CreateAccountFragmentDirections.actionCreateAcountFragmentToLoginFragment()
                controller.navigate(action)
            } else {
                // TODO: Display error message that the email was invalid.
            }
        }

        return viewOfLayout
    }

    private fun String.isValidEmail(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}