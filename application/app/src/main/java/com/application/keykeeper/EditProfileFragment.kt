package com.application.keykeeper

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.findNavController



class EditProfileFragment : Fragment() {
    private lateinit var viewOfLayout: View
    private lateinit var checkBoxEmail: CheckBox
    private lateinit var editTextEmail: EditText
    private lateinit var checkBoxName: CheckBox
    private lateinit var editTextName: EditText
    private lateinit var checkBoxPassword: CheckBox
    private lateinit var editTextPassword: EditText
    private lateinit var buttonSaveChanges: Button

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Initialize views
        checkBoxEmail = viewOfLayout.findViewById(R.id.checkBoxEmail)
        editTextEmail = viewOfLayout.findViewById(R.id.editTextEmail)
        checkBoxName = viewOfLayout.findViewById(R.id.checkBoxName)
        editTextName = viewOfLayout.findViewById(R.id.editTextName)
        checkBoxPassword = viewOfLayout.findViewById(R.id.checkBoxPassword)
        editTextPassword = viewOfLayout.findViewById(R.id.editTextPassword)
        buttonSaveChanges = viewOfLayout.findViewById(R.id.buttonSaveChanges)

        // Set click listener for "Save Changes" button
        buttonSaveChanges.setOnClickListener {
            saveChanges()
        }

        // Set click listeners for checkboxes
        checkBoxEmail.setOnCheckedChangeListener { buttonView, isChecked ->
            editTextEmail.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        checkBoxName.setOnCheckedChangeListener { buttonView, isChecked ->
            editTextName.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        checkBoxPassword.setOnCheckedChangeListener { buttonView, isChecked ->
            editTextPassword.visibility = if (isChecked) View.VISIBLE else View.GONE
        }


        return viewOfLayout
    }
    private fun saveChanges() {
        // Get the current user's information from a database or API
        val user = getUserInfo()

        // Check which fields the user wants to update
        val updateEmail = checkBoxEmail.isChecked
        val updateName = checkBoxName.isChecked
        val updatePassword = checkBoxPassword.isChecked


        // Update the corresponding fields if the user has selected them
        if (updateEmail) {
            val newEmail = editTextEmail.text.toString()
            user.email = newEmail
        }

        if (updateName) {
            val newName = editTextName.text.toString()
            user.name = newName
        }

        if (updatePassword) {
            val newPassword = editTextPassword.text.toString()
            user.password = newPassword
        }

        // Save the updated user information to the database or API
        saveUserInfo(user)
    }

    private fun getUserInfo(): User {
        // Retrieve the user's information from the database or API
        // In this example, we'll just return a hard-coded user object
        return User(
            "test@example.com",
            "Test Test",
            "password123"
        )
    }

    private fun saveUserInfo(user: User) {
        // Save the user's information to the database or API
        // In this example, we'll just log the updated user object
        Log.d("EditProfileFragment", "Updated user: $user")
    }

}

// example for data
data class User(
    var email: String,
    var name: String,
    var password: String
)




