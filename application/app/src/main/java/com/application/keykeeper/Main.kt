package com.application.keykeeper

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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
    private lateinit var toolBar : Toolbar
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        toolBar = findViewById(R.id.toolbar)
        bottomNav = findViewById(R.id.bottom_navigation_view)
        setSupportActionBar(toolBar)

        var topLevelDestinations = setOf(
            R.id.nav_debug_fragment,
            R.id.nav_storage_fragment,
            R.id.nav_account_fragment
        )

        var config = AppBarConfiguration(topLevelDestinations)
        var navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        bottomNav.setupWithNavController(navController)
        // Link the nav controller and action bar together so that the label on the action bar
        // updates when the fragment view changes.
        setupActionBarWithNavController(navController, config)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handles changes in the navbar selection
        Log.v("onOptionsItemSelected", item.itemId.toString())
        if (item.itemId == android.R.id.home) {
            // Treat navigation to home as a back button press, otherwise the view wont change.
            onBackPressed()
            return true
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