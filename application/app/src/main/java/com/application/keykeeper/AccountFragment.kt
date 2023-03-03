package com.application.keykeeper

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class AccountFragment: Fragment() {
    private lateinit var viewOfLayout: View
    private lateinit var buttonLogout: Button
    private lateinit var changeEmailButton: Button
    private lateinit var changePasswordButton: Button
    private lateinit var emailLabel: TextView




    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_account, container, false)

        // Find view components
        buttonLogout = viewOfLayout.findViewById(R.id.account_logout_button)
        changeEmailButton = viewOfLayout.findViewById(R.id.account_change_email_button)
        changePasswordButton = viewOfLayout.findViewById(R.id.account_change_password_button)
        emailLabel = viewOfLayout.findViewById(R.id.account_email_label)

        // Assign on click listeners
        buttonLogout.setOnClickListener {
            navigateToLogin()
        }

        changeEmailButton.setOnClickListener {
            val controller = viewOfLayout.findNavController()
            val action = AccountFragmentDirections.actionNavAccountFragmentToNavEditFragment()
            controller.navigate(action)
        }

        changePasswordButton.setOnClickListener {
            val controller = viewOfLayout.findNavController()
            val action = AccountFragmentDirections.actionNavAccountFragmentToNavEditFragment()
            controller.navigate(action)
        }

        return viewOfLayout
    }

    private fun navigateToLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}