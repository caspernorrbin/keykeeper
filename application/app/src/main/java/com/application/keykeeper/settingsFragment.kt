package com.application.keykeeper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.app.AlertDialog


class settingsFragment : Fragment() {
    private lateinit var viewOfLayout: View
    private lateinit var editText: EditText
    private lateinit var button: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewOfLayout = inflater.inflate(R.layout.fragment_settings, container, false)
        editText = viewOfLayout.findViewById(R.id.et_server_address)
        button = viewOfLayout.findViewById(R.id.btn_clear_data)




        // Inflate the layout for this fragment
        return viewOfLayout
    }


}