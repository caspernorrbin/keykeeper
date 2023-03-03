package com.application.keykeeper

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import structure.User


class EditProfileFragment : Fragment() {
    private lateinit var viewOfLayout: View
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextOldPassword: EditText
    private lateinit var buttonSaveChanges: Button

    // Declare a variable to hold the user data
    private lateinit var user: User
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Initialize views
        editTextEmail = viewOfLayout.findViewById(R.id.editTextEmail)
        editTextPassword = viewOfLayout.findViewById(R.id.editTextPassword)
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
            saveChanges()
        }
        // Fetch user data from the database
        user = getUserInfo()

        // Display the user data in the EditText fields
        editTextEmail.setText(user.email)
        editTextPassword.setText(user.password)

        return viewOfLayout
    }
    private fun saveChanges() {

        val oldPassword = editTextOldPassword.text.toString()


        // Check if the old password matches the current password
        if (oldPassword != user.password) {
            editTextOldPassword.error = "Incorrect password"
            return
        }

        val newEmail = editTextEmail.text.toString()
        val newPassword = editTextPassword.text.toString()

        // Update the user data if necessary
        if (newEmail != user.email) {
            user.email = newEmail
        }

        if (newPassword != user.password) {
            user.password = newPassword
        }

        // Save the updated user information to the database
        saveUserInfo(user)

        // Refresh the user data from the database
        user = getUserInfo()

        // Display the updated user data in the EditText fields
        editTextEmail.setText(user.email)
        editTextPassword.setText(user.password)
    }

    // TODO the next function for retrieving data from database
    private fun getUserInfo(): User {
        // Retrieve the user's information from the database
        return User()
    }

    // TODO the next function for saving data to database
    private fun saveUserInfo(user: User) {
        // Save the user's information to the database


    }

}