package com.application.keykeeper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import data.storageExampleItems as items
import structure.CredentialsAdapter

class StorageFragment: Fragment() {
    private lateinit var viewOfLayout: View
    private lateinit var searchView: SearchView
    private lateinit var textView: TextView
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<*>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_storage, container, false)
        searchView = viewOfLayout.findViewById(R.id.storage_search_view)
        listView = viewOfLayout.findViewById(R.id.storage_list_view)
        textView = viewOfLayout.findViewById(R.id.storage_text_view)
        textView.text = ""
        // Setup the adapter for the list view. It creates menu items from a list of data and
        // populates the view with them.
        adapter = CredentialsAdapter(viewOfLayout.context, R.layout.storage_item, items)
        // Setup filtering of the list view using the searchView.
        listView.adapter = adapter
        searchView.setOnQueryTextListener(onQueryTextListener())
        // Allow the search bar to be opened from anywhere in its area, and not only the icon.
        searchView.setOnClickListener { searchView.isIconified = false }
        return viewOfLayout
    }

    private fun onQueryTextListener(): SearchView.OnQueryTextListener {
        return object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (items.map{ it.label }.contains(query)) {
                    adapter.filter.filter(query)
                } else {
                    textView.text = resources.getText(R.string.storage_query_no_match)
                }
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                textView.text = ""
                return false
            }
        }
    }
}