package com.application.keykeeper

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class AccountFragment: Fragment() {
    private lateinit var viewOfLayout: View
    private lateinit var buttonLogout: Button

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_account, container, false)
        // Find view components
        buttonLogout = viewOfLayout.findViewById(R.id.account_logout_button)
        // Assign on click listeners
        buttonLogout.setOnClickListener {
            navigateToLogin()
        }
        return viewOfLayout
    }

    private fun navigateToLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}