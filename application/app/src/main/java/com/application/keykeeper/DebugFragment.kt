package com.application.keykeeper

import com.application.keykeeper.AccountCreate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DebugFragment: Fragment() {

    private lateinit var viewOfLayout: View

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_debug, container, false)

        // Add onClick listener for buttonCreateAccount.
        var buttonCreate = viewOfLayout.findViewById(R.id.buttonCreateAccount) as Button;
        buttonCreate.setOnClickListener {

            val ac = AccountCreate("test@test.com", "pw");
            ac.sendCreateRequest();
        }

        return viewOfLayout
    }
}