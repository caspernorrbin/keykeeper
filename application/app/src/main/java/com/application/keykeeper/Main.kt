package com.application.keykeeper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class Main : AppCompatActivity() {
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        setSupportActionBar(findViewById(R.id.toolbar))
        var config = AppBarConfiguration(setOf(R.id.nav_storage_fragment, R.id.nav_account_fragment))
        navController = findNavController(R.id.nav_host_fragment)
        bottomNav = findViewById(R.id.bottom_navigation_view)
        bottomNav.setupWithNavController(navController)
        setupActionBarWithNavController(navController, config)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        // menuInflater.inflate(R.menu.menu_main, menu)
        return false
    }
}