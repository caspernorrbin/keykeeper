package com.application.keykeeper

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import structure.Model
import structure.Utils


class EditProfileFragment : Fragment() {
    private lateinit var textView: TextView
    private lateinit var viewOfLayout: View
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextPassword2: EditText
    private lateinit var editTextOldPassword: EditText
    private lateinit var buttonSaveChanges: Button

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Initialize views
        textView = viewOfLayout.findViewById(R.id.textView)
        editTextEmail = viewOfLayout.findViewById(R.id.editTextEmail)
        editTextPassword = viewOfLayout.findViewById(R.id.editTextPassword)
        editTextPassword2 = viewOfLayout.findViewById(R.id.editTextPassword2)
        editTextOldPassword = viewOfLayout.findViewById(R.id.editTextOldPassword)
        buttonSaveChanges = viewOfLayout.findViewById(R.id.buttonSaveChanges)

        // Disable the "Save Changes" button initially
        buttonSaveChanges.isEnabled = false

        // Add a text change listener to the old password field
        editTextOldPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Enable the button if the old password field is not empty
                buttonSaveChanges.isEnabled = !s.isNullOrEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        // Set click listener for "Save Changes" button
        buttonSaveChanges.setOnClickListener {
            updateAccount()
        }

        return viewOfLayout
    }

    private fun updateAccount() {
        val newEmail = editTextEmail.text.toString()
        val oldPassword = editTextOldPassword.text.toString()
        val newPassword = editTextPassword.text.toString()
        val newPassword2 = editTextPassword2.text.toString()

        if (newEmail != "" && !newEmail.isValidEmail()) {
            Utils.showStatusMessage(textView, "Invalid email format", true)
        } else if (newPassword != newPassword2) {
            Utils.showStatusMessage(textView, "Passwords do not match", true)
        } else {
            Model.Communication.updateAccount(oldPassword, newEmail, newPassword) { success, message ->
                if (success) {
                    navigateToLogin()
                }
                else {
                    Utils.showStatusMessage(textView, message, true)
                }
            }
        }
    }

    private fun navigateToLogin() {
        Model.clearValues()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun String.isValidEmail(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}