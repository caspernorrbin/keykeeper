package com.application.keykeeper

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class Main : AppCompatActivity() {
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var toolbar : Toolbar
    private lateinit var navController: NavController

    private lateinit var toolbarStorageAddItemButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        toolbar = findViewById(R.id.toolbar)
        bottomNav = findViewById(R.id.bottom_navigation_view)
        setSupportActionBar(toolbar)

        val topLevelDestinations = setOf(
            R.id.nav_storage_fragment,
            R.id.nav_account_fragment,
        )

        val config = AppBarConfiguration(topLevelDestinations)
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        bottomNav.setupWithNavController(navController)
        // Link the nav controller and action bar together so that the label on the action bar
        // updates when the fragment view changes.
        setupActionBarWithNavController(navController, config)

        toolbarStorageAddItemButton = toolbar.findViewById(R.id.toolbar_add_storage_item_button)

        // Change visibility of "add" button on the toolbar when navigating to and from the Storage view
        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbarStorageAddItemButton.visibility = when(destination.id) {
                R.id.nav_storage_fragment -> View.VISIBLE
                else -> View.GONE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handles changes in the navbar selection
        Log.v("onOptionsItemSelected", item.itemId.toString())
        if (item.itemId == android.R.id.home) {
            // Treat navigation to home as a back button press, otherwise the view wont change.
            onBackPressed()
            return true
        }

        if (item.itemId == R.id.nav_storage_fragment) {
            Log.v("onOptionsItemSelected", "nav_storage_fragment: True")
        } else {
            Log.v("onOptionsItemSelected", "nav_storage_fragment: False")
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        // Handles back button presses from the toolbar, visible in submenus
        Log.v("onBackPressed", supportFragmentManager.backStackEntryCount.toString())
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}