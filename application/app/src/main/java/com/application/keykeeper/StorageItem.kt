package com.application.keykeeper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

class StorageItem: Fragment()  {
    private lateinit var viewOfLayout: View
    private lateinit var label: TextView
    private val args by navArgs<StorageItemArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.storage_open_item, container, false)
        label = viewOfLayout.findViewById(R.id.storage_open_label)
        // Display the credentials label to se if it is working.
        label.text = args.credentials.label
        return viewOfLayout
    }
}