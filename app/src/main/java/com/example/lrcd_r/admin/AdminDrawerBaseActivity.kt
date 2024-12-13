package com.example.lrcd_r.admin

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.lrcd_r.Login
import com.example.lrcd_r.R
import com.google.android.material.navigation.NavigationView

open class AdminDrawerBaseActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout

    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_admin_drawer_base) // Your base layout
        val contentFrame = findViewById<FrameLayout>(R.id.activityContainer_admin) // Placeholder
        layoutInflater.inflate(
            layoutResID,
            contentFrame,
            true
        ) // Inflate child layout into placeholder

        toolbar = findViewById(R.id.toolbar_admin)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toolbarLayout = layoutInflater.inflate(R.layout.toolbar_layout, null)
        toolbar.addView(toolbarLayout) //inflate the logo sa toolbar

        menuInflater.inflate(R.menu.admin_main_drawer_menu, toolbar.menu)

        drawerLayout = findViewById(R.id.drawerLayout_admin) // Initialize drawerLayout not done

        val navigationView = drawerLayout.findViewById<NavigationView>(R.id.nav_view_admin)
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
            R.id.nav_availability_admin -> {
                startActivity(Intent(this, AdminHomepage::class.java))
                overridePendingTransition(0, 0)
            }
            R.id.nav_reservations_admin -> {
                startActivity(Intent(this, AdminReservationsActivity::class.java))
                overridePendingTransition(0, 0)
            }
        }
        return false
    }

    fun btnLogout(view: View) {
        startActivity(Intent(this, Login::class.java))
        overridePendingTransition(0, 0)
    }
}