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
import com.google.android.material.bottomnavigation.BottomNavigationView

class LoginFragment: Fragment() {

    private lateinit var viewOfLayout: View
    private lateinit var appBar: AppBarLayout
    private lateinit var navbar: BottomNavigationView
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonCreateAccount: Button

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_login, container, false)

        // Find activity elements
        val activity = requireActivity()
        appBar = activity.findViewById(R.id.appBarLayout)
        navbar = activity.findViewById(R.id.bottom_navigation_view)

        // Disable navbar & appbar
        appBar.visibility = View.GONE
        navbar.visibility = View.INVISIBLE

        // Find elements
        emailInput = viewOfLayout.findViewById(R.id.login_email_input)
        passwordInput = viewOfLayout.findViewById(R.id.login_password_input)
        buttonLogin = viewOfLayout.findViewById(R.id.login_button);
        buttonCreateAccount = viewOfLayout.findViewById(R.id.login_create_button)

        // Add onClick listeners.
        buttonLogin.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            // TODO: Validate login
            if(email.isValidEmail()) {
                // Navigate to app view
                appBar.visibility = View.VISIBLE
                navbar.visibility = View.VISIBLE
                val controller = viewOfLayout.findNavController()
                val action = LoginFragmentDirections.actionLoginFragmentToNavStorageFragment()
                controller.navigate(action)
            } else {
                // TODO: Display error message that the email was invalid.
            }
        }

        buttonCreateAccount.setOnClickListener {
            // Navigate to create account view
            val controller = viewOfLayout.findNavController()
            val action = LoginFragmentDirections.actionLoginFragmentToCreateAcountFragment()
            controller.navigate(action)
        }

        return viewOfLayout
    }

    private fun String.isValidEmail(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}