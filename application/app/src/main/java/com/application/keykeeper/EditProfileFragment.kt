package com.application.keykeeper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import structure.User


class EditProfileFragment : Fragment() {
    private lateinit var viewOfLayout: View
    private lateinit var checkBoxEmail: CheckBox
    private lateinit var editTextEmail: EditText
    private lateinit var checkBoxPassword: CheckBox
    private lateinit var editTextPassword: EditText
    private lateinit var editTextOldPassword: EditText
    private lateinit var buttonSaveChanges: Button

    // Declare a variable to hold the user data
    private lateinit var user: User
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Initialize views
        checkBoxEmail = viewOfLayout.findViewById(R.id.checkBoxEmail)
        editTextEmail = viewOfLayout.findViewById(R.id.editTextEmail)
        checkBoxPassword = viewOfLayout.findViewById(R.id.checkBoxPassword)
        editTextPassword = viewOfLayout.findViewById(R.id.editTextPassword)
        editTextOldPassword = viewOfLayout.findViewById(R.id.editTextOldPassword)
        buttonSaveChanges = viewOfLayout.findViewById(R.id.buttonSaveChanges)


        // Set click listener for "Save Changes" button
        buttonSaveChanges.setOnClickListener {
            saveChanges()
        }
        // Set enabled state of email and password fields based on checkbox state
        checkBoxEmail.setOnCheckedChangeListener { _, isChecked ->
            editTextEmail.isEnabled = isChecked
        }

        checkBoxPassword.setOnCheckedChangeListener { _, isChecked ->
            editTextPassword.isEnabled = isChecked
        }

        // Fetch user data from the database
        user = getUserInfo()

        // Display the user data in the EditText fields
        editTextEmail.setText(user.email)
        editTextPassword.setText(user.password)

        return viewOfLayout
    }
    private fun saveChanges() {

        // Check which fields the user wants to update
        val updateEmail = checkBoxEmail.isChecked
        val updatePassword = checkBoxPassword.isChecked
        val oldPassword = editTextOldPassword.text.toString()


        // Check if the old password matches the current password
        if (oldPassword != user.password) {
            editTextOldPassword.error = "Incorrect password"
            return
        }

        // Update the corresponding fields if the user has selected them
        if (updateEmail) {
            val newEmail = editTextEmail.text.toString()
            user.email = newEmail
        }


        if (updatePassword) {
            val newPassword = editTextPassword.text.toString()
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