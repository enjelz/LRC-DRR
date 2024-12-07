package com.example.lrcd_r.users

import android.content.Intent
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.lrcd_r.R
import com.google.android.material.navigation.NavigationView

open class DrawerBaseActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout

    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_drawer_base) // Your base layout
        val contentFrame = findViewById<FrameLayout>(R.id.activityContainer) // Placeholder
        layoutInflater.inflate(
            layoutResID,
            contentFrame,
            true
        ) // Inflate child layout into placeholder

        toolbar = findViewById(R.id.toolbars)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toolbarLayout = layoutInflater.inflate(R.layout.toolbar_layout, null)
        toolbar.addView(toolbarLayout) //inflate the logo sa toolbar

        menuInflater.inflate(R.menu.main_drawer_menu, toolbar.menu)

        drawerLayout = findViewById(R.id.drawerLayout) // Initialize drawerLayout

        val navigationView = drawerLayout.findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.menu_drawer_open,
            R.string.menu_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)

        when (item.itemId) {
            R.id.nav_reserve -> {
                startActivity(Intent(this, Reserve::class.java))
                overridePendingTransition(0, 0)
            }
            R.id.nav_reservations -> {
                startActivity(Intent(this, ReservationsActivity::class.java))
                overridePendingTransition(0, 0)
            }
        }
        return false
    }
}